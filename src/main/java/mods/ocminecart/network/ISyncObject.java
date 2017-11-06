package mods.ocminecart.network;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncObject {

	void writeSyncData(NBTTagCompound data);

	void readSyncData(NBTTagCompound data);
}
