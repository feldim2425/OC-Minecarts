
![Connect locomotives to the network](item:ocminecart:itemcartremotemodule@0)

This item allows users to control a Locomotive with a computer.
Just right click a locomotive with the module to install it.

Currently the modules can only send and receive messages from a 4 block radius, but they need no power.

The standard receiver port is 2 and the standard response port is 1.

The remote module can also receive private messages.
To get the address of the module right click the locomotive with a Remote Module Analyzer
(Shift-right click to copy the address to the clipboard)

Example call: `modem.broadcast(2, "<command>", <arg1>, <arg2>, ...)`

Standard functions:
`doc([function name or "-t":sting]):string`   Returns:
*  The documentation of the given command
*  A serialized table with all commands
*  (if arg1 is "-t") A compressed serialized table with all commands (no '\n' or space)

`response_port([port:number]):number` sets the response port and returns the new port. -1 to response on the same port as the last recieved message

`command_port([port:number]):number` sets the command port and returns the new port. -1 to accept all ports

`response_broadcast([value:boolean]):boolean` if the value is true it will respond with private messages.
