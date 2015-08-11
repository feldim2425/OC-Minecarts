package mods.ocminecart.common.items;

import mods.ocminecart.OCMinecart;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static Item item_ComputerCart;
	public static Item item_ComputerCartCase;
	
	public static void init(){
		item_ComputerCart=new ItemComputerCart().setCreativeTab(OCMinecart.itemGroup);
		item_ComputerCartCase=new ComputerCartCase().setCreativeTab(OCMinecart.itemGroup);
		
		GameRegistry.registerItem(item_ComputerCart,"itemcomputercart");
		GameRegistry.registerItem(item_ComputerCartCase,"itemcomputercartcase");
	}
}
