package mods.ocminecart.client.gui;

import li.cil.oc.api.component.TextBuffer;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.container.NetworkRailBaseContainer;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.GuiButtonClick;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class NetworkRailBaseGui extends GuiContainer{
	
	public static final ResourceLocation texture = new ResourceLocation(OCMinecart.MODID,"textures/gui/netrailbasegui.png");
	
	private NetworkRailBaseTile tile;
	
	public NetworkRailBaseGui(InventoryPlayer inventory, NetworkRailBaseTile entity) {
		super(new NetworkRailBaseContainer(inventory,entity));
		
		this.tile = entity;
		
		this.xSize = 176;
		this.ySize = 166;
	}
	
	public void initGui(){
		super.initGui();
		
		this.buttonList.add(new GuiButton(0, this.guiLeft+8, this.guiTop+34, 100, 20, this.getModeButtonTxt()));
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
			tile.onButtonPress(0);
			button.displayString = getModeButtonTxt();
			ModNetwork.channel.sendToServer(new GuiButtonClick(tile,0));
			break;
		}
	}
	

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,int p_146976_2_, int p_146976_3_) {
		GL11.glColor3d(1F, 1F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
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

}
