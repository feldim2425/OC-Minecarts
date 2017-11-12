package mods.ocminecart.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public final class ItemStackUtil {

	private ItemStackUtil() {

	}

	public static boolean isStackEmpty(ItemStack stack) {
		return stack == null;
	}

	public static ItemStack getEmptyStack() {
		return null;
	}

	public static ItemStack sizeCheck(ItemStack stack) {
		if (stack != null && stack.stackSize < 1) {
			return getEmptyStack();
		}
		return stack;
	}

	public static void dropItemList(List<ItemStack> drops, World worldObj, BlockPos pos, boolean randomizeMovement) {
		dropItemList(drops, worldObj, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, randomizeMovement);
	}

	public static void dropItemList(List<ItemStack> drops, World worldObj, double x, double y, double z, boolean randomizeMovement) {
		for (ItemStack stack : drops) {
			if (!isStackEmpty(stack)) {
				EntityItem entityitem = new EntityItem(worldObj, x, y, z, stack);
				entityitem.setPickupDelay(10);
				if (!randomizeMovement) {
					entityitem.motionX = 0;
					entityitem.motionY = 0;
					entityitem.motionZ = 0;
				}
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
	}
}
