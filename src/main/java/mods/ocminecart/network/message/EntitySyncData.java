package mods.ocminecart.network.message;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.common.ISyncEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class EntitySyncData implements IMessage{
	
	protected int enID;
	protected int dimId;
	protected NBTTagCompound nbt;
		
	public EntitySyncData(){}
		
	public EntitySyncData(Entity entity){
		enID = entity.getEntityId();
		dimId = entity.worldObj.provider.dimensionId;
		if(entity instanceof ISyncEntity){
			nbt = new NBTTagCompound();
			((ISyncEntity) entity).writeSyncData(nbt);
		}
		else nbt = null;
	}
		
	@Override
	public void fromBytes(ByteBuf buf) {
		this.enID = buf.readInt();
		this.dimId = buf.readInt();
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.enID);
		buf.writeInt(this.dimId);
		ByteBufUtils.writeTag(buf, nbt);
	}
		
	public static class Handler implements IMessageHandler<EntitySyncData, IMessage>{

		@Override
		public IMessage onMessage(EntitySyncData message, MessageContext ctx) {
			if(message.nbt != null){	//If the nbt is null then the entity is not syncable and we dosn't need to handle this
				World world = Minecraft.getMinecraft().thePlayer.worldObj;
				if(world != null && world.provider.dimensionId == message.dimId){ // just to make sure that the player has not moved to an other dimension
					Entity entity = world.getEntityByID(message.enID);
					if(entity != null && (entity instanceof ISyncEntity)){
						((ISyncEntity) entity).readSyncData(message.nbt);
					}
				}
			}
			return null;
		}


	}

}
