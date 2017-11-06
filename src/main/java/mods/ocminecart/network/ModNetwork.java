package mods.ocminecart.network;


import mods.ocminecart.OCMinecart;
import mods.ocminecart.network.messages.MessageNbtSyncRequest;
import mods.ocminecart.network.messages.MessageNbtSyncResponse;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetwork {

	private static SimpleNetworkWrapper wrapper;

	public static void init() {
		wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(OCMinecart.MOD_ID);
		int id = 0;

		wrapper.registerMessage(MessageNbtSyncRequest.Handler.class, MessageNbtSyncRequest.class, ++id, Side.SERVER);
		wrapper.registerMessage(MessageNbtSyncResponse.Handler.class, MessageNbtSyncResponse.class, ++id, Side.CLIENT);
	}

	public static void sendToNearPlayers(IMessage msg, BlockPos pos, World world) {
		sendToNearPlayers(msg, pos.getX(), pos.getY(), pos.getZ(), world);
	}

	public static void sendToNearPlayers(IMessage msg, double x, double y, double z, World world) {
		PlayerList manager = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
		int serverview = manager.getViewDistance() * 16;
		wrapper.sendToAllAround(msg, new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, serverview + 1));
	}

	public static SimpleNetworkWrapper getWrapper() {
		return wrapper;
	}
}
