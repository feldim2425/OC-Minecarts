package mods.ocminecart;


public class Settings {
	
	public static final String OC_ResLoc = "opencomputers"; // Resource domain for OpenComputers
	public static final String OC_Namespace = "oc:"; // Namespace for OpenComputers NBT Data
	
	public static float OC_SoundVolume;
	public static double OC_IC2PWR;
	
	public static int ComputerCartBaseCost; //Config Value -> basecost
	public static int ComputerCartComplexityCost; //Config Value -> costmultiplier
	public static int ComputerCartEnergyCap; //Config Value -> energystore
	public static double ComputerCartEnergyUse; //Config Value -> energyuse
	public static int ComputerCartCreateEnergy; //Config Value -> newenergy
	public static double ComputerCartEngineUse; //Config Value -> engineuse
	public static double ComputerCartETrackLoad; //Config Value -> maxtrackcharge
	
	public static int NetRailPowerTransfer; //Config Value -> transferspeed
	
	public static boolean GeneralFixCartBox; //Config Value -> cartboxfix
	
	
	public static void init(){
		OCMinecart.config.load();
		
		OCMinecart.config.addCustomCategoryComment("computercart", "Some settings for the computer cart");
		
		ComputerCartBaseCost = OCMinecart.config.get("computercart", "basecost", 50000 , "Energy cost for a ComputerCart with Complexity 0 [default: 50000]").getInt(50000);
		ComputerCartComplexityCost = OCMinecart.config.get("computercart", "costmultiplier", 10000 , "Energy - Complexity multiplier [default: 10000]").getInt(10000);
		ComputerCartEnergyCap = OCMinecart.config.get("computercart", "energystore", 20000 , "Energy a Computer cart can store [default: 20000]").getInt(20000);
		ComputerCartEnergyUse = OCMinecart.config.get("computercart", "energyuse", 0.25 , "Energy a Computer cart consume every tick [default: 0.25]").getDouble(0.25);
		ComputerCartCreateEnergy= OCMinecart.config.get("computercart", "newenergy", 20000 , "Energy new a Computer cart has stored [default: 20000]").getInt(20000);
		ComputerCartEngineUse = OCMinecart.config.get("computercart", "engineuse", 2 , "Energy multiplier for the Engine. Speed times Value [default: 2]").getDouble(2);
		ComputerCartETrackLoad = OCMinecart.config.get("computercart", "maxtrackcharge", 100 , "Max. Energy a cart can take from a electric track[default: 100]").getInt(100);
		
		OCMinecart.config.addCustomCategoryComment("networkrail", "Some settings for the network rail");
		
		NetRailPowerTransfer= OCMinecart.config.get("networkrail", "transferspeed",150 , "Energy that a network rail can transfer per tick [default: 100]").getInt(150);
		
		OCMinecart.config.addCustomCategoryComment("general", "Some general settings for the mod");
		
		GeneralFixCartBox = OCMinecart.config.get("general", "cartboxfix", true, "Fix the computer cart bounding box if railcraft is installed [default:true]").getBoolean(true);
		
		OCMinecart.config.save();
		ocValues();
	}
	
	private static void ocValues(){
		OC_SoundVolume = li.cil.oc.Settings.get().soundVolume();
		OC_IC2PWR = li.cil.oc.Settings.get().ratioIndustrialCraft2();
	}
}
