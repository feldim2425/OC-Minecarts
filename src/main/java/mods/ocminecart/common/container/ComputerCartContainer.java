package mods.ocminecart.common.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.component.TextBuffer;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.ocminecart.common.container.slots.ContainerSlot;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComputerCartContainer extends Container {
	
	public static final int YSIZE_SCR = 256;
	public static final int YSIZE_NOSCR = 108;
	public static final int XSIZE = 256;
	public static final int DELTA = YSIZE_SCR - YSIZE_NOSCR; 
	
	private ComputerCart entity;
	private boolean hasScreen=false;
	
	public int smaxEnergy = -1;
	public int sEnergy = -1;
	public int sizeinv = -1;
	public int selSlot = -1;
	public boolean updatesize = false;
	
	
	public ComputerCartContainer(InventoryPlayer inventory,ComputerCart entity) {
		this.entity=entity;
		
		this.initComponents(this.entity.getCompinv().getComponents());
		
		this.addSlotToContainer(new ContainerSlot(entity.compinv, 20 , 188,232 - ((this.hasScreen) ? 0 : DELTA), entity.compinv.getContainer(0)));
		this.addSlotToContainer(new ContainerSlot(entity.compinv, 21 , 206,232 - ((this.hasScreen) ? 0 : DELTA), entity.compinv.getContainer(1)));
		this.addSlotToContainer(new ContainerSlot(entity.compinv, 22 , 224,232 - ((this.hasScreen) ? 0 : DELTA), entity.compinv.getContainer(2)));
		
		for(int i=0;i<entity.maininv.getMaxSizeInventory();i+=1){
			this.addSlotToContainer(new Slot(entity.maininv, i, -10000, -10000));
		}
		
		this.addPlayerInv(6, 174 -((this.hasScreen) ? 0 : DELTA), inventory);
	}
	
	private void initComponents(Iterable<ManagedEnvironment> iterable){
		Iterator<ManagedEnvironment> list = iterable.iterator();
		while(list.hasNext()){
			ManagedEnvironment env = list.next();
			if(env instanceof TextBuffer) this.hasScreen=true;
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceToEntity(entity)<=64;
	}
	
	
	private void addPlayerInv(int x,int y, InventoryPlayer inventory){
		for(int i=0;i<9;i++){
			this.addSlotToContainer(new Slot(inventory, i, x+i*18, y+58));
		}
		
		for(int i=0;i<3;i++){
			for(int j=0;j<9;j++){
				this.addSlotToContainer(new Slot(inventory, j+i*9+9, x+j*18, y+i*18));
			}
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		Slot s = this.getSlot(slot);
		if(s==null || !s.getHasStack()) return null;
		ItemStack stack = s.getStack();
		List<Integer> a = getPlayerInvSlots(stack,player);
		List<Integer> b = getCartInvSlots(stack);
		if(s.inventory.equals(player.inventory)){
			for(int sl : b){
				transferToSlot(sl,stack);
				if(stack.stackSize<1){
					s.putStack(null);
					return null;
				}
			}
			for(int sl : a){
				transferToSlot(sl,stack);
				if(stack.stackSize<1){
					s.putStack(null);
					return null;
				}
			}
		}
		else{
			for(int sl : a){
				transferToSlot(sl,stack);
				if(stack.stackSize<1){
					s.putStack(null);
					return null;
				}
			}
			for(int sl : b){
				transferToSlot(sl,stack);
				if(stack.stackSize<1){
					s.putStack(null);
					return null;
				}
			}
		}
		if(stack==null || stack.stackSize<1) s.putStack(null);
		return (stack==null || stack.stackSize<1) ? null : stack;
	}
	
	private List<Integer> getCartInvSlots(ItemStack stack){
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(Slot slot : (List<Slot>)this.inventorySlots){
			if(slot.inventory.equals(this.entity.maininv) || slot.inventory.equals(this.entity.compinv)){
				slots.add(slot.slotNumber);
			}
		}
		return sortSlots(stack,slots);
	}
	
	private List<Integer> getPlayerInvSlots(ItemStack stack, EntityPlayer player){
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(Slot slot : (List<Slot>)this.inventorySlots){
			if(slot.inventory.equals(player.inventory)){
				slots.add(slot.slotNumber);
			}
		}
		return sortSlots(stack,slots);
	}
	
	private List<Integer> sortSlots(ItemStack stack, List<Integer> slots){
		ArrayList<Integer> res = new ArrayList<Integer>();
		for(int slot : slots){
			Slot s = this.getSlot(slot);
			if(s.getHasStack() && s.getStack().isItemEqual(stack)){
				res.add(slot);
			}
		}
		for(int slot : slots){
			Slot s = this.getSlot(slot);
			if(!s.getHasStack() || !s.getStack().isItemEqual(stack)){
				res.add(slot);
			}
		}
		return res;
	}
	
	private void transferToSlot(int slotindex, ItemStack stack){
		Slot slot = this.getSlot(slotindex);
		if(slot == null || !slot.isItemValid(stack) || stack == null || stack.stackSize < 1) return;
		if(slot.getHasStack() && !slot.getStack().isItemEqual(stack)) return;
		int max = Math.min(slot.getSlotStackLimit(), Math.min(slot.inventory.getInventoryStackLimit(), stack.getMaxStackSize()));
		int cur = (slot.getHasStack()) ? slot.getStack().stackSize : 0;
		int transfer = max - cur;
		transfer = Math.min(stack.stackSize, transfer);
		if(transfer > 0){
			ItemStack sstack = stack.copy();
			sstack.stackSize = transfer + cur;
			slot.putStack(sstack);
			stack.stackSize-=transfer;
		}
		return;
	}
	
	public ComputerCart getEntity(){
		return this.entity;
	}
	
	public boolean getHasScreen(){
		return this.hasScreen;
	}
	
	public void addCraftigToICrafters(ICrafting craft){
		super.addCraftingToCrafters(craft);
		
		craft.sendProgressBarUpdate(this, 0, (int) this.entity.getCurEnergy());
		craft.sendProgressBarUpdate(this, 1, (int) this.entity.getMaxEnergy());
		craft.sendProgressBarUpdate(this, 2, this.entity.getInventorySpace());
		craft.sendProgressBarUpdate(this, 3, this.entity.selectedSlot());
	}
	
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		
		if(this.entity.worldObj.isRemote) return;
		
		for(int i=0;i<this.crafters.size();i+=1){
        	ICrafting craft = (ICrafting) this.crafters.get(i);
        	
        	if(this.entity.getCurEnergy() != this.sEnergy){
        		craft.sendProgressBarUpdate(this, 0, (int) (this.entity.getCurEnergy()));
        	}
        	
        	if(this.entity.getMaxEnergy() != this.smaxEnergy){
        		craft.sendProgressBarUpdate(this, 1, (int) (this.entity.getMaxEnergy()));
        	}
        	
        	if(this.entity.getInventorySpace() != this.sizeinv){
        		craft.sendProgressBarUpdate(this, 2, this.entity.getInventorySpace());
        	}
        	
        	if(this.entity.selectedSlot() != this.selSlot){
        		craft.sendProgressBarUpdate(this, 3, this.entity.selectedSlot());
        	}
        }
		
		this.smaxEnergy=(int) this.entity.getMaxEnergy();
		this.sEnergy=(int) this.entity.getCurEnergy();
		this.sizeinv=this.entity.getInventorySpace();
		this.selSlot = this.entity.selectedSlot();
	}
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int updateid, int value){
		switch(updateid){
		case 0:
			this.sEnergy = value;
			break;
		case 1:
			this.smaxEnergy = value;
			break;
		case 2:
			this.updatesize = true;
			this.sizeinv = value;
			break;
		case 3:
			this.selSlot = value;
			break;
		}
	}
	
}
