package mods.ocminecart.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class EnergyBar {
	public static void drawBar(int posx, int posy, int h, int w,int zlevel, double percent, ResourceLocation bar){
		Tessellator tes = Tessellator.instance;
		Minecraft.getMinecraft().renderEngine.bindTexture(bar);
		
		RenderHelper.disableStandardItemLighting();
		
		percent = (percent > 1) ? 1 : percent;
		
		double dw = w * percent;
		
		tes.startDrawingQuads();
		tes.addVertexWithUV(posx + dw, posy + h, zlevel, percent, 1);
		tes.addVertexWithUV(posx + dw, posy, zlevel, percent, 0);
		tes.addVertexWithUV(posx, posy, zlevel, 0, 0);
		tes.addVertexWithUV(posx, posy + h, zlevel, 0, 1);
		tes.draw();
		
		RenderHelper.enableStandardItemLighting();
	}
}
