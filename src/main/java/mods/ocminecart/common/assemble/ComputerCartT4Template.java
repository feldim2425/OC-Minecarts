package mods.ocminecart.common.assemble;

import java.util.ArrayList;

import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.common.Tier;
import mods.ocminecart.common.assemble.util.General;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

public class ComputerCartT4Template {
	
	private static final int MAX_COMPLEXITY = 9001;
	
	public static boolean select(ItemStack stack){
		return stack.getItem().equals(ModItems.item_ComputerCartCase) && stack.getItemDamage()==3;
	}
	
	public static Object[] validate(IInventory inventory){
		return General.validate(inventory, MAX_COMPLEXITY);
	}
	
	public static Object[] assemble(IInventory inventory){
		ArrayList<Pair<Integer, ItemStack>> comp = new ArrayList<Pair<Integer, ItemStack>>();
		int size = inventory.getSizeInventory();
		for(int i=1;i<size;i+=1){
			if(inventory.getStackInSlot(i)!=null) comp.add(Pair.of(i-1,inventory.getStackInSlot(i)));
		}
		
		ItemStack stack = new  ItemStack(ModItems.item_ComputerCart,1);
		ItemComputerCart.setTags(stack, comp , 3);
		return new Object[]{stack};
	}
	
	public static int[] getContainerTier(){
		return new int[]{2,2,2};
	}
	
	public static int[] getUpgradeTier(){
		return new int[]{2,2,2,2,2,2,2,2,2};
	}
	
	public static Iterable<Pair<String, Integer>> getComponentSlots(){
		ArrayList<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
		list.add(Pair.of(Slot.Card, 2));
		list.add(Pair.of(Slot.Card, 2));
		list.add(Pair.of(Slot.Card, 2));
		
		list.add(Pair.of(Slot.CPU, 2));
		list.add(Pair.of(Slot.Memory, 2));
		list.add(Pair.of(Slot.Memory, 2));
		
		list.add(Pair.of("eeprom", Tier.Any()));
		list.add(Pair.of(Slot.HDD, 2));
		return list;
	}
}
