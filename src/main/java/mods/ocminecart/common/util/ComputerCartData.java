package mods.ocminecart.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ComputerCartData {

	private Map<Integer, ItemStack> components = new HashMap<Integer, ItemStack>();
	private int tier = -1;
	private double energy = -1;

	public void saveItemData(NBTTagCompound nbt) {
		if (nbt != null){
			NBTTagList list = new NBTTagList();
			Iterator<Entry<Integer, ItemStack>> items = components.entrySet().iterator();
			while(items.hasNext()){
				Entry<Integer,ItemStack> e = items.next();
				if(e.getValue()!=null){
					NBTTagCompound slot = new NBTTagCompound();
					slot.setTag("item", e.getValue().writeToNBT(new NBTTagCompound()));
					slot.setInteger("slot", e.getKey());
					list.appendTag(slot);
				}
			}
			
			nbt.setTag("componentinv", list);
			nbt.setInteger("tier", tier);
			nbt.setDouble("energy", energy);
		}
	}

	public void loadItemData(NBTTagCompound nbt) {
		if (nbt != null){
			if (nbt.hasKey("componentinv")) {
				NBTTagList list = (NBTTagList) nbt.getTag("componentinv");
				for (int i = 0; i < list.tagCount(); i += 1) {
					NBTTagCompound invslot = list.getCompoundTagAt(i);
					components.put(invslot.getInteger("slot"), ItemStack.loadItemStackFromNBT(invslot.getCompoundTag("item")));
				}
			}
			
			if(nbt.hasKey("energy")) this.energy = nbt.getDouble("energy");
			if(nbt.hasKey("tier")) this.tier = nbt.getInteger("tier");
		}
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public Map<Integer, ItemStack> getComponents() {
		return components;
	}

	public void setComponents(Map<Integer, ItemStack> components) {
		this.components = components;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}
}
