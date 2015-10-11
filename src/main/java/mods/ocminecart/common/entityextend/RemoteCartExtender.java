package mods.ocminecart.common.entityextend;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.util.ItemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import li.cil.oc.api.API;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;

public abstract class RemoteCartExtender implements WirelessEndpoint, IExtendedEntityProperties{
	
	public final static String PROP_ID = OCMinecart.MODID+"RCcart";
	
	protected EntityMinecart entity;
	protected boolean enabled=false;
	private int oX,oY,oZ;
	
	@Override
	public int x() {
		return (int)entity.posX;
	}

	@Override
	public int y() {
		return (int)entity.posY;
	}

	@Override
	public int z() {
		return (int)entity.posZ-1;
	}
	
	public final void setEnabled(boolean state) {
		if(this.enabled != state){
			this.enabled = state;
			if(state){
				API.network.joinWirelessNetwork(this);
				RemoteExtenderRegister.addRemoteUpdate(this);
				this.oX = x();
				this.oY = y();
				this.oZ = z();
			}
			else{
				API.network.leaveWirelessNetwork(this);
				RemoteExtenderRegister.removeRemoteUpdate(this);
			}
			this.changeEnabled();
		}
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	protected void changeEnabled() {}
	
	public void update() {
		if(this.entity.isDead){
			this.setEnabled(false);
			ItemUtil.dropItem(new ItemStack(ModItems.item_CartRemoteModule), this.entity.worldObj, 
					this.entity.posX, this.entity.posY, this.entity.posZ, true);
		}
		else if(oX!=x() || oY!=y() || oZ!=z()){
			API.network.updateWirelessNetwork(this);
			this.oX = x();
			this.oY = y();
			this.oZ = z();
		}
	}

	@Override
	public World world() {
		return entity.worldObj;
	}

	@Override
	public abstract void receivePacket(Packet packet, WirelessEndpoint sender);

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setBoolean(OCMinecart.MODID+":rc_enabled", enabled);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		if(nbt.hasKey(OCMinecart.MODID+":rc_enabled"))
			enabled = nbt.getBoolean(OCMinecart.MODID+":rc_enabled");
		
		if(this.enabled){
			API.network.joinWirelessNetwork(this);
			RemoteExtenderRegister.addRemoteUpdate(this);
			this.oX = x();
			this.oY = y();
			this.oZ = z();
		}
		else{
			API.network.leaveWirelessNetwork(this);
			RemoteExtenderRegister.removeRemoteUpdate(this);
		}
	}

	@Override
	public void init(Entity entity, World world) {
		if(!(entity instanceof EntityMinecart)) return;
		this.entity = (EntityMinecart)entity;
	}
	
}
