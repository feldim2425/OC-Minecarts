package mods.ocminecart.utils;


import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public final class MinecartUtil {

	private MinecartUtil() {
	}

	public static Vec3d correctMinecartPosition(BlockPos pos, IBlockState state) {
		double deltaY = 1D / 16D;
		if ((state.getBlock() instanceof BlockRailBase) && state.getValue(((BlockRailBase) state.getBlock()).getShapeProperty()).isAscending()) {
			deltaY += 0.5D;
		}

		return new Vec3d(pos.getX() + 0.5D, pos.getY() + deltaY, pos.getZ() + 0.5D);
	}

	public static EnumFacing calculateFacing(Entity entity) {
		Vec2f vec = entity.getPitchYaw();
		float yaw = (vec.y + 360f - 90f) % 360f;
		return EnumFacing.getHorizontal((int) Math.floor((yaw + 45f) / 90f + 0.5f));
	}
}
