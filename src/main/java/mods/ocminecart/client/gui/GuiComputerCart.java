package mods.ocminecart.client.gui;

import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.client.KeyBindings;
import li.cil.oc.client.renderer.TextBufferRenderCache;
import mods.ocminecart.client.render.gui.BufferRenderer;
import mods.ocminecart.client.texture.ResourceTexture;
import mods.ocminecart.client.texture.SlotIcons;
import mods.ocminecart.common.container.ContainerComputerCart;
import mods.ocminecart.common.container.slots.SlotComponent;
import mods.ocminecart.common.driver.CustomDriverRegistry;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.integration.jei.JeiAdapter;
import mods.ocminecart.utils.ContainerUtil;
import mods.ocminecart.utils.ItemStackUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiComputerCart extends GuiContainer {

	private final double maxBufferWidth = 240.0;
	private final double maxBufferHeight = 140.0;
	private final double bufferRenderWidth = Math.min(maxBufferWidth, TextBufferRenderCache.renderer().charRenderWidth() * 50);
	private final double bufferRenderHeight = Math.min(maxBufferHeight, TextBufferRenderCache.renderer().charRenderHeight() * 16);
	private final int bufferX = (int)(8 + (this.maxBufferWidth - this.bufferRenderWidth) /2);
	private final int bufferY = (int)(8 + (this.maxBufferHeight - this.bufferRenderHeight) /2);
	private double bufferscale;
	private int txtWidth;
	private int txtHeight;

	private TextBuffer screen;
	private boolean keyboard;
	private Map<Integer, Character> pressedKeys = new HashMap<Integer, Character>();
	private int currentSlot = -1;

	public GuiComputerCart(EntityComputerCart entityComputerCart, EntityPlayer player) {
		super(new ContainerComputerCart(entityComputerCart, player));
	}

	@Override
	public void initGui(){
		screen = ((ContainerComputerCart)inventorySlots).findScreen();
		keyboard = ((ContainerComputerCart)inventorySlots).hasKeyboard();

		this.ySize= (screen == null) ? ContainerComputerCart.YSIZE_NOSCR :ContainerComputerCart.YSIZE_SCR;
		this.xSize= ContainerComputerCart.XSIZE;

		super.initGui();

		//this.updateSlots();

		BufferRenderer.init(Minecraft.getMinecraft().renderEngine);
		boolean guiSizeChange = true;

		BufferRenderer.compileBackground((int)this.bufferRenderWidth, (int)this.bufferRenderHeight, true);
		if(this.screen!=null) {
			this.txtHeight = this.screen.getHeight();
			this.txtWidth = this.screen.getWidth();
		}

		/*this.btPower = new ImageButton(0, this.guiLeft+5, 5+this.guiTop+offset, 18, 18, null, textureOnOffButton, true);

		this.buttonList.add(this.btPower);*/

		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (this.screen != null){
			for (Map.Entry<Integer, Character> e : pressedKeys.entrySet()) {
				this.screen.keyUp(e.getValue(), e.getKey(), null);
			}
		}
		Keyboard.enableRepeatEvents(false);
		JeiAdapter.drawHighlights(Collections.emptyList());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		Minecraft.getMinecraft().getTextureManager().bindTexture((screen==null)? ResourceTexture.OC_GUI_ROBOT_NS.location : ResourceTexture.OC_GUI_ROBOT.location );
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		this.renderGuiSlots();
	}

	private void renderGuiSlots() {
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for(Slot slot : this.inventorySlots.inventorySlots){
			if(ItemStackUtil.isStackEmpty(slot.getStack()) && (slot instanceof SlotComponent)){
				TextureAtlasSprite typeicon = SlotIcons.fromType(((SlotComponent) slot).getSlotType());
				if(typeicon!=null) {
					this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition,this.guiTop+slot.yDisplayPosition, typeicon, 16, 16);
				}
			}
		}
		this.drawTexturedModalRect(this.guiLeft+170,this.guiTop+this.ySize-24, SlotIcons.fromTier(-1), 16, 16);
		GlStateManager.disableBlend();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my) {
		super.drawGuiContainerForegroundLayer(mx,my);
		if(this.screen!=null){
			this.drawBufferLayer();

			double bw = this.txtWidth * TextBufferRenderCache.renderer().charRenderWidth();
			double bh = this.txtHeight * TextBufferRenderCache.renderer().charRenderHeight();
			double scaleX = Math.min(this.bufferRenderWidth / bw , 1);
			double scaleY = Math.min(this.bufferRenderHeight / bh , 1);
			this.bufferscale = Math.min(scaleX, scaleY);
		}

		this.renderHighlights(mx, my);
	}

	private void renderHighlights(int mx, int my) {
		Slot slot = ContainerUtil.findSlotAt(mx-this.guiLeft, my-this.guiTop, inventorySlots.inventorySlots);
		if(slot!=null){
			if(slot instanceof SlotComponent){
				GlStateManager.disableDepth();
				GlStateManager.disableLighting();
				this.zLevel += 100;

				for(Slot slot2 : this.inventorySlots.inventorySlots){
					if(slot2.getHasStack() && slot.isItemValid(slot2.getStack())){
						this.drawGradientRect(slot2.xDisplayPosition, slot2.yDisplayPosition, slot2.xDisplayPosition + 16, slot2.yDisplayPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
					}
				}

				this.zLevel -= 100;
				GlStateManager.enableDepth();
				GlStateManager.enableLighting();

				if (slot.slotNumber != currentSlot) {
					JeiAdapter.drawHighlights(JeiAdapter.getVisibleStacks().stream().filter((stack) -> slot.isItemValid(stack)).collect(Collectors.toList()));
				}
			}
			else if(slot.getHasStack()){
				ItemStack stack = slot.getStack();
				GlStateManager.disableDepth();
				GlStateManager.disableLighting();
				this.zLevel += 100;

				for(Slot slot2 : this.inventorySlots.inventorySlots){
					if(slot2 instanceof SlotComponent){
						if(slot2.isItemValid(stack)){
							this.drawGradientRect(slot2.xDisplayPosition, slot2.yDisplayPosition, slot2.xDisplayPosition + 16, slot2.yDisplayPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
						}
					}
				}

				this.zLevel -= 100;
				GlStateManager.enableDepth();
				GlStateManager.enableLighting();
			}
			currentSlot = slot.slotNumber;
			return;
		}
		else {
			if (currentSlot != -1) {
				JeiAdapter.drawHighlights(Collections.emptyList());
			}
			currentSlot = -1;
		}

		ItemStack jeiStack = JeiAdapter.getStackUnderMouse();
		if(!ItemStackUtil.isStackEmpty(jeiStack)){
			GlStateManager.disableDepth();
			GlStateManager.disableLighting();
			this.zLevel += 100;

			for(Slot slot2 : this.inventorySlots.inventorySlots){
				if(slot2 instanceof SlotComponent){
					if(slot2.isItemValid(jeiStack)){
						this.drawGradientRect(slot2.xDisplayPosition, slot2.yDisplayPosition, slot2.xDisplayPosition + 16, slot2.yDisplayPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
					}
				}
			}

			this.zLevel -= 100;
			GlStateManager.enableDepth();
			GlStateManager.enableLighting();
		}
	}

	private void drawBufferLayer(){
		GlStateManager.pushMatrix();

		GlStateManager.translate(bufferX, bufferY, 0);
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		RenderHelper.disableStandardItemLighting();

		GlStateManager.pushMatrix();
		GlStateManager.translate(-3, -3, 0);
		GlStateManager.color(1, 1, 1, 1);
		BufferRenderer.drawBackground();
		GlStateManager.popMatrix();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		double scaleX = bufferRenderWidth / this.screen.renderWidth();
		double scaleY = bufferRenderHeight / this.screen.renderHeight();
		double scale = Math.min(scaleX, scaleY);
		if (scaleX > scale) {
			GlStateManager.translate(this.screen.renderWidth() * (scaleX - scale) / 2, 0, 0);
		}
		else if (scaleY > scale) {
			GlStateManager.translate(0,this.screen.renderHeight() * (scaleY - scale) / 2, 0);
		}
		GlStateManager.scale(this.bufferscale * scale, this.bufferscale * scale, scale);
		BufferRenderer.drawText(this.screen);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		int code = Keyboard.getEventKey();

		if (this.screen != null && this.keyboard && code != Keyboard.KEY_ESCAPE && code != Keyboard.KEY_F11) {
			if (Keyboard.getEventKeyState()) {
					char ch = Keyboard.getEventCharacter();
					if (!pressedKeys.containsKey(code) || !ignoreRepeat(ch, code)) {
						this.screen.keyDown(ch, code, null);
						pressedKeys.put(code, ch);
					}
				}
				else{
					if(pressedKeys.containsKey(code)){
						this.screen.keyUp(pressedKeys.remove(code), code, null);
					}
				}

				if (KeyBindings.isPastingClipboard()) {
					this.screen.clipboard(GuiScreen.getClipboardString(), null);
				}
		}
		else {
			super.handleKeyboardInput();
		}
	}

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
}
