package mods.ocminecart;

import mods.ocminecart.common.CommonProxy;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = OCMinecart.MODID, name=OCMinecart.NAME, version=OCMinecart.VERSION, dependencies = "required-after:OpenComputers@[1.5.13,)")
public class OCMinecart {
	public static final String MODID = "ocminecart";
	public static final String VERSION = "0.3a";
	public static final String NAME = "OC-Minecarts";
	
	@Instance(OCMinecart.MODID)
	public static OCMinecart instance;
	
	public static Logger logger = LogManager.getLogger(OCMinecart.NAME);
	
	@SidedProxy(serverSide="mods.ocminecart.common.CommonProxy", clientSide="mods.ocminecart.client.ClientProxy")
	public static CommonProxy proxy;
	
	public static Configuration config;
	
	public static CreativeTabs itemGroup = new CreativeTabs(OCMinecart.MODID+".modtab"){
		@Override
		public Item getTabIconItem() {
			return ModItems.item_ComputerCartCase;
		}
	};
	
	//public static CreativeTabs itemGroup = li.cil.oc.api.CreativeTab.instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		Settings.init();
		
		proxy.preInit();
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event){
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit();
	}
}
