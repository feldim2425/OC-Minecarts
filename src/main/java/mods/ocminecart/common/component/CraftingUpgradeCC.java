package mods.ocminecart.common.component;

import cpw.mods.fml.common.FMLCommonHandler;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.ocminecart.common.minecart.IComputerCart;
import mods.ocminecart.common.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import java.util.ArrayList;

/*
 * Copy from li.cil.oc.server.component.CraftingUpgrade
 */
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
	    return cinv.craft(count);
	 }
	
	private class CraftingInventory extends InventoryCrafting{
		private int possibleAmount = 0;
		
		public CraftingInventory() {
			super( new Container(){
				@Override
				public boolean canInteractWith(EntityPlayer p_75145_1_) { return true; }
			}, 3, 3);
		}
		
		public Object[] craft(int wcount){
			load();
			CraftingManager craft = CraftingManager.getInstance();
			int ccount = 0;
			boolean valid = craft.findMatchingRecipe(this, CraftingUpgradeCC.this.cart.world()) != null;
			if(valid){
				while(ccount<wcount){
					ItemStack result = craft.findMatchingRecipe(this, CraftingUpgradeCC.this.cart.world());
					if(result==null || result.stackSize < 1) break;
					ccount += result.stackSize;
					FMLCommonHandler.instance().firePlayerCraftingEvent(CraftingUpgradeCC.this.cart.player(), result, this);
					ArrayList<ItemStack> citems = new ArrayList<ItemStack>();
					for(int slot=0;slot<this.getSizeInventory();slot++){
						ItemStack stack = this.getStackInSlot(slot);
						if(stack!=null)this.decrStackSize(slot, 1);
						if(stack!=null && stack.getItem().hasContainerItem(stack)){
							ItemStack container = stack.getItem().getContainerItem(stack);
							if(container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()){
								 MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(CraftingUpgradeCC.this.cart.player(), container));
							}
							else if(container.getItem().doesContainerItemLeaveCraftingGrid(container) || getStackInSlot(slot) != null){
								citems.add(container);
							}
							else{
								this.setInventorySlotContents(slot, container);
							}
						}
					}
					
					save();
					InventoryUtil.addToPlayerInventory(result, CraftingUpgradeCC.this.cart.player());
					for(ItemStack stack : citems){
						InventoryUtil.addToPlayerInventory(stack, CraftingUpgradeCC.this.cart.player());
					}
					load();
				}
			}
			return new Object[]{valid,ccount};
		}
		
		private void load(){
			IInventory hinv = CraftingUpgradeCC.this.cart.mainInventory();
			this.possibleAmount=Integer.MAX_VALUE;
			for(int slot=0;slot<this.getSizeInventory();slot++){
				ItemStack stack = hinv.getStackInSlot(toParentSlot(slot));
				this.setInventorySlotContents(slot, stack);
				if(stack!=null){
					this.possibleAmount = Math.min(this.possibleAmount, stack.stackSize);
				}
			}
		}
		
		private void save(){
			IInventory hinv = CraftingUpgradeCC.this.cart.mainInventory();
			for(int slot=0;slot<this.getSizeInventory();slot++){
				hinv.setInventorySlotContents(toParentSlot(slot), this.getStackInSlot(slot));
			}
		}
		
		private int toParentSlot(int slot){
		     int col = slot % 3;
		     int row = slot / 3;
		     return row * 4 + col;
		}
	}
	
}
