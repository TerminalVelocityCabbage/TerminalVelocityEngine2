# Terminal Velocity Engine 2 Model and Animation Technical Writeup

The purpose of this document is to provide a technical overview of how models and animations are defined and implemented in games made with 
Terminal Velocity Engine 2.

## Files and Their Purposes
All files are stored in the `assets` folder of the game, and are stored in toml files with a standard file extension and store some specific data related to that type of element:
- Models: `.model.toml` Stores geometry data and variant information to generate models ingame from.
- Animations: `.animation.toml` Stores keyframes and transformations for a given model. It is not specific to a model but rather the defined bones within.
- Animation Controllers: `.animation_controller.toml` Configures how and at what speed/intensity an animation should be played for a given context.

## The Model Format

TVE Models are defined in TOML files, known for the easy-to-read nature, no erroneous symbols, and the ability to be easily edited.
Most of the time users will not need to edit these models directly because tools will be eventually provided to make it easier.
However for now I want to make it so simple that a user could, if they felt like it, define them manually.

The model format is divided into the following sections usually in this order:
1. Metadata: The generic information about the model
2. Variants: Variants of this model
3. Bones: The hierarchy of bones in this model
4. Cubes: The cubes that make up the meshes of this model

We will go over each of these sections in reverse order, since they generally build on top of each other.
### Cubes
Cubes are the basic building blocks of a model. TVE is built around an art style similar to that of Minecraft, where every model is just
a collection of cubes. Cubes are defined by the following properties:
- name: The name of the cube.
- parent: The name of the parent cube or bone.
- size: The size of a cube in pixels, texture mapping will be based on these dimensions.
- grow: The extra dimension given to a cube per axis. Can be a decimal, does not affect the texture mapping.
- position: The position of the cube's local origin relative to its parent's origin.
- offset: The position of the cube relative to its local origin.
- rotation: The rotation of the cube relative to its local origin.
- textures: The textures used for this cube's faces UVs and texture layer mapping. Any faces excluded from being given a
uv will be excluded from the mesh entirely.

An example of a cube definition:

```toml
[[cubes]]
name = "cube_1" #Must be an alphanumeric string. must not be null
parent = "root" #must match the parent or bone name exactly, use "root" for cubes which should be relative to the model origin. Default: "root"
size = [16, 16, 16] #a size 3 array of integers x,y,z Default: [0,0,0]
grow = [0.1, 0, 0] #a size 3 array of floats x,y,z Default: [0,0,0]
position = [0, 1.25, 8] #a size 3 array of floats x,y,z Default: [0,0,0]
offset = [0, 5.5, 0] #a size 3 array of floats x,y,z Default: [0,0,0]
rotation = [15.5, 10, 115] #a size 3 array of floats x,y,z. Default: [0,0,0]
[cubes.textures]
layer = "texture_1" #Must match the name of a texture layer exactly.
py_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the positive y face of the cube.
ny_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the negative y face of the cube.
pz_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the positive z face of the cube.
nz_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the negative z face of the cube.
px_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the positive x face of the cube.
nx_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the negative x face of the cube.
```
### Bones
Bones defined the animatable portions of a model. Defined in hierarchical order. Bones are defined by the following properties:
- name: The name of the bone.
- parent: The parent bone of this bone.
- offset: The origin of this bone relative to its parent.
- rotation: The rotation of this bone relative to its origin.
```toml
[[bones]]
name = "bone_1" #Must be an alphanumeric string
parent = "root" #Must match the name of a bone exactly.
offset = [0,0,1.5] #a size 3 array of floats x,y,z. Default: [0,0,0]
rotation = [0,0,30.0] #a size 3 array of floats x,y,z. Default: [0,0,0]
```
### Variants
Some models have multiple variants. You can enable and disable geometry per variant by including or excluding bones from a variant,
you can also control what texture assets are mapped to each texture layer by variants to control how the model looks. You can
also import an entire model definition from another model file and attach it to a bone by a variant. At least one variant
is required for a model to be valid. That variant must be defined in the metadata section of the model file as it's default.

Variants are defined by the following properties:
- name: The name of this variant, this will be mapped to an identifier in game as the assignable name of a model.
- parent: The name of the parent variant. Defaults to the default variant.
- bones: An array of bones to be included in this variant. Must be a non-empty array.
- exclude_bones: An optional array of bones to be excluded from this variant. Bones not included by any parent excluded here will be ignored.
- textures: A map of texture layers to texture asset identifiers. Every layer must be defined.

An example of a variant definition:
```toml
[[variants]]
name = "default" #Must be an alphanumeric string
bones = ["bone_1", "bone_2"] #Must be an array of bone names
textures = {layer_1 = "texture_1", layer_2 = "texture_2"}
```
Variants inherit from their parent, or from the default if no parent is specified, so for child variants you can define 
the differences, duplicate definitions will be ignored.
```toml
[[variants]]
name = "variant_2" #Must be an alphanumeric string
parent = "variant_1" #Must match the name of a variant exactly.
bones = ["bone_1", "bone_3"] #Bone 3 is an additional bone included by this variant. Bone 1 is ignored because it is already included by the default variant.
exclude_bones = ["bone_2"] #Bone 2 is excluded from this variant.
textures = {layer_2 = "texture_5"} #layer_1 is inherited from the parent variant.
```
### Metadata
Metadata about the model defines some generic information about the model. Properties of the metadata are:
- texture_layers: defines the layers of a texture that a cube can reference in it's own properties. 
```toml
[metadata]
model_version = "1.0.0" #Must be a semantic version string.
texture_layers = ["layer_1", "layer_2"] #Must be an array of strings.
default_variant = "default" #Must match the name of a variant exactly.
```