package mods.ocminecart.network.message;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ComputercartInventory implements IMessage{
	
	int entityid;
	int dimid;
	int slot;
	ItemStack stack;
	
	public ComputercartInventory(){}
	
	public ComputercartInventory(ComputerCart cart, int slot, ItemStack stack){
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
	
	public static class Handler implements IMessageHandler<ComputercartInventory, IMessage>{

		@Override
		public IMessage onMessage(ComputercartInventory message, MessageContext ctx) {
			World w = DimensionManager.getWorld(message.dimid);
			if(w != null){
				Entity e = w.getEntityByID(message.entityid);
				if(e != null && (e instanceof ComputerCart)){
					((ComputerCart)e).compinv.setInventorySlotContents(message.slot, message.stack);
				}
			}
			return null;
		}
		
	}

}
