package com.terminalvelocitycabbage.engine.ecs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComponentFilter {

    //List of components from the .excludes(...) node
    Set<Class<? extends Component>> excludedComponents;
    //List of components from the .allOf(...) node
    Set<Class<? extends Component>> requiredAllOfComponents;
    //List of components from the .oneOf(...) node
    Set<Set<Class<? extends Component>>> requiredAnyOfComponents;
    //List of components from the .onlyOneOf(...) node
    Set<Set<Class<? extends Component>>> requiredOnlyOneOfComponents;

    private ComponentFilter(
            Set<Class<? extends Component>> excludedComponents,
            Set<Class<? extends Component>> requiredAllOfComponents,
            Set<Set<Class<? extends Component>>> requiredAnyOfComponents,
            Set<Set<Class<? extends Component>>> requiredOnlyOneOfComponents) {
        this.excludedComponents = excludedComponents;
        this.requiredAllOfComponents = requiredAllOfComponents;
        this.requiredAnyOfComponents = requiredAnyOfComponents;
        this.requiredOnlyOneOfComponents = requiredOnlyOneOfComponents;
    }

    /**
     * @return a new builder instance to start creating your filter with
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param unsortedEntities the list of unsorted entities that you want to filter with the provided ComponentFilter
     * @return a filtered list of entities that match the provided filter
     */
    public List<Entity> filter(Set<Entity> unsortedEntities) {

        List<Entity> sortedEntities;

        //Sort out entities that contain components that are not allowed
        sortedEntities = unsortedEntities.stream().filter(entity -> {
            for (Class<? extends Component> component : getExcludedComponents()) {
                if (entity.containsComponent(component)) return false;
            }
            return true;
        }).toList();

        //Sort out entities that don't contain all components that are required
        sortedEntities = sortedEntities.stream().filter(entity -> {
            for (Class<? extends Component> component : getRequiredAllOfComponents()) {
                if (!entity.containsComponent(component)) return false;
            }
            return true;
        }).toList();

        //Sort out entities that don't contain any of the one of list
        for (Set<Class<? extends Component>> componentAnyComparisons : getRequiredAnyOfComponents()) {
            sortedEntities = sortedEntities.stream().filter(entity -> {
                for (Class<? extends Component> component : componentAnyComparisons) {
                    if (entity.containsComponent(component)) return true;
                }
                return false;
            }).toList();
        }

        //Sort out entities that have more than one matching from only one of components
        for (Set<Class<? extends Component>> componentOneOfComparisons : getRequiredOnlyOneOfComponents()) {
            sortedEntities = sortedEntities.stream().filter(entity -> {
                int foundMatches = 0;
                for (Class<? extends Component> component : componentOneOfComparisons) {
                    if (entity.containsComponent(component)) {
                        foundMatches++;
                        if (foundMatches > 1) return false;
                    }
                }
                //Because exactly one match must be found so this must be true
                return true;
            }).toList();
        }

        return sortedEntities;
    }

    public Set<Class<? extends Component>> getExcludedComponents() {
        return excludedComponents;
    }

    public Set<Class<? extends Component>> getRequiredAllOfComponents() {
        return requiredAllOfComponents;
    }

    public Set<Set<Class<? extends Component>>> getRequiredAnyOfComponents() {
        return requiredAnyOfComponents;
    }

    public Set<Set<Class<? extends Component>>> getRequiredOnlyOneOfComponents() {
        return requiredOnlyOneOfComponents;
    }

    public static class Builder {

        //List of components from the .excludes(...) node
        Set<Class<? extends Component>> excludedComponents;
        //List of components from the .allOf(...) node
        Set<Class<? extends Component>> requiredAllOfComponents;
        //List of components from the .oneOf(...) node
        Set<Set<Class<? extends Component>>> requiredAnyOfComponents;
        //List of components from the .onlyOneOf(...) node
        Set<Set<Class<? extends Component>>> requiredOnlyOneOfComponents;

        private Builder() {
            excludedComponents = new HashSet<>();
            requiredAllOfComponents = new HashSet<>();
            requiredAnyOfComponents = new HashSet<>();
            requiredOnlyOneOfComponents = new HashSet<>();
        }

        /**
         * Adds the specified components to the only one of required components list, when used to filter any entity
         * which does not contain exactly one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that you want exactly one of to match
         * @return the current builder
         */
        @SafeVarargs
        public final Builder onlyOneOf(Class<? extends Component>... requiredComponents) {
            return this.onlyOneOf(Set.of(requiredComponents));
        }

        /**
         * Adds the specified components to the only one of required components list, when used to filter any entity
         * which does not contain exactly one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that you want exactly one of to match
         * @return the current builder
         */
        public final Builder onlyOneOf(Set<Class<? extends Component>> requiredComponents) {
            this.requiredOnlyOneOfComponents.add(requiredComponents);
            return this;
        }

        /**
         * Adds the specified components to the any required components list, when used to filter any of the entities
         * which do not contain at least one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components you want at least one of to match
         * @return the current builder
         */
        @SafeVarargs
        public final Builder anyOf(Class<? extends Component>... requiredComponents) {
            return this.anyOf(Set.of(requiredComponents));
        }

        /**
         * Adds the specified components to the any required components list, when used to filter any of the entities
         * which do not contain at least one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components you want at least one of to match
         * @return the current builder
         */
        private final Builder anyOf(Set<Class<? extends Component>> requiredComponents) {
            this.requiredAnyOfComponents.add(requiredComponents);
            return this;
        }

        /**
         * Adds the specified components to the required components list, when used to filter any entities which do not
         * contain ALL the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that must all exist to match
         * @return the current builder
         */
        @SafeVarargs
        public final Builder allOf(Class<? extends Component>... requiredComponents) {
            this.requiredAllOfComponents.addAll(Set.of(requiredComponents));
            return this;
        }

        /**
         * Adds the specified components to the excluded components list, when used to filter any entities which contain
         * the specified components will not be added to the filtered list
         *
         * @param excludedComponents the components you want none of to match
         * @return the current builder
         */
        @SafeVarargs
        public final Builder excludes(Class<? extends Component>... excludedComponents) {
            this.excludedComponents.addAll(Set.of(excludedComponents));
            return this;
        }

        /**
         * Builds this builder into a new instance of a ComponentFilter
         *
         * @return a new ComponentFilter instance based on this builder's match requirements
         */
        public final ComponentFilter build() {
            return new ComponentFilter(
                    excludedComponents,
                    requiredAllOfComponents,
                    requiredAnyOfComponents,
                    requiredOnlyOneOfComponents);
        }

    }
}
