package mods.ocminecart.common.disassemble;

import li.cil.oc.common.block.Item;
import mods.ocminecart.common.blocks.ModBlocks;
import net.minecraft.item.ItemStack;

public class NetworkRailBaseTemplate {
	
	public static boolean select(ItemStack stack){
		return stack.getItem()==Item.getItemFromBlock(ModBlocks.block_NetworkRailBase);
	}
	
	public static ItemStack[] disassemble(ItemStack stack, ItemStack[] ingredients){
		return ingredients;
	}
}
