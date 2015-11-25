package mods.ocminecart.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import li.cil.oc.api.component.TextBuffer;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.client.KeyBindings;
import li.cil.oc.client.renderer.TextBufferRenderCache;
import li.cil.oc.client.renderer.gui.BufferRenderer;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.Settings;
import mods.ocminecart.client.SlotIcons;
import mods.ocminecart.client.gui.widget.EnergyBar;
import mods.ocminecart.client.gui.widget.ImageButton;
import mods.ocminecart.client.gui.widget.SliderButton;
import mods.ocminecart.common.container.ComputerCartContainer;
import mods.ocminecart.common.container.slots.ContainerSlot;
import mods.ocminecart.common.inventory.ComponetInventory;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.interaction.NEI;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.GuiButtonClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.ItemPanel;
import codechicken.nei.LayoutManager;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

//I used the code from OpenComputer's Robot GUI to make this class (ImageButton, Buffer renderer, Mouse & Keyboard handler, ...)
//Credits to Sangar
public class ComputerCartGui extends GuiContainer {
	
	//Resources
	private ResourceLocation textureNoScreen = new ResourceLocation( Settings.OC_ResLoc , "textures/gui/robot_noscreen.png");
	private ResourceLocation textureScreen = new ResourceLocation( Settings.OC_ResLoc , "textures/gui/robot.png");
	private ResourceLocation textureOnOffButton = new ResourceLocation( Settings.OC_ResLoc , "textures/gui/button_power.png");
	private ResourceLocation ebar = new ResourceLocation( Settings.OC_ResLoc , "textures/gui/bar.png");
	private ResourceLocation selection = new ResourceLocation( Settings.OC_ResLoc , "textures/gui/robot_selection.png");
	
	//Container (as instance of ComputerCartContainer)
	private ComputerCartContainer container;
	
	//Textbuffer and keyboard
	private int txtWidth, txtHeight;
	
	private double maxBufferWidth = 240.0;
	private double maxBufferHeight = 140.0;
	private double bufferscale = 0.0;
	private double bufferRenderWidth = Math.min(maxBufferWidth, TextBufferRenderCache.renderer().charRenderWidth() * 50);
	private double bufferRenderHeight = Math.min(maxBufferHeight, TextBufferRenderCache.renderer().charRenderHeight() * 16);
	private int bufferX = (int)(8 + (this.maxBufferWidth - this.bufferRenderWidth) /2);
	private int bufferY = (int)(8 + (this.maxBufferHeight - this.bufferRenderHeight) /2);
	private TextBuffer textbuffer;
	private boolean hasKeyboard=false;
	private Map<Integer, Character> pressedKeys = new HashMap<Integer, Character>();
	
	//Other stuff
	private ImageButton btPower;
	
	private Slot hoveredSlot = null;
	private ItemStack hoveredNEI = null;
	private SliderButton invslider = null;
	
	private boolean[] disSlot = new boolean[16];
	private boolean guiSizeChange = false;
	private int offset = 0;
	
//-------Init functions-------//
	
	public ComputerCartGui(InventoryPlayer inventory, ComputerCart entity) {
		super(new ComputerCartContainer(inventory,entity));
		this.container=(ComputerCartContainer) this.inventorySlots;
		
		this.initComponents(entity.compinv);
		
		this.ySize= (container.getHasScreen()) ? ComputerCartContainer.YSIZE_SCR : ComputerCartContainer.YSIZE_NOSCR;
		this.xSize= ComputerCartContainer.XSIZE;
		this.offset = (this.textbuffer!=null) ? ComputerCartContainer.DELTA : 0;
		this.invslider= new SliderButton(244,8 + offset, 6, 13, 94);
	}
	
	//Initialize components. get Screen and check if there is a Keyboard
	private void initComponents(ComponetInventory compinv){
		Iterator<ManagedEnvironment> list = compinv.getComponents().iterator();
		while(list.hasNext()){
			ManagedEnvironment env = list.next();
			if(env instanceof TextBuffer) this.textbuffer = (TextBuffer) env;
			else if(env instanceof li.cil.oc.server.component.Keyboard) this.hasKeyboard = true;
		}
	}
	
	public void initGui(){
		super.initGui();
		
		this.updateSlots();
		
		BufferRenderer.init(Minecraft.getMinecraft().renderEngine);
		this.guiSizeChange=true;
		
		this.txtHeight = (this.textbuffer!=null) ? this.textbuffer.getHeight() : 0;
		this.txtWidth = (this.textbuffer!=null) ? this.textbuffer.getWidth() : 0;
		
		BufferRenderer.compileBackground((int)this.bufferRenderWidth, (int)this.bufferRenderHeight, true);
		
		this.btPower = new ImageButton(0, this.guiLeft+5, 5+this.guiTop+offset, 18, 18, null, textureOnOffButton, true);
		
		this.buttonList.add(this.btPower);
		
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

//-------Override render functions-------//
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor3d(1F, 1F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture((container.getHasScreen())? textureScreen : textureNoScreen );
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		this.renderGuiSlots();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        if(this.container.getHasScreen() && this.textbuffer!=null){
            this.drawBufferLayer();

        	double bw = this.txtWidth * TextBufferRenderCache.renderer().charRenderWidth();
        	double bh = this.txtHeight * TextBufferRenderCache.renderer().charRenderHeight();
        	double scaleX = Math.min(this.bufferRenderWidth / bw , 1);
        	double scaleY = Math.min(this.bufferRenderHeight / bh , 1);
        	this.bufferscale = Math.min(scaleX, scaleY);
        }
        
        //Widgets
        EnergyBar.drawBar(26, 8 + offset, 12, 140, 150, (double)this.container.sEnergy / (double)this.container.smaxEnergy, ebar);
        this.invslider.drawSlider(this.zLevel, this.invslider.getAktive() || this.invslider.isMouseHoverButton(mx - this.guiLeft, my - this.guiTop));
        
        //Highlight
        Iterator<Slot> list = this.container.inventorySlots.iterator();
        while(list.hasNext()) this.drawSlotHighlight(list.next());
        
        //Render selected slot
        if(this.container.sizeinv > 0)
        	this.drawSelection();
        
        //Tooltips
        if(this.func_146978_c(this.btPower.xPosition, this.btPower.yPosition, 18, 18, mx+this.guiLeft, my+this.guiTop)){
        	List<String> ls = new ArrayList<String>();
        	if(this.container.getEntity().getRunning()){
        		 ls.add(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".gui.turnoff"));
        	}
        	else{
        		ls.add(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".gui.turnon"));
        		ls.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".gui.useanalyzer"));
        	}
            this.drawHoverText(ls, mx - this.guiLeft, my - this.guiTop, Minecraft.getMinecraft().fontRenderer);
        }
        if(this.func_146978_c(26+this.guiLeft, 8+this.guiTop+offset, 140, 12, mx+this.guiLeft, my+this.guiTop)){
        	List<String> ls = new ArrayList<String>();
        	int per = (int)(((double)this.container.sEnergy / (double)this.container.smaxEnergy)*100);
        	ls.add("Energy: "+per+"% ("+this.container.sEnergy+" / "+this.container.smaxEnergy+")");
        	this.drawHoverText(ls, mx - this.guiLeft, my - this.guiTop, Minecraft.getMinecraft().fontRenderer);
        }
        
        GL11.glPopAttrib();
	}
	
	public void drawScreen(int mx, int my, float dt){
		this.hoveredSlot=null;
		Iterator<Slot> list = this.container.inventorySlots.iterator();
		while(list.hasNext()){
			Slot slot = list.next();
			if(slot!=null){
				if(this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mx, my))
					this.hoveredSlot = slot;
			}
		}
		this.hoveredNEI = NEI.hoveredStack(this, mx, my);
		
		super.drawScreen(mx, my, dt);
		
		if (Loader.isModLoaded("NotEnoughItems")) {
		      GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		      this.drawNEIHighlight();
		      GL11.glPopAttrib();
		}
	}

//-------Events-------//
	
	protected void actionPerformed(GuiButton button) {
		switch(button.id){
		case 0:
			ModNetwork.channel.sendToServer(GuiButtonClick.entityButtonClick(this.container.getEntity(),0,1));
			break;
		}
	}
	
	public void updateScreen(){
		if(this.container.getEntity().getRunning() != btPower.getToggle()) btPower.setToggle(this.container.getEntity().getRunning());
		if(this.container.updatesize){
			this.invslider.scrollTo(0);
			this.invslider.setMaxsteps(this.container.sizeinv / 4 - 4);
			this.container.updatesize=false;
		}
		if(this.invslider.hasUpdate()){
			this.invslider.doneUpdate();
			this.updateSlots();
		}
	}
	
	protected void mouseClicked(int x, int y, int button) {
	    super.mouseClicked(x, y, button);
	    boolean isMiddleMouseButton = button == 2;
	    boolean isBoundMouseButton = KeyBindings.isPastingClipboard();
	    if (this.textbuffer != null && (isMiddleMouseButton || isBoundMouseButton)) {
	      if (this.hasKeyboard) {
	    	  this.textbuffer.clipboard(GuiScreen.getClipboardString(), null);
	      }
	    }
	    else if(button==0){
	    	if(this.invslider.isMouseHoverBox(x - this.guiLeft, y - this.guiTop))
	    		this.invslider.setAktive(true);
	    }
	 }
	
	protected void mouseMovedOrUp(int x, int y, int button){
		super.mouseMovedOrUp(x, y, button);
		if(button == 0 && this.invslider.getAktive()){
			this.invslider.setAktive(false);
		}
	}
	
	public void handleMouseInput() {
	    super.handleMouseInput();
	    if (Mouse.hasWheel() && Mouse.getEventDWheel() != 0) {
	      int mouseX = Mouse.getEventX() * width / mc.displayWidth - guiLeft;
	      int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop;
	      if (isCoordinateOverInventory(mouseX, mouseY) || 
	    		  invslider.isMouseHoverBox(mouseX, mouseY)) {
	        if (Math.signum(Mouse.getEventDWheel()) < 0) invslider.scrollDown();
	        else invslider.scrollUp();
	      }
	    }
	  }
	
	protected void mouseClickMove(int x, int y, int button, long time){
		super.mouseClickMove(x, y, button, time);
		if(this.invslider.getAktive()){
			this.invslider.scrollMouse(y - this.guiTop);
		}
	}
	
	 public void handleKeyboardInput() {
		    if (NEI.isInputFocused()) return;

		    int code = Keyboard.getEventKey();
		    
		    if (this.textbuffer != null && code != Keyboard.KEY_ESCAPE && code != Keyboard.KEY_F11) {
		      if (this.hasKeyboard) {
		        if (Keyboard.getEventKeyState()) {
		          char ch = Keyboard.getEventCharacter();
		          if (!pressedKeys.containsKey(code) || !ignoreRepeat(ch, code)) {
		            this.textbuffer.keyDown(ch, code, null);
		            pressedKeys.put(code, ch);
		          }
		        }
		        else{
		        	if(pressedKeys.containsKey(code)){
		        		this.textbuffer.keyUp(pressedKeys.remove(code), code, null);
		        	}
		        }
		        
		        if (KeyBindings.isPastingClipboard()) {
			          this.textbuffer.clipboard(GuiScreen.getClipboardString(), null);
			    }
			  }
		        	
		  }
		    else super.handleKeyboardInput();
	 }
	 
	 public void onGuiClosed(){
		super.onGuiClosed();
		if (this.textbuffer != null) 
			 for(Entry<Integer, Character> e : pressedKeys.entrySet()) {
				 this.textbuffer.keyUp(e.getValue(), e.getKey(), null);
			 }
		Keyboard.enableRepeatEvents(false);
	 }
	 
	 private boolean isCoordinateOverInventory(int x, int y){
		 return x >= 170 && x < 240 && y >= 8 + offset && y < 78 + offset;      
	 }
	 
	 private void updateSlots(){
		 Iterator<Slot> slots = this.container.inventorySlots.iterator();
		 while(slots.hasNext()){
			 Slot s = slots.next();
			 int index = s.getSlotIndex() - this.invslider.getScroll() * 4;
			 if(s.inventory.equals(this.container.getEntity().maininv)){
				 if(index>=0 && index < 16 && s.getSlotIndex() < this.container.sizeinv){
					 s.xDisplayPosition = 170 + (index % 4) * 18;
					 s.yDisplayPosition = 8 + offset + (index / 4) * 18;
					 this.disSlot[index] = false;
				 }
				 else{
					 s.xDisplayPosition = -10000;
					 s.yDisplayPosition = -10000;
					 if(index>=0 && index < 16) this.disSlot[index] = true;
				 }
			 }
		 }
	 }
	
//-------Render functions----------//
	 
	//Fuction from OC. used in handleKeyboardInput()
	private boolean ignoreRepeat(char ch, int code) {
	    return code == Keyboard.KEY_LCONTROL ||
	      code == Keyboard.KEY_RCONTROL ||
	      code == Keyboard.KEY_LMENU ||
	      code == Keyboard.KEY_RMENU ||
	      code == Keyboard.KEY_LSHIFT ||
	      code == Keyboard.KEY_RSHIFT ||
	      code == Keyboard.KEY_LMETA ||
	      code == Keyboard.KEY_RMETA;
	  }
	
	//Render the Highlight for Components
	protected void drawSlotHighlight(Slot slot) {
		if(Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null){
			boolean highlight = false;
			if(!(slot instanceof ContainerSlot) || (((ContainerSlot)slot).getSlotType() != "none" && ((ContainerSlot)slot).getTier() != -1)){
				boolean inPlayerInv = slot.inventory == Minecraft.getMinecraft().thePlayer.inventory;
				if(this.hoveredSlot!=null){
					if(this.hoveredSlot.getHasStack() && (slot instanceof ContainerSlot) && slot.isItemValid(this.hoveredSlot.getStack())) highlight=true;
					else if(slot.getHasStack() && (this.hoveredSlot instanceof ContainerSlot) && this.hoveredSlot.isItemValid(slot.getStack()))
						highlight=true;
				}
				else{
					if(this.hoveredNEI!=null && (slot instanceof ContainerSlot) && slot.isItemValid(this.hoveredNEI)){
						highlight=true;
					}
				}
			}
			
			if(highlight){
				this.zLevel += 100;
				this.drawGradientRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
				this.zLevel -= 100;
			}
		}
	}
	
	//Render the Highlight for NEI Slots
	@Optional.Method(modid = "NotEnoughItems")
	private void drawNEIHighlight(){
		ItemPanel panel = LayoutManager.itemPanel;
		if(panel == null) return;
		this.zLevel += 350;
		for(int i=0;i<ItemPanel.items.size();i+=1){
			Rectangle4i rect = panel.getSlotRect(i);
			ItemStack slot = panel.getStackMouseOver(rect.x, rect.y);
			if(slot!=null && this.hoveredSlot!=null){
				if((this.hoveredSlot.inventory != Minecraft.getMinecraft().thePlayer.inventory) && (this.hoveredSlot instanceof ContainerSlot) && this.hoveredSlot.isItemValid(slot)){
					 drawGradientRect( rect.x1() + 1, rect.y1() + 1, rect.x2(), rect.y2(), 0x40FFFFFF, 0x40FFFFFF);
				}
			}
		}
		this.zLevel -= 350;
	}
	
	//Render Tooltips
	private void drawHoverText(List<String> text, int x, int y, FontRenderer font){
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
		      if (posX + textWidth > this.width - this.guiLeft) {
		    	  
		          posX -= 28 + textWidth;
		      }
		      if (posY + textHeight + 6 > this.height) {
		          posY = this.height - textHeight - 6;
		      }
		      
		      this.zLevel = 300;
		      int bg = 0xF0100010;
		      this.drawGradientRect(posX - 3, posY - 4, posX + textWidth + 3, posY - 3, bg, bg);
		      this.drawGradientRect(posX - 3, posY + textHeight + 3, posX + textWidth + 3, posY + textHeight + 4, bg, bg);
		      this.drawGradientRect(posX - 3, posY - 3, posX + textWidth + 3, posY + textHeight + 3, bg, bg);
		      this.drawGradientRect(posX - 4, posY - 3, posX - 3, posY + textHeight + 3, bg, bg);
		      this.drawGradientRect(posX + textWidth + 3, posY - 3, posX + textWidth + 4, posY + textHeight + 3, bg, bg);
		      int color1 = 0x505000FF;
		      int color2 = 0x505000FE;
		      this.drawGradientRect(posX - 3, posY - 3 + 1, posX - 3 + 1, posY + textHeight + 3 - 1, color1, color2);
		      this.drawGradientRect(posX + textWidth + 2, posY - 3 + 1, posX + textWidth + 3, posY + textHeight + 3 - 1, color1, color2);
		      this.drawGradientRect(posX - 3, posY - 3, posX + textWidth + 3, posY - 3 + 1, color1, color1);
		      this.drawGradientRect(posX - 3, posY + textHeight + 2, posX + textWidth + 3, posY + textHeight + 3, color2, color2);
		      
		      for(int i=0;i<text.size();i+=1){
		    	  font.drawStringWithShadow(text.get(i), posX, posY, -1);
		    	  if (i == 0) {
		              posY += 2;
		          }
		    	  posY += 10;
		      }
		      this.zLevel = 0;
		      
		      GL11.glEnable(GL11.GL_LIGHTING);
		      GL11.glEnable(GL11.GL_DEPTH_TEST);
		      RenderHelper.enableStandardItemLighting();
		      GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
	
	//Draw Screen if there is one
	private void drawBufferLayer(){
		GL11.glPushMatrix();
		GL11.glTranslatef(bufferX, bufferY, 0);
			Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
			RenderHelper.disableStandardItemLighting();
			GL11.glPushMatrix();
			GL11.glTranslatef(-3, -3, 0);
			GL11.glColor4f(1, 1, 1, 1);
			BufferRenderer.drawBackground();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_BLEND);
		    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			double scaleX = bufferRenderWidth / this.textbuffer.renderWidth();
	      	double scaleY = bufferRenderHeight / this.textbuffer.renderHeight();
	      	double scale = Math.min(scaleX, scaleY);
	      	if (scaleX > scale) {
	      		GL11.glTranslated(this.textbuffer.renderWidth() * (scaleX - scale) / 2, 0, 0);
	      	}
	      	else if (scaleY > scale) {
	      		GL11.glTranslated(0,this.textbuffer.renderHeight() * (scaleY - scale) / 2, 0);
	      	}
	      	GL11.glScaled(scale, scale, scale);
	      	GL11.glScaled(this.bufferscale, this.bufferscale, 1);
	      	BufferRenderer.drawText(this.textbuffer);
	      	RenderHelper.enableStandardItemLighting();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
	}

	//Render the Background Icons
	private void renderGuiSlots(){
		Iterator<Slot> list = this.container.inventorySlots.iterator();
		this.mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		IIcon non = SlotIcons.fromTier(-1);
		this.drawTexturedModelRectFromIcon(this.guiLeft+170, this.guiTop+84+offset, non, 16, 16);
		
		//Render the Icons for Container Slots
		while(list.hasNext()){
			Slot slot = list.next();
			if(slot instanceof ContainerSlot){
				IIcon typeicon = SlotIcons.fromSlot(((ContainerSlot) slot).getSlotType());
				if(typeicon!=null) this.drawTexturedModelRectFromIcon(this.guiLeft+slot.xDisplayPosition,this.guiTop+slot.yDisplayPosition, typeicon, 16, 16);
			}
		}
		//Render Icons for disabled Inventory Slots
		for(int i=0;i<4;i+=1){
			for(int j=0;j<4;j+=1){
				int xpos = this.guiLeft + 170 + i * 18;
				int ypos = this.guiTop + 8 + offset + j * 18;
				if(this.disSlot[i*j]){
					this.drawTexturedModelRectFromIcon(xpos,ypos, SlotIcons.fromTier(-1), 16, 16);
				}
			}
		}
	}
	private void drawSelection() {
		int slot = this.container.selSlot - this.invslider.getScroll() * 4;
	    if (slot >= 0 && slot < 16) {
	    	GL11.glEnable(GL11.GL_BLEND);
	    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    	RenderHelper.disableStandardItemLighting();
	    	Minecraft.getMinecraft().renderEngine.bindTexture(selection);
	    	double now = System.currentTimeMillis() / 1000.0;
	    	double offsetV = (int)((now % 1) * 17) / 17D;
	    	int x = 168 + (slot % 4) * (18);
	    	int y = 6 + offset + (slot / 4) * (18);
	    	
	    	//OCMinecart.logger.info(x+" : "+y);
	    	
	    	Tessellator t = Tessellator.instance;
	    	t.startDrawingQuads();
	      	t.addVertexWithUV(x, y, zLevel, 0, offsetV);
	      	t.addVertexWithUV(x, y + 20, zLevel, 0, offsetV + 1D/17D);
	      	t.addVertexWithUV(x + 20, y + 20, zLevel, 1, offsetV + 1D/17D);
	      	t.addVertexWithUV(x + 20, y, zLevel, 1, offsetV);
	      	t.draw();
	      	RenderHelper.enableStandardItemLighting();
	      	GL11.glDisable(GL11.GL_BLEND);
	    }
	  }
}
