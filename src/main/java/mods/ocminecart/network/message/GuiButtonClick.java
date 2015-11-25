package mods.ocminecart.network.message;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

//Message from Client to send Server a GuiButton event if the Inventory is a Entity
public class GuiButtonClick implements IMessage {

	public int uiID;
	public int buttonid;
	public NBTTagCompound dat;
	
	public GuiButtonClick(){}
	
	public static GuiButtonClick entityButtonClick(Entity e, int button, int guiID){
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("en", e.getEntityId());
		data.setInteger("dim", e.worldObj.provider.dimensionId);
		return new GuiButtonClick(guiID,button,data);
	}
	
	public static GuiButtonClick tileButtonClick(TileEntity entity, int button, int guiID){
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("x", entity.xCoord);
		data.setInteger("y", entity.yCoord);
		data.setInteger("z", entity.zCoord);
		data.setInteger("dim",entity.getWorldObj().provider.dimensionId);
		return new GuiButtonClick(guiID,button,data);
	}
	
	public GuiButtonClick(int guiID, int button, NBTTagCompound data){
		uiID = guiID;
		buttonid = button;
		dat = data;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		uiID = buf.readInt();
		buttonid = buf.readInt();
		if(buf.isReadable())
			dat = ByteBufUtils.readTag(buf);
		if(dat==null) dat = new NBTTagCompound();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(uiID);
		buf.writeInt(buttonid);
		if(dat!=null) ByteBufUtils.writeTag(buf, dat);
	}
	
	public static class Handler implements IMessageHandler<GuiButtonClick, IMessage>{

		@Override
		public IMessage onMessage(GuiButtonClick message, MessageContext ctx) {
			switch(message.uiID){
			case 0:
				TileEntity tile = DimensionManager.getWorld(message.dat.getInteger("dim")).getTileEntity(message.dat.getInteger("x"), 
						message.dat.getInteger("y"), message.dat.getInteger("z"));
				if(tile!=null){
					if(tile instanceof NetworkRailBaseTile) ((NetworkRailBaseTile) tile).onButtonPress(message.buttonid);
				}
				break;
			case 1:
				Entity entity = DimensionManager.getWorld(message.dat.getInteger("dim")).getEntityByID(message.dat.getInteger("en"));
				if(entity!=null){
					if(entity instanceof ComputerCart && message.buttonid == 0) ((ComputerCart) entity).setRunning(!((ComputerCart) entity).getRunning());
				}
				break;
			}
			return null;
		}
		
	}
}
