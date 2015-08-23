package mods.ocminecart.common.container.slots;

import mods.ocminecart.client.SlotIcons;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;

public class ContainerSlot extends Slot{
	
	private int tier;
	private String type;
	
	public ContainerSlot(IInventory inventory, int index, int xpos, int ypos, int tier, String type) {
		super(inventory, index, xpos, ypos);
		this.tier = tier;
		this.type = type;
	}
	
	 public IIcon getBackgroundIconIndex(){
		 return SlotIcons.fromTier(this.tier);
	 }
	 
	 public String getSlotType(){
		 return this.type;
	 }
}
