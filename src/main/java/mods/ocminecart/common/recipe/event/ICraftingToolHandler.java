package mods.ocminecart.common.recipe.event;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ICraftingToolHandler {
	
	public boolean match(IInventory grid, ItemStack result);
	
	public List<ItemStack> getItems(IInventory grid, ItemStack result);
	
}
