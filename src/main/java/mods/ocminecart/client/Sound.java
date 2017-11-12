package mods.ocminecart.client;

import li.cil.oc.api.network.EnvironmentHost;
import mods.ocminecart.ConfigSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.HashMap;
import java.util.Map;

public final class Sound {

	private static Map<EnvironmentHost, Map<ResourceLocation, Float>> globalTimeouts = new HashMap<>();

	public static boolean play(EnvironmentHost host, ResourceLocation sound) {
		if (globalTimeouts.containsKey(host)) {
			Map<ResourceLocation, Float> timer = globalTimeouts.get(host);
			Float time = (timer.containsKey(sound)) ? timer.get(sound) : 0F;
			if (time <= System.currentTimeMillis()) {
				host.world().playSound(Minecraft.getMinecraft().thePlayer, host.xPosition(), host.yPosition(), host.zPosition(), new SoundEvent(sound), SoundCategory.BLOCKS, ConfigSettings.oc_soundvol, 1);
				timer.put(sound, System.currentTimeMillis() + 500F);
				return true;
			}
			return false;
		}
		else {
			Map<ResourceLocation, Float> timer = new HashMap<>();
			host.world().playSound(Minecraft.getMinecraft().thePlayer, host.xPosition(), host.yPosition(), host.zPosition(), new SoundEvent(sound), SoundCategory.BLOCKS, ConfigSettings.oc_soundvol, 1);
			timer.put(sound, System.currentTimeMillis() + 500F);
			globalTimeouts.put(host, timer);
			return true;
		}
	}

}
