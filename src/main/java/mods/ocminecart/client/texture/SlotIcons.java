package mods.ocminecart.client.texture;

import li.cil.oc.api.driver.item.Slot;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.LinkedList;
import java.util.List;

public final class SlotIcons {

	private final static String[] SLOT_TYPES = new String[]{
			Slot.Card, Slot.ComponentBus, Slot.Container, Slot.CPU, "eeprom", Slot.Floppy, Slot.HDD, Slot.Memory,
			Slot.Tablet, "tool", Slot.Upgrade};

	private SlotIcons() {
	}

	public static TextureAtlasSprite fromTier(int tier) {
		if (tier >= 0 && tier < 3) {
			return TextureHelper.getSpriteUnsafe("opencomputers:icons/tier" + tier);
		}
		else if (tier == -1) {
			return TextureHelper.getSprite("opencomputers:icons/na");
		}
		return null;
	}

	public static TextureAtlasSprite fromType(String type) {
		return TextureHelper.getSpriteUnsafe("opencomputers:icons/" + type);
	}

	/* default */
	static List<String> getIconResources() {
		List<String> list = new LinkedList<>();

		for (String slotT : SLOT_TYPES) {
			list.add("opencomputers:icons/" + slotT);
		}

		list.add("opencomputers:icons/na");
		for (int i = 0; i < 3; i += 1) {
			list.add("opencomputers:icons/tier" + i);
		}
		return list;
	}
}
