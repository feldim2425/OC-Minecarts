package mods.ocminecart.client.manual;

import li.cil.oc.api.manual.PathProvider;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.blocks.ModBlocks;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ManualPathProvider implements PathProvider{
	
	private static final String PATH_PREFIX = OCMinecart.MODID+"/%LANGUAGE%/";
	
	@Override
	public String pathFor(ItemStack stack) {
		if(stack==null) return null;
		Item item = stack.getItem();
		
		if(item == ModItems.item_ComputerCartCase) return PATH_PREFIX+"item/cartcase.md";
		else if(item == ModItems.item_ComputerCart) return PATH_PREFIX+"item/cart.md";
		else if(item == ModItems.item_CartRemoteModule) return PATH_PREFIX+"item/remote.md";
		else if(item == ModItems.item_CartRemoteAnalyzer) return PATH_PREFIX+"item/remoteanalyzer.md";
		else if(item == ModItems.item_LinkingUpgrade) return PATH_PREFIX+"item/linkingupgrade.md";
		
		return null;
	}

	@Override
	public String pathFor(World world, int x, int y, int z) {
		Block block = world.getBlock(x,y,z);
		if(block==null) return null;
		
		if(block == ModBlocks.block_NetworkRailBase) return PATH_PREFIX+"block/netrailbase.md";
		else if(block == ModBlocks.block_NetworkRail) return PATH_PREFIX+"block/netrail.md";
		
		return null;
	}

}
