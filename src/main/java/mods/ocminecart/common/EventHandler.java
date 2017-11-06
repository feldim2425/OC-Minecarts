package mods.ocminecart.common;

import mods.ocminecart.common.hooks.MinecartKillEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class EventHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@SubscribeEvent
	public void onMinecartDeath(MinecartKillEvent event) {
		Entity sourceEntity = event.getDamageSource().getEntity();
		if ((sourceEntity instanceof EntityPlayer) && ((EntityPlayer) sourceEntity).capabilities.isCreativeMode
				&& !event.getEntity().hasCustomName()) {
			event.setCanceled(true);
			event.getMinecart().setDead();
		}
	}

}
