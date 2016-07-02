package mods.ocminecart.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import li.cil.oc.api.machine.MachineHost;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class UpdateRunning implements IMessage{
	protected int enID;
	protected int dimId;
	protected boolean newVal;
		
	public UpdateRunning(){}
		
	public UpdateRunning(Entity entity, boolean newVal){
		this.enID = entity.getEntityId();
		this.dimId = entity.worldObj.provider.dimensionId;
		this.newVal = newVal;
	}
		
	@Override
	public void fromBytes(ByteBuf buf) {
		this.enID = buf.readInt();
		this.dimId = buf.readInt();
		this.newVal = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.enID);
		buf.writeInt(this.dimId);
		buf.writeBoolean(this.newVal);
	}
		
	public static class Handler implements IMessageHandler<UpdateRunning, IMessage>{

		@Override
		public IMessage onMessage(UpdateRunning message, MessageContext ctx) {
			World w = Minecraft.getMinecraft().thePlayer.worldObj;
			if(w!=null && w.provider.dimensionId == message.dimId){
				Entity e = w.getEntityByID(message.enID);
				if(e!=null && (e instanceof MachineHost)){
					if(e instanceof ComputerCart) ((ComputerCart)e).setRunning(message.newVal);
				}
			}
			return null;
		}


	}

}
