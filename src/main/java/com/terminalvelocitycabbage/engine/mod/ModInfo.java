package com.terminalvelocitycabbage.engine.mod;

import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.util.touples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModInfo {

    private ModIdentity identity;
    private ModAuthors authors;
    private ModDependencies dependencies;

    public String getNamespace() {
        return identity.namespace;
    }

    public String getName() {
        return identity.name;
    }

    public String getDescription() {
        return identity.description;
    }

    public Version getVersion() {
        //TODO replace with direct conversion to version from toml file
        return Version.parse(identity.version);
    }

    public List<String> getCreators() {
        return authors.creators;
    }

    public List<String> getContributors() {
        return authors.contributors;
    }

    public List<Pair<String, Version>> getAllDependencies() {
        //TODO replace with call directly to parsed fields when issue is resolved
        List<Pair<String, Version>> deps = new ArrayList<>();
        deps.addAll(getRequiredDependencies());
        deps.addAll(getOptionalDependencies());
        return deps;
    }

    public List<Pair<String, Version>> getRequiredDependencies() {
        //TODO replace with call directly to parsed fields when issue is resolved
        List<Pair<String, Version>> requiredDeps = new ArrayList<>();
        dependencies.required.forEach(s -> {
            String[] split = s.split(":");
            requiredDeps.add(new Pair<>(split[0], Version.parse(split[1])));
        });
        return requiredDeps;
    }

    public List<Pair<String, Version>> getOptionalDependencies() {
        //TODO replace with call directly to parsed fields when issue is resolved
        List<Pair<String, Version>> optionalDeps = new ArrayList<>();
        dependencies.optional.forEach(s -> {
            String[] split = s.split(":");
            optionalDeps.add(new Pair<>(split[0], Version.parse(split[1])));
        });
        return optionalDeps;
    }

    public static class ModIdentity {
        String namespace;
        String name;
        String description;
        String version; //TODO add @SpecValidator here to confirm in semver format
    }

    public static class ModAuthors {
        List<String> creators;
        List<String> contributors;
    }

    //TODO replace with converted section below pending response on https://github.com/TheElectronWill/night-config/issues/169
    public static class ModDependencies {
        List<String> required;
        List<String> optional;
    }
}

//TODO use below code when issue response is had to solve subsequent issue
//public class ModInfo {
//
//    private ModIdentity identity;
//    private ModAuthors authors;
//    private ModDependencies dependencies;
//
//    public String getNamespace() {
//        return identity.namespace;
//    }
//
//    public String getName() {
//        return identity.name;
//    }
//
//    public String getDescription() {
//        return identity.description;
//    }
//
//    public Version getVersion() {
//        //TODO replace with direct conversion to version from toml file
//        return Version.parse(identity.version);
//    }
//
//    public List<String> getCreators() {
//        return authors.creators;
//    }
//
//    public List<String> getContributors() {
//        return authors.contributors;
//    }
//
//    public List<VersionPair> getAllDependencies() {
//        List<VersionPair> deps = new ArrayList<>();
//        deps.addAll(getRequiredDependencies());
//        deps.addAll(getOptionalDependencies());
//        return deps;
//    }
//
//    public List<VersionPair> getRequiredDependencies() {
//        return dependencies.required;
//    }
//
//    public List<VersionPair> getOptionalDependencies() {
//        return dependencies.optional;
//    }
//
//    public static class ModIdentity {
//        String namespace;
//        String name;
//        String description;
//        String version; //TODO add @SpecValidator here to confirm in semver format
//    }
//
//    public static class ModAuthors {
//        List<String> creators;
//        List<String> contributors;
//    }
//
//    //This is a temporary workaround for a night-config feature. When not requiring a class to parse to replace this
//    public static class VersionPair {
//        //A Pair defined as <Namespace, Version>
//        @Conversion(VersionToStringConverter.class)
//        String namespace;
//        Version version;
//
//        public VersionPair(String namespace, Version version) {
//            this.namespace = namespace;
//            this.version = version;
//        }
//
//        public String getNamespace() {
//            return namespace;
//        }
//
//        public Version getVersion() {
//            return version;
//        }
//    }
//
//    public static class ModDependencies {
//        List<VersionPair> required;
//        List<VersionPair> optional;
//    }
//
//    static class VersionToStringConverter implements Converter<String, VersionPair> {
//
//        @Override
//        public String convertToField(VersionPair stringVersionPair) {
//            return stringVersionPair.getNamespace() + ":" + stringVersionPair.getVersion();
//        }
//
//        @Override
//        public VersionPair convertFromField(String s) {
//            String[] split = s.split(":");
//            return new VersionPair(split[0], Version.parse(split[1]));
//        }
//    }
//}
