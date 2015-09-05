package mods.ocminecart.common;

import java.util.HashMap;
import java.util.Map;

import li.cil.oc.api.driver.EnvironmentHost;
import mods.ocminecart.Settings;

public class Sound {
	private static Map<EnvironmentHost,Map<String,Float>> globalTimeouts = new HashMap<EnvironmentHost,Map<String, Float>>();
	
	public static boolean play(EnvironmentHost host, String sound){
		if(globalTimeouts.containsKey(host)){
			Map<String,Float> timer = globalTimeouts.get(host);
			if(timer.getOrDefault(sound, 0F) <= System.currentTimeMillis()){
				host.world().playSoundEffect(host.xPosition(), host.yPosition(), host.zPosition(), Settings.OC_ResLoc+":"+sound, Settings.OC_SoundVolume, 1);
				timer.put(sound, System.currentTimeMillis()+500F);
				return true;
			}
			return false;
		}
		else{
			Map<String,Float> timer = new HashMap<String, Float>();
			host.world().playSoundEffect(host.xPosition(), host.yPosition(), host.zPosition(), Settings.OC_ResLoc+":"+sound, Settings.OC_SoundVolume, 1);
			timer.put(sound, System.currentTimeMillis()+500F);
			globalTimeouts.put(host, timer);
			return true;
		}
	}
}
