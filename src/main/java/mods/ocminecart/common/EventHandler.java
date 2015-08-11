package mods.ocminecart.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.ocminecart.client.SlotIcons;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;


public class EventHandler {
	
	public static void initHandler(){
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		//FMLCommonHandler.instance().bus().register(new EventHandler());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemIconRegister(TextureStitchEvent event) {
		if(event.map.getTextureType()==1) SlotIcons.register(event.map);
	}
}
