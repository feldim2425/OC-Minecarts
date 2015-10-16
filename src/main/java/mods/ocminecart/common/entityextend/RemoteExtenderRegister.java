package mods.ocminecart.common.entityextend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import scala.remote;
import mods.ocminecart.OCMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;

public class RemoteExtenderRegister {
	
	private static HashMap<Class<? extends EntityMinecart>, Class<? extends RemoteCartExtender>> list = 
			new HashMap<Class<? extends EntityMinecart>, Class<? extends RemoteCartExtender>>();
	
	private static ArrayList<RemoteCartExtender> updater = new ArrayList<RemoteCartExtender>();

	public static boolean registerRemote(Class<? extends EntityMinecart> cart, Class<? extends RemoteCartExtender> remote){
		if(list.containsKey(cart)) return false;
		list.put(cart, remote);
		return true;
	}
	
	public static int addRemote(EntityMinecart cart){
		if(!list.containsKey(cart.getClass())) return 1;
		if(cart.getExtendedProperties(RemoteCartExtender.PROP_ID)!=null) return 2;
		Class<? extends RemoteCartExtender> c = list.get(cart.getClass());
		try {
			RemoteCartExtender remote = c.newInstance();
			cart.registerExtendedProperties(RemoteCartExtender.PROP_ID, remote);
		} catch (Exception e) {
			OCMinecart.logger.fatal("Error. Tried to add the remote extender "+c.getName()+" to Minecart "+cart.getClass().getName());
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
	public static boolean hasRemote(EntityMinecart cart){
		return cart.getExtendedProperties(RemoteCartExtender.PROP_ID)!=null &&
				(cart.getExtendedProperties(RemoteCartExtender.PROP_ID) instanceof RemoteCartExtender);
	}
	
	public static int enableRemote(EntityMinecart cart, boolean b){
		if(!hasRemote(cart)) return 1;
		RemoteCartExtender ext = (RemoteCartExtender) cart.getExtendedProperties(RemoteCartExtender.PROP_ID);
		if(ext==null) return 1;
		if(ext.isEnabled() == b) return 2;
		ext.setEnabled(b);
		return 0;
	}
	
	public static boolean isRemoteEnabled(EntityMinecart cart){
		if(!hasRemote(cart)) return false;
		RemoteCartExtender ext = (RemoteCartExtender) cart.getExtendedProperties(RemoteCartExtender.PROP_ID);
		if(ext==null) return false;
		return ext.isEnabled();
	}
	
	
	public static boolean addRemoteUpdate(RemoteCartExtender ext){
		synchronized(updater){
			if(updater.contains(ext)) return false;
			updater.add(ext);
			return true;
		}
	}
	
	public static boolean removeRemoteUpdate(RemoteCartExtender ext){
		synchronized(updater){
			if(!updater.contains(ext)) return false;
			updater.remove(ext);
			return true;
		}
	}
	
	public static void serverTick(){
		synchronized(updater){
			for(int i=0;i<updater.size();i+=1){
				updater.get(i).update();
			}
		}
	}
	
	public static RemoteCartExtender getExtender(EntityMinecart cart){
		if(!hasRemote(cart)) return null;
		return (RemoteCartExtender) cart.getExtendedProperties(RemoteCartExtender.PROP_ID);
	}
	
	public static void register(){
		registerRemote(mods.railcraft.common.carts.EntityLocomotiveSteamSolid.class, RemoteSteamLocomotive.class);
		registerRemote(mods.railcraft.common.carts.EntityLocomotiveElectric.class, RemoteElectricLocomotive.class);
	}

}
