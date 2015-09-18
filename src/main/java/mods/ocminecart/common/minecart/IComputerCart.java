package mods.ocminecart.common.minecart;

import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.internal.Agent;
import li.cil.oc.api.internal.Tiered;
import li.cil.oc.api.network.Environment;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

public interface IComputerCart extends EnvironmentHost, Environment, Agent, Tiered, IInventory, IFluidHandler {
	
	//Get Number of Components
	int componentCount();
	
	//Returns Component in Slot
	Environment getComponentInSlot(int index);
	
	//Synchronize Slot with all near Players
	void synchronizeSlot(int slot);

}
