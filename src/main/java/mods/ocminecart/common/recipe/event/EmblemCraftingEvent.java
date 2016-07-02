package mods.ocminecart.common.recipe.event;

import mods.ocminecart.common.items.ModItems;
import mods.railcraft.api.core.items.IToolCrowbar;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EmblemCraftingEvent implements ICraftingToolHandler {

	@Override
	public boolean match(IInventory grid, ItemStack result) {
		return (findResult(grid)!=-1 && findCrowbar(grid)!=-1 && result.getItem()==ModItems.item_ComputerCart);
	}

	@Override
	public List<ItemStack> getItems(IInventory grid, ItemStack result) {
		grid.getStackInSlot(findCrowbar(grid)).stackSize++;
		return null;
	}
	
	private int findCrowbar(IInventory grid){
		int size = grid.getSizeInventory();
		for(int i=0; i<size; i+=1){
			ItemStack stack = grid.getStackInSlot(i);
			if(stack!=null && (stack.getItem() instanceof IToolCrowbar))
				return i;
		}
		return -1;
	}
	
	private int findResult(IInventory grid){
		int size = grid.getSizeInventory();
		for(int i=0; i<size; i+=1){
			ItemStack stack = grid.getStackInSlot(i);
			if(stack!=null && stack.getItem() == ModItems.item_ComputerCart)
				return i;
		}
		return -1;
	}

}
