package mods.ocminecart.common.tanks;

import li.cil.oc.api.internal.MultiTank;
import li.cil.oc.api.network.Environment;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.utils.ItemStackUtil;
import mods.ocminecart.utils.NBTTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ComputerCartMultiTank implements MultiTank {

	private EntityComputerCart cart;
	private List<IFluidTank> tanks;
	private int selectedTank;

	public ComputerCartMultiTank(EntityComputerCart cart) {
		this.cart = cart;
		this.tanks = new ArrayList<>();
	}

	@Override
	public int tankCount() {
		return tanks.size();
	}

	@Override
	public IFluidTank getFluidTank(int index) {
		if(checkIndex(index)){
			return tanks.get(index);
		}
		return null;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("selected", selectedTank);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("selected", NBTTypes.INT.getTypeID())) {
			selectedTank = nbt.getInteger("selected");
		}
	}

	public void setSelectedTank(int selectedTank) {
		this.selectedTank = selectedTank;
	}

	public int getSelectedTank() {
		return selectedTank;
	}

	public void reloadTanks(){
		List<IFluidTank> tanks = new LinkedList<>();
		for(int i=0;i<cart.getComponentInventory().getSizeInventory();i++)
		{
			Environment env = cart.getComponentInventory().getComponent(i);
			if(env instanceof IFluidTank){
				tanks.add((IFluidTank) env);
			}
		}
		this.tanks.clear();
		this.tanks.addAll(tanks);
	}

	public boolean checkIndex(int index){
		return index >= 0 && index < tanks.size();
	}
}
