package mods.ocminecart.network.message;

import mods.ocminecart.common.ISyncEntity;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class EntitySyncRequest implements IMessage{
	
	protected int entityid;
	protected int dimid;
	
	public EntitySyncRequest(){}
	
	public EntitySyncRequest(int dim, int entity){
		this.entityid = entity;
		this.dimid = dim;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityid = buf.readInt();
		this.dimid = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.entityid);
		buf.writeInt(this.dimid);
	}
	
	public static class Handler implements IMessageHandler<EntitySyncRequest, IMessage>{

		@Override
		public IMessage onMessage(EntitySyncRequest message, MessageContext ctx) {
			World world = DimensionManager.getWorld(message.dimid);
			if(world!=null){
				Entity entity = world.getEntityByID(message.entityid);
				if( entity!=null && (entity instanceof ISyncEntity)){
					NBTTagCompound tag = new NBTTagCompound();
					((ISyncEntity) entity).writeSyncData(tag);
					return new EntitySyncData(message.dimid, message.entityid, tag);
				}
			}
			return null;
		}
		
	}
}
