package mods.ocminecart.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.items.ItemCartRemoteModule;
import net.minecraft.nbt.NBTTagCompound;

public class ConfigSyncMessage implements IMessage{
	
	public NBTTagCompound config = null;
	
	public ConfigSyncMessage(){}
	
	public ConfigSyncMessage(NBTTagCompound nbt){
		config = (nbt==null) ? new NBTTagCompound() : nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		config=ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, config);
	}
	
	public static class Handler implements IMessageHandler<ConfigSyncMessage, IMessage>{

		@Override
		public IMessage onMessage(ConfigSyncMessage msg, MessageContext ctx) {
			ItemCartRemoteModule.range = msg.config.getIntArray("remoterange");
			return null;
		}
		
	}
}
