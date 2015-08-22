package mods.ocminecart.common.util;

import li.cil.oc.api.Persistable;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.nbt.NBTTagCompound;

public class Plug implements Environment, Persistable{
	
	private IPlugable master;
	private Node netnode;
	
	public Plug(IPlugable master){
		this.master=master;
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

	@Override
	public void load(NBTTagCompound nbt) {
		NBTTagCompound ntag = nbt.getCompoundTag("node");
		
		if(this.netnode instanceof ComponentConnector) ((ComponentConnector)this.netnode).load(nbt);
		else if(this.netnode instanceof Connector) ((Connector)this.netnode).load(nbt);
		else if(this.netnode instanceof Component) ((Component)this.netnode).load(nbt);
		else this.netnode.save(ntag);
		
	}

	@Override
	public void save(NBTTagCompound nbt) {
		NBTTagCompound ntag = new NBTTagCompound();
		if(this.netnode instanceof ComponentConnector) ((ComponentConnector)this.netnode).save(nbt);
		else if(this.netnode instanceof Connector) ((Connector)this.netnode).save(nbt);
		else if(this.netnode instanceof Component) ((Component)this.netnode).save(nbt);
		else this.netnode.save(ntag);
		nbt.setTag("node", ntag);
	}
}
