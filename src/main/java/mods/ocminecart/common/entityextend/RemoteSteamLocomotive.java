package mods.ocminecart.common.entityextend;

import java.util.List;

import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;
import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;

public class RemoteSteamLocomotive extends RemoteCartExtender {
	
	@Override
	protected void processCommand(String cmd, Object[] args){
		super.processCommand(cmd, args);
		if(cmd==null) return;
		EntityLocomotiveSteamSolid loco = (EntityLocomotiveSteamSolid) this.entity;
		if(cmd.equals("speed")){
			if(args.length>0 && args[0] instanceof Double){
				int speed = (int)(double)args[0];
				speed = -speed+3;
				speed = Math.max(0, Math.min(4, speed));
				loco.setSpeed(LocoSpeed.VALUES[speed]);
			}
			this.sendPacket(new Object[]{3-loco.getSpeed().ordinal()}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("mode")){
			if(args.length>0 && args[0] instanceof Double){
				int mode = (int)(double)args[0];
				mode = -mode+2;
				mode = Math.max(0, Math.min(2, mode));
				loco.setMode(LocoMode.VALUES[mode]);
			}
			this.sendPacket(new Object[]{2-loco.getMode().ordinal()}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("whistle")){
			loco.whistle();
		}
	}

	@Override
	protected List<String> getCommands() {
		List<String> cmds = super.getCommands();
		cmds.add("speed");
		cmds.add("mode");
		cmds.add("steam");
		cmds.add("heat");
		cmds.add("water");
		cmds.add("whistle");
		return cmds;
	}

	@Override
	protected String getDoc(String cmd) {
		String su = super.getDoc(cmd);
		if(su!=null) return su;
		
		return null;
	}
	
}
