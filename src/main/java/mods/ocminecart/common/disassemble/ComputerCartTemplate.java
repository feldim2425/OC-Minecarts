package mods.ocminecart.common.disassemble;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import li.cil.oc.common.Tier;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.util.ComputerCartData;
import net.minecraft.item.ItemStack;

public class ComputerCartTemplate {
	
	public static boolean select(ItemStack stack){
		
		return stack.getItem()==ModItems.item_ComputerCart;
	}
	
	public static ItemStack[] disassemble(ItemStack stack, ItemStack[] ingredients){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();	
		
		ComputerCartData data = ItemComputerCart.getData(stack);
		
		if(stack.getItem()==ModItems.item_ComputerCart){
			if(data.getTier() < 3 && data.getTier() !=Tier.None()) 
				list.add(new ItemStack(ModItems.item_ComputerCartCase,1,data.getTier()));
			
			if(data.getComponents()!=null){
				Iterator<Entry<Integer, ItemStack>> comp = data.getComponents().entrySet().iterator();
				while(comp.hasNext()){
					list.add(comp.next().getValue());
				}
			}
		}
		ItemStack[] items = new ItemStack[list.size()];
		for(int i=0;i<list.size();i+=1) items[i] = list.get(i);
		return items;
	}
}
