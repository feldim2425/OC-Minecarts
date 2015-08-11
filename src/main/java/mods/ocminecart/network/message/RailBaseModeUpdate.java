package mods.ocminecart.network.message;

import java.util.logging.Level;
import java.util.logging.Logger;

import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

//The response-message from Server to Client after GUI Button click in NetworkRailBase-GUI
//Sends the current connection mode to all near Players
public class RailBaseModeUpdate implements IMessage{
	
	public int dim;
	public int nMode;
	public int x;
	public int y;
	public int z;
	
	public RailBaseModeUpdate(){
		
	}
	
	public RailBaseModeUpdate(NetworkRailBaseTile tile, int Mode){
		dim=tile.getWorldObj().provider.dimensionId;
		nMode=tile.getMode();
		x=tile.xCoord;
		y=tile.yCoord;
		z=tile.zCoord;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		dim=buf.readInt();
		x=buf.readInt();
		y=buf.readInt();
		z=buf.readInt();
		nMode=buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dim);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(nMode);
	}
	
	public static class Handler implements IMessageHandler<RailBaseModeUpdate, IMessage>{

		@Override
		public IMessage onMessage(RailBaseModeUpdate message, MessageContext ctx) {
			World world=DimensionManager.getWorld(message.dim);
			if(world != null){
				TileEntity entity = world.getTileEntity(message.x, message.y, message.z);
				if(entity!=null){
					if(entity instanceof NetworkRailBaseTile){
						((NetworkRailBaseTile) entity).setMode(message.nMode);
					}
				}
			}
			return null;
		}
		
	}

}
