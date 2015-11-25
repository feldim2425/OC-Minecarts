package mods.ocminecart.common.driver;

import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import mods.ocminecart.common.component.LinkingUpgrade;
import mods.ocminecart.common.minecart.IComputerCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DriverLinkingUpgrade implements Item, HostAware, EnvironmentAware {

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		return LinkingUpgrade.class;
	}

	@Override
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		return IComputerCart.class.isAssignableFrom(host) && EntityMinecart.class.isAssignableFrom(host) && worksWith(stack);
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return false;
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack,EnvironmentHost host) {
		if(!worksWith(stack, host.getClass())) return null;
		return new LinkingUpgrade((IComputerCart) host);
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.Upgrade;
	}

	@Override
	public int tier(ItemStack stack) {
		return 0;
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
		return CustomDriver.dataTag(stack);
	}

}
