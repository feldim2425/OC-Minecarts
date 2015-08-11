package mods.ocminecart.common;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncEntity {
	
	public void writeSyncData(NBTTagCompound nbt);
	
	public void readSyncData(NBTTagCompound nbt);
	
}
