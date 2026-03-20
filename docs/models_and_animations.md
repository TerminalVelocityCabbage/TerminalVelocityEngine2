# Terminal Velocity Engine 2 Model and Animation Technical Writeup

The purpose of this document is to provide a technical overview of how models and animations are defined and implemented in games made with 
Terminal Velocity Engine 2.

## Files and Their Purposes
All files are stored in the `assets` folder of the game, and are stored in toml files with a standard file extension and store some specific data related to that type of element:
- `xxx.model.toml` Stores geometry and variant information to generate models ingame from.
- `xxx.animation.toml` Stores transformations for a given bone over time.
- `xxx.animation_controller.toml` Configures how and when animations are played.

## The Model Format

TVE Models are defined in TOML files, known for the easy-to-read nature, no erroneous symbols, and the ability to be easily edited.
Most of the time users will not need to edit these models directly because tools will be eventually provided to make it easier.
However, for now I want to make it so simple that a user could, if they felt like it, define them manually.

The model format is divided into the following sections usually in this order:
1. Metadata: The generic information about the model
2. Variants: Variants of this model
3. Bones: The hierarchy of bones in this model
4. Cubes: The cubes that make up the meshes of this model
5. Anchors: Anchor points in the model

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
[[cube]]
name = "cube_1" #Must be an alphanumeric string. must not be null
parent = "root" #must match the parent or bone name exactly, use "root" for cubes which should be relative to the model origin. Default: "root"
size = [16, 16, 16] #a size 3 array of integers x,y,z Default: [0,0,0]
grow = [0.1, 0, 0] #a size 3 array of floats x,y,z Default: [0,0,0]
position = [0, 1.25, 8] #a size 3 array of floats x,y,z Default: [0,0,0]
offset = [0, 5.5, 0] #a size 3 array of floats x,y,z Default: [0,0,0]
rotation = [15.5, 10, 115] #a size 3 array of floats x,y,z. Default: [0,0,0]
[cube.textures]
layer = "texture_1" #Must match the name of a texture layer exactly.
py_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the positive y face of the cube.
ny_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the negative y face of the cube.
pz_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the positive z face of the cube.
nz_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the negative z face of the cube.
px_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the positive x face of the cube.
nx_uv = [0,0,16,16] #a size 4 array of integers x1,y1,x2,y2 in pixels mapping to the negative x face of the cube.
```
### Anchors
Anchors are points in the model that are parented to cubes or bones that have their positions and rotations reported to
the game as a way to anchor other things to the model. These things can be like sawing a particle effect at a specific location,
playing a sound where it originates, or even to control the position of another entity in the game. Anchors are defined by the
following properties similar to a cube:
- name: the name of the anchor, used to retrieve it's position and rotation later.
- parent: the name of the cube or bone this anchor is parented to.
- position: the position of the anchor relative to its parent's origin.
- offset: the position of the anchor relative to its local origin.
- rotation: the rotation of the anchor relative to its origin and parent.

An example of an anchor definition:
```toml
[[anchor]]
name = "anchor_1" #Must be an alphanumeric string
parent = "cube_3" #Must match the name of a cube or bone exactly.
position = [0,0,0] #a size 3 array of floats x,y,z.
offset = [0,0,0] #a size 3 array of floats x,y,z.
rotation = [0,0,0] #a size 3 array of floats x,y,z.
```
### Bones
Bones defined the animatable portions of a model. Defined in hierarchical order. Bones are defined by the following properties:
- name: The name of the bone.
- parent: The parent bone of this bone.
- offset: The origin of this bone relative to its internal origin.
- position: The position of this bone's origin relative to its parent's.
- rotation: The rotation of this bone relative to its origin.
```toml
[[bone]]
name = "bone_1" #Must be an alphanumeric string
parent = "root" #Must match the name of a bone exactly.
offset = [0,0,1.5] #a size 3 array of floats x,y,z. Default: [0,0,0]
position = [0,0,1.5] #a size 3 array of floats x,y,z. Default: [0,0,0]
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
[[variant]]
name = "default" #Must be an alphanumeric string
bones = ["bone_1", "bone_2"] #Must be an array of bone names
textures = {layer_1 = "texture_1", layer_2 = "texture_2"}
```
Variants inherit from their parent, or from the default if no parent is specified, so for child variants you can define 
the differences, duplicate definitions will be ignored.
```toml
[[variant]]
name = "variant_2" #Must be an alphanumeric string
parent = "variant_1" #Must match the name of a variant exactly.
bones = ["bone_1", "bone_3"] #Bone 3 is an additional bone included by this variant. Bone 1 is ignored because it is already included by the default variant.
exclude_bones = ["bone_2"] #Bone 2 is excluded from this variant.
textures = {layer_2 = "texture_5"} #layer_1 is inherited from the parent variant.
```
### Metadata
Metadata about the model defines some generic information about the model. Properties of the metadata are:
- model_version: tells the loader which version of the model format this model is using.
- texture_layers: defines the layers of a texture that a cube can reference in its own properties.
- default_variant: defines the default variant of this model.
```toml
[metadata]
model_version = "1.0.0" #Must be a semantic version string.
texture_layers = ["layer_1", "layer_2"] #Must be an array of strings.
default_variant = "default" #Must match the name of a variant exactly.
```

## The Animation Format
Animations are defined in a TOML file. Properties of the animation are:
- metadata: metadata about the animation.
- layers: a table of arrays for layers and their max influence.
- keyframes: a table of arrays for keyframes and their properties.
- events: generic events that an artist can use to trigger actions in game.

### Metadata
Metadata about the animation defines some generic information about the animation.
- version: a semantic version string.
- duration: The duration of the animation in ticks.
- tickrate: the default tickrate of this animation per second.
- looping: whether or not this animation should loop.

An example of an animation metadata definition:
```toml
[metadata]
version = "1.0.0" #The version of the animation format.
duration = 15 #The duration of the animation in ticks.
tickrate = 20 #The default tickrate of this animation per second.
looping = true #Whether or not this animation should loop.
```
### Layers
Layers are a way to group keyframes of a certain action together.
Layers are defined by the following properties:
- name: The name of the layer. This will be used in animation controllers to toggle layers and adjust their influence.
- influence: The influence of this layer on the overall animation. This is effectivley the max influence of the layer.
All transformations will be multiplied by this value.

An example of a layer definition:
```toml
[[layer]]
name = "layer_1" #The name of the layer.
influence = 1.0 #The default influence of this layer.
```
### Keyframes
Keyframes are the actual transformations that are applied to a bone over time (most of the time).
Keyframes are defined by the following properties:
- layer: The layer of the animation this keyframe applies to.
- timeframe: The time in ticks at which this keyframe starts and ends.
- bones: The bones that this keyframe influences.

Bones transformations are defined by the following properties:
- interpolation: The interpolation method used to interpolate between keyframes. ease in and out or both.
- position: The change in position of the bone in pixels.
- offset: The change in offset of the bone in pixels.
- rotation: The change in rotation of the bone in degrees.
- grow: The change in grow of the bone in pixels.
```toml
[[keyframe]]
layer = "layer_1" #The layer of the animation this keyframe applies to.
timeframe = [0, 15] #The time in ticks at which this keyframe starts and ends.
[keyframe."bone_1"]
interpolation = ["linear", "linear"] #none, linear, step, sin, quadratic, cubic, quartic, quintic, exponential, circular, back, elastic, bounce, catmulrom
position = [0,0,0] #The position of bone_1 in pixels.
offset = [0,0,0] #The offset of bone_1 in pixels.
rotation = [0,0,0] #The rotation of bone_1 in degrees.
grow = [0,0,0] #The grow of bone_1 in pixels.
```

### Events
Events are generic events that an artist can use to trigger actions in game.
Events are defined by the following properties:
- name: The name of the event
- layer: The layer of the animation this event applies to. (optional)
- type: The type of event that this should execute (sound, generic)
- timeframe: The start and optionally end time of the event in ticks.
- anchor: The anchor that this event should be triggered at. (optional)

An example of an event definition:
```toml
[[event]]
name = "roar"
layer = "layer_1"
type = "sound"
timeframe = [10] #plays this stound 10 ticks into the aniumation.
anchor = "back_of_throat"

[[event]]
name = "spawn_fire"
type = "generic"
timeframe = [0,10] #plays this stound 10 ticks into the aniumation.
anchor = "back_of_throat"
```

## The Animation Controller Format
Animation controllers define how and when animations are played. These take in some context for the entity being animated 
and process it into variables that animations may or may not use to determine how to play their animations.
Animation controllers should be able to toggle animation layers and their influence.

Animation controllers are defined by the following properties:
- variables: The variables that are used to evaluate context and determine how animations are played.
- animations: The actual evaluation functions that determine when and how animations are played.

An example of an animation controller definition:
```toml
[variables]
speed = "float" #name = type
on_ground = "bool"
above_water = "bool"
height = "float"
```
```toml
[[animations]]
name = "idle" #must match an actual name of an animation
when = "on_ground" #optional boolean expression that determines when this animation should be evaluated. must match a variable name exactly.
influence = "1.0 - clamp(speed / 5.0, 0.0, 1.0)" #expression that determines the weight of this animation based on some context.

[[animations]]
name = "fall"
when = "!on_ground && !above_water" #expression can be negated with !
influence = "1.0"

[[animations]]
name = "dive"
when = "!on_ground && above_water"
influence = "1.0"
[animation.layers]
arm_swinging = "height > 10" #you can control influence of specific layers of an animation here.
```