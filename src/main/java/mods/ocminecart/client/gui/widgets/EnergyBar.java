package mods.ocminecart.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class EnergyBar {

    public static void drawBar(int posx, int posy, int h, int w,int zlevel, double percent, ResourceLocation bar){
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer buf = tes.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(bar);

        RenderHelper.disableStandardItemLighting();

        percent = (percent > 1) ? 1 : percent;

        double dw = w * percent;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(posx + dw, posy + h, zlevel).tex(percent, 1).endVertex();
        buf.pos(posx + dw, posy, zlevel).tex(percent, 0).endVertex();
        buf.pos(posx, posy, zlevel).tex(0, 0).endVertex();
        buf.pos(posx, posy + h, zlevel).tex(0, 1).endVertex();
        tes.draw();

        RenderHelper.enableStandardItemLighting();
    }

    public static void renderTooltip(int posx, int posy, int h, int w, int guiLeft, int guiTop, int width, int height, int mx, int my, int energy, int maxEnergy){
        if(mx >= posx && mx <= posx + w && my >= posy && my <= posy + h){
            List<String> text = new ArrayList<>(1);
            int per = (int)(((double)energy / (double)maxEnergy)*100);
            text.add("Energy: "+per+"% ("+energy+" / "+maxEnergy+")");
            GuiUtils.drawHoveringText(text, mx, my, width, height, guiLeft+width, Minecraft.getMinecraft().fontRendererObj);
        }
    }
}
