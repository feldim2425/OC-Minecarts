package mods.ocminecart.common.items.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ItemEntityInteract {
	
	public boolean onEntityClick(EntityPlayer p, Entity e, ItemStack s);
	
}
