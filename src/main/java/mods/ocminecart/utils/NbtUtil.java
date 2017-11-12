package mods.ocminecart.utils;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class NbtUtil {

	private NbtUtil() {

	}

	public static NBTTagCompound getOrCreateTag(NBTTagCompound nbt) {
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		return nbt;
	}

	public static NBTTagCompound getOrCreateTag(ItemStack stack) {
		NBTTagCompound nbt = getOrCreateTag(stack.getTagCompound());
		stack.setTagCompound(nbt);
		return nbt;
	}

	public static void clearTag(NBTTagCompound nbt) {
		for (String key : new HashSet<>(nbt.getKeySet())) {
			nbt.removeTag(key);
		}
	}

	public static List<NBTBase> toArrayList(NBTTagList tagList) {
		ArrayList<NBTBase> list = new ArrayList<>(tagList.tagCount());
		for (int i = 0; i < tagList.tagCount(); i++) {
			list.add(tagList.get(i));
		}
		return list;
	}
}
