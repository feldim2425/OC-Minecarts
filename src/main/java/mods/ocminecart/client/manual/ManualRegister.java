package mods.ocminecart.client.manual;

import li.cil.oc.api.Manual;
import li.cil.oc.api.prefab.ItemStackTabIconRenderer;
import li.cil.oc.api.prefab.ResourceContentProvider;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.item.ItemStack;

public class ManualRegister {
	
	public static void registermanual(){
		Manual.addProvider(new ResourceContentProvider(OCMinecart.MODID, "doc/"));
		Manual.addProvider(new ManualPathProvider());
		
		Manual.addTab(new ItemStackTabIconRenderer(new ItemStack(ModItems.item_ComputerCartCase,1,0)), "gui."+OCMinecart.MODID+".manual", OCMinecart.MODID+"/%LANGUAGE%/index.md");
	}
}
