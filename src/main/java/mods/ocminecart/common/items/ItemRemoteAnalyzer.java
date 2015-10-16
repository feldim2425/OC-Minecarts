package mods.ocminecart.common.items;

import java.util.List;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.assemble.util.TooltipUtil;
import mods.ocminecart.common.entityextend.RemoteExtenderRegister;
import mods.ocminecart.common.items.interfaces.ItemEntityInteract;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ItemUseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRemoteAnalyzer extends Item implements ItemEntityInteract{
	
	public ItemRemoteAnalyzer(){
		super();
		this.setMaxStackSize(1);
		this.setTextureName(OCMinecart.MODID+":remoteanalyzer");
		this.setUnlocalizedName(OCMinecart.MODID+".remoteanalyzer");
	}
	
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player){ return true; }
	
	//Called in the EventHandler
	public boolean onEntityClick(EntityPlayer p, Entity e, ItemStack s){
		if(e instanceof EntityMinecart && RemoteExtenderRegister.hasRemote((EntityMinecart) e)){
			if(p.worldObj.isRemote) return true;
			if(RemoteExtenderRegister.getExtender((EntityMinecart) e).isEnabled()){
				RemoteExtenderRegister.getExtender((EntityMinecart) e).onAnalyzeModule(p);
				
				NBTTagCompound usedat = new NBTTagCompound();
				usedat.setString("address", RemoteExtenderRegister.getExtender((EntityMinecart) e).getAddress());
				ModNetwork.channel.sendTo(new ItemUseMessage(1,p.getEntityId(),usedat), (EntityPlayerMP) p);
			}
			else
				p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"No Module found."));
			return true;
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void onMPUsage(EntityPlayer p, NBTTagCompound data){
		if(p!=Minecraft.getMinecraft().thePlayer) return;
		if(p.isSneaking()){
			GuiScreen.setClipboardString(data.getString("address"));
			p.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat."+OCMinecart.MODID+".clipboard")));
		}
		p.swingItem();
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
		if(!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())){
			String key = GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
			String formkey = "[" + EnumChatFormatting.WHITE + key + EnumChatFormatting.GRAY + "]";
			list.add(StatCollector.translateToLocalFormatted("tooltip."+OCMinecart.MODID+".moreinfo", formkey));
		}
		else{
			list.addAll(TooltipUtil.trimString(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".remoteanalyzer.desc")));
		}
	}
}
