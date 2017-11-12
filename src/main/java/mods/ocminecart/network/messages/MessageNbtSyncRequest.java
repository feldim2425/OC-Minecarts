package mods.ocminecart.network.messages;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.network.ISyncObject;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.SyncMessageType;
import mods.ocminecart.network.SynchronizedMessageHandler;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNbtSyncRequest implements IMessage {

	private int worldId;
	private BlockPos pos;
	private int entityId;
	private SyncMessageType type;

	public MessageNbtSyncRequest() {

	}

	public MessageNbtSyncRequest(ISyncObject object) {
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
	}

	public static class Handler extends SynchronizedMessageHandler<MessageNbtSyncRequest> {

		@Override
		protected void handleMessage(MessageNbtSyncRequest message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.getEntityWorld();
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
				ModNetwork.getWrapper().sendTo(new MessageNbtSyncResponse(object), ctx.getServerHandler().playerEntity);
			}
		}
	}
}
