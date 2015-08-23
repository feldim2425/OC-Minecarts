package mods.ocminecart.common.container.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotGhost extends Slot{

	public SlotGhost(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	 public int getSlotStackLimit(){
		 return 0;
	 }
	 
	 public boolean isItemValid(ItemStack stack){
		 return inventory.isItemValidForSlot(this.getSlotIndex(), stack);
	 }
	 
	 public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
	 {
		 super.onPickupFromSlot(player, stack);
		 player.inventory.setItemStack(null);
	 }
	 
	 public void putStack(ItemStack stack){
		 if(stack!=null)stack.stackSize=1;
		 super.putStack(stack);
	 }
	 
	 public ItemStack getStack(){
		 ItemStack stack =inventory.getStackInSlot(this.getSlotIndex());
		 if(stack!=null)stack.stackSize=0;
		 return stack;
	 }
}
