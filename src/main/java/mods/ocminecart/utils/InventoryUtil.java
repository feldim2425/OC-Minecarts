package mods.ocminecart.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public final class InventoryUtil {

	private InventoryUtil() {
	}

	public static ItemStack putStack(IItemHandler itemHandler, ItemStack stack, int slotoffset) {
		stack = stack.copy();
		int size = itemHandler.getSlots();
		for (int i = 0; i < size && !ItemStackUtil.isStackEmpty(stack); i++) {
			stack = itemHandler.insertItem((i + slotoffset) % size, stack, false);
		}
		return stack;
	}

	public static ItemStack pullStack(IItemHandler itemHandler, int amount, int slotoffset, boolean oneOnly, Predicate<ItemStack> predicate) {
		int size = itemHandler.getSlots();
		ItemStack stack = null;
		for (int i = 0; i < size && amount > 1; i++) {
			int slot = (i + slotoffset) % size;
			ItemStack inSlot = itemHandler.getStackInSlot(slot);
			if (!ItemStackUtil.isStackEmpty(inSlot) && predicate.test(inSlot)) {
				if (stack == null) {
					inSlot = itemHandler.extractItem(slot, amount, false);
					if (!ItemStackUtil.isStackEmpty(inSlot)) {
						amount -= inSlot.stackSize;
						stack = inSlot.copy();
					}

					if (oneOnly) {
						amount = 0;
					}
				}
				else {
					inSlot = itemHandler.extractItem(slot, amount, false);
					if (!ItemStackUtil.isStackEmpty(inSlot)) {
						amount -= inSlot.stackSize;
						stack.stackSize += inSlot.stackSize;
					}
				}
			}
		}
		return stack == null ? ItemStackUtil.getEmptyStack() : stack;
	}
}
