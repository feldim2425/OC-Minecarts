package mods.ocminecart.client.renderer.gui;

import li.cil.oc.api.component.TextBuffer;
import mods.ocminecart.Settings;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

//This Class is just a Java rewrite of li.cil.oc.common.renderer.gui.BufferRenderer (Credits to Sangar)
public class BufferRenderer {
	
	private final ResourceLocation Gui_Borders = new ResourceLocation(Settings.OC_ResLoc , "textures/gui/borders.png");
	private final int margin = 2;
	private final int innermargin = 1;
	
	private TextureManager texturemanager;
	private int displayLists = 0;
	
	public BufferRenderer(TextureManager tm){
		this.texturemanager=tm;
		this.displayLists=GLAllocation.generateDisplayLists(2);
	}
	
	public void compileBackground(int bufferWidth, int bufferHigth){
		if(this.texturemanager!=null){
			int innerWidth = this.innermargin * 2 + bufferWidth;
			int innerHeight = this.innermargin * 2 + bufferHigth;
			
			GL11.glNewList(this.displayLists, GL11.GL_COMPILE);
			
			int c0 = 5;
			int c1 = 7;
			int c2 = 9;
			int c3 = 11;
			
		    drawBorder(0, 0, margin, margin, c0, c0, c1, c1);
		    drawBorder(margin, 0, innerWidth, margin, c1 + 0.25, c0, c2 - 0.25, c1);
		    drawBorder(margin + innerWidth, 0, margin, margin, c2, c0, c3, c1);
		    
		    drawBorder( 0, margin, margin, innerHeight, c0, c1 + 0.25, c1, c2 - 0.25);
		    drawBorder( margin, margin, innerWidth, innerHeight, c1 + 0.25, c1 + 0.25, c2 - 0.25, c2 - 0.25);
		    drawBorder( margin + innerWidth, margin, margin, innerHeight, c2, c1 + 0.25, c3, c2 - 0.25);
		    
		    drawBorder( 0, margin + innerHeight, margin, margin, c0, c2, c1, c3);
		    drawBorder( margin, margin + innerHeight, innerWidth, margin, c1 + 0.25, c2, c2 - 0.25, c3);
		    drawBorder( margin + innerWidth, margin + innerHeight, margin, margin, c2, c2, c3, c3);
		    
		    GL11.glEnd();
		    GL11.glEndList();
		}
	}
	
	public void drawBackground(){
		if(this.texturemanager!=null){
			GL11.glCallList(this.displayLists);
		}
	}
	
	public boolean drawText(TextBuffer buffer){
		boolean ret = false;
		if(this.texturemanager!=null){
			GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glDepthMask(false);
			ret = buffer.renderText();
			GL11.glPopAttrib();
		}
		return ret;
	}
	
	private void drawBorder(double x, double y, double w, double h, double u1, double v1, double u2, double v2){
		double u1d = u1 / 16D;
		double v1d = v1 / 16D;
		double u2d = u2 / 16D;
		double v2d = v2 / 16D;
		
		GL11.glTexCoord2d(u1d, v2d);
		GL11.glVertex3d(x, y + h, 0);
		GL11.glTexCoord2d(u2d, v2d);
		GL11.glVertex3d(x + w, y + h, 0);
		GL11.glTexCoord2d(u2d, v1d);
		GL11.glVertex3d(x + w, y, 0);
		GL11.glTexCoord2d(u1d, v1d);
		GL11.glVertex3d(x, y, 0);
	}
	
}
