package mods.ocminecart.common.entityextend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import li.cil.oc.api.API;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.util.ItemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.base.Charsets;

import cpw.mods.fml.server.FMLServerHandler;

public abstract class RemoteCartExtender implements WirelessEndpoint, IExtendedEntityProperties{
	
	public final static String PROP_ID = OCMinecart.MODID+"RCcart";
	
	protected EntityMinecart entity;
	protected boolean enabled=false;
	protected boolean respbroadcast = true;
	private int nextResp = -1;
	private String nextAddr = null;
	protected int respport = 1;
	protected int cmdport = 2;
	private String uuid;
	private String owner = null;
	private boolean lock = true;
	private ItemStack drop = null;
	private int maxWlanStrength=4;
	private int curWlanStrength=4;
	
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
		
		if(isEnabled()){	//reset some settings
			curWlanStrength = 4;
			uuid = UUID.randomUUID().toString();
			respport = 1;
			cmdport = 2;
			respbroadcast = true;
			nextResp = -1;
			lock=true;
		}
	}
	
	protected void processCommand(String cmd, Object[] args) {
		if(cmd == null || cmd.equals("doc")){
			String a1 = (args.length >= 1 && (args[0] instanceof String)) ? (String)args[0] : null;
			if(a1 != null && this.getCommands().contains(a1))
			{
				String doc = this.getDoc(a1);
				doc = (doc == null || doc == "") ? "No documentation available" : doc;
				this.sendPacket(new Object[]{doc}, this.getRespPort(), this.getRespAddress());
			}
			else if(a1!=null && a1.equals("-t"))
			{
				this.sendPacket(new Object[]{this.getCmdList(true)}, this.getRespPort(), this.getRespAddress());
			}
			else
			{
				this.sendPacket(new Object[]{this.getCmdList(false)}, this.getRespPort(), this.getRespAddress());
			}
		}
		else if(cmd.equals("response_port")){
			boolean isValid = args.length>=1 && (args[0] instanceof Double);
			int port = (isValid) ? (int)(double)args[0] : 0; //Double is a Class and have to be converted to a double (type)
			port = Math.max(-1, port);
			if(isValid) this.respport = port;
			this.sendPacket(new Object[]{this.respport}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("command_port")){
			boolean isValid = args.length>=1 && (args[0] instanceof Double);
			int port = (isValid) ? (int)(double)args[0] : 0;
			port = Math.max(-1, port);
			if(isValid) this.cmdport = port;
			this.sendPacket(new Object[]{this.cmdport}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("response_broadcast")){
			boolean isValid = args.length>=1 && (args[0] instanceof Boolean);
			boolean value = (isValid) ? (boolean)args[0] : false;
			if(isValid) this.respbroadcast = value;
			this.sendPacket(new Object[]{this.respbroadcast}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("wlan_strength")){
			int strength = (args.length>0 && (args[0] instanceof Double)) ? (int)((double)((Double)args[0])) : this.curWlanStrength;	//Double (object) -> double (number) -> int
			strength = Math.min(strength, this.maxWlanStrength);
			this.curWlanStrength = strength;
			this.sendPacket(new Object[]{strength, this.maxWlanStrength}, this.getRespPort(), this.getRespAddress());
		}
			
	}
	
	private String getCmdList(boolean compress){
		Iterator<String> it = this.getCommands().iterator();
		if(!compress){
			String st = "{\n";
			while(it.hasNext())
			{
				st += "  "+it.next()+ ((it.hasNext()) ? " ," : "") + "\n";
			}
			st += "}";
			return st;
		}
		else{
			String st = "{";
			while(it.hasNext())
			{
				st += it.next()+ ((it.hasNext()) ? "," : "");
			}
			st += "}";
			return st;
		}
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
		Packet packet = API.network.newPacket(uuid, des, port, msg);
		API.network.sendWirelessPacket(this, 4, packet);
	}
	
	protected List<String> getCommands(){
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("doc");
		cmd.add("response_port");
		cmd.add("command_port");
		cmd.add("response_broadcast");
		cmd.add("wlan_strength");
		return cmd;
	}
	
	protected String getDoc(String cmd){
		if(cmd == null) return null;
		if(cmd.equals("doc"))
			return "doc([func:string]):table or string -- get a list of functions or a documentation for a function. -t (as 2. String) to get a compressed list";
		else if(cmd.equals("response_port"))
			return "response_port([port:number]):number -- sets the response port and returns the new port. -1 to response on the same port as the last message";
		else if(cmd.equals("command_port"))
			return "command_port([port:number]):number -- sets the command port and returns the new port. -1 to accept all ports";
		else if(cmd.equals("response_broadcast"))
			return "response_broadcast([value:boolean]):boolean -- if the value is true it will respond with private messages.";
		else if(cmd.equals("wlan_strength"))
			return "wlan_strength([value:number]):number,number -- get/set the current and get the max. wireless strength.";
		else if(cmd.equals("login"))
			return "login(password:string):boolean -- login when a password is set (200 ticks timeout after every command).";
		return null;
	}
	
	public void update() {
		if(this.world().isRemote) return;
		if(this.entity.isDead){
			this.setEnabled(false);
			this.dropItem();
		}
		else{
			API.network.updateWirelessNetwork(this);
		}
	}

	@Override
	public World world() {
		return entity.worldObj;
	}

	@Override
	public void receivePacket(Packet packet, WirelessEndpoint sender){
		if(packet.ttl()<0 || !inRange(sender,curWlanStrength)) return;
		if(!(packet.destination()==null || packet.destination().equals(this.uuid)) || !(this.cmdport==-1 || packet.port()==this.cmdport))
			return;
		if(!(packet.data()[0] instanceof byte[])) return;
		this.nextAddr = packet.source();
		this.nextResp = packet.port();
		String cmd = new String((byte[])packet.data()[0],Charsets.UTF_8);
		Object[] data = (this.getCommands().contains(cmd))? this.processPacket(packet.data()) : new Object[]{};
		this.processCommand((this.getCommands().contains(cmd))? cmd : null, data);
	}
	
	private Object[] processPacket(Object[] data){
		if(data.length-1<1) return new Object[]{};
		Object[] res = new Object[data.length-1];
		for(int i=1;i<data.length;i+=1)
		{
			if(data[i] instanceof byte[])
			{
				res[i-1] = new String((byte[]) data[i], Charsets.UTF_8);
			}
			else
			{
				res[i-1] = data[i];
			}
		}
		return res;
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setBoolean(OCMinecart.MODID+":rc_enabled", enabled);
		if(enabled){
			NBTTagCompound rc = new NBTTagCompound();
			rc.setString("rc_uuid", this.uuid);
			rc.setBoolean("rc_respbroadcast", this.respbroadcast);
			rc.setInteger("rc_respport", this.respport);
			rc.setInteger("rc_cmdport", this.cmdport);
			rc.setInteger("rc_maxwlan", this.maxWlanStrength);
			rc.setInteger("rc_curwlan", this.curWlanStrength);
			if(owner!=null) rc.setString("rc_owner", this.owner);
			else rc.removeTag("rc_owner");
			rc.setBoolean("rc_locked", this.lock);
			if(drop!=null){
				NBTTagCompound dropnbt = new NBTTagCompound();
				drop.writeToNBT(dropnbt);
				rc.setTag("rc_dropitem", dropnbt);
			}
			this.writeModuleNBT(rc);
			nbt.setTag(OCMinecart.MODID+":rc_settings", rc);
		}
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		if(nbt.hasKey(OCMinecart.MODID+":rc_enabled"))
			enabled = nbt.getBoolean(OCMinecart.MODID+":rc_enabled");
		
		if(nbt.hasKey(OCMinecart.MODID+":rc_settings") && this.enabled){
			NBTTagCompound rc = nbt.getCompoundTag(OCMinecart.MODID+":rc_settings");
			if(rc.hasKey("rc_uuid")) this.uuid = rc.getString("rc_uuid");
			if(rc.hasKey("rc_respbroadcast")) this.respbroadcast = rc.getBoolean("rc_respbroadcast");
			if(rc.hasKey("rc_respport")) this.respport = rc.getInteger("rc_respport");
			if(rc.hasKey("rc_cmdport")) this.cmdport = rc.getInteger("rc_cmdport");
			if(rc.hasKey("rc_maxwlan")) this.maxWlanStrength = rc.getInteger("rc_maxwlan");
			if(rc.hasKey("rc_curwlan")) this.cmdport = rc.getInteger("rc_curwlan");
			if(rc.hasKey("rc_dropitem")){
				NBTTagCompound dropnbt = rc.getCompoundTag("rc_dropitem");
				drop=ItemStack.loadItemStackFromNBT(dropnbt);
			}
			if(rc.hasKey("rc_owner")) this.owner=rc.getString("rc_owner");
			if(rc.hasKey("rc_locked")) this.lock=rc.getBoolean("rc_locked");
			this.loadModuleNBT(rc);
		}
		
		if(this.enabled){
			API.network.joinWirelessNetwork(this);
			RemoteExtenderRegister.addRemoteUpdate(this);
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
	
	public String getAddress(){
		return (this.enabled) ? this.uuid : null;
	}
	
	protected void loadModuleNBT(NBTTagCompound nbt){};
	protected void writeModuleNBT(NBTTagCompound nbt){}

	public void onAnalyzeModule(EntityPlayer p) {
		p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"Address: "+EnumChatFormatting.RESET+this.uuid));
		p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"Response Port: "+EnumChatFormatting.RESET+this.respport));
		p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"Command Port: "+EnumChatFormatting.RESET+this.cmdport));
		p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"Boradcast Response: "+EnumChatFormatting.RESET+this.respbroadcast));
		p.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"Wireless Strength: "+EnumChatFormatting.RESET+this.curWlanStrength+" / "+this.maxWlanStrength));
	}
	
	public void dropItem(){
		if(this.drop!=null)
			ItemUtil.dropItem(this.drop, this.entity.worldObj, 
					this.entity.posX, this.entity.posY, this.entity.posZ, true);
	}
	
	public boolean editableByPlayer(EntityPlayer p, boolean noPublic){
		return (!this.lock && !noPublic) || this.owner==null || p.getUniqueID().toString().equals(this.owner)
				|| MinecraftServer.getServer().getConfigurationManager().func_152596_g(p.getGameProfile());
	}
	
	public EntityMinecart getCart(){
		return this.entity;
	}
	
	public ItemStack getRemoteItem(){
		return this.drop;
	}
	
	public void setRemoteItem(ItemStack drop){
		if(drop!=null){
			this.drop=drop.copy();
			this.drop.stackSize = 1;
		}
		else this.drop=null;
	}

	public int getMaxWlanStrength() {
		return maxWlanStrength;
	}

	public void setMaxWlanStrength(int maxWlanStrength) {
		this.maxWlanStrength = maxWlanStrength;
		this.curWlanStrength=Math.min(this.curWlanStrength, this.maxWlanStrength);
	}

	public int getCurWlanStrength() {
		return curWlanStrength;
	}

	public void setCurWlanStrength(int curWlanStrength) {
		this.curWlanStrength = Math.min(curWlanStrength, maxWlanStrength);
	}
	
	public void setOwner(String uuid){
		this.owner=uuid;
	}
	
	public String getOwner(){
		return this.owner;
	}
	
	public void setLocked(boolean lock){
		this.lock=lock;
	}

	public boolean isLocked(){
		return this.lock;
	}
}
