package mods.ocminecart.utils;

import net.minecraft.inventory.Slot;

import java.util.Collection;

public final class ContainerUtil {

	private ContainerUtil() {
	}

	public static Slot findSlotAt(int mouseX, int mouseY, Collection<Slot> slots) {
		for (Slot slot : slots) {
			int x = slot.xDisplayPosition - 1;
			int y = slot.yDisplayPosition - 1;
			if (mouseX >= x && mouseX <= x + 17 && mouseY >= y && mouseY <= y + 17) {
				return slot;
			}
		}
		return null;
	}
}
