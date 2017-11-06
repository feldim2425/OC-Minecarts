package mods.ocminecart.client.texture;

import mods.ocminecart.OCMinecart;
import net.minecraft.util.ResourceLocation;

public enum ResourceTexture {
	ENTITY_COMPUTERCART(new ResourceLocation(OCMinecart.MOD_ID, "textures/entity/computercart.png")),
	OC_GUI_BORDER(new ResourceLocation("opencomputers", "textures/gui/borders.png")),
	OC_GUI_ROBOT(new ResourceLocation("opencomputers", "textures/gui/robot.png")),
	OC_GUI_ROBOT_NS(new ResourceLocation("opencomputers", "textures/gui/robot_noscreen.png")),
	OC_GUI_POWERBUTTON(new ResourceLocation("opencomputers", "textures/gui/button_power.png")),
	OC_GUI_POWERBAR(new ResourceLocation("opencomputers", "textures/gui/bar.png")),
	OC_GUI_SLOTSELECT(new ResourceLocation("opencomputers", "textures/gui/robot_selection.png"));
	public final ResourceLocation location;

	ResourceTexture(ResourceLocation location) {
		this.location = location;
	}
}
