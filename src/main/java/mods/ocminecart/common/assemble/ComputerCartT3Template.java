package mods.ocminecart.common.assemble;

import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.common.Tier;
import mods.ocminecart.common.assemble.util.General;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class ComputerCartT3Template {
	
	private static final int MAX_COMPLEXITY = 20;
	
	public static boolean select(ItemStack stack){
		return stack.getItem().equals(ModItems.item_ComputerCartCase) && stack.getItemDamage()==2;
	}
	
	public static Object[] validate(IInventory inventory){
		return General.validate(inventory, MAX_COMPLEXITY);
	}
	
	public static Object[] assemble(IInventory inventory){
		return General.assemble(inventory, 2);
	}
	
	public static int[] getContainerTier(){
		return new int[]{2,1,1};
	}
	
	public static int[] getUpgradeTier(){
		return new int[]{2,2,2,1,1,1,0,0,0};
	}
	
	public static Iterable<Pair<String, Integer>> getComponentSlots(){
		ArrayList<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
		list.add(Pair.of(Slot.Card, 2));
		list.add(Pair.of(Slot.Card, 1));
		list.add(Pair.of(Slot.Card, 1));
		
		list.add(Pair.of(Slot.CPU, 2));
		list.add(Pair.of(Slot.Memory, 2));
		list.add(Pair.of(Slot.Memory, 2));
		
		list.add(Pair.of("eeprom", Tier.Any()));
		list.add(Pair.of(Slot.HDD, 2));
		return list;
	}
}
