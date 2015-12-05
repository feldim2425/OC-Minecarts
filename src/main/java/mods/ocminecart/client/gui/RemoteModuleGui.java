package mods.ocminecart.client.gui;

import java.util.ArrayList;
import java.util.List;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.client.gui.widget.GuiUtil;
import mods.ocminecart.common.container.RemoteModuleContainer;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.GuiButtonClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RemoteModuleGui extends GuiContainer {
	private GuiTextField pass;
	private ResourceLocation texture = new ResourceLocation(OCMinecart.MODID+":textures/gui/remotemodulegui.png");
	
	private int passMsgR = 100;
	private boolean oPerm = false;
	private boolean locked = false;
	
	public RemoteModuleGui(){
		super(new RemoteModuleContainer());
	
		this.xSize=150;
		this.ySize=117;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		pass = new GuiTextField(Minecraft.getMinecraft().fontRenderer,8,85,75,10);
		pass.setFocused(false);
		pass.setEnabled(false);
		pass.setMaxStringLength(10);
		
		this.buttonList.add(new GuiButton(0, 85+this.guiLeft, 84+this.guiTop, 60, 20, StatCollector.translateToLocal("gui."+OCMinecart.MODID+".general.confirm")));
		this.buttonList.add(new GuiButton(1, 126+this.guiLeft, 4+this.guiTop, 20, 20, "U"));
		this.buttonList.add(new GuiButton(2, 8+this.guiLeft, 20+this.guiTop, 84, 20, EnumChatFormatting.RED+
				StatCollector.translateToLocal("gui."+OCMinecart.MODID+".remotem.remmodule")));
		
		((GuiButton)this.buttonList.get(0)).enabled=false;
		((GuiButton)this.buttonList.get(1)).enabled=false;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,int p_146976_2_, int p_146976_3_) {
		GL11.glColor3d(1F, 1F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		func_146110_a(this.guiLeft,this.guiTop,0,0,this.xSize,this.ySize, this.xSize,this.ySize);  //Same as drawTexturedModalRect, but allows custom image sizes
		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my) {
	    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
	    
	    this.fontRendererObj.drawString(StatCollector.translateToLocal("gui."+OCMinecart.MODID+".remmconf"), 8,8, 0x404040);
		pass.drawTextBox();
		int stat = this.getContainer().passstate;
		if(stat==1) this.fontRendererObj.drawString(StatCollector.translateToLocal("gui."+OCMinecart.MODID+".general.success"), 10, 100, 0x10AA10);
		else if(stat==2) this.fontRendererObj.drawString(StatCollector.translateToLocal("gui."+OCMinecart.MODID+".general.failed"), 10, 100, 0xFF3030);
		
		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui."+OCMinecart.MODID+".remotem.chpass"), 8, 73, 0x404040);
		
		GuiButton b1 = (GuiButton) this.buttonList.get(0);
		if(this.func_146978_c(b1.xPosition, b1.yPosition, b1.width, b1.height, mx+this.guiLeft, my+this.guiTop)){
			List<String> txt = new ArrayList<String>();
			txt.add(EnumChatFormatting.WHITE+StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".gui.chpass"));
			txt.add(EnumChatFormatting.GRAY+StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".gui.empass"));
			GuiUtil.drawHoverText(txt, mx-this.guiLeft, my-this.guiTop, this.width, this.height, this.guiLeft, Minecraft.getMinecraft().fontRenderer);
		}
		
		b1 = (GuiButton) this.buttonList.get(1);
		if(this.func_146978_c(b1.xPosition, b1.yPosition, b1.width, b1.height, mx+this.guiLeft, my+this.guiTop)){
			List<String> txt = new ArrayList<String>();
			txt.add(EnumChatFormatting.WHITE+((locked)?"Locked":"Unlocked"));
			txt.add(EnumChatFormatting.GRAY+StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".gui.lockbtn"));
			GuiUtil.drawHoverText(txt, mx-this.guiLeft, my-this.guiTop, this.width, this.height, this.guiLeft, Minecraft.getMinecraft().fontRenderer);
		}
		
		GL11.glPopAttrib();
	}
	
	protected void keyTyped(char ch, int key){
		if(key==Keyboard.KEY_ESCAPE) super.keyTyped(ch, key);
		else if(pass.isFocused()) pass.textboxKeyTyped(ch, key);
		else super.keyTyped(ch, key);
	}
	
	protected void mouseClicked(int x, int y, int key){
		super.mouseClicked(x, y, key);
		
		pass.mouseClicked(x-this.guiLeft,y-this.guiTop,key);
	}
	
	public void actionPerformed(GuiButton button){
		switch(button.id){
		case 0:
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("password", pass.getText());
			ModNetwork.channel.sendToServer(new GuiButtonClick(2,0,nbt));
			this.pass.setText("");
			break;
		case 1:
			ModNetwork.channel.sendToServer(new GuiButtonClick(2,1,null));
			break;
		case 2:
			ModNetwork.channel.sendToServer(new GuiButtonClick(2,2,null));
			break;
		}
	}
	
	private RemoteModuleContainer getContainer(){
		return (RemoteModuleContainer) this.inventorySlots;
	}
	
	public void updateScreen(){
		if(this.getContainer().passstate!=0){
			this.passMsgR--;
			if(this.passMsgR<=0) this.getContainer().passstate=0;
		}
		else if(this.passMsgR!=100) this.passMsgR=100;
		
		if(locked!=this.getContainer().locked){
			locked=this.getContainer().locked;
			((GuiButton)this.buttonList.get(1)).displayString=(locked)?"L":"U";
		}
		
		if(oPerm!=this.getContainer().perm){
			oPerm = this.getContainer().perm;
			this.pass.setEnabled(oPerm);
			((GuiButton)this.buttonList.get(0)).enabled=oPerm;
			((GuiButton)this.buttonList.get(1)).enabled=oPerm;
		}
	}
}
