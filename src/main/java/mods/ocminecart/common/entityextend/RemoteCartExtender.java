package mods.ocminecart.common.entityextend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	protected boolean respbroadcast = false;
	private int oX,oY,oZ;
	private int nextResp = -1;
	private String nextAddr = null;
	protected int respport = -1;
	protected int cmdport = -1;
	
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
	
	protected void changeEnabled() {
		NBTTagCompound nbt = this.entity.getEntityData();
		nbt.removeTag(OCMinecart.MODID+":rc_settings");
	}
	
	protected void processCommand(String cmd, Object[] args) {
		if(cmd == null || cmd.equals("doc")){
			if(args.length > 1 && (args[1] instanceof String) && this.getCommands().contains(args[1]))
			{
				this.sendPacket(new Object[]{this.getDoc((String) args[1])}, this.getRespPort(), this.getRespAddress());
			}
			else
			{
				this.sendPacket(new Object[]{this.getCmdList()}, this.getRespPort(), this.getRespAddress());
			}
		}
			
			
	}
	
	private String getCmdList(){
		Iterator<String> it = this.getCommands().iterator();
		String st = "{\n";
		while(it.hasNext())
		{
			st += "  "+it.next()+ ((it.hasNext()) ? " ," : "") + "\n";
		}
		st = "}";
		return st;
	}
	
	protected final int getRespPort() {
		if(this.respport>=0) return this.respport;
		return this.nextResp;
	}
	
	protected final String getRespAddress() {
		if(this.respbroadcast) return null;
		return this.nextAddr;
	}
	
	protected final void sendPacket(Object[] msg, int port, String des){
		if(this.getRespPort()<0) return;
		Packet packet = API.network.newPacket("", des, port, msg);
		API.network.sendWirelessPacket(this, 8, packet);
	}
	
	protected List<String> getCommands(){
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("doc");
		cmd.add("response_port");
		cmd.add("command_port");
		cmd.add("response_broadcast");
		return cmd;
	}
	
	protected String getDoc(String cmd){
		if(cmd == null || cmd.equals("doc"))
			return "doc([func:string]):table or string -- get a list of functions or a documentation for a function";
		
		return null;
	}
	
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
	synchronized public void receivePacket(Packet packet, WirelessEndpoint sender){
		if(packet.ttl()<0 || !this.inRange(sender, 8)) return;
		if(!(packet.data()[0] instanceof String)) return;
		this.nextAddr = packet.source();
		this.nextResp = packet.port();
		String cmd = (String) packet.data()[0];
		Object[] data = (this.getCommands().contains(cmd))? packet.data() : new Object[]{cmd};
		this.processCommand((this.getCommands().contains(cmd))? cmd : null, packet.data());
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setBoolean(OCMinecart.MODID+":rc_enabled", enabled);
		if(enabled){
			NBTTagCompound rc = new NBTTagCompound();
			rc.setBoolean("rc_respbroadcast", this.respbroadcast);
			rc.setInteger("rc_respport", this.respport);
			rc.setInteger("rc_cmdport", this.cmdport);
			nbt.setTag(OCMinecart.MODID+":rc_settings", rc);
		}
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		if(nbt.hasKey(OCMinecart.MODID+":rc_enabled"))
			enabled = nbt.getBoolean(OCMinecart.MODID+":rc_enabled");
		
		if(nbt.hasKey(OCMinecart.MODID+":rc_settings") && this.enabled){
			NBTTagCompound rc = nbt.getCompoundTag(OCMinecart.MODID+":rc_settings");
			if(rc.hasKey("rc_respbroadcast")) this.respbroadcast = rc.getBoolean("rc_respbroadcast");
			if(rc.hasKey("rc_respport")) this.respport = rc.getInteger("rc_respport");
			if(rc.hasKey("rc_cmdport")) this.cmdport = rc.getInteger("rc_cmdport");	
		}
		
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
	
	public boolean inRange(WirelessEndpoint w, double range){
		int x = this.x() - w.x();
		int y = this.y() - w.y();
		int z = this.z() - w.z();
		return (x*x) + (y*y) + (z*z) <= (range * range); 
	}
	
}
