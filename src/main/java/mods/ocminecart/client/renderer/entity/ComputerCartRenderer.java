package mods.ocminecart.client.renderer.entity;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class ComputerCartRenderer extends Render {
	
	private static final ResourceLocation minecartTextures = new ResourceLocation(OCMinecart.MODID+":textures/entity/computercart.png");
	protected ComputerCartModel modelMinecart = new ComputerCartModel();
	 
	@Override
	public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
		
		ComputerCart cart=(ComputerCart)entity;
		
		GL11.glPushMatrix();
        this.bindEntityTexture(cart);
        long i = (long)cart.getEntityId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f2 = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f3 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f4 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(f2, f3, f4);
        double d3 = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * (double)p_76986_9_;
        double d4 = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * (double)p_76986_9_;
        double d5 = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * (double)p_76986_9_;
        double d6 = 0.30000001192092896D;
        Vec3 vec3 = cart.func_70489_a(d3, d4, d5);
        float f5 = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * p_76986_9_;

        if (vec3 != null)
        {
            Vec3 vec31 = cart.func_70495_a(d3, d4, d5, d6);
            Vec3 vec32 = cart.func_70495_a(d3, d4, d5, -d6);

            if (vec31 == null)
            {
                vec31 = vec3;
            }

            if (vec32 == null)
            {
                vec32 = vec3;
            }

            x += vec3.xCoord - d3;
            y += (vec31.yCoord + vec32.yCoord) / 2.0D - d4;
            z += vec3.zCoord - d5;
            Vec3 vec33 = vec32.addVector(-vec31.xCoord, -vec31.yCoord, -vec31.zCoord);

            if (vec33.lengthVector() != 0.0D)
            {
                vec33 = vec33.normalize();
                p_76986_8_ = (float)(Math.atan2(vec33.zCoord, vec33.xCoord) * 180.0D / Math.PI);
                f5 = (float)(Math.atan(vec33.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(90.0F - p_76986_8_,0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f5, 1.0F, 0.0F, 0.0F);
        float f7 = (float)cart.getRollingAmplitude() - p_76986_9_;
        float f8 = cart.getDamage() - p_76986_9_;

        if (f8 < 0.0F)
        {
            f8 = 0.0F;
        }

        if (f7 > 0.0F)
        {
            GL11.glRotatef(MathHelper.sin(f7) * f7 * f8 / 10.0F * (float)cart.getRollingDirection(), 0.0F, 0.0F, 1.0F);
        }
        
        GL11.glColor3f(1, 1, 1);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.renderTile(cart, 0.0625F);
        GL11.glPopMatrix();
		
	}


	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return minecartTextures;
	}

}
