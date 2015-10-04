package mods.ocminecart.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemUtil {
	public static void dropItemList(Iterable<ItemStack> items, World world, double x, double y, double z, boolean motion){
		if(!world.isRemote){
			Iterator<ItemStack> itemlist = items.iterator();
			while(itemlist.hasNext()){
				ItemStack stack = itemlist.next();
				if(stack.stackSize > 0){
					EntityItem entityitem = new EntityItem(world, x, y, z, stack);
					entityitem.delayBeforeCanPickup = 10;
					if(!motion){
						entityitem.motionX=0;
						entityitem.motionY=0;
						entityitem.motionZ=0;
					}
					world.spawnEntityInWorld(entityitem);
				}
			}
		}
	}
	
	public static ItemStack suckItems(World world, int x, int y, int z, ItemStack filter, int num){
		if(!world.isRemote){
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(1,1,1,16,16,16);
			box.addCoord(x, y, z);
			List entls = world.getEntitiesWithinAABB(EntityItem.class, box);
			Iterator entit = entls.iterator();
			while(entit.hasNext()){
				Entity ent = (Entity) entit.next();
				if(!(ent instanceof EntityItem)) continue;
				ItemStack stack = ((EntityItem)ent).getEntityItem();
				if(!(filter==null || (filter!=null && filter.isItemEqual(stack)))) continue;
				ItemStack ret = stack.copy();
				ret.stackSize = Math.min(ret.stackSize, num);
				stack.stackSize-=ret.stackSize;
				if(stack.stackSize<1)ent.setDead();
				return ret;
			}
		}
		return null;
	}
	
	public static ItemStack sumItemStacks(ItemStack stackA, ItemStack stackB, boolean pull){
		ItemStack res = null;
		if(stackA==null || stackB==null) return null;
		if(!stackA.isItemEqual(stackB)) return null;
		res = stackA.copy();
		int size = stackA.stackSize + stackB.stackSize;
		size = Math.min(size, res.getMaxStackSize());
		res.stackSize = size;
		if(pull){
			if(size >= stackA.stackSize){
				size-=stackA.stackSize;
				stackA.stackSize=0;
			}
			else{
				stackA.stackSize-=size;
				size=0;
			}
			
			if(size >= stackB.stackSize){
				size-=stackB.stackSize;
				stackB.stackSize=0;
			}
			else{
				stackB.stackSize-=size;
				size=0;
			}
		}
		return res;
	}
}
