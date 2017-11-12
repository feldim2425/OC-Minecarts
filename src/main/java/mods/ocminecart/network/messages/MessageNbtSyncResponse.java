package mods.ocminecart.network.messages;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.network.ISyncObject;
import mods.ocminecart.network.SyncMessageType;
import mods.ocminecart.network.SynchronizedMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNbtSyncResponse implements IMessage {

	private int worldId;
	private BlockPos pos;
	private int entityId;
	private SyncMessageType type;
	private NBTTagCompound data;

	public MessageNbtSyncResponse() {

	}

	public MessageNbtSyncResponse(ISyncObject object) {
		data = new NBTTagCompound();
		object.writeSyncData(data);
		if (object instanceof TileEntity) {
			type = SyncMessageType.TILE_ENTITY;
			pos = ((TileEntity) object).getPos();
			worldId = ((TileEntity) object).getWorld().provider.getDimension();
		}
		else if (object instanceof Entity) {
			type = SyncMessageType.ENTITY;
			entityId = ((Entity) object).getEntityId();
			worldId = ((Entity) object).getEntityWorld().provider.getDimension();
		}
		else {
			throw new IllegalArgumentException("Only TileEntities and Entities are allowed for NBT Sync Request");
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		type = SyncMessageType.values()[buf.readByte()];
		worldId = buf.readInt();
		switch (type) {
			case ENTITY:
				entityId = buf.readInt();
				break;
			case TILE_ENTITY:
				pos = BlockPos.fromLong(buf.readLong());
				break;
		}
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte((byte) type.ordinal());
		buf.writeInt(worldId);
		switch (type) {
			case ENTITY:
				buf.writeInt(entityId);
				break;
			case TILE_ENTITY:
				buf.writeLong(pos.toLong());
				break;
		}
		ByteBufUtils.writeTag(buf, data);
	}

	public static class Handler extends SynchronizedMessageHandler<MessageNbtSyncResponse> {

		@Override
		protected void handleMessage(MessageNbtSyncResponse message, MessageContext ctx) {
			World world = Minecraft.getMinecraft().theWorld;
			if (message.worldId != world.provider.getDimension()) {
				return;
			}

			ISyncObject object = null;
			switch (message.type) {
				case ENTITY:
					Entity entity = world.getEntityByID(message.entityId);
					if (entity instanceof ISyncObject) {
						object = (ISyncObject) entity;
					}
					break;
				case TILE_ENTITY:
					TileEntity tileEntity = world.getTileEntity(message.pos);
					if (tileEntity instanceof ISyncObject) {
						object = (ISyncObject) tileEntity;
					}
					break;
			}

			if (object != null) {
				object.readSyncData(message.data);
			}
		}
	}
}
