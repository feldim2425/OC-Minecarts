package mods.ocminecart.client.render.item;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import mods.ocminecart.client.model.entity.ModelComputerCart;
import mods.ocminecart.client.texture.ResourceTexture;
import mods.ocminecart.client.texture.TextureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;


public class RendererComputerCartItem implements IItemRenderer, IPerspectiveAwareModel {

	private ModelComputerCart model = new ModelComputerCart();
	private ItemCameraTransforms.TransformType transformType;

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return TextureHelper.getSprite("");
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@Override
	public void renderItem(ItemStack item) {
		GlStateManager.pushMatrix();
		if (transformType == ItemCameraTransforms.TransformType.GUI) {
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.scale(0.0375, 0.0375, 0.0375);
			GlStateManager.rotate(30f, 1, 0, 0);
			GlStateManager.rotate(225f, 0, 1, 0);
			GlStateManager.rotate(180f, 1, 0, 0);
		}
		else {
			GlStateManager.translate(0.5, 0.175, 0.5);
			GlStateManager.scale(0.04, 0.04, 0.04);
			GlStateManager.rotate(180f, 1, 0, 0);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceTexture.ENTITY_COMPUTERCART.location);
		model.render(null, 0, 0, 0, 0, 0, 1f);
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		transformType = cameraTransformType;
		return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_ITEM.getTransforms(), cameraTransformType);
	}

}
