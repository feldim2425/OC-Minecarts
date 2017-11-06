package mods.ocminecart.common;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.common.item.ModItems;
import mods.ocminecart.common.recipes.Recipes;
import mods.ocminecart.network.ModNetwork;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;


public class CommonProxy {

	public void preInit() {
		EventHandler.init();

		ModNetwork.init();
		ModItems.init();

		EntityRegistry.registerModEntity(EntityComputerCart.class, "computercart", 0, OCMinecart.getInstance(), 80, 1,
				true);
	}

	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(OCMinecart.getInstance(), new GuiHandler());

		Recipes.init();
	}

	public void postInit() {

	}

}
