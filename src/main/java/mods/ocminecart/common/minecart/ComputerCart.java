package mods.ocminecart.common.minecart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import li.cil.oc.api.API;
import li.cil.oc.api.Manual;
import li.cil.oc.api.internal.MultiTank;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.integration.opencomputers.DriverCPU;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.ISyncEntity;
import mods.ocminecart.common.inventory.ComponetInventory;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ComputercartInventory;
import mods.ocminecart.network.message.EntitySyncRequest;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;


public class ComputerCart extends AdvCart implements MachineHost, Analyzable, Robot, ISyncEntity{
	
	private int tier = -1;
	private Machine machine;
	private boolean firstupdate = true;
	private boolean chDim = false;
	
	public ComponetInventory compinv = new ComponetInventory(this){

		@Override
		public int getSizeInventory() {
			// 9 Upgrade Slots; 3 Container Slots; 8 Component Slots(CPU,Memory,...); 3 Provided Container Slots (the Container Slots in the GUI)
			return 23; 
		}
		
		@Override
		protected void onItemAdded(int slot, ItemStack stack){
			super.onItemAdded(slot, stack);
			ModNetwork.sendToNearPlayers(new ComputercartInventory((ComputerCart) this.host,slot,stack), this.host.xPosition(), this.host.xPosition(), this.host.xPosition(), this.host.world());
		}
		
		@Override
		protected void onItemRemoved(int slot, ItemStack stack){
			super.onItemRemoved(slot, stack);
			ModNetwork.sendToNearPlayers(new ComputercartInventory((ComputerCart) this.host,slot,stack), this.host.xPosition(), this.host.xPosition(), this.host.xPosition(), this.host.world());
		}
		
	};
	
	
	public ComputerCart(World p_i1712_1_) {
		super(p_i1712_1_);
	}

	public ComputerCart(World w, double x, double y, double z, Iterable<Pair<Integer, ItemStack>> components, int tier) {
		super(w,x,y,z);
		this.tier=tier;
		
		Iterator<Pair<Integer, ItemStack>> list = components.iterator();
		while(list.hasNext()){
			Pair<Integer, ItemStack> pair = list.next();
			if(pair.getKey() < this.compinv.getSizeInventory() && pair.getValue() != null){
				compinv.setInventorySlotContents(pair.getKey(), pair.getValue());
			}
		}
		
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		this.machine = li.cil.oc.api.Machine.create(this);
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()){
			this.machine.setCostPerTick(1.0);
		}
		
	}
	
	/*------NBT/Sync-Stuff-------*/
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		
		if(nbt.hasKey("components")) this.compinv.readNBT((NBTTagList) nbt.getTag("components"));
		if(nbt.hasKey("tier")) this.tier = nbt.getInteger("tier");
		
		this.machine.onHostChanged();
		if(nbt.hasKey("machine"))this.machine.load(nbt.getCompoundTag("machine"));
		
		this.connectNetwork();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		
		this.compinv.saveComponents();
		
		nbt.setTag("components", this.compinv.writeNTB());
		nbt.setInteger("tier", this.tier);
		
		NBTTagCompound machine = new NBTTagCompound();
		this.machine.save(machine);
		nbt.setTag("machine", machine);
	}
	
	@Override
	public void writeSyncData(NBTTagCompound nbt) {
		this.compinv.saveComponents();
		nbt.setTag("components", this.compinv.writeNTB());
	}

	@Override
	public void readSyncData(NBTTagCompound nbt) {
		this.compinv.readNBT((NBTTagList) nbt.getTag("components"));
		this.compinv.connectComponents();
	}
	
	/*--------------------*/
	
	/*------Interaction-------*/
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		if(this.firstupdate){
			this.firstupdate=false;
			if(this.worldObj.isRemote) ModNetwork.channel.sendToServer(new EntitySyncRequest(this));
			else{
				if(this.machine.node().network()==null){
					this.connectNetwork();
				}
			}
		}
		
		if(!this.worldObj.isRemote){
			this.machine.update();
			this.compinv.updateComponents();
			((Connector)this.machine.node()).changeBuffer(50); //Just for testing (Infinite Energy)
		}
		
	}
	
	private void connectNetwork(){
		API.network.joinNewNetwork(machine.node());
		this.compinv.connectComponents();
	}
	
	@Override
	public void setDead(){
		super.setDead();
		if (!this.worldObj.isRemote && !this.chDim) {
			this.machine.stop();
			this.machine.node().remove();
			this.compinv.disconnectComponents();
			this.compinv.saveComponents();
		}
	}
	
	@Override
	public boolean interactFirst(EntityPlayer p){
		boolean openwiki = p.getHeldItem()!=null && p.isSneaking() && p.getHeldItem().getItem() == API.items.get("manual").item();

		if(this.worldObj.isRemote && openwiki){
			Manual.navigate(OCMinecart.MODID+"/%LANGUAGE%/item/cart.md");
			Manual.openFor(p);
		}
		else if(!this.worldObj.isRemote && !openwiki){
			this.machine.start();
			p.openGui(OCMinecart.instance, 1, this.worldObj, this.getEntityId(), -10, 0);
		}
		return true;
	}
	
	@Override
	public Node[] onAnalyze(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return new Node[]{this.machine.node()};
	}
	
	
	/*------------------------*/
	
	/*-----Minecart/Entity-Stuff-------*/
	@Override
	public int getMinecartType() {
		return -1;
	}

	public static EntityMinecart create(World w, double x, double y, double z, Iterable<Pair<Integer, ItemStack>> components, int tier) {
		return new ComputerCart(w, x, y, z, components, tier);
	}
	
	@Override
	public ItemStack getCartItem(){
		ItemStack stack = new ItemStack(ModItems.item_ComputerCart);
		
		
		ArrayList<Pair<Integer,ItemStack>> components = new ArrayList<Pair<Integer,ItemStack>>();
		for(int i=0;i<compinv.getSizeInventory();i+=1){
			if(compinv.getStackInSlot(i)!=null)
				components.add(Pair.of(i, compinv.getStackInSlot(i)));
		}
		
		return ItemComputerCart.setTags(stack, components, tier);
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
	public void markChanged() {
		// TODO Auto-generated method stub
		
	}

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
	public void onMachineConnect(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMachineDisconnect(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IInventory equipmentInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInventory mainInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultiTank tank() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int selectedSlot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSelectedSlot(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int selectedTank() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSelectedTank(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityPlayer player() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID ownerUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection facing() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return this.machine.node();
	}

	@Override
	public void onConnect(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int tier() {
		return this.tier();
	}
	/*-----------------------------*/
	
	/*-------Inventory--------*/
	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,int p_102007_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void markDirty() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		// TODO Auto-generated method stub
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
	public void synchronizeSlot(int slot) {
		// TODO Auto-generated method stub
		
	}
	
	/*----------------------------*/

	@Override
	public boolean shouldAnimate() {
		// TODO Auto-generated method stub
		return false;
	}


	public ComponetInventory getCompinv() {
		return this.compinv;
	}
	
	
}