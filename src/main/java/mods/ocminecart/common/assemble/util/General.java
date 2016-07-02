package mods.ocminecart.common.assemble.util;

import mods.ocminecart.Settings;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.util.ComputerCartData;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.HashMap;

public class General {
	public static Object[] validate(IInventory inventory, int maxcomplexity){
		String need = ComponentCheck.checkRequiredComponents(inventory);
		if(need!=null){
			IChatComponent[] warnings = new IChatComponent[]{ new ChatComponentText(need+"!") };
			return new Object[]{false, new ChatComponentText(EnumChatFormatting.RED+need), warnings};
		}
		if(Complexity.calculate(inventory)>maxcomplexity){
			return new Object[]{false, new ChatComponentText(EnumChatFormatting.RED+"Complexity: "+Complexity.calculate(inventory)+" / "+maxcomplexity)};
		}
		return new Object[]{true, new ChatComponentText("Complexity: "+Complexity.calculate(inventory)+" / "+maxcomplexity)};
	}
	
	public static ItemStack createItemStack(int tier, HashMap<Integer,ItemStack> components){
		ItemStack stack = new  ItemStack(ModItems.item_ComputerCart,1);
		ComputerCartData data = new ComputerCartData();
		data.setComponents(components);
		data.setTier(tier);
		data.setEnergy(Settings.ComputerCartCreateEnergy);
		
		ItemComputerCart.setData(stack, data);
		return stack;
	}
	
	public static Object[] assemble(IInventory inventory, int tier){
		HashMap<Integer, ItemStack> comp = new HashMap<Integer, ItemStack>();
		int size = inventory.getSizeInventory();
		for(int i=1;i<size;i+=1){
			if(inventory.getStackInSlot(i)!=null) comp.put(i-1,inventory.getStackInSlot(i));
		}
		
		int energy = (tier<3) ? Settings.ComputerCartBaseCost+Settings.ComputerCartComplexityCost * Complexity.calculate(inventory) : 0;
		
		return new Object[]{General.createItemStack(tier, comp), energy};
	}
}
