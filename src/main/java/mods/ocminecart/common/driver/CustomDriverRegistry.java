package mods.ocminecart.common.driver;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.network.EnvironmentHost;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class CustomDriverRegistry {
	public final static String OC_DATATAG = "oc:data";
	private static List<Item> drivers = new ArrayList<Item>();

	public static void init() {

	}

	public static NBTTagCompound dataTag(ItemStack stack) {
		return stack.getSubCompound(OC_DATATAG, true);
	}

	public static NBTTagCompound dataTag(Item driver, ItemStack stack) {
		NBTTagCompound tag = null;
		if (driver != null) {
			tag = driver.dataTag(stack);
		}
		if (tag == null) {
			tag = dataTag(stack);
		}
		return tag;
	}

	private static void registerNewDriver(Item driver) {
		if (!drivers.contains(driver)) {
			drivers.add(driver);
		}
	}

	public static Item driverFor(ItemStack stack, Class<? extends EnvironmentHost> host) {
		for (Item driver : drivers) {
			if ((driver instanceof HostAware) && ((HostAware) driver).worksWith(stack, host)) {
				return driver;
			}
		}
		return Driver.driverFor(stack, host);
	}

	public static Item driverFor(ItemStack stack) {
		return Driver.driverFor(stack);
	}
}
