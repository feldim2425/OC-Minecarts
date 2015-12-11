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
import mods.ocminecart.common.util.StringUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.base.Charsets;

public abstract class RemoteCartExtender implements WirelessEndpoint, IExtendedEntityProperties{
	
	public final static String PROP_ID = OCMinecart.MODID+"RCcart";
	
	protected EntityMinecart entity;
	private World worldObj;
	private int posX;
	private int posY;
	private int posZ;
	
	protected boolean enabled=false;
	protected boolean respbroadcast = true;
	private int nextResp = -1;
	private String nextAddr = null;
	protected int respport = 1;
	protected int cmdport = 2;
	private String uuid;
	private String address;
	private String owner = null;
	private byte[] password = null;
	private boolean lock = true;
	private ItemStack drop = null;
	private int maxWlanStrength=4;
	private int curWlanStrength=4;
	
	@Override
	public int x() {
		return posX;
	}

	@Override
	public int y() {
		return posY;
	}

	@Override
	public int z() {
		return posZ;
	}
	
	public final void setEnabled(boolean state) { this.setEnabled(state,false); }
	
	public final void setEnabled(boolean state, boolean force) {
		if(this.enabled != state || force){
			this.enabled = state;
			if(state){
				if(this.maxWlanStrength>0)API.network.joinWirelessNetwork(this);
				RemoteExtenderRegister.addRemoteUpdate(this);
			}
			else{
				API.network.leaveWirelessNetwork(this);
				RemoteExtenderRegister.removeRemote(this.uuid);
			}
			this.changeEnabled();
		}
	}
	
	public String getUUID(){
		return this.uuid;
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	protected void changeEnabled() {
		NBTTagCompound nbt = this.entity.getEntityData();
		nbt.removeTag(OCMinecart.MODID+":rc_settings");
		
		if(isEnabled()){	//reset some settings
			curWlanStrength = 4;
			address = UUID.randomUUID().toString();
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
			else if((a1!=null && a1.equals("-t")) || a1==null)
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
			int port = (isValid) ? (int)(double)(Double)args[0] : 0; //Double is a Class and have to be converted to a double (type)
			port = Math.max(-1, port);
			if(isValid) this.respport = port;
			this.sendPacket(new Object[]{this.respport}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("command_port")){
			boolean isValid = args.length>=1 && (args[0] instanceof Double);
			int port = (isValid) ? (int)(double)(Double)args[0] : 0;
			port = Math.max(-1, port);
			if(isValid) this.cmdport = port;
			this.sendPacket(new Object[]{this.cmdport}, this.getRespPort(), this.getRespAddress());
		}
		else if(cmd.equals("response_broadcast")){
			boolean isValid = args.length>=1 && (args[0] instanceof Boolean);
			boolean value = (isValid) ? (Boolean)args[0] : false;
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
		String st = "{";
		while(it.hasNext())
		{
			st += it.next()+ ((it.hasNext()) ? "," : "");
		}
		st += "}";
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
		Packet packet = API.network.newPacket(uuid, des, port, msg);
		API.network.sendWirelessPacket(this, this.curWlanStrength, packet);
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
			return "doc([func:string]):table or string -- get a list of functions or a documentation for a function.";
		else if(cmd.equals("response_port"))
			return "response_port([port:number]):number -- sets the response port and returns the new port. -1 to response on the same port as the last message";
		else if(cmd.equals("command_port"))
			return "command_port([port:number]):number -- sets the command port and returns the new port. -1 to accept all ports";
		else if(cmd.equals("response_broadcast"))
			return "response_broadcast([value:boolean]):boolean -- if the value is true it will respond with private messages.";
		else if(cmd.equals("wlan_strength"))
			return "wlan_strength([value:number]):number,number -- get/set the current and get the max. wireless strength.";
		return null;
	}
	
	public void update() {
		if(this.world().isRemote) return;
		boolean hasEntity = worldObj.getLoadedEntityList().contains(this.entity);
		boolean chunkLoaded = worldObj.getChunkFromBlockCoords((int)entity.posX, (int)entity.posZ).isChunkLoaded;
		if(this.entity.isDead || this.entity.getDamage()>=this.getMaxModuleDamage()){
			this.setEnabled(false, true);
			if(this.entity.getDamage()>=this.getMaxModuleDamage()){
				this.dropItem();
			}
		}
		else{
			if(!chunkLoaded || !hasEntity){
				/*  
				 * Stop the updates on chunk unload.
				 * A chunk reload should reinitialize the entity and reactivate the updates.
				 */
				RemoteExtenderRegister.removeRemoteUpdate(this);
			}
			this.posX=(int) entity.posX;
			this.posY=(int) entity.posY;
			this.posZ=(int) (entity.posZ-1);
			if(this.maxWlanStrength>0) API.network.updateWirelessNetwork(this);
		}
	}

	@Override
	public World world() {
		return worldObj;
	}

	@Override
	public void receivePacket(Packet packet, WirelessEndpoint sender){
		if(packet.ttl()<0 || !inRange(sender,curWlanStrength)) return;
		if(!(packet.destination()==null || packet.destination().equals(this.uuid)) || !(this.cmdport==-1 || packet.port()==this.cmdport))
			return;
		if(!(packet.data()[0] instanceof byte[]) || (!(packet.data()[1] instanceof byte[]) && this.hasPassword())) return;
		
		boolean usePassword = false;
		if(this.hasPassword()){
			String passw = new String((byte[])packet.data()[1],Charsets.UTF_8);
			if(passw.length()<=2 || !passw.substring(0, 2).equals("::") 
					|| !this.isCorrectPassword(passw.substring(2, passw.length()))) return;
			usePassword=true;
		}
		else if(packet.data()[1] instanceof byte[]){
			String passw = new String((byte[])packet.data()[1],Charsets.UTF_8);
			if(passw.length()>=2 || passw.substring(0, 2).equals("::"))
				usePassword=true;
		}
		
		this.nextAddr = packet.source();
		this.nextResp = packet.port();
		
		String cmd = new String((byte[])packet.data()[0],Charsets.UTF_8);
		Object[] data = (this.getCommands().contains(cmd))? this.processPacket(packet.data(), usePassword) : new Object[]{};
		this.processCommand((this.getCommands().contains(cmd))? cmd : null, data);
	}
	
	private Object[] processPacket(Object[] data, boolean hasPassword){
		int dataoffset = (hasPassword) ? 2 : 1;
		if(data.length-dataoffset<1) return new Object[]{};
		Object[] res = new Object[data.length-dataoffset];
		for(int i=dataoffset;i<data.length;i+=1)
		{
			if(data[i] instanceof byte[])
			{
				res[i-dataoffset] = new String((byte[]) data[i], Charsets.UTF_8);
			}
			else
			{
				res[i-dataoffset] = data[i];
			}
		}
		return res;
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setBoolean(OCMinecart.MODID+":rc_enabled", enabled);
		if(enabled){
			NBTTagCompound rc = new NBTTagCompound();
			rc.setString("rc_address", this.address);
			rc.setBoolean("rc_respbroadcast", this.respbroadcast);
			rc.setInteger("rc_respport", this.respport);
			rc.setInteger("rc_cmdport", this.cmdport);
			if(password!=null) rc.setByteArray("rc_password", this.password);
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
		boolean hasEntity = worldObj.getLoadedEntityList().contains(this.entity);
		if(!hasEntity && this.worldObj != entity.worldObj) return;
		
		if(nbt.hasKey(OCMinecart.MODID+":rc_enabled"))
			enabled = nbt.getBoolean(OCMinecart.MODID+":rc_enabled");
		else 
			enabled = false;
		
		if(nbt.hasKey(OCMinecart.MODID+":rc_settings") && this.enabled){
			NBTTagCompound rc = nbt.getCompoundTag(OCMinecart.MODID+":rc_settings");
			if(rc.hasKey("rc_address")) this.address = rc.getString("rc_address");
			if(rc.hasKey("rc_respbroadcast")) this.respbroadcast = rc.getBoolean("rc_respbroadcast");
			if(rc.hasKey("rc_respport")) this.respport = rc.getInteger("rc_respport");
			if(rc.hasKey("rc_cmdport")) this.cmdport = rc.getInteger("rc_cmdport");
			if(rc.hasKey("rc_password")) this.password = rc.getByteArray("rc_password");
			if(rc.hasKey("rc_maxwlan")) this.maxWlanStrength = rc.getInteger("rc_maxwlan");
			if(rc.hasKey("rc_curwlan")) this.curWlanStrength = rc.getInteger("rc_curwlan");
			if(rc.hasKey("rc_dropitem")){
				NBTTagCompound dropnbt = rc.getCompoundTag("rc_dropitem");
				drop=ItemStack.loadItemStackFromNBT(dropnbt);
			}
			if(rc.hasKey("rc_owner")) this.owner=rc.getString("rc_owner");
			if(rc.hasKey("rc_locked")) this.lock=rc.getBoolean("rc_locked");
			this.loadModuleNBT(rc);
		}
		
		if(this.enabled){
			if(this.maxWlanStrength>0) API.network.joinWirelessNetwork(this);
			RemoteExtenderRegister.addRemoteUpdate(this);
		}
		else{
			API.network.leaveWirelessNetwork(this);
			RemoteExtenderRegister.removeRemoteUpdate(this);	// Just to make sure nothing bad happens
		}
	}

	@Override
	public void init(Entity entity, World world) {
		if(!(entity instanceof EntityMinecart)) return;
		if(RemoteExtenderRegister.containsEntity(entity.getUniqueID())) return;
		this.entity = (EntityMinecart)entity;
		this.worldObj = world;
		this.posX=(int) entity.posX;
		this.posY=(int) entity.posY;
		this.posZ=(int) (entity.posZ-1);
		this.uuid=UUID.randomUUID().toString();
	}
	
	public boolean inRange(WirelessEndpoint w, double range){
		int x = this.x() - w.x();
		int y = this.y() - w.y();
		int z = this.z() - w.z();
		return (x*x) + (y*y) + (z*z) <= (range * range); 
	}
	
	public String getAddress(){
		return (this.enabled) ? this.address : null;
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
	
	//Max damage the cart/locomotive can have.
	public int getMaxModuleDamage(){
		return 40;
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
		if(this.maxWlanStrength>1 && maxWlanStrength<1 && this.enabled)
			API.network.leaveWirelessNetwork(this);
		else if(this.maxWlanStrength<1 && maxWlanStrength>1 && this.enabled)
			API.network.joinWirelessNetwork(this);
		this.maxWlanStrength = maxWlanStrength;
		this.curWlanStrength=Math.min(this.curWlanStrength, this.maxWlanStrength);
	}
	
	public void setPassword(String password){
		if(password==null || password.length()<1){
			this.password=null;
			return;
		}
		this.password=StringUtil.getMD5Array(password);
	}
	
	public boolean hasPassword(){
		return this.password!=null;
	}
	
	public boolean isCorrectPassword(String password){
		if(this.password==null) return true;
		byte[] pass = StringUtil.getMD5Array(password);
		String hex1 = StringUtil.byteToHex(this.password);
		String hex2 = StringUtil.byteToHex(pass);
		return hex1.equals(hex2);
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
