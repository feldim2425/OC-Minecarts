package mods.ocminecart.common.recipes.disassembler;


import mods.ocminecart.common.item.ItemComputerCart;
import mods.ocminecart.common.item.ModItems;
import mods.ocminecart.common.item.data.DataComputerCart;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComputerCartDisassembler {

	public static boolean select(ItemStack stack) {
		return ModItems.Items.COMPUTER_CART.get().equals(stack.getItem());
	}

	public static ItemStack[] disassemble(ItemStack stack, ItemStack[] ingredients) {
		DataComputerCart data = ((ItemComputerCart) stack.getItem()).getData(stack);

		List<ItemStack> list = new LinkedList<>();
		if (ModItems.Items.COMPUTER_CART.get().equals(stack.getItem())) {
			if (data.getTier() < 3) {
				list.add(new ItemStack(ModItems.Items.COMPUTER_CART_CASE.get(), 1, data.getTier()));
			}

			for (Map.Entry<Integer, ItemStack> comp : data.getComponents().entrySet()) {
				list.add(comp.getValue());
			}
		}

		ItemStack[] items = new ItemStack[list.size()];
		return list.toArray(items);
	}
}
