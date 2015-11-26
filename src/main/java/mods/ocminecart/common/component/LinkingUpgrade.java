package mods.ocminecart.common.component;

import java.util.HashMap;

import net.minecraft.entity.item.EntityMinecart;
import cpw.mods.fml.common.Loader;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
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
		return unlink0(cindex,false);
	}
	
	@Callback(doc = "function([index:number]):boolean,[string] -- Unlink cart (with the index) in front of this cart")
	public Object[] unlinkFront(Context ctx, Arguments args){
		checkRailcraft();
		int cindex = args.optInteger(0, 1);
		return unlink0(cindex,true);
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
