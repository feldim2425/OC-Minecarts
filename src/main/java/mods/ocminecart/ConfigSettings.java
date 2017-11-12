package mods.ocminecart;

import java.util.LinkedList;
import java.util.List;

public class ConfigSettings {

	public static int cart_basecost;
	public static int cart_costmult;
	public static double cart_energy_start;
	public static double cart_energy_buffer;
	public static double cart_energy_usage;

	public static float oc_soundvol;
	public static double oc_dropdelay;
	public static double oc_suckdelay;

	public static void init() {
		OCMinecart.getConfig().load();
		confValues();
		confOrder();
		OCMinecart.getConfig().save();

		confOpenComputers();
	}

	private static void confOpenComputers() {
		oc_soundvol = li.cil.oc.Settings.get().soundVolume();
		oc_dropdelay = li.cil.oc.Settings.get().dropDelay();
		oc_suckdelay = li.cil.oc.Settings.get().suckDelay();
	}

	private static void confOrder() {
		List<String> computercart = new LinkedList<String>();
		computercart.add("basecost");
		computercart.add("costmult");
		computercart.add("startenergy");
		computercart.add("buffer");
		computercart.add("usage");
		OCMinecart.getConfig().setCategoryPropertyOrder("computercart", computercart);
	}

	private static void confValues() {
		OCMinecart.getConfig().setCategoryComment("computercart", "Setting for the Computer Cart");

		cart_basecost = OCMinecart.getConfig().get("computercart", "basecost", 50000, "Base cost to assemble a computer cart. [default: 50000]").getInt();
		cart_costmult = OCMinecart.getConfig().get("computercart", "costmult", 10000, "Additional cost to assemble a computer cart for each complexity point [default: 10000]").getInt();
		cart_energy_start = OCMinecart.getConfig().get("computercart", "startenergy", 20000.0, "Energy a newly assembled computer cart has stored [default: 20000.0]").getDouble();
		cart_energy_buffer = OCMinecart.getConfig().get("computercart", "buffer", 20000.0, "Amount of energy a computer cart can hold without upgrades. [default: 20000.0]").getDouble();
		cart_energy_usage = OCMinecart.getConfig().get("computercart", "usage", 0.25, "Amount of energy a computer cart uses each tick. [default: 0.25]").getDouble();
	}
}
