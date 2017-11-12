package mods.ocminecart.client.render.gui;

import li.cil.oc.api.internal.TextBuffer;
import mods.ocminecart.client.texture.ResourceTexture;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;

/**
 * Copy from li.cil.oc.client.renderer.gui.BufferRenderer
 * Translated from Scala to Java
 */
public final class BufferRenderer {

	private static final int innerMargin = 1;

	private static TextureManager textureManager;
	private static int displayLists = 0;

	private BufferRenderer() {
	}

	public static synchronized void init(TextureManager tm) {
		textureManager = tm;
		displayLists = GLAllocation.generateDisplayLists(2);
	}

	public static void compileBackground(int bufferWidth, int bufferHeight, boolean forRobot) {
		if (textureManager == null) {
			return;
		}

		int innerWidth = innerMargin * 2 + bufferWidth;
		int innerHeight = innerMargin * 2 + bufferHeight;

		GL11.glNewList(displayLists, GL11.GL_COMPILE);

		textureManager.bindTexture(ResourceTexture.OC_GUI_BORDER.location);

		GL11.glBegin(GL11.GL_QUADS);

		int margin = (forRobot) ? 2 : 7;

		int c0 = 0;
		int c1 = 7;
		int c2 = 9;
		int c3 = 16;

		if (forRobot) {
			c0 = 5;
			c1 = 7;
			c2 = 9;
			c3 = 11;
		}

		// Top border (left corner, middle bar, right corner).
		drawBorder(
				0, 0, margin, margin,
				c0, c0, c1, c1);
		drawBorder(
				margin, 0, innerWidth, margin,
				c1 + 0.25, c0, c2 - 0.25, c1);
		drawBorder(
				margin + innerWidth, 0, margin, margin,
				c2, c0, c3, c1);

		// Middle area (left bar, screen background, right bar).
		drawBorder(
				0, margin, margin, innerHeight,
				c0, c1 + 0.25, c1, c2 - 0.25);
		drawBorder(
				margin, margin, innerWidth, innerHeight,
				c1 + 0.25, c1 + 0.25, c2 - 0.25, c2 - 0.25);
		drawBorder(
				margin + innerWidth, margin, margin, innerHeight,
				c2, c1 + 0.25, c3, c2 - 0.25);

		// Bottom border (left corner, middle bar, right corner).
		drawBorder(
				0, margin + innerHeight, margin, margin,
				c0, c2, c1, c3);
		drawBorder(
				margin, margin + innerHeight, innerWidth, margin,
				c1 + 0.25, c2, c2 - 0.25, c3);
		drawBorder(
				margin + innerWidth, margin + innerHeight, margin, margin,
				c2, c2, c3, c3);

		GL11.glEnd();

		GL11.glEndList();
	}

	public static void drawBackground() {
		if (textureManager != null) {
			textureManager.bindTexture(ResourceTexture.OC_GUI_BORDER.location);
			GL11.glCallList(displayLists);
		}
	}

	public static boolean drawText(TextBuffer screen) {
		if (textureManager != null) {
			GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glDepthMask(false);
			boolean changed = screen.renderText();
			GL11.glPopAttrib();
			return changed;
		}
		else {
			return false;
		}
	}

	private static void drawBorder(double x, double y, double w, double h, double u1, double v1, double u2, double v2) {
		double u1d = u1 / 16.0;
		double u2d = u2 / 16.0;
		double v1d = v1 / 16.0;
		double v2d = v2 / 16.0;
		GL11.glTexCoord2d(u1d, v2d);
		GL11.glVertex3d(x, y + h, 0);
		GL11.glTexCoord2d(u2d, v2d);
		GL11.glVertex3d(x + w, y + h, 0);
		GL11.glTexCoord2d(u2d, v1d);
		GL11.glVertex3d(x + w, y, 0);
		GL11.glTexCoord2d(u1d, v1d);
		GL11.glVertex3d(x, y, 0);
	}
}
