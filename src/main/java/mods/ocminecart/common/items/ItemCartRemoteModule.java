package mods.ocminecart.common.items;

import java.util.List;
import java.util.Random;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.assemble.util.TooltipUtil;
import mods.ocminecart.common.entityextend.RemoteExtenderRegister;
import mods.ocminecart.common.items.interfaces.ItemEntityInteract;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ItemUseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCartRemoteModule extends Item implements ItemEntityInteract{
	
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
			if(p.worldObj.isRemote) return true;
			int err = RemoteExtenderRegister.enableRemote((EntityMinecart) e, true);
			
			NBTTagCompound usedat = new NBTTagCompound();
			usedat.setDouble("posX", e.posX);
			usedat.setDouble("posY", e.posY);
			usedat.setDouble("posZ", e.posZ);
			usedat.setByte("error", (byte)err);
			ModNetwork.sendToNearPlayers(new ItemUseMessage(0,p.getEntityId(),usedat), e.posX, e.posY, e.posZ, e.worldObj);
			if(!p.capabilities.isCreativeMode && err == 0) s.stackSize--;
				
			return true;
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void onMPUsage(EntityPlayer p, NBTTagCompound data){
		World worldObj = p.worldObj;
		double posX = data.getDouble("posX");
		double posY = data.getDouble("posY");
		double posZ = data.getDouble("posZ");
		byte error = data.getByte("error");
		Random r = new Random();
		if(error == 0){
			p.swingItem();
			p.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+StatCollector.translateToLocal("chat."+OCMinecart.MODID+".moduleinstalled")));
		}
		else if(error == 1) p.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+StatCollector.translateToLocal("chat."+OCMinecart.MODID+".invalidcart")));
		else p.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+StatCollector.translateToLocal("chat."+OCMinecart.MODID+".hasmodule")));
		for(int i=0;i<100;i++){
			if(error == 0)
				worldObj.spawnParticle("happyVillager", posX+(r.nextDouble()-0.5)*1.4, posY+(r.nextDouble()-0.5)*1.4, posZ+(r.nextDouble()-0.5)*1.4, 0, 0, 0);
			else
				worldObj.spawnParticle("smoke", posX+(r.nextDouble()-0.5)*1.4, posY-0.3, posZ+(r.nextDouble()-0.5)*1.4, 0, 0, 0);
		}
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
