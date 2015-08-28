package mods.ocminecart.network.message;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

//Message from Client to send Server a GuiButton event if the Inventory is a Entity
public class GuiEntityButtonClick implements IMessage {

	public int enID;
	public int dimId;
	public int buttonid;
	
	public GuiEntityButtonClick(){}
	
	public GuiEntityButtonClick(Entity entity, int button){
		enID = entity.getEntityId();
		dimId = entity.worldObj.provider.dimensionId;
		buttonid = button;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		enID = buf.readInt();
		dimId = buf.readInt();
		buttonid = buf.readInt();
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(enID);
		buf.writeInt(dimId);
		buf.writeInt(buttonid);
	}
	
	public static class Handler implements IMessageHandler<GuiEntityButtonClick, IMessage>{

		@Override
		public IMessage onMessage(GuiEntityButtonClick message, MessageContext ctx) {
			Entity entity = DimensionManager.getWorld(message.dimId).getEntityByID(message.enID);
			if(entity!=null){
				if(entity instanceof ComputerCart && message.buttonid == 0) ((ComputerCart) entity).setRunning(!((ComputerCart) entity).getRunning());
			}
			return null;
		}
		
	}
}
