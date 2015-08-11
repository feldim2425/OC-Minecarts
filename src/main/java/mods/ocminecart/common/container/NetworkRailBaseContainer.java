package mods.ocminecart.common.container;

import cpw.mods.fml.common.FMLCommonHandler;
import mods.ocminecart.common.SlotGhost;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class NetworkRailBaseContainer extends Container {
	
	private NetworkRailBaseTile entity;
	
	public NetworkRailBaseContainer(InventoryPlayer inventory,NetworkRailBaseTile entity) {
		this.entity=entity;
		
		this.addPlayerInv(8, 84, inventory);
		this.addSlotToContainer(new SlotGhost(entity,0,116,35));
	}

	public boolean canInteractWith(EntityPlayer player) {
		return entity.isUseableByPlayer(player);
	}
	
	private void addPlayerInv(int x,int y, InventoryPlayer inventory){
		for(int i=0;i<3;i++){
			for(int j=0;j<9;j++){
				this.addSlotToContainer(new Slot(inventory, j+i*9+9, x+j*18, y+i*18));
			}
		}
		
		for(int i=0;i<9;i++){
			this.addSlotToContainer(new Slot(inventory, i, x+i*18, y+58));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		return null;
	}
	
	
	public NetworkRailBaseTile getTile(){
		return entity;
	}

}
