package mods.ocminecart.common.recipe;

import li.cil.oc.api.Items;
import mods.ocminecart.common.items.interfaces.IComponentInventoryItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.Map;

public class RomCrafting implements IRecipe{
	
	private ItemStack output;
	private int romslot;

	public RomCrafting(ItemStack output, int romslot) {
		this.output = output;
		this.romslot = romslot;
	}

	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		return this.findResult(grid)!=-1 && this.findRom(grid)!=-1 && !this.hasUselessItem(grid);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		if(this.output.getItem() instanceof IComponentInventoryItem){
			if(this.findResult(grid)==-1 || this.findRom(grid)==-1 || this.hasUselessItem(grid))
				return null;
			
			int rom = this.findRom(grid);
			int res = this.findResult(grid);
			ItemStack host = grid.getStackInSlot(res).copy();
			ItemStack oldRom = null;
			Map<Integer,ItemStack> comp = ((IComponentInventoryItem)host.getItem()).getComponentList(host);
			if(comp == null) return null;
			if(comp.containsKey(this.romslot)) oldRom = comp.get(this.romslot);
			
			ItemStack newRom = grid.getStackInSlot(rom).copy();
			newRom.stackSize = 1;
			host.stackSize = 1;
			
			comp.put(this.romslot, newRom);
			
			((IComponentInventoryItem)host.getItem()).setComponentList(host, comp);
			
			return host;
		}
		return null;
	}
	

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		ItemStack out = this.output.copy();
		if(out.getItem() instanceof IComponentInventoryItem){
			out.stackSize = 1;
			return out;
		}
		return null;
	}
	
	private int findRom(InventoryCrafting grid){
		int size = grid.getSizeInventory();
		for(int i=0; i<size; i+=1){
			ItemStack stack = grid.getStackInSlot(i);
			if(stack!=null && Items.get(stack) == Items.get("eeprom"))
				return i;
		}
		return -1;
	}
	
	private int findResult(InventoryCrafting grid){
		int size = grid.getSizeInventory();
		for(int i=0; i<size; i+=1){
			ItemStack stack = grid.getStackInSlot(i);
			if(stack!=null && stack.isItemEqual(this.output))
				return i;
		}
		return -1;
	}
	
	private boolean hasUselessItem(InventoryCrafting grid){
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
