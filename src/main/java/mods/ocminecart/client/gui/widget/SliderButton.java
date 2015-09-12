package mods.ocminecart.client.gui.widget;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class SliderButton {
	
	private ResourceLocation texture;
	private int w;
	private int h;
	private int maxh;
	private int posx;
	private int posy;
	private int slidery;
	private boolean aktivate = false;
	
	private int maxsteps = 0;
	private int scroll;
	private boolean update = false;
	
	public SliderButton(int posx, int posy ,int w, int h, int maxh){
		this.texture = new ResourceLocation( Settings.OC_ResLoc , "textures/gui/button_scroll.png");;
		this.w = w;
		this.h = h;
		this.maxh = maxh;
		this.posx = posx;
		this.posy = posy;
		this.slidery = 0;
	}
	
	public void drawSlider(float zLevel ,boolean highlight){
		
		Tessellator tes = Tessellator.instance;
		
		double v0 = (highlight) ? 0.5 : 0;
		double v1 = v0 + 0.5;
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		RenderHelper.disableStandardItemLighting();
		
		tes.startDrawingQuads();
		tes.addVertexWithUV(posx + w, posy + h + slidery, zLevel, 1, v1);
		tes.addVertexWithUV(posx + w, posy + slidery, zLevel, 1, v0);
		tes.addVertexWithUV(posx, posy + slidery, zLevel, 0, v0);
		tes.addVertexWithUV(posx, posy + h + slidery, zLevel, 0, v1);
		tes.draw();
	
		RenderHelper.enableStandardItemLighting();
	}
	
	public boolean isMouseHoverBox(int mx, int my){
		return mx >= posx - 1 && mx < posx + w + 1 && my >= posy - 1 && my < posy + maxh + 1;
	}
	
	public boolean isMouseHoverButton(int mx, int my){
		return mx >= posx - 1 && mx < posx + w + 1 && my >= posy + slidery - 1 && my < posy + slidery + h + 1;
	}
	
	public void scrollMouse(int my){
		this.scrollTo((int)Math.round((my - posy + 1 - 6.5  ) * (maxsteps-1) / (maxh - 13.0)));
	}
	
	public void scrollTo(int pos){
		if(pos<0) this.scroll = 0;
		else if(pos>this.maxsteps) this.scroll = this.maxsteps;
		else this.scroll = pos;
		
		this.slidery = this.maxsteps<1 ? 0 : (this.maxh - this.h - 2) * this.scroll / this.maxsteps;
		this.update=true;
	}
	
	public int getScroll(){
		return this.scroll;
	}
	
	public void scrollDown() {
		this.scrollTo(this.scroll + 1);
	}
	
	public void scrollUp() {
		this.scrollTo(this.scroll - 1);
	}
	
	/*----------Setter/Getter---------*/
	
	public void setAktive(boolean state){
		this.aktivate = state;
	}
	
	public boolean getAktive(){
		return this.aktivate;
	}

	public int getMaxsteps() {
		return maxsteps;
	}

	public void setMaxsteps(int maxsteps) {
		if(maxsteps<0) maxsteps = 0;
		this.maxsteps = maxsteps;
	}
	
	public boolean hasUpdate(){
		return this.update;
	}
	
	public void doneUpdate(){
		this.update = false;
	}
}
