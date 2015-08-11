package mods.ocminecart.common.disassemble;

import li.cil.oc.api.IMC;


public class DisassembleRegister {
	public static void register(){
		IMC.registerDisassemblerTemplate("Network Rail Controller", 
				"mods.ocminecart.common.disassemble.NetworkRailBaseTemplate.select",
				"mods.ocminecart.common.disassemble.NetworkRailBaseTemplate.disassemble");
		
		IMC.registerDisassemblerTemplate("Network Rail", 
				"mods.ocminecart.common.disassemble.NetworkRailTemplate.select",
				"mods.ocminecart.common.disassemble.NetworkRailTemplate.disassemble");
		
		IMC.registerDisassemblerTemplate("Computercartcase", 
				"mods.ocminecart.common.disassemble.ComputerCartCaseTemplate.select",
				"mods.ocminecart.common.disassemble.ComputerCartCaseTemplate.disassemble");
		
		IMC.registerDisassemblerTemplate("Computer Cart", 
				"mods.ocminecart.common.disassemble.ComputerCartTemplate.select",
				"mods.ocminecart.common.disassemble.ComputerCartTemplate.disassemble");
	}
}
