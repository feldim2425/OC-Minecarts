package mods.ocminecart.client;

import mods.ocminecart.common.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModCreativeTab extends CreativeTabs {

	public static final ModCreativeTab TAB = new ModCreativeTab();

	public ModCreativeTab() {
		super("ocminecart");
	}

	@Override
	public Item getTabIconItem() {
		return ModItems.getItem("computer_cart_case");
	}


}
