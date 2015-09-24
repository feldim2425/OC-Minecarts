package mods.ocminecart.common.util;

public class ColorUtil {
	public static byte[] colorToRGB(int color){
		byte rgb[] = new byte[3];
		rgb[2]=(byte)(color & 0x0000FF);
		rgb[1]=(byte)((color & 0x00FF00)>>8);
		rgb[0]=(byte)((color & 0xFF0000)>>16);
		return rgb;
	}
	
	public static int colorFromRGB(byte[] rgb){
		int color = 0;
		color = color | rgb[2];
		color = color | (rgb[1] << 8);
		color = color | (rgb[0] << 16);
		return color;
	}
}
