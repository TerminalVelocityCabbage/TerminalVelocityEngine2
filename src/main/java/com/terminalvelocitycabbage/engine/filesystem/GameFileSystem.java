package com.terminalvelocitycabbage.engine.filesystem;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceLocation;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.HashMap;
import java.util.Map;

/**
 * A GameFileSystem is a way to merge all resources available to your project into a single virtual file system that
 * all entrypoints registered can have access to in the same unified way. The way it does this is pretty simple, but
 * not obvious without a bit of a run through.
 *
 * Firstly ClientBase and ServerBase already store a GameFileSystem so there is no need to create this yourself.
 *
 * To construct a game file system you need to define a few things for the compiler to use.
 * - ResourceSources which are basically the containers for resources, like mods, the game itself, or resourcepacks, but
 *   are not really per mod etc., it's more like per location assets exist. For example a mod may come with some default
 *   resources like configs, which would be a JarSource (which extends ResourceSource) and those configs will be likely
 *   extracted into the user's filesystem, so they can edit them, from there they would be a SystemSource, but belong to
 *   the same mod namespace.
 * - ResourceRoots which are stored on the ResourceSource that define that sub-roots for a type of resource in the
 *   source, like models, textures, etc.
 */
public class GameFileSystem {

    final Registry<ResourceSource> sourceRegistry;
    //TODO replace the map with a resource type registry so we can remove ResourceType to allow mods to create more etc.
    final Registry<ResourceLocation> resourceLocationRegistry;
    final Map<String, Map<String, Resource>> fileSystemContents;

    public GameFileSystem() {
        this.sourceRegistry = new Registry<>();
        this.resourceLocationRegistry = new Registry<>();
        this.fileSystemContents = new HashMap<>();
    }

    /**
     * Registers the provided source to this sourceRegistry with an identifier of "namespace:resource_source"
     *
     * @param sourceIdentifier the namespace of this source
     * @param source the actual source to register
     */
    public void registerResourceSource(Identifier sourceIdentifier, ResourceSource source) {
        getSourceRegistry().register(sourceIdentifier, source);
    }

    /**
     * Registers the provided resource to the registry of it's type
     * @param sourceIdentifier The identifier of the resource you are registering
     * @param resourceType The type of resource you are registering
     */
    public void registerResource(Identifier sourceIdentifier, ResourceType resourceType, String fileName) {
        resourceLocationRegistry.register(sourceIdentifier, new ResourceLocation(sourceIdentifier, resourceType, new Identifier(sourceIdentifier.getNamespace(), fileName)));
    }

    public Registry<ResourceSource> getSourceRegistry() {
        return sourceRegistry;
    }

    public void init() {

        //List Sources
        Log.info("Initializing filesystem with " + getSourceRegistry().getRegistryContents().size() + " sources:");
        getSourceRegistry().getRegistryContents().forEach((identifier, source) -> Log.info("  - " + identifier));

        //Init filesystem types
        Log.info("Initializing filesystem root types:");
        for (ResourceType type: ResourceType.values()) {
            Log.info("  - Initializing VFS root for type: " + type.getName());
            fileSystemContents.put(type.getName(), new HashMap<>());
        }

        //List sources' type roots
        for (Map.Entry<Identifier, ResourceSource> entry : getSourceRegistry().getRegistryContents().entrySet()) {
            Identifier identifier = entry.getKey();
            ResourceSource source = entry.getValue();
            Log.info("Registering source roots for identifier: " + identifier);
            for (String root : source.getRoots()) {
                //TODO init roots?
                Log.info("  - " + root);
            }
        }

        //Compile filesystem
        Log.info("Compiling " + resourceLocationRegistry.getRegistryContents().size() + " registered resources into the VFS:");
        for (Map.Entry<Identifier, ResourceLocation> entry : resourceLocationRegistry.getRegistryContents().entrySet()) {

            ResourceLocation resourceLocation = entry.getValue();
            //Get the resource from its resource location and make it available on this file system
            Log.info("  - Searching for " + resourceLocation.type().getName() + " resource: " + resourceLocation.resourceIdentifier());
            Resource resource = sourceRegistry.get(resourceLocation.resourceSourceIdentifier())
                    .getResource(resourceLocation.resourceIdentifier().getName(), resourceLocation.type());
            fileSystemContents.get(resourceLocation.type().getName()).put(resourceLocation.resourceIdentifier().toString(), resource);
        }
    }

    public void listResources() {
        Log.info("Listing virtual filesystem resource identifiers:");
        fileSystemContents.forEach((resourceType, identifierResourceMap) -> {
            Log.info("  " + resourceType + "s: (" + identifierResourceMap.size() + "):");
            identifierResourceMap.forEach((identifier, resource) -> {
                Log.info("    - " + identifier);
            });
        });
    }

    public Resource getResource(ResourceType resourceType, Identifier identifier) {

        String resourceName = resourceType.getName();
        Resource resource = fileSystemContents.get(resourceName).get(identifier.toString());

        Log.info("trying to get resource for: " + resourceName + " with " + identifier + " which is " + resource);

        return resource;
    }
}
