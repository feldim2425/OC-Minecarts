package mods.ocminecart.common.assemble.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class General {
	public static Object[] validate(IInventory inventory, int maxcomplexity){
		String need = ComponentCheck.checkRequiredComponents(inventory);
		if(need!=null){
			IChatComponent[] warnings = new IChatComponent[]{ new ChatComponentText("Insert "+need+"!") };
			return new Object[]{false, new ChatComponentText(EnumChatFormatting.RED+need), warnings};
		}
		if(Complexity.calculate(inventory)>maxcomplexity){
			return new Object[]{false, new ChatComponentText(EnumChatFormatting.RED+"Complexity: "+Complexity.calculate(inventory)+" / "+maxcomplexity)};
		}
		return new Object[]{true, new ChatComponentText("Complexity: "+Complexity.calculate(inventory)+" / "+maxcomplexity)};
	}
}
