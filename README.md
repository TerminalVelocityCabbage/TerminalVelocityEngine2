# Terminal Velocity Engine 2

A lightweight code first engine which aims to succeed the original Terminal Velocity Engine. 

The engine is intended for procedurally generated games, used by Terminal Velocity Cabbage. This engine would be an example of what an engine that would make a game like Minecraft or Terraria would use if they did not use their own tightly integrated engines. A lot of the features that we are including are taken as lessons learned by the mistakes the developers of those games made as we ran into them modding those games. I believe that if the developers of Minecraft knew what they know now, and wanted to rewrite the game from scratch with a decoupled engine they would land on a game engine that is designed in a very similar way to how this one is designed.

This engine is in heavy development and there is no release cycle implemented. The current reccomended workflow would be to add the engine as a submodule for the game that you are making.

## Engine Goals & TODO:
- [x] Free and open sourced to make our games better through the modding community
- [x] Data Oriented and Moddable Design
- [x] Entity Component System based Architecture
- [x] Robust configurable input system
- [x] Common scheduler for easy logic execution and off-thread utilities
- [x] Configurable Render Graph
- [x] Networking Utilties - This is in it's infancy, basically can only be used to send and recieve decodable packets
- [ ] Fast and customizable Renderer
- [ ] Easy to use User Interface ultitities
- [ ] High fidelity sound system with environmental imapacts
- [x] Utilities for adding ingame translations
- [ ] Easy to use Profiling Tools
- [ ] Utilities for Particles
- [ ] Blockbench Model/animations
- [ ] Collision detection utiltiies

## Engine NON-Goals:
- [ ] A fully featured visual editor
- [ ] A High Visual Fidelity UE5 level Graphics

## Potential Future Goals:
These are things that are not currently in scope, but onlt because we devs have no use for them in our game concepts, thus programming them into the engine would be a waste of our time. We are open to these being added as PRs and we will happily maintain them with the community's help, but will not be taking the initiative ourselves to implement them.
- [ ] Full skeletal animation system
- [ ] Realistic Physics implementation (PhysX or Bullet i.e.)
