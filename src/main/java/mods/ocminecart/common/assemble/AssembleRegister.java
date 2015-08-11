package mods.ocminecart.common.assemble;

import li.cil.oc.api.IMC;
import mods.ocminecart.common.minecart.ComputerCart;

public class AssembleRegister {
	public static void register(){
		IMC.registerAssemblerTemplate("Computer Cart Tier 1", 
				"mods.ocminecart.common.assemble.ComputerCartT1Template.select", 
				"mods.ocminecart.common.assemble.ComputerCartT1Template.validate", 
				"mods.ocminecart.common.assemble.ComputerCartT1Template.assemble",
				ComputerCart.class, ComputerCartT1Template.getContainerTier(),ComputerCartT1Template.getUpgradeTier(), ComputerCartT1Template.getComponentSlots());
		
		IMC.registerAssemblerTemplate("Computer Cart Tier 2", 
				"mods.ocminecart.common.assemble.ComputerCartT2Template.select", 
				"mods.ocminecart.common.assemble.ComputerCartT2Template.validate", 
				"mods.ocminecart.common.assemble.ComputerCartT2Template.assemble",
				ComputerCart.class, ComputerCartT2Template.getContainerTier(),ComputerCartT2Template.getUpgradeTier(), ComputerCartT2Template.getComponentSlots());
	
		IMC.registerAssemblerTemplate("Computer Cart Tier 3", 
				"mods.ocminecart.common.assemble.ComputerCartT3Template.select", 
				"mods.ocminecart.common.assemble.ComputerCartT3Template.validate", 
				"mods.ocminecart.common.assemble.ComputerCartT3Template.assemble",
				ComputerCart.class, ComputerCartT3Template.getContainerTier(),ComputerCartT3Template.getUpgradeTier(), ComputerCartT3Template.getComponentSlots());
		
		IMC.registerAssemblerTemplate("Computer Cart Creative", 
				"mods.ocminecart.common.assemble.ComputerCartT4Template.select", 
				"mods.ocminecart.common.assemble.ComputerCartT4Template.validate", 
				"mods.ocminecart.common.assemble.ComputerCartT4Template.assemble",
				ComputerCart.class, ComputerCartT4Template.getContainerTier(),ComputerCartT4Template.getUpgradeTier(), ComputerCartT4Template.getComponentSlots());
	}
}
