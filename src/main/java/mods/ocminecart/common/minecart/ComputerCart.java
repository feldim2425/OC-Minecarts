package mods.ocminecart.common.minecart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import li.cil.oc.api.API;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Container;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.internal.MultiTank;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.Tier;
import li.cil.oc.common.block.Case;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.ISyncEntity;
import mods.ocminecart.common.inventory.ComponetInventory;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.message.ComputercartInventory;
import mods.ocminecart.network.message.EntitySyncRequest;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Locale;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

//FIXME: write and read Host to NBT to connect inventory-node
public class ComputerCart extends AdvCart implements MachineHost, Analyzable, Robot, ISyncEntity{ //the Robot interface is necessary for install Crafting Upgrades
	
	Machine machine;
	
	private int color = 0xFF00FF;
	private int tier=Tier.None();
	
	private boolean firstupdate = true;
	
	//TODO: Resize inventory and add main inventory 
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
	
	public ComputerCart(World world, double x, double y,double z, Iterable<Pair<Integer,ItemStack>>  components, int tier){
		super(world,x,y,z);	
		this.tier=tier;
		
		Iterator<Pair<Integer,ItemStack>> complist = components.iterator();
		while(complist.hasNext()){
			Pair<Integer,ItemStack> p = complist.next();
			compinv.setInventorySlotContents(p.getLeft(), p.getRight());
		}
	}
	
	public ComputerCart(World world) {
		super(world);
	}
	
	
	protected void entityInit(){
		super.entityInit();
		this.machine = li.cil.oc.api.Machine.create(this);
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
	
	 public ItemStack getPickedResult(MovingObjectPosition target){ return null; } 
	
	public static ComputerCart create(World world, double x, double y,double z, Iterable<Pair<Integer,ItemStack>> components, int tier){
		return new ComputerCart(world,x,y,z,components,tier);
	}
	
	public boolean hasCustomInventoryName(){ return true; } // Drop Item on Kill when Player is in Creative Mode
	
	@Override
	public World world() {return this.worldObj;}

	@Override
	public double xPosition() {return this.posX;}

	@Override
	public double yPosition() {return this.posY;}

	@Override
	public double zPosition() {return this.posZ;}
	
	@Override
	public void markChanged() {}

	@Override
	public Machine machine() { return this.machine;}
	
	public double[] getRGBColor(){
		double[] rgb = new double[3];
		rgb[0]=((color&0xFF0000)>>16)/255;
		rgb[1]=((color&0x00FF00)>>8)/255;
		rgb[2]=(color&0x0000FF)/255;
		return rgb;
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
		
		return 0;
	}

	@Override
	public void onMachineConnect(Node node) {
		
		
	}

	@Override
	public void onMachineDisconnect(Node node) {
		
		
	}
	
	//-----Container----//
	
	public String containerSlotType(int slot){
		if(slot<3 && this.compinv.getStackInSlot(slot)!=null){
			Item drv = Driver.driverFor(this.compinv.getStackInSlot(slot), this.getClass());
			if(drv!=null && (drv instanceof Container)){
				return ((Container)drv).providedSlot(this.compinv.getStackInSlot(slot));
			}
		}
		return Slot.None;
	}
	
	public int containerSlotTier(int slot){
		if(slot<3 && this.compinv.getStackInSlot(slot)!=null){
			Item drv = Driver.driverFor(this.compinv.getStackInSlot(slot), this.getClass());
			if(drv!=null && (drv instanceof Container)){
				return ((Container)drv).providedTier(this.compinv.getStackInSlot(slot));
			}
		}
		return Tier.None();
	}
	
	//----END-Container---//
	
	@Override
	public Node[] onAnalyze(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return new Node[]{null};
	}
	
	public int getTier(){
		return this.tier;
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		NBTTagCompound tag = nbt.getCompoundTag("ComputerCart");
		ArrayList<ItemStack>componentarray=new ArrayList<ItemStack>();
		
		if(tag!=null){
			this.tier = tag.getInteger("tier");
			
			NBTTagList compinv = (NBTTagList) tag.getTag("componentinv");
			if(compinv!=null){
				this.compinv.readNBT(compinv);
			}
			NBTTagCompound mtag = (NBTTagCompound) tag.getTag("machine");
			if(mtag!=null){
				this.machine.load(mtag);
			}
			NBTTagCompound invtag = (NBTTagCompound) tag.getTag("componentnode");
			if(invtag!=null){
				this.compinv.node().load(invtag);
			}
			
		}
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		this.compinv.saveComponents();
		
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound mtag = new NBTTagCompound();
		NBTTagCompound invtag = new NBTTagCompound();
		NBTTagList compinv = this.compinv.writeNTB();
		
		tag.setInteger("tier", this.tier);
		tag.setTag("componentinv", compinv);
		this.compinv.node().save(invtag);
		tag.setTag("componentnode", invtag);
		this.machine.save(mtag);
		tag.setTag("machine", mtag);
		
		nbt.setTag("ComputerCart", tag);
	}
	
	@Override
	public void writeSyncData(NBTTagCompound nbt) {
		this.compinv.saveComponents();
		NBTTagList compinv = this.compinv.writeNTB();
		nbt.setTag("componentinv", compinv);
		
		System.out.println(this.machine.node().address());
	}

	@Override
	public void readSyncData(NBTTagCompound nbt) {
		NBTTagList compinv = (NBTTagList) nbt.getTag("componentinv");
		if(compinv!=null){
			this.compinv.readNBT(compinv);
		}
		
		this.compinv.connectComponents();
	}
	
	public void onUpdate(){
		super.onUpdate();
		if(this.firstupdate){
			this.firstupdate=false;
			if(this.worldObj.isRemote){
				ModNetwork.channel.sendToServer(new EntitySyncRequest(this.worldObj.provider.dimensionId, this.getEntityId()));
			}
			
			API.network.joinNewNetwork(this.node());
			if(!this.worldObj.isRemote) this.node().connect(this.compinv.node());
		}
	}
	
	public boolean interactFirst(EntityPlayer p){
		p.openGui(OCMinecart.instance, 1, this.worldObj, this.getEntityId(), -10, 0);
		return true;
	}
	
	
	@Override
	public IInventory equipmentInventory() {
		return this.compinv;
	}

	@Override
	public IInventory mainInventory() {
		return null;
	}

	@Override
	public MultiTank tank() {
		return null;
	}

	@Override
	public int selectedSlot() {
		return 0;
	}

	@Override
	public void setSelectedSlot(int index) {
		
	}

	@Override
	public int selectedTank() {
		return 0;
	}

	@Override
	public void setSelectedTank(int index) {
	}

	@Override
	public EntityPlayer player() {
		
		return null;
	}

	@Override
	public String name() {
		
		return null;
	}

	@Override
	public void setName(String name) {
		
		
	}

	@Override
	public String ownerName() {
		
		return null;
	}

	@Override
	public UUID ownerUUID() {
		
		return null;
	}

	@Override
	public ForgeDirection facing() {
		return null;
	}

	@Override
	public ForgeDirection toGlobal(ForgeDirection value) {
		
		return null;
	}

	@Override
	public ForgeDirection toLocal(ForgeDirection value) {
		
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
		return this.tier;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		
		return null;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		
		return false;
	}

	@Override
	public int getSizeInventory() {
		
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		
		
	}

	@Override
	public String getInventoryName() {
		
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		
		return 0;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(this.machine.users().length<1) return true;
		for(int i=0;i<this.machine.users().length;i+=1){
			if(this.machine.users()[i] == player.getCommandSenderName()) return true;
		}
		return false;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) { return null; }

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) { return false; }

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) { return false; }

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return null;
	}

	@Override
	public int componentCount() {
		int size = 0;
		Iterator<ManagedEnvironment> list = this.compinv.getComponents().iterator();
		while(list.hasNext()){
			if(list.next()!=null) size+=1;
		}
		return size;
	}

	@Override
	public Environment getComponentInSlot(int index) {
		return this.compinv.getSlotComponent(index);
	}

	@Override
	public void synchronizeSlot(int slot) {
		if(this.compinv.getStackInSlot(slot) != null && this.compinv.getSlotComponent(slot) != null)
			this.compinv.save(this.compinv.getSlotComponent(slot),Driver.driverFor(this.getStackInSlot(slot), this.getClass()), this.getStackInSlot(slot));
		ModNetwork.sendToNearPlayers(new ComputercartInventory(this,slot,this.compinv.getStackInSlot(slot)), this.posX, this.posY, this.posZ, this.worldObj);
	}

	@Override
	public boolean shouldAnimate() {
		return false;
	}
}
