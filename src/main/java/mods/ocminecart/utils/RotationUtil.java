package mods.ocminecart.utils;

import net.minecraft.util.EnumFacing;

public final class RotationUtil {

	private RotationUtil() {
	}

	public static EnumFacing calcLocalDirection(EnumFacing value, EnumFacing face) {
		int n = face.getHorizontalIndex();
		int d = value.getHorizontalIndex();
		if (n < 0 || d < 0) {
			return value;
		}
		return EnumFacing.getHorizontal((d + n + 4) % 4);
	}

	public static EnumFacing calcGlobalDirection(EnumFacing value, EnumFacing face) {
		int n = face.getHorizontalIndex();
		int d = value.getHorizontalIndex();
		if (n < 0 || d < 0) {
			return value;
		}
		return EnumFacing.getHorizontal((d - n + 4) % 4);
	}
}
