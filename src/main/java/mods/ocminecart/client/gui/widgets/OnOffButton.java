package mods.ocminecart.client.gui.widgets;

import mods.ocminecart.client.texture.ResourceTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class OnOffButton extends GuiButton {

    private boolean state = false;

    public OnOffButton(int buttonId, int x, int y) {
        super(buttonId, x, y, 18, 18, "");
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        mc.renderEngine.bindTexture(ResourceTexture.OC_GUI_POWERBUTTON.location);
        float u = state ? 18F / 36F : 0;
        float u2 = u + 18F / 36F;
        float v = isMouseOver() ? 18F / 36F : 0;
        float v2 = v + 18F / 36F;

        int x = this.xPosition;
        int x2 = x + 18;
        int y = this.yPosition;
        int y2 = y + 18;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x, y2 , 0.0D).tex(u, v2).endVertex();
        vertexbuffer.pos(x2, y2, 0.0D).tex(u2, v2).endVertex();
        vertexbuffer.pos(x2, y, 0.0D).tex(u2, v).endVertex();
        vertexbuffer.pos(x,y, 0.0D).tex(u, v).endVertex();
        tessellator.draw();
    }
}
