package mods.ocminecart.network;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

abstract public class SynchronizedMessageHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {

	@Override
	public IMessage onMessage(REQ message, MessageContext ctx) {
		final IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
		if (thread.isCallingFromMinecraftThread()) {
			handleMessage(message, ctx);
		}
		else {
			thread.addScheduledTask(() -> handleMessage(message, ctx));
		}
		return getReplyMessage(message, ctx);
	}

	protected IMessage getReplyMessage(REQ message, MessageContext ctx) {
		return null;
	}

	protected abstract void handleMessage(final REQ message, final MessageContext ctx);
}
