package mods.ocminecart.common.recipe.event;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ICraftingToolHandler {
	
	public boolean match(IInventory grid, ItemStack result);
	
	public List<ItemStack> getItems(IInventory grid, ItemStack result);
	
}
