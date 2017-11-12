package mods.ocminecart.common.recipes.assembler;


import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.common.Tier;
import mods.ocminecart.ConfigSettings;
import mods.ocminecart.common.item.ItemComputerCart;
import mods.ocminecart.common.item.ModItems;
import mods.ocminecart.common.item.data.DataComputerCart;
import mods.ocminecart.utils.AssemblerUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComputerCartAssembler {
	private static final int MAX_COMPLEXITY_1 = 12;
	private static final int MAX_COMPLEXITY_2 = 18;
	private static final int MAX_COMPLEXITY_3 = 20;
	private static final int MAX_COMPLEXITY_C = 9001;


	public static Object[] assemble(IInventory inventory) {
		Map<Integer, ItemStack> comp = new HashMap<>();
		int size = inventory.getSizeInventory();
		for (int i = 1; i < size; i += 1) {
			if (inventory.getStackInSlot(i) != null) {
				comp.put(i - 1, inventory.getStackInSlot(i));
			}
		}

		ItemStack stack = new ItemStack(ModItems.Items.COMPUTER_CART.get());
		DataComputerCart data = new DataComputerCart();
		data.setComponents(comp);
		data.setTier(inventory.getStackInSlot(0).getMetadata());
		data.setEnergy((float) ConfigSettings.cart_energy_start);

		((ItemComputerCart) ModItems.Items.COMPUTER_CART.get()).setData(stack, data);


		int energy = (data.getTier() < 3) ? ConfigSettings.cart_basecost + ConfigSettings.cart_costmult * AssemblerUtils.calculateFromAssemblerInv(inventory) : 0;
		return new Object[]{stack, energy};
	}

	public static Object[] validate(IInventory inventory) {
		switch (inventory.getStackInSlot(0).getMetadata()) {
			case 0:
				return AssemblerUtils.validate(inventory, MAX_COMPLEXITY_1);
			case 1:
				return AssemblerUtils.validate(inventory, MAX_COMPLEXITY_2);
			case 2:
				return AssemblerUtils.validate(inventory, MAX_COMPLEXITY_3);
			case 3:
				return AssemblerUtils.validate(inventory, MAX_COMPLEXITY_C);
		}
		return new Object[]{false, new TextComponentString("ERROR! No Tier!")};
	}

	// === SELECT == //

	public static boolean select_t1(ItemStack stack) {
		return stack.getItem().equals(ModItems.Items.COMPUTER_CART_CASE.get()) && stack.getItemDamage() == 0;
	}

	public static boolean select_t2(ItemStack stack) {
		return stack.getItem().equals(ModItems.Items.COMPUTER_CART_CASE.get()) && stack.getItemDamage() == 1;
	}

	public static boolean select_t3(ItemStack stack) {
		return stack.getItem().equals(ModItems.Items.COMPUTER_CART_CASE.get()) && stack.getItemDamage() == 2;
	}

	public static boolean select_tc(ItemStack stack) {
		return stack.getItem().equals(ModItems.Items.COMPUTER_CART_CASE.get()) && stack.getItemDamage() == 3;
	}


	// === CONTAINER === //

	public static int[] getContainer_t1() {
		return new int[]{1, 0, 0};
	}

	public static int[] getContainer_t2() {
		return new int[]{2, 1, 0};
	}

	public static int[] getContainer_t3() {
		return new int[]{2, 2, 1};
	}

	public static int[] getContainer_tc() {
		return new int[]{2, 2, 2};
	}

	// === UPGRADE === //

	public static int[] getUpgrade_t1() {
		return new int[]{0, 0, 0};
	}

	public static int[] getUpgrade_t2() {
		return new int[]{1, 1, 1, 0, 0, 0};
	}

	public static int[] getUpgrade_t3() {
		return new int[]{2, 2, 2, 1, 1, 1, 0, 0, 0};
	}

	public static int[] getUpgrade_tc() {
		return new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2};
	}

	// === COMPONENT SLOTS === //

	public static Iterable<Pair<String, Integer>> getComponentSlots_t1() {
		List<Pair<String, Integer>> list = new LinkedList<Pair<String, Integer>>();
		list.add(Pair.of(Slot.Card, 0));
		list.add(Pair.of(Slot.None, -1));
		list.add(Pair.of(Slot.None, -1));

		list.add(Pair.of(Slot.CPU, 0));
		list.add(Pair.of(Slot.Memory, 0));
		list.add(Pair.of(Slot.Memory, 0));

		list.add(Pair.of("eeprom", Tier.Any()));
		list.add(Pair.of(Slot.HDD, 0));
		return list;
	}

	public static Iterable<Pair<String, Integer>> getComponentSlots_t2() {
		List<Pair<String, Integer>> list = new LinkedList<Pair<String, Integer>>();
		list.add(Pair.of(Slot.Card, 1));
		list.add(Pair.of(Slot.Card, 0));
		list.add(Pair.of(Slot.None, -1));

		list.add(Pair.of(Slot.CPU, 1));
		list.add(Pair.of(Slot.Memory, 1));
		list.add(Pair.of(Slot.Memory, 1));

		list.add(Pair.of("eeprom", Tier.Any()));
		list.add(Pair.of(Slot.HDD, 1));
		return list;
	}

	public static Iterable<Pair<String, Integer>> getComponentSlots_t3() {
		List<Pair<String, Integer>> list = new LinkedList<Pair<String, Integer>>();
		list.add(Pair.of(Slot.Card, 2));
		list.add(Pair.of(Slot.Card, 1));
		list.add(Pair.of(Slot.Card, 1));

		list.add(Pair.of(Slot.CPU, 2));
		list.add(Pair.of(Slot.Memory, 2));
		list.add(Pair.of(Slot.Memory, 2));

		list.add(Pair.of("eeprom", Tier.Any()));
		list.add(Pair.of(Slot.HDD, 2));
		return list;
	}

	public static Iterable<Pair<String, Integer>> getComponentSlots_tc() {
		List<Pair<String, Integer>> list = new LinkedList<Pair<String, Integer>>();
		list.add(Pair.of(Slot.Card, 2));
		list.add(Pair.of(Slot.Card, 2));
		list.add(Pair.of(Slot.Card, 2));

		list.add(Pair.of(Slot.CPU, 2));
		list.add(Pair.of(Slot.Memory, 2));
		list.add(Pair.of(Slot.Memory, 2));

		list.add(Pair.of("eeprom", Tier.Any()));
		list.add(Pair.of(Slot.HDD, 2));
		return list;
	}
}
