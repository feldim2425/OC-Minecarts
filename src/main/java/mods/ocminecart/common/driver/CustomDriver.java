package mods.ocminecart.common.driver;

import li.cil.oc.api.API;
import li.cil.oc.api.Driver;
import li.cil.oc.api.IMC;
import li.cil.oc.api.Items;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.server.component.UpgradeInventoryController;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.Settings;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* 
 * This is the best way I found to override the OC Item Drivers for the computer cart
 * without changing the Drivers for other Hosts (Computer, Robot, ...)
 */

public class CustomDriver {

	private static ArrayList<Item> drivers = new ArrayList<Item>();
	
	public static void init(){
		CustomDriver.registerNewDriver(new DriverInventoryController());
	}
	
	//Data tags for OC components
	public static NBTTagCompound dataTag(ItemStack stack){
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = stack.getTagCompound();
		if (!nbt.hasKey(Settings.OC_Namespace + "data")) {
			nbt.setTag(Settings.OC_Namespace + "data", new NBTTagCompound());
		}
		return nbt.getCompoundTag(Settings.OC_Namespace + "data");
	}
	
	//Add and get Drivers
	private static void registerNewDriver(Item driver){
		if(!CustomDriver.drivers.contains(driver)){
			CustomDriver.drivers.add(driver);
		}
	}
	
	public static Item driverFor(ItemStack stack, Class<? extends EnvironmentHost> host){
		Iterator<Item> drvlist = CustomDriver.drivers.iterator();
		while(drvlist.hasNext()){
			Item driver = drvlist.next();
			if((driver instanceof HostAware) && ((HostAware)driver).worksWith(stack, host)){
				return driver;
			}
		}
		return Driver.driverFor(stack, host);
	}
	
	public static Item driverFor(ItemStack stack){
		return Driver.driverFor(stack);
	}
	
}
