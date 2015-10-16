package mods.ocminecart.common.entityextend;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;
import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;
import mods.railcraft.common.fluids.Fluids;

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
		else if(cmd.equals("heat")){
			this.sendPacket(new Object[]{loco.boiler.getHeat()}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("steam")){
			FluidTankInfo[] info = loco.getTankInfo(ForgeDirection.UNKNOWN);
			for(int i=0;i<info.length;i+=1)
			{
				if(info[i].fluid != null && info[i].fluid.isFluidEqual(FluidRegistry.getFluidStack("steam", 1)))
				{
					this.sendPacket(new Object[]{info[i].fluid.amount,"steam"}, this.getRespPort(), this.getRespAddress());
					return;
				}
			}
			this.sendPacket(new Object[]{0,"steam"}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("water")){
			FluidTankInfo[] info = loco.getTankInfo(ForgeDirection.UNKNOWN);
			for(int i=0;i<info.length;i+=1)
			{
				if(info[i].fluid != null && info[i].fluid.isFluidEqual(new FluidStack(FluidRegistry.WATER,1)))
				{
					this.sendPacket(new Object[]{info[i].fluid.amount,"water"}, this.getRespPort(), this.getRespAddress());
					return;
				}
			}
			this.sendPacket(new Object[]{0 ,"water"}, this.getRespPort(), this.getRespAddress());
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
		
		if(cmd.equals("speed"))
			return "speed([value:number]):number -- set/get the speed. -1 reverse; 0 slowest; 3 max. speed";
		else if(cmd.equals("mode"))
			return "mode([value:number]):number -- set/get the mode. 0 shutdown; 1 idle; 2 running";
		else if(cmd.equals("whistle"))
			return "whistle() -- make the whistle sound";
		else if(cmd.equals("steam"))
			return "steam():number -- get Steam amount in mB.";
		else if(cmd.equals("water"))
			return "water():number -- get Water amount in mB.";
		else if(cmd.equals("heat"))
			return "heat():number -- get boiler heat";
		
		return null;
	}
	
}
