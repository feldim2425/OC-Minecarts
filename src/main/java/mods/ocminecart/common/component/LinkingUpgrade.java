package mods.ocminecart.common.component;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.ocminecart.common.minecart.IComputerCart;

public class LinkingUpgrade extends ManagedEnvironment{
	
	private IComputerCart cart;
	
	public LinkingUpgrade (IComputerCart cart){
		super();
		this.cart=cart;
		this.setNode(Network.newNode(this, Visibility.Network).withComponent("linking").create());
	}
	
	@Callback(direct = true, doc = "function([index:number]):boolean,[string] -- Unlink cart (with the index) thats behind this cart")
	public Object[] unlinkBack(Context ctx, Arguments args){
		int cart = args.optInteger(0, 1);
		if(cart<1) throw new IllegalArgumentException("invalid index");
		return null;
	}
}
