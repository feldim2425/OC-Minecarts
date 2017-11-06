package mods.ocminecart.common.hooks;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class RedstonePower {

	public static final int NO_OVERRIDE = -1;

	public static int getOverrideRedstonePower(World world, BlockPos pos, EnumFacing facing) {
		return NO_OVERRIDE;
	}

}
