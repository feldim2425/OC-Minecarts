package mods.ocminecart.common.assemble.util;

import java.util.ArrayList;
import java.util.List;

import mods.ocminecart.OCMinecart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class TooltipUtil {
	
	private final static int MAX_WIDTH = 220;
	
	private final static FontRenderer font = Minecraft.getMinecraft().fontRenderer;
	
	public static List<String> trimString(String tooltip){
		String sp="";
		int cur = 0;
		while(sp.length()<tooltip.length() && cur<tooltip.length()){
			int clen = 0;
			while(clen+cur<tooltip.length() && font.getStringWidth(tooltip.substring(cur, clen+cur))<MAX_WIDTH) clen+=1; //Find the max number of chars
			String cut = tooltip.substring(cur, clen+cur);
			int pos = -1;
			if(font.getStringWidth(cut)>MAX_WIDTH) pos = cut.lastIndexOf(' ');
			
			if(pos > 0 && cut.charAt(pos)==' ') {
				char[] buf = cut.toCharArray();
				buf[pos] = '\n';
				cut = String.valueOf(buf);
				cut = cut.substring(0,pos+1);
			}
			sp = sp + cut;
			cur+=cut.length();
		}
		ArrayList<String> res = new ArrayList<String>();
		OCMinecart.logger.info(sp);
		String[] s = sp.split("\\n");
		for(int i=0;i<s.length;i+=1){
			res.add(s[i]);
		}
		return res;
		
	}
}
