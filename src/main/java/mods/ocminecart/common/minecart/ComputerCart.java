package mods.ocminecart.common.minecart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import li.cil.oc.api.API;
import li.cil.oc.api.internal.MultiTank;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.inventory.ComponetInventory;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ComputercartInventory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;


public class ComputerCart extends AdvCart implements MachineHost, Analyzable, Robot{
	
	private int tier = -1;
	private Machine machine;
	private boolean firstupdate = true;
	
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
	
	protected void entityInit(){
		super.entityInit();
		this.machine = li.cil.oc.api.Machine.create(this);
	}
	
	/*------NBT-Stuff-------*/
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("components")) this.compinv.readNBT((NBTTagList) nbt.getTag("components"));
		if(nbt.hasKey("tier")) this.tier = nbt.getInteger("tier");
		if(nbt.hasKey("machine")) this.machine.load(nbt.getCompoundTag("machine"));
		if(nbt.hasKey("compnode")) this.compinv.node().load(nbt.getCompoundTag("compnode"));
		
		this.compinv.connectComponents();
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		
		this.compinv.saveComponents();
		
		nbt.setTag("components", this.compinv.writeNTB());
		nbt.setInteger("tier", this.tier);
		
		NBTTagCompound machine = new NBTTagCompound();
		this.machine.save(machine);
		nbt.setTag("machine", machine);
		
		NBTTagCompound compnode = new NBTTagCompound();
		this.compinv.node().save(compnode);
		nbt.setTag("compnode", machine);
	}
	
	public void onUpdate(){
		super.onUpdate();
		if(this.firstupdate){
			this.firstupdate=false;
			if(!this.worldObj.isRemote){
				API.network.joinNewNetwork(this.machine.node());
				API.network.joinNewNetwork(this.compinv.node());
				this.machine.node().connect(this.compinv.node());
			}
		}
	}
	
	/*--------------------*/
	
	/*-----Minecart/Entity-Stuff-------*/
	@Override
	public int getMinecartType() {
		return -1;
	}

	public static EntityMinecart create(World w, double x, double y, double z, Iterable<Pair<Integer, ItemStack>> components, int tier) {
		return new ComputerCart(w, x, y, z, components, tier);
	}
	
	public boolean interactFirst(EntityPlayer p){
		if(!this.worldObj.isRemote){
			p.openGui(OCMinecart.instance, 1, this.worldObj, this.getEntityId(), -10, 0);
		}
		return true;
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
	
	public boolean hasCustomInventoryName(){ return true; } // Drop Item on Kill when Player is in Creative Mode
	
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
			if(compinv.getStackInSlot(i)!=null)
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
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}
	/*-----------------------------*/
	
	/*-------Inventory--------*/
	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Environment getComponentInSlot(int index) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Node[] onAnalyze(EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return null;
	}

	public ComponetInventory getCompinv() {
		return this.compinv;
	}
	
}