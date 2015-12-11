package mods.ocminecart.common.entityextend;

import java.util.List;

import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.carts.EntityLocomotiveElectric;

public class RemoteElectricLocomotive extends RemoteCartExtender{
	@Override
	protected void processCommand(String cmd, Object[] args){
		super.processCommand(cmd, args);
		if(cmd==null) return;
		EntityLocomotiveElectric loco = (EntityLocomotiveElectric) this.entity;
		if(cmd.equals("speed")){
			if(args.length>0 && args[0] instanceof Double){
				int speed = (int)(double)(Double)args[0];
				speed = -speed+3;
				speed = Math.max(0, Math.min(4, speed));
				loco.setSpeed(LocoSpeed.VALUES[speed]);
			}
			this.sendPacket(new Object[]{3-loco.getSpeed().ordinal()}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("mode")){
			if(args.length>0 && args[0] instanceof Double){
				int mode = (int)(double)(Double)args[0];
				mode = (mode<=0) ? 2 : 0;
				mode = Math.max(0, Math.min(2, mode));
				loco.setMode(LocoMode.VALUES[mode]);
			}
			this.sendPacket(new Object[]{ (loco.getMode().ordinal()==2)? 0 : 1}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("whistle")){
			loco.whistle();
		}
		else if(cmd.equals("energy")){
			this.sendPacket(new Object[]{loco.getChargeHandler().getCharge()}, this.getRespPort(), this.getRespAddress());
		}
	}

	@Override
	protected List<String> getCommands() {
		List<String> cmds = super.getCommands();
		cmds.add("speed");
		cmds.add("mode");
		cmds.add("whistle");
		cmds.add("energy");
		return cmds;
	}

	@Override
	protected String getDoc(String cmd) {
		String su = super.getDoc(cmd);
		if(su!=null) return su;
		
		if(cmd.equals("speed"))
			return "speed([value:number]):number -- set/get the speed. -1 reverse; 0 slowest; 3 max. speed";
		else if(cmd.equals("mode"))
			return "mode([value:number]):number -- set/get the mode. 0 shutdown; 1 running";
		else if(cmd.equals("whistle"))
			return "whistle() -- make the whistle sound";
		else if(cmd.equals("energy"))
			return "energy():number -- get the stored energy";
		
		
		return null;
	}
}
