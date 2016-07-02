package mods.ocminecart.common.entityextend;

import cpw.mods.fml.common.Loader;
import li.cil.oc.api.API;
import mods.ocminecart.OCMinecart;
import net.minecraft.entity.item.EntityMinecart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
		if(updater.contains(ext)) return false;
		updater.add(ext);
		return true;
	}
	
	public static boolean containsEntity(UUID uuid){
		if(updater.isEmpty() || uuid == null) return false;
		for(int i=0;i<updater.size();i+=1){
			if(updater.get(i).entity!=null && updater.get(i).entity.getUniqueID().equals(uuid) ){
				return true;
			}
		}
		return false;
	}
	
	public static void removeRemoteUpdate(EntityMinecart entity){
		if(updater.isEmpty() || entity == null) return;
		for(int i=0; i<updater.size(); i++){
			RemoteCartExtender e = updater.get(i);
			if(!e.isEnabled() || e.entity.getUniqueID() == entity.getUniqueID()){
				e.setEnabled(false,true);
			}
		}
	}
	
	public static boolean removeRemoteUpdate(RemoteCartExtender ext){
		if(!updater.contains(ext)) return false;
		updater.remove(ext);
		API.network.leaveWirelessNetwork(ext); //Sometimes it doesn't leave the network (Dimension changing)
		return true;
	}
	
	public static void serverTick(){
		ArrayList<Integer> marked = new ArrayList<Integer>();
		for(int i=0;i<updater.size();i+=1){
			if(updater.get(i).isEnabled())
				updater.get(i).update();
			else
				marked.add(i);
		}
		
		if(!marked.isEmpty()){
			for(int i=0;i<marked.size();i+=1){
				updater.remove(marked.get(i));
				System.out.println("Remove");
			}
		}
	}
	
	public static RemoteCartExtender getExtender(EntityMinecart cart){
		if(!hasRemote(cart)) return null;
		return (RemoteCartExtender) cart.getExtendedProperties(RemoteCartExtender.PROP_ID);
	}
	
	public static void reinit() {
		updater.clear();
	}
	
	public static void register(){
		if(Loader.isModLoaded("Railcraft")){
			registerRemote(mods.railcraft.common.carts.EntityLocomotiveSteamSolid.class, RemoteSteamLocomotive.class);
			registerRemote(mods.railcraft.common.carts.EntityLocomotiveElectric.class, RemoteElectricLocomotive.class);
		}
	}

}
