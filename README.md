# Allium Peripherals ![Modrinth Downloads](https://img.shields.io/badge/dynamic/json?color=5da545&label=modrinth&query=downloads&url=https://api.modrinth.com/api/v1/mod/allium-peripherals&style=flat&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMSAxMSIgd2lkdGg9IjE0LjY2NyIgaGVpZ2h0PSIxNC42NjciICB4bWxuczp2PSJodHRwczovL3ZlY3RhLmlvL25hbm8iPjxkZWZzPjxjbGlwUGF0aCBpZD0iQSI+PHBhdGggZD0iTTAgMGgxMXYxMUgweiIvPjwvY2xpcFBhdGg+PC9kZWZzPjxnIGNsaXAtcGF0aD0idXJsKCNBKSI+PHBhdGggZD0iTTEuMzA5IDcuODU3YTQuNjQgNC42NCAwIDAgMS0uNDYxLTEuMDYzSDBDLjU5MSA5LjIwNiAyLjc5NiAxMSA1LjQyMiAxMWMxLjk4MSAwIDMuNzIyLTEuMDIgNC43MTEtMi41NTZoMGwtLjc1LS4zNDVjLS44NTQgMS4yNjEtMi4zMSAyLjA5Mi0zLjk2MSAyLjA5MmE0Ljc4IDQuNzggMCAwIDEtMy4wMDUtMS4wNTVsMS44MDktMS40NzQuOTg0Ljg0NyAxLjkwNS0xLjAwM0w4LjE3NCA1LjgybC0uMzg0LS43ODYtMS4xMTYuNjM1LS41MTYuNjk0LS42MjYuMjM2LS44NzMtLjM4N2gwbC0uMjEzLS45MS4zNTUtLjU2Ljc4Ny0uMzcuODQ1LS45NTktLjcwMi0uNTEtMS44NzQuNzEzLTEuMzYyIDEuNjUxLjY0NSAxLjA5OC0xLjgzMSAxLjQ5MnptOS42MTQtMS40NEE1LjQ0IDUuNDQgMCAwIDAgMTEgNS41QzExIDIuNDY0IDguNTAxIDAgNS40MjIgMCAyLjc5NiAwIC41OTEgMS43OTQgMCA0LjIwNmguODQ4QzEuNDE5IDIuMjQ1IDMuMjUyLjgwOSA1LjQyMi44MDljMi42MjYgMCA0Ljc1OCAyLjEwMiA0Ljc1OCA0LjY5MSAwIC4xOS0uMDEyLjM3Ni0uMDM0LjU2bC43NzcuMzU3aDB6IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiM1ZGE0MjYiLz48L2c+PC9zdmc+)

For CC:T for Fabric & Minecraft 1.16.5

Allium offers a couple peripherals - and more planned as ideas are proposed. The two currently provided are the survival and creative chat modem. These modems function identically to the
chat module from [SquidDev's Plethora](https://github.com/SquidDev-CC/Plethora) peripherals mod. 
This mod depends on [ComputerCraft: Restitched](https://www.curseforge.com/minecraft/mc-mods/cc-restitched).
 
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
a reality, from shouting at me to refresh gradle, to dealing with my ineptitude while trying to implement block
entities. I'd also like to thank [SquidDev](https://github.com/SquidDev) in particular for assisting me when I came 
across issues with the CC end of the mod.
