package mods.ocminecart.utils;


import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MinecartUtil {

	public static Vec3d correctMinecartPosition(BlockPos pos, IBlockState state) {
		double deltaY = 1D / 16D;
		if ((state.getBlock() instanceof BlockRailBase) && state.getValue(((BlockRailBase) state.getBlock()).getShapeProperty()).isAscending()) {
			deltaY += 0.5D;
		}

		return new Vec3d(pos.getX() + 0.5D, pos.getY() + deltaY, pos.getZ() + 0.5D);
	}

	private MinecartUtil() {
	}
}
