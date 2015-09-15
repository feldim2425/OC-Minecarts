package mods.ocminecart.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class Inventory implements IInventory{
	
	private ItemStack[] stacks = new ItemStack[this.getSizeInventory()];

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot<this.getSizeInventory())
			return this.stacks[slot];
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int number) {
		if(slot>=0 && slot<this.getSizeInventory()){
			if(number >= stacks[slot].stackSize){
				ItemStack get = stacks[slot];
				stacks[slot]=null;
				this.slotChanged(slot);
				return get;
			}
			else{
				ItemStack ret = stacks[slot].splitStack(number);
				if(stacks[slot].stackSize<1){
					stacks[slot] = null;
				}
				this.slotChanged(slot);
				return ret;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(stack != null && stack.stackSize < 1) stack = null;
		if(slot<this.getSizeInventory()){
			stacks[slot] = stack;
			this.slotChanged(slot);
		}
	}
	
	//This is the same as setInventorySlotContents but will not send a Signal to the machine;
	private void updateSlotContents(int slot, ItemStack stack){
		if(stack != null && stack.stackSize < 1) stack = null;
		if(slot<this.getSizeInventory()){
			stacks[slot] = stack;
		}
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}
	
	public NBTTagList writeToNBT(){
		NBTTagList tag = new NBTTagList();
		for(byte i=0;i<this.getSizeInventory();i+=1){
			NBTTagCompound slot = new NBTTagCompound();
			ItemStack stack = this.getStackInSlot(i);
			slot.setByte("slot", i);
			if(stack!=null){
				NBTTagCompound item = new NBTTagCompound();
				stack.writeToNBT(item);
				slot.setTag("item", item);
			}
			tag.appendTag(slot);
		}
		return tag;
	}
	
	public void readFromNBT(NBTTagList tag){
		int tagc = tag.tagCount();
		for(int i=0;i<tagc;i+=1){
			NBTTagCompound slot = tag.getCompoundTagAt(i);
			if(slot.hasKey("item")){
				this.updateSlotContents(slot.getByte("slot"), ItemStack.loadItemStackFromNBT(slot.getCompoundTag("item")));
			}
		}
	}
	
	protected abstract void slotChanged(int slot);

}
