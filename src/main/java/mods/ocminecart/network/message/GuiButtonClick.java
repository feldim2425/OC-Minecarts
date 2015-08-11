package mods.ocminecart.network.message;

import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

//Message from Client to send Server a GuiButton event
public class GuiButtonClick implements IMessage {
	
	public int posx;
	public int posy;
	public int posz;
	public int dimId;
	public int buttonid;
	
	public GuiButtonClick(){
		
	}
	
	public GuiButtonClick(TileEntity entity, int button){
		posx = entity.xCoord;
		posy = entity.yCoord;
		posz = entity.zCoord;
		dimId = entity.getWorldObj().provider.dimensionId;
		buttonid = button;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		posx = buf.readInt();
		posy = buf.readInt();
		posz = buf.readInt();
		dimId = buf.readInt();
		buttonid = buf.readInt();
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(posx);
		buf.writeInt(posy);
		buf.writeInt(posz);
		buf.writeInt(dimId);
		buf.writeInt(buttonid);
	}
	
	public static class Handler implements IMessageHandler<GuiButtonClick, IMessage>{

		@Override
		public IMessage onMessage(GuiButtonClick message, MessageContext ctx) {
			TileEntity tile = DimensionManager.getWorld(message.dimId).getTileEntity(message.posx, message.posy, message.posz);
			if(tile!=null){
				if(tile instanceof NetworkRailBaseTile) ((NetworkRailBaseTile) tile).onButtonPress(message.buttonid);
			}
			return null;
		}
		
	}

}
