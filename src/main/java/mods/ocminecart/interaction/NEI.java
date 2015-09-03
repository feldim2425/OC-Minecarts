package mods.ocminecart.interaction;

import li.cil.oc.integration.Mods;
import codechicken.nei.LayoutManager;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class NEI {
	public static ItemStack hoveredStack(GuiContainer container , int mx, int my){
		if(Loader.isModLoaded("NotEnoughItems")){
			try{
				return LayoutManager.instance().getStackUnderMouse(container, mx, my);
			}
			catch(Throwable e){
				return null;
			}
		}
		else
			return null;
	}
	
	public static boolean isInputFocused(){
		if(Loader.isModLoaded("NotEnoughItems")){
			try{
				return LayoutManager.getInputFocused()!=null;
			}
			catch(Throwable e){
				return false;
			}
		}
		else
			return false;
	}
}
