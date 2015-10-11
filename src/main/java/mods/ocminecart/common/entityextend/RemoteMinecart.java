package mods.ocminecart.common.entityextend;

import mods.ocminecart.OCMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;

public class RemoteMinecart extends RemoteCartExtender{

	@Override
	public void receivePacket(Packet packet, WirelessEndpoint sender) {
		
	}
	
	@Override
	public void init(Entity entity, World world){
		super.init(entity, world);
	}
	
	public void changeEnabled(){
		super.setEnabled(this.enabled);
		OCMinecart.logger.info(">>> State: "+this.enabled);
	}


}
