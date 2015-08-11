package mods.ocminecart.common.assemble.util;

import java.util.ArrayList;
import java.util.Iterator;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.common.Tier;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Complexity {
	public static int calculate(Iterable<ItemStack> components){
		Iterator<ItemStack> list = components.iterator();
		int complexity = 0;
		while(list.hasNext()){
			ItemStack comp = list.next();
			Item drv = Driver.driverFor(comp);
			if(drv!=null && drv.tier(comp)!=Tier.None() && drv.tier(comp)!=Tier.Any() && drv.slot(comp)!="eeprom"){
				complexity+=drv.tier(comp)+1;
			}
		}
		
		return complexity;
	}
	
	public static int calculate(IInventory inventory){
		ArrayList<ItemStack> components = new ArrayList<ItemStack>();
		for(int i=1;i<inventory.getSizeInventory();i+=1){
			components.add(inventory.getStackInSlot(i));
		}
		return calculate(components);
	}
}
