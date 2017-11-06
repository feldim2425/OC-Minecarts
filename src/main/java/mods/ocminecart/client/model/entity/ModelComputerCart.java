package mods.ocminecart.client.model.entity;

import mods.ocminecart.common.entity.EntityComputerCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;


public class ModelComputerCart extends ModelBase {

	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;

	public ModelComputerCart() {
		textureWidth = 256;
		textureHeight = 64;

		Shape1 = new ModelRenderer(this, 0, 0);
		Shape1.addBox(0F, 0F, 0F, 16, 7, 20);
		Shape1.setRotationPoint(-8F, -1F, -10F);
		Shape1.setTextureSize(64, 32);
		Shape1.mirror = true;
		Shape2 = new ModelRenderer(this, 72, 0);
		Shape2.addBox(0F, 0F, 0F, 14, 1, 18);
		Shape2.setRotationPoint(-7F, -2F, -9F);
		Shape2.setTextureSize(64, 32);
		Shape2.mirror = true;
		Shape3 = new ModelRenderer(this, 0, 27);
		Shape3.addBox(0F, 0F, 0F, 16, 4, 20);
		Shape3.setRotationPoint(-8F, -6F, -10F);
		Shape3.setTextureSize(64, 32);
		Shape3.mirror = true;
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		EntityComputerCart cart = (EntityComputerCart) entity;
		Shape1.render(scale);
		Shape3.render(scale);
		if (cart != null && cart.isRunning()) {
			int color = cart.getLightColor();
			float r = ((color >> 16) & 0xFF) / 255F;
			float g = ((color >> 8) & 0xFF) / 255F;
			float b = (color & 0xFF) / 255F;
			GlStateManager.disableLighting();
			Minecraft.getMinecraft().entityRenderer.disableLightmap();
			GlStateManager.enableBlend();
			GlStateManager.color(r, g, b);
		} else {
			GlStateManager.color(0, 0, 0);
		}
		Shape2.render(scale);
		if (entity != null) {
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			Minecraft.getMinecraft().entityRenderer.enableLightmap();
		}
	}

	public void renderItem(float scale) {
		Shape1.render(scale);
		Shape3.render(scale);
		GlStateManager.color(0, 0, 0);
		Shape2.render(scale);
	}
}
