package mods.ocminecart;

import mods.ocminecart.common.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = OCMinecart.MOD_ID, name = OCMinecart.NAME, version = OCMinecart.VERSION, dependencies = OCMinecart.DEPENDENCIES)
public class OCMinecart {

	public static final String MOD_ID = "ocminecart";
	public static final String NAME = "OC-Minecart";
	public static final String VERSION = "0.0.1";
	public static final String DEPENDENCIES = "required-after:Forge@[12.18.3.2511,);" +
			"required-after:OpenComputers@[1.7.0.128,);" +
			"required-after:CodeChickenLib@[2.5.9.308,);";

	@Instance
	private static OCMinecart instance;
	@SidedProxy(serverSide = "mods.ocminecart.common.CommonProxy", clientSide = "mods.ocminecart.client.ClientProxy")
	private static CommonProxy proxy;

	private static Configuration config;
	private static Logger logger = LogManager.getLogger(OCMinecart.NAME);

	public static OCMinecart getInstance() {
		return instance;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Configuration getConfig() {
		return config;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		ConfigSettings.init();

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
