package mods.ocminecart.client;

import mods.ocminecart.client.manual.ManualRegister;
import mods.ocminecart.client.render.entity.RendererComputerCart;
import mods.ocminecart.common.CommonProxy;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.common.item.ModItems;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();

		EventHandlerClient.init();
		ModItems.initClient();

		RenderingRegistry.registerEntityRenderingHandler(EntityComputerCart.class,
				(manager) -> new RendererComputerCart(manager));
	}

	@Override
	public void init() {
		super.init();

		ManualRegister.registermanual();
	}

	@Override
	public void postInit() {
		super.postInit();
	}

}
