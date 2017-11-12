package mods.ocminecart.common.inventory;

import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.utils.ItemStackUtil;
import mods.ocminecart.utils.NBTTypes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class ComputerCartInventory implements IInventory {

	public static final int MAX_SIZE = 27;

	private int actualSize;
	private final ItemStack[] slots = new ItemStack[MAX_SIZE];

	private int selectedSlot;

	public ComputerCartInventory(EntityComputerCart entityComputerCart) {
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("selected", selectedSlot);

		NBTTagList nbtList = new NBTTagList();
		for (int i = 0; i < slots.length; i++) {
			ItemStack stack = this.slots[i];
			if(ItemStackUtil.isStackEmpty(stack)){
				continue;
			}
			NBTTagCompound slotNBT = new NBTTagCompound();
			NBTTagCompound itemNBT = new NBTTagCompound();
			stack.writeToNBT(itemNBT);
			slotNBT.setInteger("slot", i);
			slotNBT.setTag("item", itemNBT);
			nbtList.appendTag(slotNBT);
		}
		nbt.setTag("slots", nbtList);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("selected", NBTTypes.INT.getTypeID())) {
			selectedSlot = nbt.getInteger("selected");
		}

		if (nbt.hasKey("slots", NBTTypes.TAG_LIST.getTypeID())) {
			NBTTagList nbtList = (NBTTagList) nbt.getTag("slots");
			for (int i = 0; i < nbtList.tagCount(); i++) {
				NBTTagCompound slotNBT = nbtList.getCompoundTagAt(i);
				if (!slotNBT.hasKey("item", NBTTypes.TAG_COMPOUND.getTypeID()) || !slotNBT.hasKey("slot", NBTTypes.INT.getTypeID())) {
					continue;
				}
				ItemStack stack = ItemStack.loadItemStackFromNBT(slotNBT.getCompoundTag("item"));
				int slot = slotNBT.getInteger("slot");
				if(slot < this.slots.length) {
					this.slots[slot] = stack;
				}
			}
		}
	}

	public void recalculateSize(){

	}
	
	@Override
	public int getSizeInventory() {
		return actualSize;
	}

	@Nullable
	@Override
	public ItemStack getStackInSlot(int index) {
		if(checkSlot(index)){
			return slots[index];
		}
		return ItemStackUtil.getEmptyStack();
	}

	@Nullable
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(!checkSlot(index) || count < 1){
			return ItemStackUtil.getEmptyStack();
		}
		ItemStack stack = slots[index];
		if(ItemStackUtil.isStackEmpty(stack)){
			return ItemStackUtil.getEmptyStack();
		}
		ItemStack ret = stack.splitStack(count);
		if(stack.stackSize < 1){
			slots[index] = ItemStackUtil.getEmptyStack();
		}
		return ret;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(!checkSlot(index)){
			return ItemStackUtil.getEmptyStack();
		}
		ItemStack stack = slots[index];
		slots[index] = ItemStackUtil.getEmptyStack();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		if(checkSlot(index)){
			if(ItemStackUtil.isStackEmpty(stack)){
				slots[index] = ItemStackUtil.getEmptyStack();
			}
			else {
				slots[index] = stack;
			}
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return checkSlot(index);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	public boolean checkSlot(int index){
		return index >= 0 && index < actualSize;
	}

	public void setSelectedSlot(int selectedSlot) {
		this.selectedSlot = selectedSlot;
	}

	public int getSelectedSlot() {
		return selectedSlot;
	}
}
