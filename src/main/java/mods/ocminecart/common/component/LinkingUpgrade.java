package mods.ocminecart.common.component;

import java.util.HashMap;

import net.minecraft.entity.item.EntityMinecart;
import cpw.mods.fml.common.Loader;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.ocminecart.Settings;
import mods.ocminecart.common.minecart.IComputerCart;
import mods.ocminecart.interaction.railcraft.RailcraftUtils;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;

public class LinkingUpgrade extends ManagedEnvironment{
	
	private IComputerCart cart;
	
	public LinkingUpgrade (IComputerCart cart){
		super();
		this.cart=cart;
		this.setNode(Network.newNode(this, Visibility.Network).withComponent("cartlinking").create());
	}
	
	@Callback(doc = "function([index:number]):boolean,[string] -- Unlink cart (with the index) behind this cart")
	public Object[] unlinkBack(Context ctx, Arguments args){
		checkRailcraft();
		int cindex = args.optInteger(0, 1);
		if(!hasEnergy(Settings.LinkingUnlinkCost)) return new Object[]{false, "not enougth energy"};
		Object[] res = unlink0(cindex,false);
		if(res[0].equals(true))takeEnergy(Settings.LinkingUnlinkCost);
		ctx.pause(Settings.LinkingUnlinkDelay);
		return res;
	}
	
	@Callback(doc = "function([index:number]):boolean,[string] -- Unlink cart (with the index) in front of this cart")
	public Object[] unlinkFront(Context ctx, Arguments args){
		checkRailcraft();
		int cindex = args.optInteger(0, 1);
		if(!hasEnergy(Settings.LinkingUnlinkCost)) return new Object[]{false, "not enougth energy"};
		Object[] res = unlink0(cindex,true);
		if(res[0].equals(true))takeEnergy(Settings.LinkingUnlinkCost);
		ctx.pause(Settings.LinkingUnlinkDelay);
		return res;
	}
	
	@Callback(doc = "function():number -- Link a cart behind the chain")
	public Object[] linkBack(Context ctx, Arguments args){
		checkRailcraft();
		if(!hasEnergy(Settings.LinkingLinkCost)) return new Object[]{false, "not enougth energy"};
		Object[] res = link0(false);
		if(res[0].equals(true))takeEnergy(Settings.LinkingLinkCost);
		ctx.pause(Settings.LinkingLinkDelay);
		return res;
	}
	
	@Callback(doc = "function():number -- Link a cart in front of the chain")
	public Object[] linkFront(Context ctx, Arguments args){
		checkRailcraft();
		if(!hasEnergy(Settings.LinkingLinkCost)) return new Object[]{false, "not enougth energy"};
		Object[] res = link0(true);
		if(res[0].equals(true))takeEnergy(Settings.LinkingLinkCost);
		ctx.pause(Settings.LinkingLinkDelay);
		return res;
	}
	
	@Callback(doc = "function():number -- Count carts behind this cart.")
	public Object[] countBack(Context ctx, Arguments args){
		checkRailcraft();
		return count0(false);
	}
	
	@Callback(doc = "function():number -- Count carts in front of this cart.")
	public Object[] countFront(Context ctx, Arguments args){
		checkRailcraft();
		return count0(true);
	}
	
	private boolean hasEnergy(double e){
		if(!(cart.node()instanceof Connector)) return false;
		Connector c = ((Connector)cart.node());
		return c.globalBuffer()>=e;
	}
	
	private void takeEnergy(double e){
		if(!(cart.node()instanceof Connector)) return;
		Connector c = ((Connector)cart.node());
		c.changeBuffer(-e);
	}
	
	private Object[] link0(boolean front){
		EntityMinecart ccart = RailcraftUtils.getConnectedCartSide((EntityMinecart)cart, front);
		
		HashMap<Integer, EntityMinecart> map = RailcraftUtils.sortCarts((EntityMinecart) cart);
		int dir = RailcraftUtils.getCartCountDir(map, ccart);
		int count = RailcraftUtils.countCarts((EntityMinecart) this.cart, ccart)-1;
		
		
		EntityMinecart linkcart1 = map.get(dir*count); 
		if(linkcart1==null) return new Object[]{false, "internal error"};
		
		double dyaw = 0D;
		if(!linkcart1.equals(cart)){
			if(RailcraftUtils.isCartInField(linkcart1, map.get(dir*(count-1)), 179, 0)){	//If the cart looks to the row, look at the other side
				dyaw+=180D;
			}
		}
		else if(!front)dyaw+=180D;
		
		while(dyaw>=360D) dyaw-=360D;
		while(dyaw<0D) dyaw+=360D;

		EntityMinecart linkcart2 = RailcraftUtils.findLinkableCart(linkcart1, dyaw);
		if(linkcart2==null) return new Object[]{false, "No cart in range"};
		ILinkageManager link = CartTools.linkageManager;
		boolean done = link.createLink(linkcart1, linkcart2);
		if(!done) return new Object[]{false, "No cart in range"};
		return new Object[]{true};
	}
	
	private Object[] count0(boolean front){
		int count;
		EntityMinecart c = RailcraftUtils.getConnectedCartSide((EntityMinecart)cart, front);
		if(c!=null)
			count=RailcraftUtils.countCarts((EntityMinecart) cart,c)-1;
		else
			count=0;
		return new Object[]{count};
	}
	
	private Object[] unlink0(int cindex, boolean front){
		if(cindex<1) throw new IllegalArgumentException("invalid index");
		
		EntityMinecart c = RailcraftUtils.getConnectedCartSide((EntityMinecart)cart, front);
		if(c==null) return new Object[]{false, "cart not found"};
		
		HashMap<Integer, EntityMinecart> map = RailcraftUtils.sortCarts((EntityMinecart) cart);
		int dir = RailcraftUtils.getCartCountDir(map, c);
		
		if(!map.containsKey((cindex-1)*dir) || !map.containsKey(cindex*dir)) return new Object[]{false, "cart not found"};
		
		ILinkageManager link = CartTools.linkageManager;
		link.breakLink(map.get(cindex*dir), map.get((cindex-1)*dir));
		
		return new Object[]{true};
	}
	
	private void checkRailcraft(){
		if(!Loader.isModLoaded("Railcraft"))
			throw new IllegalArgumentException("Railcraft is not installed.");
	}
}
