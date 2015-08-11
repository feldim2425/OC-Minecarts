package mods.ocminecart.client.renderer.item;

import org.lwjgl.opengl.GL11;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.client.renderer.entity.ComputerCartModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class ComputerCartItemRenderer implements IItemRenderer {
	
	ResourceLocation texture =  new ResourceLocation(OCMinecart.MODID+":textures/entity/computercart.png");
	ComputerCartModel model = new ComputerCartModel();
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch(type){
		case ENTITY: 
			return true;
		case EQUIPPED:
			return true;
		case EQUIPPED_FIRST_PERSON:
			return true;
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		if(type==ItemRenderType.ENTITY &&  (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION)) return true;
		else if(type==ItemRenderType.INVENTORY && helper == ItemRendererHelper.INVENTORY_BLOCK) return true;
		else if(type==ItemRenderType.EQUIPPED_FIRST_PERSON && helper==ItemRendererHelper.BLOCK_3D) return true;
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(type==ItemRenderType.EQUIPPED){
			GL11.glPushMatrix();
			GL11.glRotatef(130.0F,1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.7F, 0F, 0F);
			GL11.glScaled(0.7, 0.7, 0.7);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.renderItem(0.0625F);
			GL11.glPopMatrix();
		}
		else if(type==ItemRenderType.ENTITY){
			GL11.glPushMatrix();
			GL11.glRotated(180F, 1F, 0, 0);
			GL11.glScaled(0.7, 0.7, 0.7);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.renderItem(0.0625F);
			GL11.glPopMatrix();
		}else if(type==ItemRenderType.INVENTORY){
			GL11.glPushMatrix();
			GL11.glRotatef(180.0F,1.0F, 0.0F, 0.0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.renderItem(0.0625F);
			GL11.glPopMatrix();
		}
		else if(type==ItemRenderType.EQUIPPED_FIRST_PERSON){
			GL11.glPushMatrix();
			GL11.glRotatef(180.0F,1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(1F, 0F, 0.5F);
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
			model.renderItem(0.0625F);
			GL11.glPopMatrix();
		}
	}

}
