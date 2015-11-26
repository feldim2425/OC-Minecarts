package mods.ocminecart.interaction.railcraft;

import java.util.HashMap;

import mods.ocminecart.common.util.RotationHelper;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.core.items.IToolCrowbar;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
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
		
		int dir = getCartCountDir(map,first);
		if(dir==0) return 1;
		
		int count = 1;
		while(map.containsKey(dir*count)){
			count++;
		}
		return count;
	}
	
	public static int getCartCountDir(HashMap<Integer, EntityMinecart> map, EntityMinecart first){
		int dir = 0;
		if(map.containsKey(1) && map.get(1).equals(first)) dir=1;
		else if (map.containsKey(-1) && map.get(-1).equals(first)) dir=-1;
		return dir;
	}
	
	public static EntityMinecart getConnectedCartSide(EntityMinecart cart, boolean front){
		if(!Loader.isModLoaded("Railcraft")) return null;
		ILinkageManager link = CartTools.linkageManager;
		EntityMinecart cartA = link.getLinkedCartA(cart);
		EntityMinecart cartB = link.getLinkedCartB(cart);
		double angleA = (cartA!=null)?RotationHelper.calcAngle(cart.posX,cart.posZ, cartA.posX, cartA.posZ):0;
		double angleB = (cartB!=null)?RotationHelper.calcAngle(cart.posX,cart.posZ, cartB.posX, cartB.posZ):0;
		angleA=(angleA-cart.rotationYaw+360)%360;
		angleB=(angleB-cart.rotationYaw+360)%360;
		if(!front){
			angleA=(angleA+180)%360;
			angleB=(angleB+180)%360;
		}
		
		if(angleA > 90 && angleA < 270 && cartA!=null) return cartA;
		else if(angleB > 90 && angleB < 270 && cartB!=null) return cartB;
		return null;
	}
	
	
}
