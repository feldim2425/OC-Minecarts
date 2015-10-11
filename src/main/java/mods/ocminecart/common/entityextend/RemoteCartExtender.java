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
	
	@Override
	public int x() {
		return (int)Math.floor(entity.posX+0.5);
	}

	@Override
	public int y() {
		return (int)Math.floor(entity.posY+0.5);
	}

	@Override
	public int z() {
		return (int)Math.floor(entity.posZ+0.5);
	}
	
	public final void setEnabled(boolean state) {
		if(this.enabled != state){
			this.enabled = state;
			if(state){
				API.network.joinWirelessNetwork(this);
				RemoteExtenderRegister.addRemoteUpdate(this);
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
	}

	@Override
	public void init(Entity entity, World world) {
		if(!(entity instanceof EntityMinecart)) return;
		this.entity = (EntityMinecart)entity;
	}
	
}
