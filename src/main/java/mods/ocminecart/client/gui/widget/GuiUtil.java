package mods.ocminecart.client.gui.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class GuiUtil {
	
	//Render Tooltips
		public static void drawHoverText(List<String> text, int x, int y, int width, int height, int guiLeft, FontRenderer font){
			if(!text.isEmpty()){
			      GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			      RenderHelper.disableStandardItemLighting();
			      GL11.glDisable(GL11.GL_LIGHTING);
			      GL11.glDisable(GL11.GL_DEPTH_TEST);
			      
			      int textWidth = -1;
			      for(int i=0;i<text.size();i+=1){
			    	  if(font.getStringWidth(text.get(i)) > textWidth)
			    		  textWidth = font.getStringWidth(text.get(i));
			      }
			      
			      int posX = x + 12;
			      int posY = y - 12;
			      int textHeight = 8;
			      
			      if (text.size() > 1) {
			          textHeight += 2 + (text.size() - 1) * 10;
			      }
			      if (posX + textWidth > width - guiLeft) {
			    	  
			          posX -= 28 + textWidth;
			      }
			      if (posY + textHeight + 6 > height) {
			          posY = height - textHeight - 6;
			      }
			      
			      int zLevel = 300;
			      int bg = 0xF0100010;
			      drawGradientRect(posX - 3, posY - 4, posX + textWidth + 3, posY - 3, zLevel,bg, bg);
			      drawGradientRect(posX - 3, posY + textHeight + 3, posX + textWidth + 3, posY + textHeight + 4, zLevel,bg, bg);
			      drawGradientRect(posX - 3, posY - 3, posX + textWidth + 3, posY + textHeight + 3, zLevel,bg, bg);
			      drawGradientRect(posX - 4, posY - 3, posX - 3, posY + textHeight + 3, zLevel,bg, bg);
			      drawGradientRect(posX + textWidth + 3, posY - 3, posX + textWidth + 4, posY + textHeight + 3, zLevel,bg, bg);
			      int color1 = 0x505000FF;
			      int color2 = 0x505000FE;
			      drawGradientRect(posX - 3, posY - 3 + 1, posX - 3 + 1, posY + textHeight + 3 - 1, zLevel,color1, color2);
			      drawGradientRect(posX + textWidth + 2, posY - 3 + 1, posX + textWidth + 3, posY + textHeight + 3 - 1,zLevel, color1, color2);
			      drawGradientRect(posX - 3, posY - 3, posX + textWidth + 3, posY - 3 + 1, zLevel, color1, color1);
			      drawGradientRect(posX - 3, posY + textHeight + 2, posX + textWidth + 3, posY + textHeight + 3,zLevel, color2, color2);
			      
			      for(int i=0;i<text.size();i+=1){
			    	  font.drawStringWithShadow(text.get(i), posX, posY, -1);
			    	  if (i == 0) {
			              posY += 2;
			          }
			    	  posY += 10;
			      }
			      
			      GL11.glEnable(GL11.GL_LIGHTING);
			      GL11.glEnable(GL11.GL_DEPTH_TEST);
			      RenderHelper.enableStandardItemLighting();
			      GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			}
		}
	
		public static void drawGradientRect(int posX, int posY, int width, int height, int zLevel, int c1, int c2){
			float f = (float)(c1 >> 24 & 255) / 255.0F;
	        float f1 = (float)(c1 >> 16 & 255) / 255.0F;
	        float f2 = (float)(c1 >> 8 & 255) / 255.0F;
	        float f3 = (float)(c1 & 255) / 255.0F;
	        float f4 = (float)(c2 >> 24 & 255) / 255.0F;
	        float f5 = (float)(c2 >> 16 & 255) / 255.0F;
	        float f6 = (float)(c2 >> 8 & 255) / 255.0F;
	        float f7 = (float)(c2 & 255) / 255.0F;
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
	        Tessellator tessellator = Tessellator.instance;
	        tessellator.startDrawingQuads();
	        tessellator.setColorRGBA_F(f1, f2, f3, f);
	        tessellator.addVertex((double)width, (double)posY, (double)zLevel);
	        tessellator.addVertex((double)posX, (double)posY, (double)zLevel);
	        tessellator.setColorRGBA_F(f5, f6, f7, f4);
	        tessellator.addVertex((double)posX, (double)height, (double)zLevel);
	        tessellator.addVertex((double)width, (double)height, (double)zLevel);
	        tessellator.draw();
	        GL11.glShadeModel(GL11.GL_FLAT);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
}
