package mods.ocminecart.common.item;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.item.base.IModelItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public final class ModItems {

	private static Map<String, Item> items = new HashMap<>();

	private ModItems() {

	}

	public static void init() {
		for (Items regItem : Items.values()) {
			try {
				Constructor<? extends Item> construct = regItem.itemClass.getConstructor();
				registerItem(construct.newInstance(), regItem.name);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				OCMinecart.getLogger().warn("Error while registering item '" + regItem.name + "'", e);
			}
		}
	}

	public static void initClient() {
		for (Item item : items.values()) {
			if (item instanceof IModelItem) {
				((IModelItem) item).registerModel(OCMinecart.MOD_ID);
			}
		}
	}

	public static Item getItem(String name) {
		return items.get(name);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Item> T getItem(String name, Class<T> itemClass) {
		Item item = items.get(name);
		if (item != null && itemClass.isAssignableFrom(item.getClass())) {
			return (T) item;
		}
		return null;
	}

	private static <T extends Item> T registerItem(T item, String regName) {
		item.setRegistryName(OCMinecart.MOD_ID, regName);
		GameRegistry.register(item);
		items.put(regName, item);
		return item;
	}

	public enum Items {
		COMPUTER_CART(ItemComputerCart.class, "computer_cart"),
		COMPUTER_CART_CASE(ItemComputerCartCase.class, "computer_cart_case");

		public final Class<? extends Item> itemClass;
		public final String name;

		Items(Class<? extends Item> itemClass, String name) {
			this.itemClass = itemClass;
			this.name = name;
		}

		public Item get() {
			return ModItems.getItem(name);
		}
	}
}
