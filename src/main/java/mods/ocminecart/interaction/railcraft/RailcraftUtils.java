package mods.ocminecart.interaction.railcraft;

import java.util.HashMap;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.core.items.IToolCrowbar;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.Loader;

public class RailcraftUtils {
	
	public static boolean isUsingChrowbar(EntityPlayer player){
		if(!Loader.isModLoaded("Railcraft")) return false;
		if(player.getCurrentEquippedItem()!=null && (player.inventory .getCurrentItem().getItem() instanceof IToolCrowbar))
			return true;
		return false;
	}
	
	public static HashMap<Integer, EntityMinecart> sortCarts(EntityMinecart middle){
		if(!Loader.isModLoaded("Railcraft")) return null;
		ILinkageManager link = CartTools.linkageManager;
		Iterable<EntityMinecart> cartlist = link.getCartsInTrain(middle);
		if(cartlist==null) return null;
		
		HashMap<Integer, EntityMinecart> sort = new HashMap<Integer, EntityMinecart>();
		int mpos = -1;
		int count = 0;
		for(EntityMinecart c: cartlist){
			count++;
			if(c.equals(middle)) mpos=count;
			if(mpos>=0) sort.put(count-mpos, c);
		}
		if(mpos<0) return null;
		count=0;
		for(EntityMinecart c: cartlist){
			count++;
			if(count>=mpos) continue;
			sort.put(count-mpos, c);
		}
		return sort;
	}
	
	public static int countCarts(EntityMinecart cart, EntityMinecart first){
		if(!Loader.isModLoaded("Railcraft")) return 1;
		HashMap<Integer, EntityMinecart> map = sortCarts(cart);
		
		if(map==null) return 0;
		if(first==null) return 1;
		
		int dir = 0;
		if(map.containsKey(1) && map.get(1).equals(first)) dir=1;
		else if (map.containsKey(-1) && map.get(-1).equals(first)) dir=-1;
		else return 1;
		
		int count = 1;
		while(map.containsKey(dir*count)){
			count++;
		}
		return count;
	}
	
	
}
