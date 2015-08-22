package mods.ocminecart.common.assemble.util;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Slot;
import net.minecraft.inventory.IInventory;

public class ComponentCheck {
	
	public static String checkRequiredComponents(IInventory inventory){
		boolean hasCPU=false, hasRAM=false;
		
		for(int i=0;i<inventory.getSizeInventory();i+=1){
			Item drv = Driver.driverFor(inventory.getStackInSlot(i));
			if(drv!=null){
				switch(drv.slot(inventory.getStackInSlot(i))){
				case Slot.CPU:
					hasCPU=true;
					break;
				case Slot.Memory:
					hasRAM=true;
					break;
				}
			}
		}
		
		if(!hasCPU) return("a CPU");
		if(!hasRAM) return("some Memory");
		return null;
	}
}
