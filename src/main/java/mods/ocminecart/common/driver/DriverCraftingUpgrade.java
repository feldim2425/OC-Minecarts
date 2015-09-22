package mods.ocminecart.common.driver;

import li.cil.oc.api.Items;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.ocminecart.common.minecart.IComputerCart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DriverCraftingUpgrade implements Item, HostAware, EnvironmentAware {

	@Override
	public boolean worksWith(ItemStack stack) {
		if(Items.get("craftingUpgrade").item() == stack.getItem()) return true;
		return false;
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack,
			EnvironmentHost host) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slot(ItemStack stack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int tier(ItemStack stack) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		if(host.isAssignableFrom(IComputerCart.class)) return this.worksWith(stack);
		return false;
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		return null;
	}

}
