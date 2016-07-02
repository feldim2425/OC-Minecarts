package mods.ocminecart.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.container.RemoteModuleContainer;
import mods.ocminecart.common.entityextend.RemoteCartExtender;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

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
			case 2:
				if(ctx.getServerHandler().playerEntity==null) break;
				Container c = ctx.getServerHandler().playerEntity.openContainer;
				if(c!=null && (c instanceof RemoteModuleContainer)){
					if(message.buttonid==0){
						RemoteCartExtender module = ((RemoteModuleContainer)c).getModule();
						String pw = message.dat.getString("password");
						int stat = (module.editableByPlayer(ctx.getServerHandler().playerEntity ,true)) ? 1 : 2;
						if(pw==null || pw.length()>10) stat=2;
						if(stat==1) module.setPassword(pw);
						((RemoteModuleContainer)c).sendPassState(ctx.getServerHandler().playerEntity, stat);
					}
					else if(message.buttonid==1){
						RemoteCartExtender module = ((RemoteModuleContainer)c).getModule();
						if(!module.editableByPlayer(ctx.getServerHandler().playerEntity ,true)) break;
						module.setLocked(!module.isLocked());
						if(module.isLocked()) ((RemoteModuleContainer)c).lockGui();
					}
					else if(message.buttonid==2){
						RemoteCartExtender module = ((RemoteModuleContainer)c).getModule();
						if(!module.editableByPlayer(ctx.getServerHandler().playerEntity ,false)) break;
						module.setEnabled(false);
						if(module.getRemoteItem()!=null && !ctx.getServerHandler().playerEntity.inventory.addItemStackToInventory(module.getRemoteItem()))
							module.dropItem();
					}
				}
				break;
			}
			return null;
		}
		
	}
}
