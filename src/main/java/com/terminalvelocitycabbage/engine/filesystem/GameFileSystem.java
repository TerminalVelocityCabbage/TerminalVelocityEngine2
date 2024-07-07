package com.terminalvelocitycabbage.engine.filesystem;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceLocation;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceType;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.registry.RegistryPair;

import java.util.*;

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
    final Map<String, Map<String, Resource>> fileSystemContents; //TODO replace use of String for Identifier with Identifier so we can remove below
    final Map<ResourceType, List<Identifier>> resouceIdTypeMap; //TODO remove

    public GameFileSystem() {
        this.sourceRegistry = new Registry<>();
        this.resourceLocationRegistry = new Registry<>();
        this.fileSystemContents = new HashMap<>();
        this.resouceIdTypeMap = new HashMap<>();
    }

    /**
     * Registers the provided source to this sourceRegistry with an identifier of "namespace:resource_source"
     *
     * @param sourceIdentifier the namespace of this source
     * @param source the actual source to register
     */
    public RegistryPair<ResourceSource> registerResourceSource(Identifier sourceIdentifier, ResourceSource source) {
        return getSourceRegistry().register(sourceIdentifier, source);
    }

    /**
     * Registers the provided resource to the registry of it's type
     * @param sourceIdentifier The identifier of the source this resource is retrieved from
     * @param resourceType The type of resource you are registering
     * @param fileName The file name that this resource can be found as
     */
    public RegistryPair<ResourceLocation> registerResource(Identifier sourceIdentifier, ResourceType resourceType, String fileName) {
        Identifier resourceIdentifier = new Identifier(sourceIdentifier.getNamespace(), fileName);
        var ret = resourceLocationRegistry.register(resourceIdentifier, new ResourceLocation(sourceIdentifier, resourceType, resourceIdentifier));
        if (!resouceIdTypeMap.containsKey(resourceType)) resouceIdTypeMap.put(resourceType, new ArrayList<>());
        resouceIdTypeMap.get(resourceType).add(resourceIdentifier);
        return ret;
    }

    /**
     * @return the location to register resource sources
     */
    public Registry<ResourceSource> getSourceRegistry() {
        return sourceRegistry;
    }

    /**
     * Initializes this file system with locations for the registered resources
     */
    public void init() {

        //Init filesystem types
        for (ResourceType type: ResourceType.values()) {
            fileSystemContents.put(type.getName(), new HashMap<>());
        }

        //Compile filesystem
        for (Map.Entry<Identifier, ResourceLocation> entry : resourceLocationRegistry.getRegistryContents().entrySet()) {
            //Where the resource exists
            ResourceLocation resourceLocation = entry.getValue();
            //Get the resource from its resource location and make it available on this file system
            Resource resource = sourceRegistry.get(resourceLocation.resourceSourceIdentifier())
                    .getResource(resourceLocation.resourceIdentifier().getName(), resourceLocation.type());
            //Get the resource type and put this resource into it to be used later
            fileSystemContents.get(resourceLocation.type().getName()).put(resourceLocation.resourceIdentifier().toString(), resource);
        }
    }

    /**
     * Lists the resources loaded to this filesystem
     */
    public void listResources() {
        Log.info("Listing virtual filesystem resource identifiers:");
        fileSystemContents.forEach((resourceType, identifierResourceMap) -> {
            Log.info("  " + resourceType + "s: (" + identifierResourceMap.size() + "):");
            identifierResourceMap.forEach((identifier, resource) -> {
                Log.info("    - " + identifier);
            });
        });
    }

    /**
     * @param resourceType The type of resource you are retrieving see: {@link ResourceType}
     * @param identifier An identifier which identifies the resource you are trying to retrieve
     *                   Ex. testmod:trex
     * @return A Resource from this file system which matches the request
     */
    public Resource getResource(ResourceType resourceType, Identifier identifier) {
        return fileSystemContents.get(resourceType.getName()).get(identifier.toString());
    }

    /**
     * @param resourceType The type of resource you are retrieving
     * @return A collection of resources with that type
     */
    public Collection<Resource> getResourcesOfType(ResourceType resourceType) {
        if (!fileSystemContents.containsKey(resourceType)) Log.error("No resources of type " + resourceType.getName() + "exist on file system");
        return fileSystemContents.get(resourceType).values();
    }

    /**
     * @param resourceType The type of resource you are retrieving
     * @return A collection of resource identifiers with that type
     */
    public List<Identifier> getResourceIdentifiersOfType(ResourceType resourceType) {
        return resouceIdTypeMap.get(resourceType);
    }
}
