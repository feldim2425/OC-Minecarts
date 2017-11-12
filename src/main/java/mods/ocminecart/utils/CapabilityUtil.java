package mods.ocminecart.utils;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public final class CapabilityUtil {

	@CapabilityInject(IItemHandler.class)
	public static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

	private CapabilityUtil() {
	}

	public static <T> T getTileCapability(Capability<T> capability, World world, BlockPos pos, EnumFacing facing) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null || !tile.hasCapability(capability, facing)) {
			return null;
		}
		return tile.getCapability(capability, facing);
	}

	public static <T> T getEntityCapability(Capability<T> capability, Entity entity, EnumFacing facing) {
		if (entity == null || !entity.hasCapability(capability, facing)) {
			return null;
		}
		return entity.getCapability(capability, facing);
	}

	public static <T> T getCapability(Capability<T> capability, World world, BlockPos pos, EnumFacing facing) {
		T handle = getTileCapability(capability, world, pos, facing);
		if (handle != null) {
			return handle;
		}

		List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
		for (Entity entity : entityList) {
			handle = getEntityCapability(capability, entity, facing);
			if (handle != null) {
				return handle;
			}
		}
		return null;
	}
}
