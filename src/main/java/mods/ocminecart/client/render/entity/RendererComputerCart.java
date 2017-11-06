package mods.ocminecart.client.render.entity;

import mods.ocminecart.client.model.entity.ModelComputerCart;
import mods.ocminecart.client.texture.ResourceTexture;
import mods.ocminecart.common.entity.EntityComputerCart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class RendererComputerCart extends Render<EntityComputerCart> {

	private final ModelComputerCart model = new ModelComputerCart();

	public RendererComputerCart(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityComputerCart entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		this.bindEntityTexture(entity);

		double cx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double cy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double cz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

		double yaw = entityYaw;
		float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

		Vec3d vecPos = entity.getPos(cx, cy, cz);

		if (vecPos != null) {
			Vec3d vec3d1 = entity.getPosOffset(cx, cy, cz, 0.30000001192092896D);
			Vec3d vec3d2 = entity.getPosOffset(cx, cy, cz, -0.30000001192092896D);

			if (vec3d1 == null) {
				vec3d1 = vecPos;
			}

			if (vec3d2 == null) {
				vec3d2 = vecPos;
			}

			x += vecPos.xCoord - cx;
			y += (vec3d1.yCoord + vec3d2.yCoord) / 2.0D - cy;
			z += vecPos.zCoord - cz;
			Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);

			if (vec3d3.lengthVector() != 0.0D) {
				vec3d3 = vec3d3.normalize();
				yaw = (float) (Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180.0D / Math.PI);
				pitch = (float) (Math.atan(vec3d3.yCoord) * 73.0D);
			}
		}

		double ryaw = (entity.rotationYaw + 360D) % 360;
		yaw = (yaw + 360D) % 360D;
		ryaw = yaw - ryaw;
		if (ryaw <= -90 || ryaw >= 90) {
			yaw += 180D;
			pitch *= -1;
		}
		yaw = 90F - yaw;

		y += 0.45D;

		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.rotate((float) yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
		float rollamp = (float) entity.getRollingAmplitude() - partialTicks;
		float dmgamp = entity.getDamage() - partialTicks;

		if (dmgamp < 0.0F)
			dmgamp = 0.0F;

		if (rollamp > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(rollamp) * rollamp * dmgamp / 10.0F * (float) entity.getRollingDirection(), 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.color(1, 1, 1);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.model.render(entity, 0, 0, 0, 0, 0, 0.0625F);
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityComputerCart entity) {
		return ResourceTexture.ENTITY_COMPUTERCART.location;
	}

}
