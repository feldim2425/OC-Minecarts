package mods.ocminecart.common;

import mods.ocminecart.client.SlotIcons;
import mods.ocminecart.common.entityextend.RemoteExtenderRegister;
import mods.ocminecart.common.items.interfaces.ItemEntityInteract;
import mods.ocminecart.common.recipe.event.CraftingHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EventHandler {
	
	public static void initHandler(){
		EventHandler handler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemIconRegister(TextureStitchEvent event) {
		if(event.map.getTextureType()==1) SlotIcons.register(event.map);
	}
	
	@SubscribeEvent
	public void onItemCraft(ItemCraftedEvent event){
		CraftingHandler.onCraftingEvent(event);
	}
	
	@SubscribeEvent
	public void onEntityClick(EntityInteractEvent event) {
		ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if(stack!=null && stack.getItem() instanceof ItemEntityInteract){
			if(((ItemEntityInteract) stack.getItem()).onEntityClick(event.entityPlayer, event.target, stack))
				event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event){
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
		if(event.entity instanceof EntityMinecart){
			RemoteExtenderRegister.addRemote((EntityMinecart) event.entity);
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event){
		RemoteExtenderRegister.serverTick();
	}
}
