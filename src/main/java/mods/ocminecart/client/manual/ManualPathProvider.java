package mods.ocminecart.client.manual;

import li.cil.oc.api.manual.PathProvider;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManualPathProvider implements PathProvider {

	private static final String PATH_PREFIX = OCMinecart.MOD_ID+"/%LANGUAGE%/";

	@Override
	public String pathFor(ItemStack stack) {
		if(stack==null) return null;
		Item item = stack.getItem();

		if(item.equals(ModItems.Items.COMPUTER_CART_CASE.get())){
			return PATH_PREFIX+"item/cartcase.md";
		}
		else if(item.equals(ModItems.Items.COMPUTER_CART.get())){
			return PATH_PREFIX+"item/cart.md";
		}
		//else if(item == ModItems.item_CartRemoteModule) return PATH_PREFIX+"item/remote.md";
		//else if(item == ModItems.item_CartRemoteAnalyzer) return PATH_PREFIX+"item/remoteanalyzer.md";
		//else if(item == ModItems.item_LinkingUpgrade) return PATH_PREFIX+"item/linkingupgrade.md";

		return null;
	}

	@Override
	public String pathFor(World world, BlockPos pos) {
		//Block block = world.getBlockState(pos).getBlock();

		//if(block == ModBlocks.block_NetworkRailBase) return PATH_PREFIX+"block/netrailbase.md";
		//else if(block == ModBlocks.block_NetworkRail) return PATH_PREFIX+"block/netrail.md";

		return null;
	}
}
