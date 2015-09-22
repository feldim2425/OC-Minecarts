package mods.ocminecart.common.recipe.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import li.cil.oc.api.Items;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ComputerCartRomCrafting implements IExtraItemHandler{

	@Override
	public boolean match(IInventory grid, ItemStack result) {
		return this.findResult(grid)!=-1 && this.findRom(grid)!=-1 && !this.hasUselessItem(grid)
				&& result!=null && result.getItem() == ModItems.item_ComputerCart;
	}

	@Override
	public List<ItemStack> getItems(IInventory grid, ItemStack result) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		Map<Integer,ItemStack> comp = ((ItemComputerCart) ModItems.item_ComputerCart).getComponentList(grid.getStackInSlot(this.findResult(grid)));
		if(comp.containsKey(ItemComputerCart.SLOT_ROM)){
			ItemStack rom = comp.get(ItemComputerCart.SLOT_ROM);
			rom.stackSize = 1;
			list.add(rom);
		}
		return list;
		
	}
	
	private int findRom(IInventory grid){
		int size = grid.getSizeInventory();
		for(int i=0; i<size; i+=1){
			ItemStack stack = grid.getStackInSlot(i);
			if(stack!=null && Items.get(stack) == Items.get("eeprom"))
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
	
	private boolean hasUselessItem(IInventory grid){
		int rom = this.findRom(grid);
		int res = this.findResult(grid);
		int size = grid.getSizeInventory();
		for(int i=0; i<size; i+=1){
			ItemStack stack = grid.getStackInSlot(i);
			if(stack != null && i != rom && i !=res)
				return true;
		}
		return false;
	}

}
