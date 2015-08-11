package mods.ocminecart.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.common.Tier;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class SlotIcons {
	
	private static final String OC_Domain = "opencomputers:";
	
	private final static String[] SLOT_TYPES  = new String[]{
		Slot.Card, Slot.ComponentBus, Slot.Container, Slot.CPU, "eeprom", Slot.Floppy, Slot.HDD, Slot.Memory, 
		Slot.Tablet, "tool", Slot.Upgrade};
	
	
	private static Map<Integer, IIcon> tiericons = new HashMap<Integer, IIcon>();
	private static Map<String, IIcon> sloticons = new HashMap<String, IIcon>();
	
	
	public static void register(IIconRegister register){
		for(int i=0;i<SLOT_TYPES.length;i+=1){
			sloticons.put(SLOT_TYPES[i], register.registerIcon(OC_Domain+"icons/"+SLOT_TYPES[i]));
		}
		
		tiericons.put(-1, register.registerIcon(OC_Domain+"icons/na"));
		for(int i=0;i<3;i+=1){
			tiericons.put(i, register.registerIcon(OC_Domain+"icons/tier"+i));
		}
	}
	
	public static IIcon fromTier(int tier){
		if(tiericons.containsKey(tier)) return tiericons.get(tier);
		return null;
	}
	
	public static IIcon fromSlot(String slot){
		if(sloticons.containsKey(slot)) return sloticons.get(slot);
		return null;
	}
}
