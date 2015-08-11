package mods.ocminecart.network.message;

import mods.ocminecart.common.ISyncEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class EntitySyncData implements IMessage{
	
	protected int dimid;
	protected int entityid;
	protected NBTTagCompound tag;
	
	public EntitySyncData(){}
	
	public EntitySyncData(int dim, int entity, NBTTagCompound nbt){
		this.entityid = entity;
		this.dimid = dim;
		this.tag = nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound data = ByteBufUtils.readTag(buf);
		this.dimid = data.getInteger("dimid");
		this.entityid = data.getInteger("entityid");
		this.tag = (NBTTagCompound) data.getTag("data");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("dimid", this.dimid);
		data.setInteger("entityid", this.entityid);
		data.setTag("data", this.tag);
		ByteBufUtils.writeTag(buf, data);
	}
	
	public static class Handler implements IMessageHandler<EntitySyncData, IMessage>{

		@Override
		public IMessage onMessage(EntitySyncData message, MessageContext ctx) {
			World world = DimensionManager.getWorld(message.dimid);
			if(world!=null){
				Entity entity = world.getEntityByID(message.entityid);
				if(entity instanceof ISyncEntity){
					((ISyncEntity) entity).readSyncData(message.tag);
				}
			}
			return null;
		}
		
	}
}
