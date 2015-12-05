# Remote Module

![Smart Cart](item:ocminecart:itemcartremotemodule@0)

This item allows users to control a Locomotive with a computer.
Just right click a locomotive with the module to install it.

Currently the modules need no power.

Max. wireless range:

- Tier 1: 4 blocks
- Tier 2: 64 blocks
- Tier 3: 256 blocks

The standard receiver port is 2 and the standard response port is 1.

The remote module can also receive private messages.
To get the address of the module right click the locomotive with an [Remote Module Analyzer](remoteanalyzer.md)
(CTRL + Sneak-right click to copy the address to the clipboard)

For removing the remote module left-click on the cart. More information on the [Remote Module Analyzer](remoteanalyzer.md) page.

Example call: `modem.broadcast(2, "<command>", <arg1>, <arg2>, ...)`

Standard functions:
`doc([function name or "-t":sting]):string`   Returns:
*  The documentation of the given command
*  A serialized table with all commands
*  (if arg1 is "-t") A compressed serialized table with all commands (no '\n' or space)

`response_port([port:number]):number` sets the response port and returns the new port. -1 to response on the same port as the last recieved message

`command_port([port:number]):number` sets the command port and returns the new port. -1 to accept all ports

`response_broadcast([value:boolean]):boolean` if the value is true it will respond with private messages.

`wlan_strength([value:number]):number,number` get/set the current and get the max. wireless strength.
