package mods.ocminecart.common;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.assemble.AssembleRegister;
import mods.ocminecart.common.blocks.ModBlocks;
import mods.ocminecart.common.disassemble.DisassembleRegister;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.network.ModNetwork;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

public class CommonProxy {
	
	
	public void postInit() {
	}

	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(OCMinecart.instance, new GuiHandler());
		
		DisassembleRegister.register();
		AssembleRegister.register();
		Recipes.init();
	}

	public void preInit() {
		EventHandler.initHandler();
		
		ModNetwork.init();
		ModItems.init();
		ModBlocks.init();
		
		EntityRegistry.registerModEntity(ComputerCart.class, "computercart", 1,OCMinecart.instance, 80, 1, true);
		
	}
	
}
