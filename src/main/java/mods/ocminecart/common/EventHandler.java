package mods.ocminecart.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.ocminecart.Settings;
import mods.ocminecart.client.SlotIcons;
import mods.ocminecart.common.entityextend.RemoteExtenderRegister;
import mods.ocminecart.common.items.interfaces.ItemEntityInteract;
import mods.ocminecart.common.recipe.event.CraftingHandler;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ConfigSyncMessage;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.WorldEvent;


public class EventHandler {
	
	int ticks=0; // 40 Server Ticks/sec but we want only 20 
	
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
			if(((ItemEntityInteract) stack.getItem()).onEntityClick(event.entityPlayer, event.target,
					stack, ItemEntityInteract.Type.RIGHT_CLICK))
				event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityHit(AttackEntityEvent event) {
		ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if(stack!=null && stack.getItem() instanceof ItemEntityInteract){
			if(((ItemEntityInteract) stack.getItem()).onEntityClick(event.entityPlayer, event.target,
					stack, ItemEntityInteract.Type.LEFT_CLICK))
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
		ticks++;
		if(ticks<2) return;
		ticks=0;
		RemoteExtenderRegister.serverTick();
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event){
		NBTTagCompound config = new NBTTagCompound();
		config.setIntArray("remoterange", Settings.RemoteRange);
		ModNetwork.channel.sendTo(new ConfigSyncMessage(config), (EntityPlayerMP)event.player);
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event){
		if(event.world.isRemote) return;
		RemoteExtenderRegister.reinit();
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event){
		if(event.world.isRemote) return;
		RemoteExtenderRegister.reinit();
	}
}
