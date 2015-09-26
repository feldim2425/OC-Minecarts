package mods.ocminecart.common.minecart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import li.cil.oc.api.API;
import li.cil.oc.api.Manual;
import li.cil.oc.api.component.Keyboard;
import li.cil.oc.api.component.TextBuffer;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Inventory;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.internal.MultiTank;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.server.agent.Player;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.Settings;
import mods.ocminecart.common.ISyncEntity;
import mods.ocminecart.common.Sound;
import mods.ocminecart.common.blocks.INetRail;
import mods.ocminecart.common.component.ComputerCartController;
import mods.ocminecart.common.driver.CustomDriver;
import mods.ocminecart.common.inventory.ComponetInventory;
import mods.ocminecart.common.inventory.ComputercartInventory;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.util.ComputerCartData;
import mods.ocminecart.common.util.ItemUtil;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ComputercartInventoryUpdate;
import mods.ocminecart.network.message.EntitySyncRequest;
import mods.ocminecart.network.message.UpdateRunning;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import cpw.mods.fml.common.FMLCommonHandler;

//The internal.Robot interface will get replaced by a custom Interface later.
//But I will do that later because I have to create custom dirvers for some internal components. (and I'am lazy) ;)
//I hope this will work and I can override the Drivers for this Host.

/*
 * Items that need new Dirvers:
 * - Crafting Upgrade
 * - Inventory Controller
 * - Tank Controller
 */
public class ComputerCart extends AdvCart implements MachineHost, Analyzable, ISyncEntity, IComputerCart{
	
	private final boolean isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
	
	private int tier = -1;	//The tier of the cart
	private Machine machine; //The machine object
	private boolean firstupdate = true; //true if the update() function gets called the first time
	private boolean chDim = false;	//true if the cart changing the dimension (Portal, AE2 Storage,...)
	private boolean isRun = false; //true if the machine is turned on;
	private ComputerCartController controller = new ComputerCartController(this); //The computer cart component
	private double startEnergy = -1; //Only used when placing the cart. Start energy stored in the item
	private int invsize = 0; //The current inventory size depending on the Inventory Upgrades
	private boolean onrail = false; // Store onRail from last tick to send a Signal
	private int selSlot = 0; //The index of the current selected slot
	private int selTank = 0; //The index of the current selected tank
	//private Player player = new li.cil.oc.server.agent.Player(this);  //OC's fake player
	private Player player;
	private String name; //name of the cart
	
	private int cRailX = 0;	// Position of the connected Network Rail
	private int cRailY = 0;
	private int cRailZ = 0;
	private int cRailDim = 0;
	private boolean cRailCon = false; //True if the card is connected to a network rail
	private Node cRailNode = null; // This node will not get saved in NBT because it should automatic disconnect after restart; 
	
	
	public ComponetInventory compinv = new ComponetInventory(this){

		@Override
		public int getSizeInventory() {
			// 9 Upgrade Slots; 3 Container Slots; 8 Component Slots(CPU,Memory,...); 3 Provided Container Slots (the Container Slots in the GUI)
			return 23; 
		}
		
		@Override
		protected void onItemAdded(int slot, ItemStack stack){
			if(FMLCommonHandler.instance().getEffectiveSide().isServer()){
				super.onItemAdded(slot, stack);
				((ComputerCart) this.host).synchronizeComponentSlot(slot);
			}
			
			if(this.getSlotType(slot) == Slot.Floppy) Sound.play(this.host, "floppy_insert");
			else if(this.getSlotType(slot) == Slot.Upgrade && FMLCommonHandler.instance().getEffectiveSide().isServer()){
				Item drv = CustomDriver.driverFor(stack, this.host.getClass());
				if(drv instanceof Inventory){
					((ComputerCart)host).setInventorySpace(0);
					((ComputerCart)host).checkInventorySpace();
				}
			}
		}
		
		@Override
		protected void onItemRemoved(int slot, ItemStack stack){
			super.onItemRemoved(slot, stack);
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
				((ComputerCart) this.host).synchronizeComponentSlot(slot);
			
			if(this.getSlotType(slot) == Slot.Floppy) Sound.play(this.host, "floppy_eject");
			else if(this.getSlotType(slot) == Slot.Upgrade && FMLCommonHandler.instance().getEffectiveSide().isServer()){
				Item drv = CustomDriver.driverFor(stack, this.host.getClass());
				if(drv instanceof Inventory){
					((ComputerCart)host).setInventorySpace(0);
					((ComputerCart)host).checkInventorySpace();
				}
			}
		}
		
		@Override
		public void connectItemNode(Node node){
			super.connectItemNode(node);
			if(node!=null){
				if(node.host() instanceof TextBuffer){
					for(int i=0;i<this.getSizeInventory();i+=1){
						if((this.getSlotComponent(i) instanceof Keyboard) && this.getSlotComponent(i).node()!=null)
							node.connect(this.getSlotComponent(i).node());
					}
				}
				else if(node.host() instanceof Keyboard){
					for(int i=0;i<this.getSizeInventory();i+=1){
						if((this.getSlotComponent(i) instanceof TextBuffer) && this.getSlotComponent(i).node()!=null)
							node.connect(this.getSlotComponent(i).node());
					}
				}
			}
		}
	};
	
	public ComputercartInventory maininv = new ComputercartInventory(this);
	
	public MultiTank tank = new MultiTank(){
		@Override
		public int tankCount() {
			return 0;
		}

		@Override
		public IFluidTank getFluidTank(int index) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	public ComputerCart(World p_i1712_1_) {
		super(p_i1712_1_);
	}

	public ComputerCart(World w, double x, double y, double z, ComputerCartData data) {
		super(w,x,y,z);
		if(data==null){
			this.setDead();
			data=new ComputerCartData();
		}
		this.tier=data.getTier();
		this.startEnergy=data.getEnergy();
		
		Iterator<Entry<Integer, ItemStack>> list = data.getComponents().entrySet().iterator();
		while(list.hasNext()){
			Entry<Integer, ItemStack> e = list.next();
			if(e.getKey() < this.compinv.getSizeInventory() && e.getValue() != null){
				compinv.updateSlot(e.getKey(), e.getValue());
			}
		}
		
		this.checkInventorySpace();
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		
		this.dataWatcher.addObject(3, 0x0000FF);
		
		this.machine = li.cil.oc.api.Machine.create(this);
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()){
			this.machine.setCostPerTick(Settings.ComputerCartEnergyUse);
			((Connector) this.machine.node()).setLocalBufferSize(Settings.ComputerCartEnergyCap);
		}
		
	}
	
	/*------NBT/Sync-Stuff-------*/
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		
		if(nbt.hasKey("components")) this.compinv.readNBT((NBTTagList) nbt.getTag("components"));
		if(nbt.hasKey("tier")) this.tier = nbt.getInteger("tier"); //TODO: Remove this in the next version.
		if(nbt.hasKey("controller")) this.controller.load(nbt.getCompoundTag("controller"));
		if(nbt.hasKey("inventory")) this.maininv.readFromNBT((NBTTagList) nbt.getTag("inventory"));
		if(nbt.hasKey("netrail")){
			NBTTagCompound netrail = nbt.getCompoundTag("netrail");
			this.cRailCon=true;
			this.cRailX = netrail.getInteger("posX");
			this.cRailY = netrail.getInteger("posY");
			this.cRailZ = netrail.getInteger("posZ");
			this.cRailDim = netrail.getInteger("posDim");
		}
		if(nbt.hasKey("settings")){
			NBTTagCompound set = nbt.getCompoundTag("settings");
			if(set.hasKey("lightcolor")) this.setLightColor(set.getInteger("lightcolor"));
			if(set.hasKey("selectedslot")) this.selSlot = set.getInteger("selectedslot");
			if(set.hasKey("selectedtank")) this.selTank = set.getInteger("selectedtank");
			if(set.hasKey("tier")) this.tier = set.getInteger("tier");
		}
		
		
		this.machine.onHostChanged();
		if(nbt.hasKey("machine"))this.machine.load(nbt.getCompoundTag("machine"));
		
		this.connectNetwork();
		this.checkInventorySpace();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		if(!this.isServer) return;
		
		super.writeEntityToNBT(nbt);
		
		this.compinv.saveComponents();
		
		nbt.setTag("components", this.compinv.writeNTB());
		nbt.setTag("inventory", this.maininv.writeToNBT());
		
		//Controller tag
		NBTTagCompound controller = new NBTTagCompound();
		this.controller.save(controller);
		nbt.setTag("controller", controller);
		
		//Data about the connected rail
		if(this.cRailCon){
			NBTTagCompound netrail = new NBTTagCompound();
			netrail.setInteger("posX", this.cRailX);
			netrail.setInteger("posY", this.cRailY);
			netrail.setInteger("posZ", this.cRailZ);
			netrail.setInteger("posDim", this.cRailDim);
			nbt.setTag("netrail", netrail);
		}
		else if(nbt.hasKey("netrail")) nbt.removeTag("netrail");
		
		//Some additional values like light color, selected Slot, ...
		NBTTagCompound set = new NBTTagCompound();
		set.setInteger("lightcolor", this.getLightColor());
		set.setInteger("selectedslot",this.selSlot);
		set.setInteger("selectedtank",this.selTank);
		set.setInteger("tier", this.tier);
		nbt.setTag("settings", set);
		
		NBTTagCompound machine = new NBTTagCompound();
		this.machine.save(machine);
		nbt.setTag("machine", machine);
	}
	
	@Override
	public void writeSyncData(NBTTagCompound nbt) {
		this.compinv.saveComponents();
		nbt.setTag("components", this.compinv.writeNTB());
		nbt.setBoolean("isRunning", this.isRun);
	}

	@Override
	public void readSyncData(NBTTagCompound nbt) {
		this.compinv.readNBT((NBTTagList) nbt.getTag("components"));
		this.isRun = nbt.getBoolean("isRunning");
		this.compinv.connectComponents();
	}
	
	/*--------------------*/
	
	/*------Interaction-------*/
	
	protected void checkInventorySpace(){
		for(int i=0;i<this.compinv.getSizeInventory();i+=1){
			if(this.compinv.getStackInSlot(i)!=null){
				ItemStack stack = this.compinv.getStackInSlot(i);
				Item drv = CustomDriver.driverFor(stack, this.getClass());
				if(drv instanceof Inventory && this.invsize<this.maininv.getMaxSizeInventory()){
					this.invsize = this.invsize+((Inventory)drv).inventoryCapacity(stack);
					if(this.invsize>this.maininv.getMaxSizeInventory()) this.invsize = this.maininv.getMaxSizeInventory();
				}
			}
		}
		Iterable<ItemStack> over = this.maininv.removeOverflowItems(this.invsize);
		ItemUtil.dropItemList(over, this.worldObj, this.posX, this.posY, this.posZ);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		//Only executed at the first function call
		if(this.firstupdate){
			this.firstupdate=false;
			//Request a entity data sync
			if(this.worldObj.isRemote) ModNetwork.channel.sendToServer(new EntitySyncRequest(this));
			else{
				if(this.startEnergy > 0) ((Connector)this.machine.node()).changeBuffer(this.startEnergy); //Give start energy
				if(this.machine.node().network()==null){
					this.connectNetwork(); //Connect all nodes (Components & Controller)
				}
				//Update onRail Value
				this.onrail = this.onRail();
			}
		}
		
		if(!this.worldObj.isRemote){
			//Update the machine and the Components
			if(this.isRun){
				this.machine.update();
				this.compinv.updateComponents();
			}
			//Check if the machine state has changed.
			if(this.isRun != this.machine.isRunning()){
				this.isRun=this.machine.isRunning();
				ModNetwork.sendToNearPlayers(new UpdateRunning(this,this.isRun), this.posX, this.posY, this.posZ, this.worldObj);
				if(!this.isRun) this.engineSpeed = 0;
			}
			//Consume energy for the Engine
			if(this.engineSpeed != 0 && !this.enableBreak && this.onRail()){
				if(!((Connector)this.machine.node()).tryChangeBuffer(-1.0 * this.engineSpeed * Settings.ComputerCartEngineUse))
				{
					this.machine.signal("engine_failed",this.engineSpeed);
					this.engineSpeed = 0;
				}
			}
			//Check if the cart is on a Track
			if(this.onrail != this.onRail())
			{
				this.onrail = !this.onrail;
				this.machine.signal("track_state",this.onrail);
			}
			//Give the cart energy if it is a creative cart
			if(this.tier==3)((Connector)this.machine.node()).changeBuffer(Integer.MAX_VALUE);
			//Connect / Disconnect a network rail
			this.checkRailConnection();
		}
	}
	
	private void connectNetwork(){
		API.network.joinNewNetwork(machine.node());
		this.compinv.connectComponents();
		this.machine.node().connect(this.controller.node());
	}
	
	@Override
	public void setDead(){
		super.setDead();
		if (!this.worldObj.isRemote && !this.chDim) {
			this.machine.stop();
			this.machine.node().remove();
			this.controller.node().remove();
			this.compinv.disconnectComponents();
			this.compinv.saveComponents();
			this.compinv.removeTagsForDrop();
		}
	}
	
	@Override
	public void killMinecart(DamageSource dms){
		super.killMinecart(dms);
		List<ItemStack> drop = new ArrayList<ItemStack>();
		for(int i=20;i<23;i+=1){
			if(compinv.getStackInSlot(i)!=null) drop.add(compinv.getStackInSlot(i));
		}
		Iterator<ItemStack> minv = this.maininv.removeOverflowItems(0).iterator();
		while(minv.hasNext()) drop.add(minv.next());
		ItemUtil.dropItemList(drop, this.worldObj, this.posX, this.posY, this.posZ);
		this.setDamage(Float.MAX_VALUE); //Sometimes the cart stay alive this should fix it.
	}
	
	@Override
	public boolean interactFirst(EntityPlayer p){
		ItemStack refMan = API.items.get("manual").createItemStack(1);
		boolean openwiki = p.getHeldItem()!=null && p.isSneaking() && p.getHeldItem().getItem() == refMan.getItem() && p.getHeldItem().getItemDamage() == refMan.getItemDamage();
		
		if(this.worldObj.isRemote && openwiki){
			Manual.navigate(OCMinecart.MODID+"/%LANGUAGE%/item/cart.md");
			Manual.openFor(p);
		}
		else if(!this.worldObj.isRemote && !openwiki){
			p.openGui(OCMinecart.instance, 1, this.worldObj, this.getEntityId(), -10, 0);
		}
		else if(this.worldObj.isRemote && !openwiki){
			p.swingItem();
		}
		return true;
	}
	
	@Override
	public Node[] onAnalyze(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return new Node[]{this.machine.node()};
	}
	
	private void checkRailConnection(){
		//If the cart isn't connected check for a new connection
		if(!this.cRailCon && this.onRail() && (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) instanceof INetRail)){
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY);
			int z = MathHelper.floor_double(this.posZ);
			INetRail netrail = (INetRail) this.worldObj.getBlock(x,y,z);
			if(netrail.isValid(this.worldObj, x, y, z, this) && netrail.getResponseEnvironment(this.worldObj, x, y, z) != null){
				this.cRailX = MathHelper.floor_double(this.posX);
				this.cRailY = MathHelper.floor_double(this.posY);
				this.cRailZ = MathHelper.floor_double(this.posZ);
				this.cRailDim = this.worldObj.provider.dimensionId;
				this.cRailCon = true;
			}
		}
		//If the cart is connected to a rail check if the connection is still valid and connect or disconnect
		if(this.cRailCon){
			World w = DimensionManager.getWorld(this.cRailDim);
			if( w.getBlock(this.cRailX,this.cRailY,this.cRailZ) instanceof INetRail){
				INetRail netrail = (INetRail) w.getBlock(this.cRailX,this.cRailY,this.cRailZ);
				//Connect a new network Rail
				if(netrail.isValid(w, this.cRailX, this.cRailY, this.cRailZ, this) && netrail.getResponseEnvironment(w, this.cRailX, this.cRailY, this.cRailZ)!=null){
					Node railnode = netrail.getResponseEnvironment(w, this.cRailX, this.cRailY, this.cRailZ).node();
					if(!this.machine.node().canBeReachedFrom(railnode)){
						this.machine.node().connect(railnode);
						this.cRailNode = railnode;
						this.machine.signal("network_rail", true);
					}
				}
				//Disconnect when the cart leaves a network rail
				else if(netrail.getResponseEnvironment(w, this.cRailX, this.cRailY, this.cRailZ)!=null){
					Node railnode = netrail.getResponseEnvironment(w, this.cRailX, this.cRailY, this.cRailZ).node();
					if(this.machine.node().canBeReachedFrom(railnode)){
						this.machine.node().disconnect(railnode);
						this.cRailCon=false;
						this.cRailNode = null;
						this.machine.signal("network_rail", false);
					}
				}
			}
			//Disconnect if the network rail is not there
			else{
				if(this.cRailNode!=null && this.machine.node().canBeReachedFrom(this.cRailNode)){
					this.machine.node().disconnect(this.cRailNode);
					this.cRailNode = null;
					this.machine.signal("network_rail", false);
				}
				this.cRailCon=false;
			}	
		}
	}
	
	/*------------------------*/
	
	/*-----Minecart/Entity-Stuff-------*/
	@Override
	public int getMinecartType() { return -1; }

	public static EntityMinecart create(World w, double x, double y, double z, ComputerCartData data) { return new ComputerCart(w, x, y, z, data); }
	
	@Override
	public ItemStack getCartItem(){
		ItemStack stack = new ItemStack(ModItems.item_ComputerCart);
		
		
		Map<Integer,ItemStack> components = new HashMap<Integer,ItemStack>();
		for(int i=0;i<20;i+=1){
			if(compinv.getStackInSlot(i)!=null)
				components.put(i, compinv.getStackInSlot(i));
		}
		
		ComputerCartData data = new ComputerCartData();
		data.setEnergy(((Connector)this.machine().node()).localBuffer());
		data.setTier(this.tier);
		data.setComponents(components);
		ItemComputerCart.setData(stack, data);
		
		return stack;
	}
	
	@Override
	public void travelToDimension(int dim){
		try{
			this.chDim = true;
			super.travelToDimension(dim);
		}
		finally{
			this.chDim = false;
			this.setDead();
		}
	}
	
	@Override
	public boolean hasCustomInventoryName(){ return true; } // Drop Item on Kill when Player is in Creative Mode
	@Override
	public ItemStack getPickedResult(MovingObjectPosition target){ return null; } 
	
	/*----------------------------------*/
	
	/*--------MachineHost--------*/
	@Override
	public World world() {
		return this.worldObj;
	}

	@Override
	public double xPosition() {
		return this.posX;
	}

	@Override
	public double yPosition() {
		return this.posY;
	}

	@Override
	public double zPosition() {
		return this.posZ;
	}

	@Override
	public void markChanged() {}

	@Override
	public Machine machine() {
		return this.machine;
	}

	@Override
	public Iterable<ItemStack> internalComponents() {
		ArrayList<ItemStack> components = new ArrayList<ItemStack>();
		for(int i=0;i<compinv.getSizeInventory();i+=1){
			if(compinv.getStackInSlot(i)!=null && this.compinv.isComponentSlot(i, compinv.getStackInSlot(i)))
				components.add(compinv.getStackInSlot(i));
		}
		return components;
	}

	@Override
	public int componentSlot(String address) {
		for(int i=0;i<this.compinv.getSizeInventory();i+=1){
			ManagedEnvironment env = this.compinv.getSlotComponent(i);
			if(env != null && env.node()!=null && env.node().address() == address) return i;
		}
		return -1;
	}

	@Override
	public void onMachineConnect(Node node) {}

	@Override
	public void onMachineDisconnect(Node node) {}

	@Override
	public IInventory equipmentInventory() { return null; }

	@Override
	public IInventory mainInventory() {
		return this.maininv;
	}

	@Override
	public MultiTank tank() {
		return null;
	}

	@Override
	public int selectedSlot() {
		return this.selSlot;
	}

	@Override
	public void setSelectedSlot(int index) {
		if(index<this.maininv.getSizeInventory())
			this.selSlot=index;
	}

	@Override
	public int selectedTank() {
		return this.selTank;
	}

	@Override
	public void setSelectedTank(int index) {
		if(index<this.tank().tankCount())
			this.selTank=index;
	}

	@Override
	public EntityPlayer player() {
		this.player.updatePositionAndRotation(player, this.facing(), this.facing());
		return this.player;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String ownerName() {
		return li.cil.oc.Settings.get().fakePlayerName();
	}

	@Override
	public UUID ownerUUID() {
		return li.cil.oc.Settings.get().fakePlayerProfile().getId();
	}

	@Override
	//http://jabelarminecraft.blogspot.co.at/p/minecraft-forge-172-finding-block.html
	public ForgeDirection facing() {
		int dir = MathHelper.floor_double((double) (this.rotationYaw * 4.0F / 360) + 0.50) & 3;
		return ForgeDirection.getOrientation(dir);
	}

	@Override
	public ForgeDirection toGlobal(ForgeDirection value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection toLocal(ForgeDirection value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node node() {
		return this.machine.node();
	}

	@Override
	public void onConnect(Node node) {}

	@Override
	public void onDisconnect(Node node) {}

	@Override
	public void onMessage(Message message) {}

	@Override
	public int tier() {
		return this.tier();
	}
	/*-----------------------------*/
	
	/*-------Inventory--------*/

	@Override
	public int getSizeInventory() {
		return this.maininv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.maininv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int number) {
		return this.maininv.decrStackSize(slot, number);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.maininv.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return "inventory."+OCMinecart.MODID+".computercart";
	}

	@Override
	public int getInventoryStackLimit() {
		return this.maininv.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSqToEntity(this)<=64 && !this.isDead;
	}

	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return this.maininv.isItemValidForSlot(slot, stack);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int componentCount() {
		int count = 0;
		Iterator<ManagedEnvironment> list=this.compinv.getComponents().iterator();
		while(list.hasNext()){
			count+=1;
			list.next();
		}
		return count;
	}

	@Override
	public Environment getComponentInSlot(int index) {
		if(index>=this.compinv.getSizeInventory()) return null;
		return this.compinv.getSlotComponent(index);
	}

	@Override
	public void synchronizeComponentSlot(int slot) {
		if(!this.worldObj.isRemote)
			ModNetwork.sendToNearPlayers(new ComputercartInventoryUpdate(this, slot, this.compinv.getStackInSlot(slot)), this.posX, this.posY, this.posZ, this.worldObj);
	}
	
	/*----------------------------*/

	/*------Setters & Getters-----*/
	public ComponetInventory getCompinv() {
		return this.compinv;
	}
	
	public void setRunning(boolean newVal) {
		if(this.worldObj.isRemote) this.isRun=newVal;
		else{
			if(newVal) this.machine.start();
			else this.machine.stop();
		}
	}
	
	public boolean getRunning() {
		return this.isRun;
	}

	public double getEnergy() {
		if(!this.worldObj.isRemote) return ((Connector)this.machine.node()).globalBuffer();
		return -1;
	}
	
	public double getMaxEnergy() {
		if(!this.worldObj.isRemote) return ((Connector)this.machine.node()).globalBufferSize();
		return -1;
	}
	
	protected void setInventorySpace(int invsize) { this.invsize = invsize; }
	public int getInventorySpace() { return this.invsize; }
	
	public boolean getBreakState(){ return this.enableBreak; }
	public void setBreakState(boolean state){ this.enableBreak = state; }
	
	public double getEngineState(){ return this.engineSpeed; }
	public void setEngineState(double speed){ this.engineSpeed = speed; }
	
	public int getLightColor(){ return this.dataWatcher.getWatchableObjectInt(3); }
	public void setLightColor(int color){ this.dataWatcher.updateObject(3, color);}
	
	
}