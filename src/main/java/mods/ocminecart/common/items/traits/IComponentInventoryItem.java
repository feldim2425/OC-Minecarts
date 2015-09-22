package mods.ocminecart.common.items.traits;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IComponentInventoryItem {
	
	public void setComponentList(ItemStack stack, Map<Integer, ItemStack> components);
	
	public Map<Integer, ItemStack> getComponentList(ItemStack stack);
	
	
}
