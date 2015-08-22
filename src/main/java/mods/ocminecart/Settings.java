package mods.ocminecart;


public class Settings {
	
	public static final String OC_ResLoc = "opencomputers"; // Resource domain for OpenComputers
	public static final String OC_Namespace = "oc:"; // Namespace for OpenComputers NBT Data
	
	
	public static int ComputerCartBaseCost; //Config Value -> basecost
	public static int ComputerCartComplexityCost; //Config Value -> costmultiplier
	
	
	public static void init(){
		OCMinecart.config.load();
		
		OCMinecart.config.addCustomCategoryComment("computercart", "Some Settings for the ComputerCart");
		
		ComputerCartBaseCost = OCMinecart.config.get("computercart", "basecost", 50000 , "Energy cost for a ComputerCart with Complexity 0 [default: 50000]").getInt(50000);
		ComputerCartComplexityCost = OCMinecart.config.get("computercart", "costmultiplier", 10000 , "Energy - Complexity multiplier [default: 10000]").getInt(10000);
		
		OCMinecart.config.save();
	}
}
