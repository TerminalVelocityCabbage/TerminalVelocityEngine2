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
- name: the name of the anchor, used to retrieve its position and rotation later.
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
- name: the name of the model.
- texture_layers: defines the layers of a texture that a cube can reference in its own properties and that layers size.
- default_variant: defines the default variant of this model.
```toml
[metadata]
model_version = "1.0.0" #Must be a semantic version string.
name = "pig" #The name of the model.
texture_layers = [{layer_1 = [16, 16]}, {layer_2 = [32, 32]}] #must be an array of objects where the key is the name of a texture layer and the value is a size 2 array of integers x,y.
default_variant = "default" #Must match the name of a variant exactly.
```

## The Animation Format
Animations are defined in a TOML file. These files should be organized in subfolders within the `animations` directory, where the subfolder name matches the model name they are intended for (e.g., `animations/model_name/animation_name.animation.toml`).
Properties of the animation are:
- metadata: metadata about the animation.
- layers: a table of arrays for layers and their max influence.
- keyframes: a table of arrays for keyframes and their properties.
- events: generic events that an artist can use to trigger actions in game.

### Metadata
Metadata about the animation defines some generic information about the animation.
- version: a semantic version string.
- name: The name of the animation.
- duration: The duration of the animation in seconds.
- looping: whether or not this animation should loop.

An example of an animation metadata definition:
```toml
[metadata]
version = "1.0.0" #The version of the animation format.
name = "pig_walking" #The name of the animation.
duration = 15.0 #The duration of the animation in seconds.
looping = true #Whether or not this animation should loop.
```

### Layers
Layers are a way to group keyframes of a certain action together.
Layers are defined by creating a layers table, and then you just define layers and their influence as a key value pair:
```toml
[layers]
layer_1 = 1.0 #The influence of layer_1 (as a decimal percentage)
layer_2 = 0.5 #define multiple layers here
```

### Keyframes
Keyframes are the actual transformations that are applied to a bone over time. Keyframes are grouped by layer
then by bone then by transformation type. To define a keyframe for a layer, bone, and transformation type, just create a
table with these properties separated as subkeys:
```toml
[layer_name.bone_name.position] #Or rotation, or offset, or grow.
```
Then define its frames where the key is end of the keyframe (must be less than or equal to the duration
value of the animation) and the value is the transformation defined by an inline table with the
following properties:
- to: The final value of the transformation.
- interpolation: The interpolation type to use for this keyframe as a two length array of strings. The first string
is when to apply the easing function (in, out, or both) and the second is the easing function to use 
(none, linear, step, sin, quadratic, cubic, quartic, quintic, exponential, circular, back, elastic, bounce, catmulrom)
```toml
[layer_1.bone_1.position]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
```

An example of a keyframe definition with multiple bones and transformations:
```toml
[layer_1.bone_1.position]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
[layer_1.bone_1.rotation]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
[layer_1.bone_1.offset]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
[layer_1.bone_1.grow]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}

[layer_1.bone_2.position]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
[layer_1.bone_2.rotation]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
[layer_1.bone_2.offset]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
[layer_1.bone_2.grow]
"0.0" = {to = [0, 0, 0], interpolation = ["both", "linear"]}
"15.0" = {to = [1, 0, 0], interpolation = ["both", "linear"]}
```

### Events
Events are actions that an artist can use to trigger actions in game timed perfectly with animations.
Events are defined by the following properties:
- name: The name of the event
- layer: The layer of the animation this event applies to. (optional)
- type: The type of event that this should execute (sound, generic)
- at: when this event should be triggered (in seconds). (you must have this OR from defined)
- from: The start and end in seconds that this animation event will evaluate as "active" (you must have this OR at defined)
- anchor: The anchor that this event should be triggered at. (optional)

An example of an event definition:
```toml
[[event]]
name = "roar"
layer = "layer_1"
type = "sound"
at = 10.0 #plays this sound 10 seconds into the animation.
anchor = "back_of_throat"

[[event]]
name = "spawn_fire"
type = "generic"
from = [0.0, 10.0] #between 0 and 10 seconds into this animation spawn_fire will be "active"
anchor = "back_of_throat"
```

## The Animation Controller Format
Animation controllers define how and when animations are played. These take in some context for the entity being animated 
and process it into variables that animations may or may not use to determine how to play their animations.
Animation controllers should be able to toggle animation layers and their influence.

Animation controllers are defined by the following properties:
- variables: The variables that are used to evaluate context and determine how animations are played.
- animations: The actual evaluation functions that determine when and how animations are played.

Some built-in variables are:
- time: The time in seconds since this animation started playing. (shorthand for anim_time("this_animation_identifier"))
- delta: The time in seconds since the last frame.
- anim_time(animation_identifier): The time in seconds since the specified animation started playing.

An example of an animation controller definition:
```toml
[variables]
speed = "float" #name = type
on_ground = "bool"
above_water = "bool"
height = "float"
position = "vector3" #Exposes position.x, position.y, position.z, position.length
```

### Built-in Functions
- `if(condition, true_value, false_value)`: Returns `true_value` if `condition` is non-zero, otherwise returns `false_value`.
- `clamp(value, min, max)`: Clamps `value` between `min` and `max`.
- `dot(x1, y1, z1, x2, y2, z2)`: Returns the dot product of two 3D vectors defined by their components.

Animations are defined by the following properties:
- animation: The identifier of the animation. (e.g. `namespace:model_name/animation_name`)
- influence: An expression that determines the weight of this animation based on some context usually expected to evaluate
to a float between 0 and 1.
- trigger: Allows the user to define the trigger for this animation (when no influence expression is defined), and what happens
after the animation is finished. Acceptable post-actions are: reset and hold. A trigger and an influence expression cannot be defined at the same time.
- layers: Allow you to control the influence of specific layers of an animation here similarly to the influence property.
- priority: An optional integer that determines the priority of this animation relative to other animations. Default: 1
- blend: An optional expression that determines how this animation should blend with other animations. Particularly useful 
for animations with conflicting priorities. (override, additive) Default: additive
- ease: How to ease the animation in and out. (linear, step, sin, quadratic, cubic, quartic, quintic, exponential, 
circular, back, elastic, bounce, catmulrom) Default: linear
- fade_in: An optional float that determines how long in seconds this animation should take to fade in.
- fade_out: An optional float that determines how long in seconds this animation should take to fade out. A fade out is 
triggered when the animation is finished playing or when it is interrupted.
- speed: An optional expression that determines the speed of this animation. Default: 1.0
- progress: An optional expression that determines the progress of this animation as a float between 0 and 1. If this is 
defined, the animation's time is set to `progress * duration`.

```toml
[[animations]]
animation = "game:tyrannosaurus_adult_v2/idle" #must match an actual identifier of an animation
influence = "1.0 - clamp(speed / 5.0, 0.0, if(on_ground, 1.0, 0.0))" #expression that determines the weight of this animation based on some context.
speed = "speed / 5.0" #modulate the speed of the animation based on the speed of the entity.

[[animations]]
animation = "game:tyrannosaurus_adult_v2/look_left"
progress = "clamp(angular_velocity / 10.0, 0.0, 1.0)" #directly set the progress of this animation based on angular velocity.

[[animations]]
animation = "game:tyrannosaurus_adult_v2/fall"
influence = "if(!on_ground && !above_water, 1.0, 0.0)"

[[animations]]
animation = "game:tyrannosaurus_adult_v2/dive"
influence = "if(!on_ground && !above_water, 1.0, 0.0)"
ease = "sin" #This animation will have its influence scaled by a sin curve during the fade in and out periods.
fade_in = 0.5 #This animation will take 0.5 seconds to fade in.
fade_out = 0.5 #This animation will take 0.5 seconds to fade out.
[animation.layers]
arm_swinging = "if(height > 10, 1.0, 0.0)" #you can control influence of specific layers of an animation here.

[[animations]]
animation = "game:tyrannosaurus_adult_v2/attack"
trigger = ["initiate_attack", "reset"] #triggers this animation to play when the specified trigger is called.
priority = 2 #set this value to control the priority of this animation relative to other animations.
blend = "override" #means that this animation will prevent any animations with a lower priority from playing.
```