package mods.ocminecart.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ComputercartInventoryUpdate implements IMessage{
	
	int entityid;
	int dimid;
	int slot;
	ItemStack stack;
	
	public ComputercartInventoryUpdate(){}
	
	public ComputercartInventoryUpdate(ComputerCart cart, int slot, ItemStack stack){
		this.entityid = cart.getEntityId();
		this.dimid = cart.worldObj.provider.dimensionId;
		this.slot = slot;
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityid = buf.readInt();
		this.dimid = buf.readInt();
		this.slot = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.entityid);
		buf.writeInt(this.dimid);
		buf.writeInt(this.slot);
		ByteBufUtils.writeItemStack(buf, stack);
	}
	
	public static class Handler implements IMessageHandler<ComputercartInventoryUpdate, IMessage>{

		@Override
		public IMessage onMessage(ComputercartInventoryUpdate message, MessageContext ctx) {
			World w = Minecraft.getMinecraft().thePlayer.worldObj;
			if(w != null && w.provider.dimensionId == message.dimid){
				Entity e = w.getEntityByID(message.entityid);
				if(e != null && (e instanceof ComputerCart)){
					((ComputerCart)e).compinv.setInventorySlotContents(message.slot, message.stack);
				}
			}
			return null;
		}
		
	}

}
