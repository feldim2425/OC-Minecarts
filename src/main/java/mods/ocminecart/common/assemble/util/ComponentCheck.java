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
				String type = drv.slot(inventory.getStackInSlot(i));
				if(type == Slot.CPU) hasCPU=true;
				else if(type == Slot.Memory) hasRAM=true;
			}
		}
		
		if(!hasCPU) return("Insert a CPU");
		if(!hasRAM) return("Insert some Memory");
		return null;
	}
}
