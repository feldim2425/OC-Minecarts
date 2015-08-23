package mods.ocminecart.common.container.slots;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Container;
import li.cil.oc.common.Tier;
import mods.ocminecart.client.SlotIcons;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ContainerSlot extends Slot{
	
	private int tier;
	private String type;
	
	public ContainerSlot(IInventory inventory, int index, int xpos, int ypos, int tier, String type) {
		super(inventory, index, xpos, ypos);
		this.tier = tier;
		this.type = type;
	}
	
	public ContainerSlot(IInventory inventory, int index, int xpos, int ypos, ItemStack container) {
		super(inventory, index, xpos, ypos);
		
		Item driver = Driver.driverFor(container);
		if(driver!=null && (driver instanceof Container)){
			this.tier = ((Container) driver).providedTier(container);
			this.type = ((Container) driver).providedSlot(container);
		}
		else{
			this.tier = Tier.None();
			this.type = li.cil.oc.api.driver.item.Slot.None;
		}
	}
	
	 public IIcon getBackgroundIconIndex(){
		 return SlotIcons.fromTier(this.tier);
	 }
	 
	 public String getSlotType(){
		 return this.type;
	 }
}
