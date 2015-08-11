package mods.ocminecart.client.gui.widget;

import java.util.Iterator;

import li.cil.oc.api.Items;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ComponentSlot extends Slot{
	
	protected EntityPlayer player;
	protected Container container;
	protected String slot;
	protected int tier;
	
	public ComponentSlot(IInventory inventory, int id, int x,int y, EntityPlayer player, Container container, int tier, String type) {
		super(inventory, id, x, y);
		this.container = container;
		this.player = player;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean func_111238_b(){
		return slot!= li.cil.oc.api.driver.item.Slot.None && super.func_111238_b() && tier!=-1;
	}
	
	public boolean isItemValid(ItemStack stack){
		return this.inventory.isItemValidForSlot(this.slotNumber, stack);
	}
	
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack){
		super.onPickupFromSlot(player, stack);
		Iterator slots = container.inventorySlots.iterator();
		while(slots.hasNext()){
			Slot slot = (Slot) slots.next();
			if(slot instanceof ComponentSlot){
				((ComponentSlot) slot).clearIfInvalid(player);
			}
		}
	}
	
	public void onSlotChanged(){
		super.onSlotChanged();
		Iterator slots = container.inventorySlots.iterator();
		while(slots.hasNext()){
			Slot slot = (Slot) slots.next();
			if(slot instanceof ComponentSlot){
				((ComponentSlot) slot).clearIfInvalid(player);
			}
		}
	}
	
	protected abstract void clearIfInvalid(EntityPlayer player);
}
