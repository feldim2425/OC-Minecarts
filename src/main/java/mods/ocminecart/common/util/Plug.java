package mods.ocminecart.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import li.cil.oc.api.Persistable;
import li.cil.oc.api.detail.Builder;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;

public class Plug implements Environment, Persistable{
	
	private IPlugable master;
	private ForgeDirection[] sides;
	private Node netnode;
	
	public Plug(IPlugable master, ForgeDirection[] sides){
		this.master=master;
		this.sides=sides;
	}
	
	public void setNode(Node snode){
		this.netnode=snode;
	}
	
	@Override
	public Node node() {
		return this.netnode;
	}

	@Override
	public void onConnect(Node node) {
		master.onPlugConnect(this, node);
	}

	@Override
	public void onDisconnect(Node node) {
		master.onPlugDisconnect(this, node);
	}

	@Override
	public void onMessage(Message message) {
		master.onPlugMessage(this, message);
	}
	
	public IPlugable getMaster(){
		return this.master;
	}
	
	public ForgeDirection[] getSides(){
		return this.sides;
	}
	
	public boolean aktiveOnSide(ForgeDirection side){
		for(int i=0;i<sides.length;++i){
			if(sides[i].equals(side)) return true;
		}
		return false;
	}

	@Override
	public void load(NBTTagCompound nbt) {
		int numsides[] = nbt.getIntArray("sides");
		NBTTagCompound ntag = nbt.getCompoundTag("node");
		
		if(this.netnode instanceof ComponentConnector) ((ComponentConnector)this.netnode).load(nbt);
		else if(this.netnode instanceof Connector) ((Connector)this.netnode).load(nbt);
		else if(this.netnode instanceof Component) ((Component)this.netnode).load(nbt);
		else this.netnode.save(ntag);
		
	}

	@Override
	public void save(NBTTagCompound nbt) {
		nbt.setIntArray("sides", forgeToIntArray(sides));
		
		NBTTagCompound ntag = new NBTTagCompound();
		if(this.netnode instanceof ComponentConnector) ((ComponentConnector)this.netnode).save(nbt);
		else if(this.netnode instanceof Connector) ((Connector)this.netnode).save(nbt);
		else if(this.netnode instanceof Component) ((Component)this.netnode).save(nbt);
		else this.netnode.save(ntag);
		nbt.setTag("node", ntag);
	}
	
	private int[] forgeToIntArray(ForgeDirection[] fside){
		int[] iside = new int[fside.length];
		
		for(int i=0;i<fside.length;i+=1){
			iside[i] = fside[i].ordinal();
		}
		
		return iside;
	}
	
	private ForgeDirection[] intToForgeArray(int[] iside){
		ForgeDirection[] fside = new ForgeDirection[iside.length];
		
		for(int i=0;i<iside.length;i+=1){
			fside[i] = ForgeDirection.VALID_DIRECTIONS[iside[i]];
		}
		return fside;
	}

}
