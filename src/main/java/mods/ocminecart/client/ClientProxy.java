package mods.ocminecart.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mods.ocminecart.client.manual.ManualRegister;
import mods.ocminecart.client.renderer.entity.ComputerCartRenderer;
import mods.ocminecart.client.renderer.item.ComputerCartItemRenderer;
import mods.ocminecart.common.CommonProxy;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	public void init(){
		super.init();
		
		RenderingRegistry.registerEntityRenderingHandler(ComputerCart.class, new ComputerCartRenderer());
		MinecraftForgeClient.registerItemRenderer(ModItems.item_ComputerCart, new ComputerCartItemRenderer());
		
		ManualRegister.registermanual();
	}
}
