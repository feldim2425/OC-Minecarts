package mods.ocminecart.network.message;

import org.lwjgl.BufferUtils;

import mods.ocminecart.common.items.ItemCartRemoteModule;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ItemUseMessage implements IMessage {
	
	int id;
	int pentid;
	NBTTagCompound data;
	
	public ItemUseMessage(){}
	
	public ItemUseMessage(int id, int pentid, NBTTagCompound data){
		this.id = id;
		this.pentid = pentid;
		this.data = (data==null) ? new NBTTagCompound() : data;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.pentid = buf.readInt();
		this.data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeInt(this.pentid);
		ByteBufUtils.writeTag(buf, this.data);
	}
	
	public static class Handler implements IMessageHandler<ItemUseMessage, IMessage>{

		@Override
		public IMessage onMessage(ItemUseMessage message, MessageContext ctx) {
			Entity p= Minecraft.getMinecraft().theWorld.getEntityByID(message.pentid);
			if(!(p instanceof EntityPlayer)) return null;
			switch(message.id){
			case 0:
				((ItemCartRemoteModule)ModItems.item_CartRemoteModule).onMPUsage((EntityPlayer)p, message.data);
			}
			return null;
		}
	}

}
