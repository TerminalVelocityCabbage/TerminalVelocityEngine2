package com.terminalvelocitycabbage.engine.ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentFilter {

    //List of components from the .excludes(...) node
    List<Class<? extends Component>> excludedComponents;
    //List of components from the .allOf(...) node
    List<Class<? extends Component>> requiredAllOfComponents;
    //List of components from the .oneOf(...) node
    List<List<Class<? extends Component>>> requiredAnyOfComponents;
    //List of components from the .onlyOneOf(...) node
    List<List<Class<? extends Component>>> requiredOnlyOneOfComponents;

    private ComponentFilter(
            List<Class<? extends Component>> excludedComponents,
            List<Class<? extends Component>> requiredAllOfComponents,
            List<List<Class<? extends Component>>> requiredAnyOfComponents,
            List<List<Class<? extends Component>>> requiredOnlyOneOfComponents) {
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
    public List<Entity> filter(List<Entity> unsortedEntities) {

        List<Entity> sortedEntities;

        //Sort out entities that contain components that are not allowed
        sortedEntities = unsortedEntities.stream().filter(entity -> {
            boolean hasExcludedComponent = false;
            for (Class<? extends Component> component : getExcludedComponents()) {
                if (entity.containsComponent(component)) hasExcludedComponent = true;
            }
            return !hasExcludedComponent;
        }).toList();

        //Sort out entities that don't contain components that are required
        sortedEntities = sortedEntities.stream().filter(entity -> {
            boolean hasRequiredComponent = true;
            for (Class<? extends Component> component : getRequiredAllOfComponents()) {
                if (!entity.containsComponent(component)) hasRequiredComponent = false;
            }
            return hasRequiredComponent;
        }).toList();

        //Sort out entities that don't contain any of the one of list
        for (List<Class<? extends Component>> componentAnyComparisons : getRequiredAnyOfComponents()) {
            sortedEntities = sortedEntities.stream().filter(entity -> {
                for (Class<? extends Component> component : componentAnyComparisons) {
                    if (entity.containsComponent(component)) return true;
                }
                return false;
            }).toList();
        }

        //Sort out entities that have more than one matching from only one of components
        for (List<Class<? extends Component>> componentOneOfComparisons : getRequiredOnlyOneOfComponents()) {
            sortedEntities = sortedEntities.stream().filter(entity -> {
                boolean alreadyFoundOneMatch = false;
                for (Class<? extends Component> component : componentOneOfComparisons) {
                    if (entity.containsComponent(component)) {
                        if (alreadyFoundOneMatch) {
                            return false;
                        } else {
                            alreadyFoundOneMatch = true;
                        }
                    }
                }
                //Because exactly one match must be found so this must be true
                return alreadyFoundOneMatch;
            }).toList();
        }

        return sortedEntities;
    }

    public List<Class<? extends Component>> getExcludedComponents() {
        return excludedComponents;
    }

    public List<Class<? extends Component>> getRequiredAllOfComponents() {
        return requiredAllOfComponents;
    }

    public List<List<Class<? extends Component>>> getRequiredAnyOfComponents() {
        return requiredAnyOfComponents;
    }

    public List<List<Class<? extends Component>>> getRequiredOnlyOneOfComponents() {
        return requiredOnlyOneOfComponents;
    }

    public static class Builder {

        //List of components from the .excludes(...) node
        List<Class<? extends Component>> excludedComponents;
        //List of components from the .allOf(...) node
        List<Class<? extends Component>> requiredAllOfComponents;
        //List of components from the .oneOf(...) node
        List<List<Class<? extends Component>>> requiredAnyOfComponents;
        //List of components from the .onlyOneOf(...) node
        List<List<Class<? extends Component>>> requiredOnlyOneOfComponents;

        private Builder() {
            excludedComponents = new ArrayList<>();
            requiredAllOfComponents = new ArrayList<>();
            requiredAnyOfComponents = new ArrayList<>();
            requiredOnlyOneOfComponents = new ArrayList<>();
        }

        /**
         * Adds the specified components to the only one of required components list, when used to filter any entity
         * which does not contain exactly one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that you want exactly one of to match
         * @return the current builder
         */
        public Builder onlyOneOf(Class<? extends Component>... requiredComponents) {
            this.onlyOneOf(new ArrayList<>(Arrays.stream(requiredComponents).toList()));
            return this;
        }

        /**
         * Adds the specified components to the only one of required components list, when used to filter any entity
         * which does not contain exactly one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that you want exactly one of to match
         * @return the current builder
         */
        public Builder onlyOneOf(List<Class<? extends Component>> requiredComponents) {
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
        public Builder anyOf(Class<? extends Component>... requiredComponents) {
            this.anyOf(new ArrayList<>(Arrays.stream(requiredComponents).toList()));
            return this;
        }

        /**
         * Adds the specified components to the any required components list, when used to filter any of the entities
         * which do not contain at least one of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components you want at least one of to match
         * @return the current builder
         */
        public Builder anyOf(List<Class<? extends Component>> requiredComponents) {
            this.requiredAnyOfComponents.add(requiredComponents);
            return this;
        }

        /**
         * Adds the specified components to the required components list, when used to filter any entities which do not
         * contain ALL of the specified components will not be added to the filtered list
         *
         * @param requiredComponent the component that must exist to match
         * @return the current builder
         */
        public Builder oneOf(Class<? extends Component> requiredComponent) {
            this.requiredAllOfComponents.add(requiredComponent);
            return this;
        }

        /**
         * Adds the specified components to the required components list, when used to filter any entities which do not
         * contain ALL of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that must all exist to match
         * @return the current builder
         */
        public Builder allOf(Class<? extends Component>... requiredComponents) {
            this.allOf(Arrays.stream(requiredComponents).toList());
            return this;
        }

        /**
         * Adds the specified components to the required components list, when used to filter any entities which do not
         * contain ALL of the specified components will not be added to the filtered list
         *
         * @param requiredComponents the components that must all exist to match
         * @return the current builder
         */
        public Builder allOf(List<Class<? extends Component>> requiredComponents) {
            this.requiredAllOfComponents.addAll(requiredComponents);
            return this;
        }

        /**
         * Adds the specified components to the excluded components list, when used to filter any entities which contain
         * the specified components will not be added to the filtered list
         *
         * @param excludedComponents the components you want none of to match
         * @return the current builder
         */
        public Builder excludes(Class<? extends Component>... excludedComponents) {
            this.excludes(Arrays.stream(excludedComponents).toList());
            return this;
        }

        /**
         * Adds the specified components to the excluded components list, when used to filter any entities which contain
         * the specified components will not be added to the filtered list
         *
         * @param excludedComponents the components you want none of to match
         * @return the current builder
         */
        public Builder excludes(List<Class<? extends Component>> excludedComponents) {
            this.excludedComponents.addAll(excludedComponents);
            return this;
        }

        /**
         * Builds this builder into a new instance of a ComponentFilter
         *
         * @return a new ComponentFilter instance based on this builder's match requirements
         */
        public ComponentFilter build() {
            return new ComponentFilter(
                    excludedComponents,
                    requiredAllOfComponents,
                    requiredAnyOfComponents,
                    requiredOnlyOneOfComponents);
        }

    }
}
