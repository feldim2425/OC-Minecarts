package mods.ocminecart.common.driver;

import li.cil.oc.api.Driver;
import li.cil.oc.api.Items;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.ocminecart.common.component.CraftingUpgradeCC;
import mods.ocminecart.common.minecart.IComputerCart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DriverCraftingUpgrade implements Item, HostAware, EnvironmentAware {

	@Override
	public boolean worksWith(ItemStack stack) {
		if(stack!= null && Items.get("craftingUpgrade").createItemStack(1).isItemEqual(stack))
				return true;
		return false;
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		if(IComputerCart.class.isAssignableFrom(host.getClass())){
			return new CraftingUpgradeCC((IComputerCart) host);
		}
		return null;
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
		return null;
	}

	@Override
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		if(IComputerCart.class.isAssignableFrom(host)) return this.worksWith(stack);
		return false;
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		return CraftingUpgradeCC.class;
	}

}
