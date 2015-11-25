package mods.ocminecart;

import java.util.ArrayList;


public class Settings {
	
	public static final String OC_ResLoc = "opencomputers"; // Resource domain for OpenComputers
	public static final String OC_Namespace = "oc:"; // Namespace for OpenComputers NBT Data
	
	public static float OC_SoundVolume;
	public static double OC_IC2PWR;
	public static double OC_SuckDelay;
	public static double OC_DropDelay;
	
	public static int ComputerCartBaseCost; //Config Value -> basecost
	public static int ComputerCartComplexityCost; //Config Value -> costmultiplier
	public static int ComputerCartEnergyCap; //Config Value -> energystore
	public static double ComputerCartEnergyUse; //Config Value -> energyuse
	public static int ComputerCartCreateEnergy; //Config Value -> newenergy
	public static double ComputerCartEngineUse; //Config Value -> engineuse
	public static double ComputerCartETrackLoad; //Config Value -> maxtrackcharge
	public static double ComputerCartETrackBuf; //Config Value -> maxtrackcharge
	public static double ComputerCartETrackLoss; //Config Value -> maxtrackcharge
	
	public static int NetRailPowerTransfer; //Config Value -> transferspeed
	
	public static boolean GeneralFixCartBox; //Config Value -> cartboxfix
	
	
	public static void init(){
		OCMinecart.config.load();
		confComments();
		confValues();
		confOrder();
		OCMinecart.config.save();
		
		ocValues();
	}
	
	private static void ocValues(){	//Get the settings from OpenCOmputers
		OC_SoundVolume = li.cil.oc.Settings.get().soundVolume();
		OC_IC2PWR = li.cil.oc.Settings.get().ratioIndustrialCraft2();
		OC_SuckDelay = li.cil.oc.Settings.get().suckDelay();
		OC_DropDelay = li.cil.oc.Settings.get().dropDelay();
	}
	
	private static void confValues(){
		// computercart
		ComputerCartBaseCost = OCMinecart.config.get("computercart", "basecost", 50000 , "Energy cost for a ComputerCart with Complexity 0 [default: 50000]").getInt(50000);
		ComputerCartComplexityCost = OCMinecart.config.get("computercart", "costmultiplier", 10000 , "Energy - Complexity multiplier [default: 10000]").getInt(10000);
		ComputerCartEnergyCap = OCMinecart.config.get("computercart", "energystore", 20000 , "Energy a Computer cart can store [default: 20000]").getInt(20000);
		ComputerCartEnergyUse = OCMinecart.config.get("computercart", "energyuse", 0.25 , "Energy a Computer cart consume every tick [default: 0.25]").getDouble(0.25);
		ComputerCartCreateEnergy= OCMinecart.config.get("computercart", "newenergy", 20000 , "Energy new a Computer cart has stored [default: 20000]").getInt(20000);
		ComputerCartEngineUse = OCMinecart.config.get("computercart", "engineuse", 2 , "Energy multiplier for the Engine. Speed times Value [default: 2]").getDouble(2);
		ComputerCartETrackBuf = OCMinecart.config.get("computercart", "trackchargebuffer", 1000 , "[Railcraft] Charge buffer for the computer cart (EU) [default: 1000]").getDouble(1000);
		ComputerCartETrackLoss = OCMinecart.config.get("computercart", "chargebufferloss", 0.1 , "[Railcraft] Charge buffer loss per tick (EU) [default: 0.1]").getDouble(0.1);
		ComputerCartETrackLoad = OCMinecart.config.get("computercart", "maxtrackcharge", 16 , "[Railcraft] Max. Energy a cart can take from the charge buffer per tick (EU) [default: 16]").getDouble(16);
		
		// networkrail
		NetRailPowerTransfer= OCMinecart.config.get("networkrail", "transferspeed",150 , "Energy that a network rail can transfer per tick [default: 100]").getInt(150);
		
		// general
		GeneralFixCartBox = OCMinecart.config.get("general", "cartboxfix", true, "Fix the computer cart bounding box if railcraft is installed [default:true]").getBoolean(true);
	}
	
	private static void confComments(){
		OCMinecart.config.addCustomCategoryComment("computercart", "Settings for the computer cart\n"
				+ "[Railcraft] <= This settings are useless if Railcraft is not installed. ");
		
		OCMinecart.config.addCustomCategoryComment("networkrail", "Settings for the network rail");
		OCMinecart.config.addCustomCategoryComment("general", "Some general settings for the mod");
		OCMinecart.config.addCustomCategoryComment("upgrades", "Some general settings for upgrades");
	}
	
	private static void confOrder(){
		ArrayList<String> computercart = new ArrayList<String>();
		computercart.add("basecost");
		computercart.add("costmultiplier");
		computercart.add("newenergy");
		computercart.add("energystore");
		computercart.add("energyuse");
		computercart.add("engineuse");
		computercart.add("maxtrackcharge");
		computercart.add("trackchargebuffer");
		computercart.add("chargebufferloss");
		OCMinecart.config.setCategoryPropertyOrder("computercart", computercart);
	}
}
