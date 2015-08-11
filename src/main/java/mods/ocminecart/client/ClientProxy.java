package mods.ocminecart.client;

import java.util.Iterator;

import mods.ocminecart.client.renderer.entity.ComputerCartRenderer;
import mods.ocminecart.client.renderer.item.ComputerCartItemRenderer;
import mods.ocminecart.common.CommonProxy;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ClientProxy extends CommonProxy {
	public void init(){
		super.init();
		
		RenderingRegistry.registerEntityRenderingHandler(ComputerCart.class, new ComputerCartRenderer());
		MinecraftForgeClient.registerItemRenderer(ModItems.item_ComputerCart, new ComputerCartItemRenderer());
	}
}
