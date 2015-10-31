package mods.ocminecart.common.driver;

import li.cil.oc.api.Driver;
import li.cil.oc.api.Items;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.server.component.UpgradeTankController;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.minecart.IComputerCart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DriverTankController implements Item, HostAware, EnvironmentAware{
	
	@Override
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		if(IComputerCart.class.isAssignableFrom(host)) return this.worksWith(stack);
		return false;
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		if(stack!= null && Items.get("tankControllerUpgrade").createItemStack(1).isItemEqual(stack)){
			return true;
		}
		return false;
	}

	@Override
	public String slot(ItemStack stack) {
		return Driver.driverFor(stack).slot(stack);
	}

	@Override
	public int tier(ItemStack stack) {
		return Driver.driverFor(stack).tier(stack);
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
		return CustomDriver.dataTag(stack);
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		if(stack!= null && Items.get("inventoryControllerUpgrade").createItemStack(1).isItemEqual(stack))
			return UpgradeTankController.Drone.class;
		return null;
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		//I can use the Component for the Drone because it just require a Agent
		if(host instanceof ComputerCart) 
			return new UpgradeTankController.Drone((ComputerCart)host);
		return null;
	}
}
