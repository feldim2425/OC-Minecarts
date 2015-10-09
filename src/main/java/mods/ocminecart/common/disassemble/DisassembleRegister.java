package mods.ocminecart.common.disassemble;

import li.cil.oc.api.IMC;
import mods.ocminecart.OCMinecart;


public class DisassembleRegister {
	public static void register(){
		IMC.registerDisassemblerTemplate("Computer Cart", 
				"mods.ocminecart.common.disassemble.ComputerCartTemplate.select",
				"mods.ocminecart.common.disassemble.ComputerCartTemplate.disassemble");
		
		IMC.registerDisassemblerTemplate(OCMinecart.NAME+"-Standard Template", 
				"mods.ocminecart.common.disassemble.StandardTemplate.select",
				"mods.ocminecart.common.disassemble.StandardTemplate.disassemble");
	}
}
