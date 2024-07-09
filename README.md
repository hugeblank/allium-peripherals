# Allium Peripherals ![Modrinth Downloads](https://img.shields.io/modrinth/dt/allium-peripherals?color=00AF5C&label=modrinth&style=flat&logo=modrinth)

For CC:T for Fabric & Minecraft 1.16.5

Allium offers a couple peripherals - and more planned as ideas are proposed. The two currently provided are the survival
and creative chat modem. These modems function identically to the
chat module from [SquidDev's Plethora](https://github.com/SquidDev-CC/Plethora) peripherals mod.
This mod depends on [ComputerCraft: Restitched](https://www.curseforge.com/minecraft/mc-mods/cc-restitched).

## Items

The current list of items in the game, along with a bit of documentation on their methods.

### Calibrated Chat Modem

To start, right click on one to bind yourself, right click to unbind. Nobody else can unbind you from a chat modem
you've bound yourself to (apart from breaking and replacing it). Once bound you have access to all the methods:

- `capture(pattern)`: add a pattern to queue an event every time you send a message that matches. Once a message gets
  captured,
  it does not get sent to anyone else.
- `uncapture(pattern?)`: removes a single given pattern or all patterns if none are supplied
- `getCaptures()`: returns a table of all captures
- `getBoundPlayer()`: returns two strings, the `username,UUID` of the `bound player`
- `say(message)`: sends a message to the `bound player`.

#### Events

|     Name     |                         Description                         |                   Params                    |
|:------------:|:-----------------------------------------------------------:|:-------------------------------------------:|
| chat_message |          Whenever `bound player` sends any message          | eventName<br/>userName<br/>message<br/>uuid |
| chat_capture | Whenever `bound player` sends message with captured pattern |  message<br/>capture<br/>username<br/>uuid  |

### Chat Modem

To start, right click on one to bind yourself, right click to unbind. Nobody else can unbind you from a chat modem
you've bound yourself to (apart from breaking and replacing it). Once bound you have access to all the methods:

- `say(message)`: sends a message to players within `sendRange`
- `setSendRange(x,y,z)`: sets `sendRange`, returns `true` if size smaller than `maxSendRange`
- `getSendRange()`: returns size of `sendRange` as 3 numbers: `x,y,z`
- `getMaxSendRange()`: returns size of `maxSendRange` as 3 numbers: `x,y,z`

- `setListenRange(x,y,z)`: sets `listenRange`, returns `true` if size smaller than `maxListenRange`
- `getListenRange()`: returns size of `listenRange` as 3 numbers: `x,y,z`
- `getMaxListenRange()`: returns size of `maxListenRange` as 3 numbers: `x,y,z`

#### Events

|     Name     |                        Description                         |                   Params                    |
|:------------:|:----------------------------------------------------------:|:-------------------------------------------:|
| chat_message | Whenever any player within `listenRange` sends any message | eventName<br/>userName<br/>message<br/>uuid |

### Creative Chat Modem

The creative chat modem is similar to a `Calibrated Chat Modem`, with the exception that it applies captures globally.
Since it
lacks the need for a player, the methods `say` do not apply to the creative chat modem, all others
from the survival modem are identical.

## Rationale & Credits

So basically I was really bored, and tired of waiting for Plethora to update (insert obligatory SquidDev comment "PRs
Welcome!"), so I went and made what I needed. I used Fabric because open source ethics, love for the community, and love
of framework.

I would like to give a huge(blank) thanks for all the users on the fabric community discord for helping me make this mod
a reality, from shouting at me to refresh gradle, to dealing with my ineptitude while trying to implement block
entities. I'd also like to thank [SquidDev](https://github.com/SquidDev) in particular for assisting me when I came
across issues with the CC end of the mod.
