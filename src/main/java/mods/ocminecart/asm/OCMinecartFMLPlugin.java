package mods.ocminecart.asm;

import mods.ocminecart.OCMinecart;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


@TransformerExclusions({"mods.ocminecart.asm"})
public class OCMinecartFMLPlugin implements IFMLLoadingPlugin {

	public static Logger logger = LogManager.getLogger(OCMinecart.NAME);
	private static boolean isObf;

	public static boolean isObfuscatedEnv() {
		return isObf;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"mods.ocminecart.asm.OCMinecartTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
