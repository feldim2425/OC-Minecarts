package mods.ocminecart.common.items;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Loader;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.assemble.util.TooltipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCartRemoteModule extends Item{
	
	public ItemCartRemoteModule(){
		super();
		this.setMaxStackSize(4);
		this.setTextureName(OCMinecart.MODID+":remotemodule");
		this.setUnlocalizedName(OCMinecart.MODID+".remotemodule");
	}
	
	 public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player){ return true; }
	
	//Called in the EventHandler
	public boolean onEntityClick(EntityPlayer p, Entity e, ItemStack s){
		if(e instanceof EntityMinecart){
			Random r = new Random();
			for(int i=0;i<100;i++){
				e.worldObj.spawnParticle("smoke", e.posX+(r.nextDouble()-0.5)*1.4, e.posY-0.4, e.posZ+(r.nextDouble()-0.5)*1.4, 0, 0, 0);
			}
			if(p.worldObj.isRemote) return true;
			p.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Incompatible cart."));
			return true;
		}
		return false;
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
		if(!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())){
			String key = GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
			String formkey = "[" + EnumChatFormatting.WHITE + key + EnumChatFormatting.GRAY + "]";
			list.add(StatCollector.translateToLocalFormatted("tooltip."+OCMinecart.MODID+".moreinfo", formkey));
		}
		else{
			list.addAll(TooltipUtil.trimString(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".remotemodule.desc")));
			list.add("Railcraft is "+(!Loader.isModLoaded("Railcraft")? 
					EnumChatFormatting.RED+"not " : EnumChatFormatting.GREEN) +"avaiable");
		}
	}
	
}
