package mods.ocminecart.common.container;

import java.util.Iterator;

import li.cil.oc.api.component.TextBuffer;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.ocminecart.common.container.slots.ContainerSlot;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ComputerCartContainer extends Container {
	
	public static final int YSIZE_SCR = 256;
	public static final int YSIZE_NOSCR = 108;
	public static final int XSIZE = 256;
	public static final int DELTA = YSIZE_SCR - YSIZE_NOSCR; 
	
	private ComputerCart entity;
	private boolean hasScreen=false;
	
	
	public ComputerCartContainer(InventoryPlayer inventory,ComputerCart entity) {
		this.entity=entity;
		
		this.initComponents(this.entity.getCompinv().getComponents());
		
		this.addSlotToContainer(new ContainerSlot(entity.compinv, 20 , 188,232 - ((this.hasScreen) ? 0 : DELTA), entity.compinv.getContainer(0)));
		this.addSlotToContainer(new ContainerSlot(entity.compinv, 21 , 206,232 - ((this.hasScreen) ? 0 : DELTA), entity.compinv.getContainer(1)));
		this.addSlotToContainer(new ContainerSlot(entity.compinv, 22 , 224,232 - ((this.hasScreen) ? 0 : DELTA), entity.compinv.getContainer(2)));
		
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
		for(int i=0;i<3;i++){
			for(int j=0;j<9;j++){
				this.addSlotToContainer(new Slot(inventory, j+i*9+9, x+j*18, y+i*18));
			}
		}
		
		for(int i=0;i<9;i++){
			this.addSlotToContainer(new Slot(inventory, i, x+i*18, y+58));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		return null;
	}
	
	public ComputerCart getEntity(){
		return this.entity;
	}
	
	public boolean getHasScreen(){
		return this.hasScreen;
	}
}
