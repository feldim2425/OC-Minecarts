package mods.ocminecart.client.manual;


import li.cil.oc.api.Manual;
import li.cil.oc.api.prefab.ItemStackTabIconRenderer;
import li.cil.oc.api.prefab.ResourceContentProvider;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.item.ModItems;
import net.minecraft.item.ItemStack;

public class ManualRegister {
	public static void registermanual() {
		Manual.addProvider(new ResourceContentProvider(OCMinecart.MOD_ID, "doc/"));
		Manual.addProvider(new ManualPathProvider());

		Manual.addTab(new ItemStackTabIconRenderer(new ItemStack(ModItems.Items.COMPUTER_CART_CASE.get(), 1, 0)), "gui." + OCMinecart.MOD_ID + ".manual", OCMinecart.MOD_ID + "/%LANGUAGE%/index.md");

	}
}
