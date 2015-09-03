package mods.ocminecart.common.component;

import li.cil.oc.api.API;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.nbt.NBTTagCompound;


public class ComputerCartController implements ManagedEnvironment{
	
	private Node node;
	private ComputerCart cart;
	
	public ComputerCartController(ComputerCart cart){
		this.cart = cart;
		node = API.network.newNode(this, Visibility.Neighbors).withComponent("computercart").create();
	}
	
	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onConnect(Node node) {
	}

	@Override
	public void onDisconnect(Node node) {
	}

	@Override
	public void onMessage(Message message) {}

	@Override
	public void load(NBTTagCompound nbt) {
		((Component)this.node).load(nbt);
	}

	@Override
	public void save(NBTTagCompound nbt) {
		((Component)this.node).save(nbt);
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void update() {}
	
	/*--------Component-Functions--------*/
	
	@Callback(doc="Testfunction to test Callback")
	public Object[] test(Context context, Arguments arguments){
		return new Object[]{"HI"};
	}

}
