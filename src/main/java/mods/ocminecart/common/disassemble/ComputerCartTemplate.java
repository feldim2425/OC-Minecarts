package mods.ocminecart.common.disassemble;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;

import li.cil.oc.common.Tier;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.item.ItemStack;

public class ComputerCartTemplate {
	
	public static boolean select(ItemStack stack){
		
		return stack.getItem()==ModItems.item_ComputerCart;
	}
	
	public static ItemStack[] disassemble(ItemStack stack, ItemStack[] ingredients){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();	
		if(stack.getItem()==ModItems.item_ComputerCart){
			if(ItemComputerCart.getTier(stack) < 3 && ItemComputerCart.getTier(stack)!=Tier.None()) 
				list.add(new ItemStack(ModItems.item_ComputerCartCase,1,ItemComputerCart.getTier(stack)));
			
			if(ItemComputerCart.getComponents(stack)!=null){
				Iterator<Pair<Integer, ItemStack>> comp = ItemComputerCart.getComponents(stack).iterator();
				while(comp.hasNext()){
					list.add(comp.next().getRight());
				}
			}
		}
		ItemStack[] items = new ItemStack[list.size()];
		for(int i=0;i<list.size();i+=1) items[i] = list.get(i);
		return items;
	}
}
