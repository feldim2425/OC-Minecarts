package mods.ocminecart.asm.utils;

import mods.ocminecart.asm.OCMinecartFMLPlugin;


public final class ObfuscationUtil {

	public static boolean isObfuscated() {
		return OCMinecartFMLPlugin.isObfuscatedEnv();
	}

	public static String getString(String deobf, String obf) {
		return isObfuscated() ? obf : deobf;
	}

	public ObfuscationUtil() {

	}
}
