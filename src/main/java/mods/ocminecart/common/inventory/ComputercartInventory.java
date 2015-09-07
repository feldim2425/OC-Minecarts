package mods.ocminecart.common.inventory;

import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.player.EntityPlayer;

public class ComputercartInventory extends Inventory {
	
	private ComputerCart cart;
	
	public ComputercartInventory(ComputerCart cart){
		this.cart=cart;
	}
	
	@Override
	public int getSizeInventory() {
		return 80;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return this.cart.isUseableByPlayer(p_70300_1_);
	}

	@Override
	protected void slotChanged(int slot) {
		if(!this.cart.worldObj.isRemote) this.cart.machine().signal("inventory_changed",slot);
	}

}
