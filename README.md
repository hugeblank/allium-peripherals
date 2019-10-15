# Allium Peripherals

For CC:T for Fabric & Minecraft 1.14.4

Just another peripherals mod for CC that nobody asked for...

Allium offers two peripherals at present. A survival, and creative chat modem. These modems function identically to the
chat module from [SquidDev's Plethora](https://github.com/SquidDev-CC/Plethora) peripherals mod.
 
## Items
The current list of items in the game, along with a bit of documentation on their methods.

### Survival Chat Modem
To start, right click on one to bind yourself, right click to unbind. Nobody else can unbind you from a chat modem 
you've bound yourself to (apart from breaking and replacing it). Once bound you have access to all the methods:
- `capture`: add a pattern to queue an event every time you send a message that matches. Once a message gets captured,
it does not get sent to anyone else.
- `uncapture`: removes a single given pattern or all patterns if none are supplied
- `getCaptures`: returns a table of all captures
- `getBoundPlayer`: returns two strings, the username of the bound player, and the UUID of the bound player
- `say`: sends a message to the bound player.

### Creative Chat Modem

The creative chat modem is similar to a survival modem, with the exception that it applies captures globally. Since it
lacks the need for a player, the methods `getBoundPlayer` and `say` do not apply to the creative chat modem, all others 
from the survival modem are identical.

## Rationale & Credits 

So basically I was really bored, and tired of waiting for Plethora to update (insert obligatory SquidDev comment "PRs 
Welcome!"), so I went and made what I needed. I used Fabric because open source ethics, love for the community, and love
of framework. 

I would like to give a huge(blank) thanks for all the users on the fabric community discord for helping me make this mod
a reality, from shouting at me to refresh gradle, to dealing with my mental breakdown while trying to implement block
entities. I'd also like to thank [SquidDev](https://github.com/SquidDev) in particular for assisting me when I came 
across issues with the CC end of the mod.