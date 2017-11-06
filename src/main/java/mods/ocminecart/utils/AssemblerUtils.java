package mods.ocminecart.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.common.Tier;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.driver.CustomDriverRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import java.util.Objects;

public class AssemblerUtils {

	public static String checkRequiredComponents(IInventory inventory){
		boolean hasCPU=false, hasRAM=false;

		for(int i=0;i<inventory.getSizeInventory();i+=1){
			Item drv = CustomDriverRegistry.driverFor(inventory.getStackInSlot(i));
			if(drv!=null){
				String type = drv.slot(inventory.getStackInSlot(i));
				switch (type){
					case Slot.CPU:
						hasCPU = true;
						break;
					case Slot.Memory:
						hasRAM = true;
						break;
				}
			}
		}

		if(!hasCPU) {
			return I18n.translateToLocal("gui."+ OCMinecart.MOD_ID+".assembler.insert_cpu");
		}
		else if(!hasRAM){
			return I18n.translateToLocal("gui."+ OCMinecart.MOD_ID+".assembler.insert_memory");
		}
		return null;
	}

	public static int calculateFromAssemblerInv(IInventory inventory){
		int complexity = 0;
		for(int i=1;i<inventory.getSizeInventory();i+=1){
			ItemStack stack = inventory.getStackInSlot(i);
			Item drv = CustomDriverRegistry.driverFor(stack);
			if(drv!=null && drv.tier(stack)!=Tier.None() && drv.tier(stack)!= Tier.Any() && !"eeprom".equals(drv.slot(stack))){
				complexity+=drv.tier(stack)+1;
			}
		}
		return complexity;
	}

	public static Object[] validate(IInventory inventory, int maxcomplexity){
		String need = checkRequiredComponents(inventory);
		if(need!=null){
			TextComponentBase[] warnings = new TextComponentBase[]{ new TextComponentString(need+"!") };
			return new Object[]{false, new TextComponentString(need), warnings};
		}

		int complexity = calculateFromAssemblerInv(inventory);
		if(complexity > maxcomplexity){
			return new Object[]{false, new TextComponentString(ChatFormatting.RED+"Complexity: "+complexity+" / "+maxcomplexity)};
		}
		return new Object[]{true, new TextComponentString("Complexity: "+complexity+" / "+maxcomplexity)};
	}

}
