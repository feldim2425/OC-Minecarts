package mods.ocminecart.common.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.minecart.IComputerCart;

public class CraftingUpgradeCC extends ManagedEnvironment{
	
	private IComputerCart cart;
	private CraftingInventory cinv;
	
	public CraftingUpgradeCC (IComputerCart cart){
		super();
		this.cart=cart;
		this.cinv = new CraftingInventory();
		this.setNode(Network.newNode(this, Visibility.Network).withComponent("crafting").create());
	}
	
	@Callback(doc = "function([count:number]):number -- Tries to craft the specified number of items in the top left area of the inventory.")
	public Object[] craft(Context context, Arguments args){
	    int count = args.optInteger(0, Integer.MAX_VALUE);
	    return new Object[]{cinv.craft(count)};
	 }
	
	private class CraftingInventory extends InventoryCrafting{
		private int possibleAmount = 0;
		
		public CraftingInventory() {
			super( new Container(){
				@Override
				public boolean canInteractWith(EntityPlayer p_75145_1_) { return true; }
			}, 3, 3);
		}
		
		public int craft(int count){
			//TODO: Finish crafting
			return 0;
		}
		
		private void load(){
			IInventory hinv = CraftingUpgradeCC.this.cart.mainInventory();
			this.possibleAmount=Integer.MAX_VALUE;
			for(int slot=0;slot<this.getSizeInventory();slot++){
				
			}
		}
	}
	
}
