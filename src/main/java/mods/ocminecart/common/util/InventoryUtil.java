package mods.ocminecart.common.util;

import java.util.ArrayList;

import mods.ocminecart.OCMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryUtil {

	public static int dropItemInventoryWorld(ItemStack stack, World world, int x, int y, int z, ForgeDirection access, int num){
		TileEntity entity = world.getTileEntity(x, y, z);
		if(entity instanceof IInventory){
			return putInventory(stack, (IInventory) entity, num, access);
		}
		return -1;
	}
	
	public static int suckItemInventoryWorld(IInventory target, int[] taccess, World world, int x, int y, int z, ForgeDirection access, int num){
		TileEntity entity = world.getTileEntity(x, y, z);
		int moved = 0;
		if(entity instanceof IInventory){
			for(int i=0;i<taccess.length && moved<1;i+=1){
				ItemStack filter = target.getStackInSlot(taccess[i]);
				num=Math.min(num, spaceforItem(filter,target,taccess));
				ItemStack mov = suckInventory(filter, (IInventory) entity, num, access);
				if(mov!=null && mov.stackSize>0){
					moved = mov.stackSize;
					putInventory(mov,target,64,ForgeDirection.UNKNOWN);
				}
			}
			return moved;
		}
		return -1;
	}
	
	public static ItemStack suckInventory(ItemStack filter,IInventory inv, int maxnum, ForgeDirection access) {
		int[] slots = getAccessible(inv,access);
		int maxcount = maxnum;
		ItemStack pulled = null;
		for(int i=0;i<inv.getSizeInventory();i+=1){
			ItemStack slot = inv.getStackInSlot(slots[i]);
			if(slot != null && (filter==null || (filter!=null && filter.isItemEqual(slot)))){
				if(!(!(inv instanceof ISidedInventory) || ((inv instanceof ISidedInventory) && ((ISidedInventory)inv).canInsertItem(slots[i], slot, access.ordinal()))))
					continue;
				int stacksize = slot.stackSize;
				if(filter==null) filter = slot.copy();
				if(maxcount>=stacksize){
					ItemStack stack = slot.copy();
					inv.setInventorySlotContents(slots[i], null);
					if(pulled == null) pulled = stack;
					else pulled = ItemUtil.sumItemStacks(stack, pulled, false);
				}
				else{
					if(pulled == null) pulled = slot.splitStack(maxcount);
					else pulled = ItemUtil.sumItemStacks( slot.splitStack(maxcount), pulled, false);
				}
			}
		}
		return pulled;
	}

	public static int putInventory(ItemStack stack, IInventory inv, int maxnum, ForgeDirection access){
		int[] slots = getAccessible(inv,access);
		int maxcount = maxnum;
		slots = sortAccessible(inv, slots, stack);
		for(int i=0;i<slots.length;i+=1){
			if(!(!(inv instanceof ISidedInventory) || ((inv instanceof ISidedInventory) && ((ISidedInventory)inv).canInsertItem(slots[i], stack, access.ordinal()))))
				continue;
			if(!inv.isItemValidForSlot(slots[i], stack)) continue;
			ItemStack slot = inv.getStackInSlot(slots[i]);
			if(slot == null || (slot!=null && slot.isItemEqual(stack))){
				int stacksize = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
				int tstack = Math.min(stacksize, maxcount);
				tstack = Math.min(tstack, stack.stackSize);
				if(tstack<1) continue;
				if(slot!=null){
					tstack = Math.min(tstack, stacksize - slot.stackSize);
					slot.stackSize+=tstack;
					stack.stackSize-=tstack;
				}
				else{
					ItemStack newstack = stack.copy();
					stack.stackSize-=tstack;
					newstack.stackSize = tstack;
					inv.setInventorySlotContents(slots[i], newstack);
				}
				maxcount-=tstack;
			}
		}
		return maxnum-maxcount;
	}
	
	public static int[] sortAccessible(IInventory inv, int[] slots, ItemStack stack){
		int[] res = new int[slots.length];
		ArrayList<Integer> sort = new ArrayList<Integer>();
		for(int i=0;i<slots.length;i+=1){	//Check all slots with matching Items
			ItemStack slot = inv.getStackInSlot(slots[i]);
			if(slot!=null && slot.isItemEqual(stack))
				sort.add(slots[i]);
		}
		for(int i=0;i<slots.length;i+=1){	//Add all other slots
			if(!sort.contains(slots[i]))
				sort.add(slots[i]);
		}
		for(int i=0;i<sort.size();i+=1){   //Convert the List to a Array
			if(i<res.length)
				res[i] = sort.get(i);
		}
		return res;
	}
	
	public static int[] prioritizeAccessible(int[] slots, int slot){
		int[] res = new int[slots.length];
		ArrayList<Integer> sort = new ArrayList<Integer>();
		for(int i=0;i<slots.length;i+=1){	//Check if slot is a accessible slot
			if(slots[i] == slot){
				for(int j=i;j<slots.length;j+=1){
					sort.add(slots[j]);
				}
			}
		}
		for(int i=0;i<slots.length;i+=1){	//Add all other slots
			if(!sort.contains(slots[i]))
				sort.add(slots[i]);
		}
		for(int i=0;i<sort.size();i+=1){   //Convert the List to a Array
			if(i<res.length)
				res[i] = sort.get(i);
		}
		return res;
	}
	
	public static int[] getAccessible(IInventory inv, ForgeDirection access){
		if((inv instanceof ISidedInventory) && access != ForgeDirection.UNKNOWN) 
			return ((ISidedInventory)inv).getAccessibleSlotsFromSide(access.ordinal());
		int sides[] = new int[inv.getSizeInventory()];
		for(int i=0;i<inv.getSizeInventory();i+=1){
			sides[i]=i;
		}
		
		return sides;
	}
	
	/*
	 * Takes all items from invA and returns the matching slots in invB
	 */
	public static int spaceforItem(ItemStack stack, IInventory inv, int[] access){
		int space = 0;
		int maxstack = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
		for(int i=0;i<access.length;i+=1){
			ItemStack slot = inv.getStackInSlot(access[i]);
			if(slot==null)
				space+=maxstack;
			else if(slot.isItemEqual(stack)){
				space+=Math.max(0, maxstack-slot.stackSize);
			}
		}
		return space;
	}
	
	
}
