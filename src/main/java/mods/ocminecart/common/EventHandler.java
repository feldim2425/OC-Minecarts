package mods.ocminecart.common;

import mods.ocminecart.client.SlotIcons;
import mods.ocminecart.common.items.ItemCartRemoteModule;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.recipe.event.CraftingHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
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
		if(stack!=null && stack.getItem() == ModItems.item_CartRemoteModule){
			if(((ItemCartRemoteModule) ModItems.item_CartRemoteModule).onEntityClick(event.entityPlayer, event.target, stack))
				event.setCanceled(true);
		}
	}
}
