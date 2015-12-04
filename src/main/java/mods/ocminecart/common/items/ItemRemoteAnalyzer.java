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
	public boolean onEntityClick(EntityPlayer p, Entity e, ItemStack s, Type t){
		if(e instanceof EntityMinecart){
			if(p.worldObj.isRemote) return true;
			if(RemoteExtenderRegister.hasRemote((EntityMinecart) e) &&
					RemoteExtenderRegister.getExtender((EntityMinecart) e).isEnabled()){
				if(t==Type.RIGHT_CLICK){
					RemoteExtenderRegister.getExtender((EntityMinecart) e).onAnalyzeModule(p);
				
					NBTTagCompound usedat = new NBTTagCompound();
					usedat.setString("address", RemoteExtenderRegister.getExtender((EntityMinecart) e).getAddress());
					ModNetwork.sendToNearPlayers(new ItemUseMessage(1,p.getEntityId(),usedat), e.posX, e.posY, e.posZ, e.worldObj);
				}
				else if(t==Type.LEFT_CLICK){
					if(RemoteExtenderRegister.getExtender((EntityMinecart) e).editableByPlayer(p,false))
						p.openGui(OCMinecart.instance, 2, e.worldObj, e.getEntityId(), -10, 0);
					else{
						NBTTagCompound usedat = new NBTTagCompound();
						usedat.setInteger("type", 1);
						ModNetwork.channel.sendTo(new ItemUseMessage(1,p.getEntityId(),usedat), (EntityPlayerMP) p);
					}
				}
			}
			else if(RemoteExtenderRegister.hasRemote((EntityMinecart) e))
				p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"No Module found."));
			else
				p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"No Module installable."));
			return true;
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void onMPUsage(EntityPlayer p, NBTTagCompound data){
		if(p!=Minecraft.getMinecraft().thePlayer) return;
		if(data.hasKey("type") && data.getInteger("type")==1){
			p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+
					StatCollector.translateToLocal("chat."+OCMinecart.MODID+".owneronly")));
		}
		else if(p.isSneaking() && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
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
