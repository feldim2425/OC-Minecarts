package mods.ocminecart.common.disassemble;

import li.cil.oc.common.block.Item;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.item.ItemStack;

public class ComputerCartCaseTemplate {
	public static boolean select(ItemStack stack){
		
		return stack.getItem()==ModItems.item_ComputerCartCase && stack.getItemDamage()<=2;
	}
	
	public static ItemStack[] disassemble(ItemStack stack, ItemStack[] ingredients){
		return ingredients;
	}
}
