package mods.ocminecart.client.gui;

import java.util.Iterator;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.container.NetworkRailBaseContainer;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import mods.ocminecart.interaction.NEI;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.GuiButtonClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.ItemPanel;
import codechicken.nei.LayoutManager;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

public class NetworkRailBaseGui extends GuiContainer{
	
	public static final ResourceLocation texture = new ResourceLocation(OCMinecart.MODID,"textures/gui/netrailbasegui.png");
	
	private NetworkRailBaseTile tile;
	private int oldMode;
	private GuiButton modeBt;

	private Slot hoveredSlot=null;
	private ItemStack hoveredNEI=null;
	
	
	public NetworkRailBaseGui(InventoryPlayer inventory, NetworkRailBaseTile entity) {
		super(new NetworkRailBaseContainer(inventory,entity));
		
		this.tile = entity;
		
		this.xSize = 176;
		this.ySize = 166;
	}
	
	public void initGui(){
		super.initGui();
		
		this.oldMode = this.tile.getMode();
		
		this.modeBt = new GuiButton(0, this.guiLeft+8, this.guiTop+34, 100, 20, this.getModeButtonTxt());
		this.buttonList.add(this.modeBt);
	}
	
	
	private String getModeButtonTxt(){
		String buttonTxt  = "";
		switch(this.tile.getMode()){
		case 0:
			buttonTxt="Messages + Power";
			break;
		case 1:
			buttonTxt="Messages";
			break;
		case 2:
			buttonTxt="Power";
			break;
		default:
			buttonTxt="Deprecated Mode";
			break;
		}
		return buttonTxt;
	}
	
	public void actionPerformed(GuiButton button){
		switch(button.id){
		case 0:
			ModNetwork.channel.sendToServer(new GuiButtonClick(tile,0));
			break;
		}
	}
	

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,int p_146976_2_, int p_146976_3_) {
		GL11.glColor3d(1F, 1F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		func_146110_a(this.guiLeft,this.guiTop,0,0,this.xSize,this.ySize, this.xSize,this.ySize);  //Same as drawTexturedModalRect, but allows custom image sizes
		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146976_2_, int p_146976_3_) {
		this.fontRendererObj.drawString(tile.getInventoryName(), this.xSize / 2  - this.fontRendererObj.getStringWidth(tile.getInventoryName()) / 2, 6, 0x404040);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize-96+2, 0x404040);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui."+OCMinecart.MODID+".networkrailbase.info"), 8, 24, 0x404040);
	
		Iterator<Slot> list = this.inventorySlots.inventorySlots.iterator();
        while(list.hasNext()) this.drawSlotHighlight(list.next());
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	public void updateScreen(){
		super.updateScreen();
		
		if(this.tile.getMode() != this.oldMode){
			this.modeBt.displayString = this.getModeButtonTxt();
			this.oldMode = this.tile.getMode();
		}
	}
	
	public TileEntity getTile(){
		return this.tile;
	}
	
	public void drawScreen(int mx, int my, float dt){
		this.hoveredSlot=null;
		Iterator<Slot> list = this.inventorySlots.inventorySlots.iterator();
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
	
	protected void drawSlotHighlight(Slot slot) {
		if(Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null){
			boolean highlight = false;
			boolean inPlayerInv = slot.inventory == Minecraft.getMinecraft().thePlayer.inventory;
			if(this.hoveredSlot!=null){
				if(this.hoveredSlot.getHasStack() && slot.inventory.equals(this.tile) && this.tile.isItemValidForSlot(slot.getSlotIndex(), this.hoveredSlot.getStack())) highlight=true;
				else if(slot.getHasStack() && this.hoveredSlot.inventory.equals(this.tile) && this.tile.isItemValidForSlot(this.hoveredSlot.getSlotIndex() ,slot.getStack()))
						highlight=true;
			}
			else{
				if(this.hoveredNEI!=null && slot.inventory.equals(this.tile) && this.tile.isItemValidForSlot(slot.getSlotIndex(), this.hoveredNEI)){
					highlight=true;
				}
			}
			
			if(highlight){
				this.zLevel += 100;
				this.drawGradientRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
				this.zLevel -= 100;
			}
		}
	}
	
	@Optional.Method(modid = "NotEnoughItems")
	private void drawNEIHighlight(){
		ItemPanel panel = LayoutManager.itemPanel;
		if(panel == null) return;
		this.zLevel += 350;
		for(int i=0;i<ItemPanel.items.size();i+=1){
			Rectangle4i rect = panel.getSlotRect(i);
			ItemStack slot = panel.getStackMouseOver(rect.x, rect.y);
			if(slot!=null && this.hoveredSlot!=null){
				if((this.hoveredSlot.inventory != Minecraft.getMinecraft().thePlayer.inventory) && this.hoveredSlot.inventory == this.tile && this.tile.isItemValidForSlot(this.hoveredSlot.getSlotIndex() ,slot)){
					 drawGradientRect( rect.x1() + 1, rect.y1() + 1, rect.x2(), rect.y2(), 0x40FFFFFF, 0x40FFFFFF);
				}
			}
		}
		this.zLevel -= 350;
	}

}
