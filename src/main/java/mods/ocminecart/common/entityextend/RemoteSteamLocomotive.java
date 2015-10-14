package mods.ocminecart.common.entityextend;

import java.util.List;

import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;

public class RemoteSteamLocomotive extends RemoteCartExtender {
	
	@Override
	protected void processCommand(String cmd, Object[] args){
		super.processCommand(cmd, args);
		
	}

	@Override
	protected List<String> getCommands() {
		List<String> cmds = super.getCommands();
		cmds.add("speed");
		cmds.add("steam");
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
