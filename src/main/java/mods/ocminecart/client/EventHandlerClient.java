package mods.ocminecart.client;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.client.render.item.RendererComputerCartItem;
import mods.ocminecart.client.texture.TextureHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class EventHandlerClient {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		TextureHelper.loadTextures(event.getMap());
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
		registry.putObject(new ModelResourceLocation(OCMinecart.MOD_ID + ":computer_cart", "inventory"), new RendererComputerCartItem());
	}
}
