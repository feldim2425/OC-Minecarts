package mods.ocminecart.network.messages;

import io.netty.buffer.ByteBuf;
import mods.ocminecart.network.IEventContainer;
import mods.ocminecart.network.SynchronizedMessageHandler;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGuiEvent implements IMessage {

    private int guiId;
    private NBTTagCompound data;

    public MessageGuiEvent(){
    }

    public MessageGuiEvent(IEventContainer container, NBTTagCompound dat){
        if(!(container instanceof Container)){
            throw new IllegalArgumentException("The Argument container has to extend Container!");
        }
        guiId = ((Container) container).windowId;
        data = dat.copy();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        guiId = buf.readInt();
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(guiId);
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler extends SynchronizedMessageHandler<MessageGuiEvent>{
        @Override
        protected void handleMessage(MessageGuiEvent message, MessageContext ctx) {
            Container container = ctx.getServerHandler().playerEntity.openContainer;
            if(container == null || container.windowId != message.guiId || !(container instanceof IEventContainer)){
                return;
            }
            ((IEventContainer) container).onClientEvent(message.data, ctx.getServerHandler().playerEntity);
        }
    }
}
