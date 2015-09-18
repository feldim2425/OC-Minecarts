package mods.ocminecart.common.util;

import java.util.Iterator;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemUtil {
	public static void dropItemList(Iterable<ItemStack> items, World world, double x, double y, double z){
		if(!world.isRemote){
			Iterator<ItemStack> itemlist = items.iterator();
			while(itemlist.hasNext()){
				ItemStack stack = itemlist.next();
				if(stack.stackSize > 0){
					EntityItem entityitem = new EntityItem(world, x, y, z, stack);
					entityitem.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(entityitem);
				}
			}
		}
	}
}
