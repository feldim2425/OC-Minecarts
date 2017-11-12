package mods.ocminecart.common.item.data;

import mods.ocminecart.utils.NBTTypes;
import mods.ocminecart.utils.NbtUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public class DataComputerCart {

	private Map<Integer, ItemStack> components = new HashMap<>();
	private int tier = 0;
	private float energy = 0.0f;

	public DataComputerCart() {

	}

	public static DataComputerCart loadFromNBT(NBTTagCompound nbt) {
		DataComputerCart data = new DataComputerCart();
		if (nbt.hasKey("tier", NBTTypes.INT.getTypeID())) {
			data.setTier(nbt.getInteger("tier"));
		}

		if (nbt.hasKey("energy", NBTTypes.FLOAT.getTypeID())) {
			data.setEnergy(nbt.getFloat("energy"));
		}

		if (nbt.hasKey("components", NBTTypes.TAG_LIST.getTypeID())) {
			NBTTagList list = nbt.getTagList("components", NBTTypes.TAG_COMPOUND.getTypeID());
			Map<Integer, ItemStack> components = new HashMap<>();
			for (NBTBase slotTag : NbtUtil.toArrayList(list)) {
				components.put(((NBTTagCompound) slotTag).getInteger("slot"), ItemStack.loadItemStackFromNBT(((NBTTagCompound) slotTag).getCompoundTag("item")));
			}
			data.setComponents(components);
		}
		return data;
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

	public void setComponents(Map<Integer, ItemStack> map) {
		components.clear();
		components.putAll(map);
	}

	public float getEnergy() {
		return energy;
	}

	public void setEnergy(float energy) {
		this.energy = energy;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("tier", this.tier);
		nbt.setFloat("energy", this.energy);

		NBTTagList compList = new NBTTagList();
		for (Map.Entry<Integer, ItemStack> component : this.components.entrySet()) {
			NBTTagCompound slotTag = new NBTTagCompound();
			NBTTagCompound itemTag = new NBTTagCompound();
			component.getValue().writeToNBT(itemTag);
			slotTag.setTag("item", itemTag);
			slotTag.setInteger("slot", component.getKey());
			compList.appendTag(slotTag);
		}
		nbt.setTag("components", compList);
	}
}
