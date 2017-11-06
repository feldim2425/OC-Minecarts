package mods.ocminecart.common.recipes.disassembler;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StandardDisassembler {

	public static boolean select(ItemStack stack){
		Item item = stack.getItem();
		if(isOnBlacklist(item)){
			return false;
		}
		return item.getRegistryName().getResourceDomain().equals(OCMinecart.MOD_ID);
	}

	public static ItemStack[] disassemble(ItemStack stack, ItemStack[] ingredients){
		return ingredients; //Just return the items from the crafting recipe.
	}

	//Some items (like the computer cart) are not craftable and will return nothing
	//In this case we don't want to handle them on the normal way.
	private static boolean isOnBlacklist(Item item){
		return ModItems.Items.COMPUTER_CART.get().equals(item);
	}
}
