package mods.ocminecart.common.items.interfaces;

import net.minecraft.item.ItemStack;

import java.util.Map;

public interface IComponentInventoryItem {
	
	public void setComponentList(ItemStack stack, Map<Integer, ItemStack> components);
	
	public Map<Integer, ItemStack> getComponentList(ItemStack stack);
	
	
}
