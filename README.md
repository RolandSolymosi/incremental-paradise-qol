# Incremental Paradise QoL client-side mod

## What this mod is
There are, for now, three main features:
* a tasks overlay
* a fully configurable command aliasing feature
* keybindings for fixed loadouts

### Task overlay
Will try to keep track of most types of tasks. All world sublocations are color-coded:
- [![#f03c15](https://placehold.co/15x15/555555/555555.png)] is the *WORLD*
- [![#f03c15](https://placehold.co/15x15/555555/555555.png) - ![#f03c15](https://placehold.co/15x15/00AA00/00AA00.png)] is *LUSH*
- [![#f03c15](https://placehold.co/15x15/555555/555555.png) - ![#f03c15](https://placehold.co/15x15/00AAAA/00AAAA.png)] is *VEIL*
- [![#f03c15](https://placehold.co/15x15/555555/555555.png) - ![#f03c15](https://placehold.co/15x15/AA0000/AA0000.png)] is *INFERNAL*
- [![#f03c15](https://placehold.co/15x15/555555/555555.png) - ![#f03c15](https://placehold.co/15x15/AA00AA/AA00AA.png)] is *ABYSS*

The task overlay will read off information from tasks' boss bars and update the according one within the overlay.

**To open the overlay, you need to open your tasks window**

### Command aliases
Command aliases allow you to set up a custom shortcut for a series of commands. On top of that, executing an alias supports warping
as well, meaning you could provide a warp location, and you will be taken there upon alias execution.

#### The general command synthax is:

`/a [alias_name or list] [option]`

`option` can be in the form of `+chain of commands`, `- or remove`, `warp location`

`warp location` can be any valid warp location you have unlocked

#### Example usage

`/a mine +/wardrobe mining /pets mining` - this sets an alias called `mine` and saves `/wardrobe mining` and `/pets mining`

`/a spirit +/wardrobe 2 /pets farming` - this sets an alias called `spirit` and saves `/wardrobe 2` and `/pets farming`

`/a list` - this outputs all saved aliases into the chat, visible only to the player

`/a mine` - this executes the alias `mine`, running all the commands saved to it, in order

`/a spirit shimmer` - this executes the alias `spirit`, running all the commands saved to it, and warps the player to shimmer

`/a spirit -` - this removes the alias `spirit`

### Fixed loadouts
There are 5 configurable keys, in the keybindings of the game, for 5 predefined layouts.
Each one equips a matching pair of wardrobe and pets set e.g. loadout 2 will equip wardrobe 2 and pets 2.