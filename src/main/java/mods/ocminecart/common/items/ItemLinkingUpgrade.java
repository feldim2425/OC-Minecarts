package mods.ocminecart.common.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Loader;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.assemble.util.TooltipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemLinkingUpgrade extends Item{
	
	public ItemLinkingUpgrade(){
		super();
		this.setMaxStackSize(64);
		this.setTextureName(OCMinecart.MODID+":linkingupgrade");
		this.setUnlocalizedName(OCMinecart.MODID+".linkingupgrade");
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
		if(!Loader.isModLoaded("Railcraft")){
			list.add(EnumChatFormatting.RED+"RAILCRAFT IS NOT INSTALLED. ITEM IS USELESS");
		}
		if(!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())){
			String key = GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
			String formkey = "[" + EnumChatFormatting.WHITE + key + EnumChatFormatting.GRAY + "]";
			list.add(StatCollector.translateToLocalFormatted("tooltip."+OCMinecart.MODID+".moreinfo", formkey));
		}
		else{
			list.addAll(TooltipUtil.trimString(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".linkingupgrade.desc")));
		}
	}
}
