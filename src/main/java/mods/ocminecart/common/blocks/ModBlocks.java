package mods.ocminecart.common.blocks;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.block.Block;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {
	public static Block block_NetworkRail;
	public static Block block_NetworkRailBase;
	
	public static void init(){
		block_NetworkRail = new NetworkRail().setCreativeTab(OCMinecart.itemGroup);
		block_NetworkRailBase = new NetworkRailBase().setCreativeTab(OCMinecart.itemGroup); 
		
		GameRegistry.registerTileEntity(NetworkRailBaseTile.class, OCMinecart.MODID+"networkrailbasetile");
		
		GameRegistry.registerBlock(block_NetworkRail, "networkrail");
		GameRegistry.registerBlock(block_NetworkRailBase, "networkrailbase");
	}
}
