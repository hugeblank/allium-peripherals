# Allium Peripherals ![Modrinth Downloads](https://img.shields.io/modrinth/dt/allium-peripherals?color=00AF5C&label=modrinth&style=flat&logo=modrinth)

For CC:T for Fabric & Minecraft 1.19.2

Allium offers a couple peripherals - and more planned as ideas are proposed. The two currently provided are the survival and creative chat modem. These modems function identically to the chat module from [SquidDev's Plethora](https://github.com/SquidDev-CC/Plethora) peripherals mod.  
This mod depends on [ComputerCraft: Restitched](https://www.curseforge.com/minecraft/mc-mods/cc-restitched).

## Items
The current list of items in the game, along with a bit of documentation on their methods.

### Survival Chat Modem
To start, right-click a Survival Chat Modem to bind yourself; right-click it again to unbind. Nobody else can unbind you from a chat modem you've bound yourself to (apart from breaking and replacing it).  
Once bound, you have access to the following:

Methods:
- `capture`: Adds the given pattern to a queue that fires an event every time the bound player sends a message that matches the pattern. Once a message is captured, it will not be visible to other players.  
**NOTE: This is currently broken on >=1.19.1; chat messages will still be visible to other players!**
- `uncapture`: Removes the given pattern from the queue, or all patterns if not specified.
- `getCaptures`: Returns a table of all queued patterns.
- `getBoundPlayer`: Returns both the username and UUID of the bound player as strings.
- `say`: Sends a message to the bound player.

Events:
- `chat_message -> (username, message, uuid)`
  - Fired when a chat message is sent by the bound player.
  - `string`: The player's username
  - `string`: The player's chat message
  - `string`: The player's UUID
- `chat_capture -> (username, message, uuid, pattern)`
  - Fired when a chat message is sent by the bound player that matches a pattern defined via `capture`.
  - `string`: The player's username
  - `string`: The player's chat message
  - `string`: The player's UUID
  - `string`: The matching pattern


### Creative Chat Modem

The Creative Chat Modem is similar to the Survival Chat Modem, with the exception that it applies captures globally. Since it lacks the need for a player, the methods `getBoundPlayer` and `say` do not apply. All other methods and events are identical to the Survival Chat Modem.

## Rationale & Credits 

So basically I was really bored, and tired of waiting for Plethora to update (insert obligatory SquidDev comment "PRs Welcome!"), so I went and made what I needed. I used Fabric because open source ethics, love for the community, and love of framework. 

I would like to give a huge(blank) thanks for all the users on the fabric community discord for helping me make this mod a reality, from shouting at me to refresh gradle, to dealing with my ineptitude while trying to implement block entities. I'd also like to thank [SquidDev](https://github.com/SquidDev) in particular for assisting me when I came across issues with the CC end of the mod.
