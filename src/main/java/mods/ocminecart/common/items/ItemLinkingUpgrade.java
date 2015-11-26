package mods.ocminecart.common.items;

import mods.ocminecart.OCMinecart;
import net.minecraft.item.Item;

public class ItemLinkingUpgrade extends Item{
	
	public ItemLinkingUpgrade(){
		super();
		this.setMaxStackSize(64);
		this.setTextureName(OCMinecart.MODID+":linkingupgrade");
		this.setUnlocalizedName(OCMinecart.MODID+".linkingupgrade");
	}
}
