package mods.ocminecart.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ComputerCartModel extends ModelBase {
	ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
  
  public ComputerCartModel()
  {
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
  
  
  public void renderTile(Entity entity,float f)
  {
    Shape1.render(f);
    Shape3.render(f);
    if(((ComputerCart) entity).machine().isRunning()){
    	ComputerCart cart = (ComputerCart) entity;
    	GL11.glDisable(GL11.GL_LIGHTING);
    	Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glColor3d(cart.getRGBColor()[0], cart.getRGBColor()[1], cart.getRGBColor()[2]);
    }
    else GL11.glColor3d(0,0,0);
    Shape2.render(f);
    GL11.glEnable(GL11.GL_LIGHTING);
    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
    GL11.glColor3d(1,1,1);
  }
  
  public void renderItem(float f){
	  Shape1.render(f);
	  Shape3.render(f);
	  GL11.glColor3d(0,0,0);
	  	Shape2.render(f);
	  GL11.glColor3d(1,1,1);
  }
}
