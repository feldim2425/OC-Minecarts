package mods.ocminecart.client.gui;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.container.NetworkRailBaseContainer;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.GuiButtonClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class NetworkRailBaseGui extends GuiContainer{
	
	public static final ResourceLocation texture = new ResourceLocation(OCMinecart.MODID,"textures/gui/netrailbasegui.png");
	
	private NetworkRailBaseTile tile;
	private int oldMode;
	private GuiButton modeBt;
	
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

}
