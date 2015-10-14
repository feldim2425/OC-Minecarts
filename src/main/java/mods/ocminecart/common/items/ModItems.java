package mods.ocminecart.common.items;

import mods.ocminecart.OCMinecart;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static Item item_ComputerCart;
	public static Item item_ComputerCartCase;
	public static Item item_CartRemoteModule;
	public static Item item_CartRemoteAnalyzer;
	
	public static void init(){
		item_ComputerCart=new ItemComputerCart().setCreativeTab(OCMinecart.itemGroup);
		item_ComputerCartCase=new ComputerCartCase().setCreativeTab(OCMinecart.itemGroup);
		item_CartRemoteModule = new ItemCartRemoteModule().setCreativeTab(OCMinecart.itemGroup);
		item_CartRemoteAnalyzer = new ItemRemoteAnalyzer().setCreativeTab(OCMinecart.itemGroup);
		
		GameRegistry.registerItem(item_ComputerCart,"itemcomputercart");
		GameRegistry.registerItem(item_ComputerCartCase,"itemcomputercartcase");
		GameRegistry.registerItem(item_CartRemoteModule,"itemcartremotemodule");
		GameRegistry.registerItem(item_CartRemoteAnalyzer,"itemcartremoteanalyzer");
	}
}
