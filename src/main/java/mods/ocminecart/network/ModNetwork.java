package mods.ocminecart.network;

import java.util.Iterator;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.network.message.ComputercartInventoryUpdate;
import mods.ocminecart.network.message.EntitySyncData;
import mods.ocminecart.network.message.EntitySyncRequest;
import mods.ocminecart.network.message.GuiButtonClick;
import mods.ocminecart.network.message.GuiEntityButtonClick;
import mods.ocminecart.network.message.UpdateRunning;
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
		
		channel.registerMessage(GuiButtonClick.Handler.class, GuiButtonClick.class, 0, Side.SERVER);
		channel.registerMessage(GuiEntityButtonClick.Handler.class, GuiEntityButtonClick.class, 1, Side.SERVER);
		channel.registerMessage(ComputercartInventoryUpdate.Handler.class, ComputercartInventoryUpdate.class, 2, Side.CLIENT);
		channel.registerMessage(EntitySyncRequest.Handler.class, EntitySyncRequest.class, 3, Side.SERVER);
		channel.registerMessage(EntitySyncData.Handler.class, EntitySyncData.class, 4, Side.CLIENT);
		channel.registerMessage(UpdateRunning.Handler.class, UpdateRunning.class, 5, Side.CLIENT);
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
