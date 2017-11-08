package mods.ocminecart.network;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IEventContainer {

    void onClientEvent(NBTTagCompound eventData, EntityPlayerMP player);
}
