package mods.ocminecart.common.recipes;

import li.cil.oc.api.IMC;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.common.recipes.assembler.ComputerCartAssembler;

public final class Recipes {

	private static String PACKAGE_PREFIX = "mods.ocminecart.common.recipes.";

	private Recipes() {
	}

	public static void init() {
		assembler();
		disassembler();
	}

	private static void assembler() {
		final String COMPUTER_CART_TEMPLATE = PACKAGE_PREFIX + "assembler.ComputerCartAssembler.";

		IMC.registerAssemblerTemplate("Computer Cart Tier 1",
				COMPUTER_CART_TEMPLATE + "select_t1",
				COMPUTER_CART_TEMPLATE + "validate",
				COMPUTER_CART_TEMPLATE + "assemble",
				EntityComputerCart.class, ComputerCartAssembler.getContainer_t1(), ComputerCartAssembler.getUpgrade_t1(), ComputerCartAssembler.getComponentSlots_t1());

		IMC.registerAssemblerTemplate("Computer Cart Tier 2",
				COMPUTER_CART_TEMPLATE + "select_t2",
				COMPUTER_CART_TEMPLATE + "validate",
				COMPUTER_CART_TEMPLATE + "assemble",
				EntityComputerCart.class, ComputerCartAssembler.getContainer_t2(), ComputerCartAssembler.getUpgrade_t2(), ComputerCartAssembler.getComponentSlots_t2());

		IMC.registerAssemblerTemplate("Computer Cart Tier 3",
				COMPUTER_CART_TEMPLATE + "select_t3",
				COMPUTER_CART_TEMPLATE + "validate",
				COMPUTER_CART_TEMPLATE + "assemble",
				EntityComputerCart.class, ComputerCartAssembler.getContainer_t3(), ComputerCartAssembler.getUpgrade_t3(), ComputerCartAssembler.getComponentSlots_t3());

		IMC.registerAssemblerTemplate("Computer Cart Creative",
				COMPUTER_CART_TEMPLATE + "select_tc",
				COMPUTER_CART_TEMPLATE + "validate",
				COMPUTER_CART_TEMPLATE + "assemble",
				EntityComputerCart.class, ComputerCartAssembler.getContainer_tc(), ComputerCartAssembler.getUpgrade_tc(), ComputerCartAssembler.getComponentSlots_tc());
	}

	private static void disassembler() {
		IMC.registerDisassemblerTemplate("Computer Cart",
				PACKAGE_PREFIX + "disassembler.ComputerCartDisassembler.select",
				PACKAGE_PREFIX + "disassembler.ComputerCartDisassembler.disassemble");

		IMC.registerDisassemblerTemplate(OCMinecart.NAME + " - Standard Template",
				PACKAGE_PREFIX + "disassembler.StandardDisassembler.select",
				PACKAGE_PREFIX + "disassembler.StandardDisassembler.disassemble");
	}
}
