package mods.ocminecart.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public final class TextureHelper {

	private static TextureAtlasSprite missing;
	private static Map<String, TextureAtlasSprite> spriteMap = new HashMap<>();

	public static void loadTextures(TextureMap map) {
		missing = map.getMissingSprite();
		for (String tex : prepareTextures()) {
			spriteMap.put(tex, map.registerSprite(new ResourceLocation(tex)));
		}
	}

	public static TextureAtlasSprite getSprite(String tex) {
		TextureAtlasSprite sprite = spriteMap.get(tex);
		return sprite == null ? missing : sprite;
	}

	public static TextureAtlasSprite getSpriteUnsafe(String tex) {
		return spriteMap.get(tex);
	}

	public static TextureAtlasSprite getMissing(){
		return missing;
	}

	private static List<String> prepareTextures(){
		List<String> preparing = new LinkedList<>();

		preparing.addAll(SlotIcons.getIconResources());

		return preparing;
	}

	private TextureHelper() {

	}
}
