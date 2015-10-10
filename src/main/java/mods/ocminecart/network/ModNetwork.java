package mods.ocminecart.network;

import java.util.Iterator;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.network.message.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ModNetwork {
	
	public static SimpleNetworkWrapper channel;
	
	public static void init(){
		channel=NetworkRegistry.INSTANCE.newSimpleChannel(OCMinecart.MODID.toLowerCase());
		int id=-1;
		
		channel.registerMessage(GuiButtonClick.Handler.class, GuiButtonClick.class, id++, Side.SERVER);
		channel.registerMessage(GuiEntityButtonClick.Handler.class, GuiEntityButtonClick.class, id++, Side.SERVER);
		channel.registerMessage(ComputercartInventoryUpdate.Handler.class, ComputercartInventoryUpdate.class, id++, Side.CLIENT);
		channel.registerMessage(EntitySyncRequest.Handler.class, EntitySyncRequest.class, id++, Side.SERVER);
		channel.registerMessage(EntitySyncData.Handler.class, EntitySyncData.class, id++, Side.CLIENT);
		channel.registerMessage(UpdateRunning.Handler.class, UpdateRunning.class, id++, Side.CLIENT);
		channel.registerMessage(ItemUseMessage.Handler.class, ItemUseMessage.class, id++, Side.CLIENT);
	}
	
	public static void sendToNearPlayers(IMessage msg, TileEntity entity){
		sendToNearPlayers(msg,entity.xCoord, entity.yCoord, entity.zCoord, entity.getWorldObj());		
	}
	
	public static void sendToNearPlayers(IMessage msg, double x, double y, double z, World world){
		ServerConfigurationManager manager = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
		Iterator playerList = manager.playerEntityList.iterator();
		while(playerList.hasNext()){
			EntityPlayerMP player = (EntityPlayerMP) playerList.next();
			int serverview = manager.getViewDistance()*16;
			if(player.getDistance(x, y, z)<=serverview && world.provider.dimensionId == player.dimension){
				channel.sendTo(msg, player);
			}
		}
	}
}
