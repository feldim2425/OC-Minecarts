package mods.ocminecart.common.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import li.cil.oc.api.API;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Container;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.Tier;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.common.FMLCommonHandler;

// This Class is just a Java rewrite of li.cil.oc.common.inventory.ComponentInventory and Inventory (Credits to Sangar)
public abstract class ComponetInventory implements IInventory, Environment{
	
	protected MachineHost host;
	private ItemStack[] slots;
	
	private ArrayList<ManagedEnvironment> updatingCompoents = new ArrayList<ManagedEnvironment>();
	private ManagedEnvironment[] components = new ManagedEnvironment[this.getSizeInventory()];
	
	public ComponetInventory(MachineHost host){
		this.host = host;
		this.slots = new ItemStack[this.getSizeInventory()];
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot<this.getSizeInventory()) return this.slots[slot];
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int number) {
		if(slot>=0 && slot<this.getSizeInventory()){
			if(number >= slots[slot].stackSize){
				ItemStack get = slots[slot];
				slots[slot]=null;
				this.onItemRemoved(slot, get);
				return get;
			}
			else{
				ItemStack ret = slots[slot].splitStack(number);
				if(slots[slot].stackSize<1){
					slots[slot] = null;
					this.onItemRemoved(slot, ret);
				}
				return ret;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot>=0 && slot<this.getSizeInventory()){
			if(stack==null && slots[slot]==null) return;
			if(slots[slot]!=null && stack !=null && slots[slot]==stack) return;
			
			ItemStack oldStack = slots[slot];
			this.updateSlot(slot, null);
			if(oldStack!=null) this.onItemRemoved(slot, oldStack);
			
			if(stack!=null && stack.stackSize>=1){
				if(stack.stackSize>=this.getInventoryStackLimit()){
					stack.stackSize=this.getInventoryStackLimit();
				}
				this.updateSlot(slot, stack);
			}
			
			if(slots[slot]!=null){
				this.onItemAdded(slot, stack);
			}
			
			this.markDirty();
		}
	}

	@Override
	public String getInventoryName() {
		return "Component Inventory";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	
	public void updateSlot(int slot, ItemStack stack){
		slots[slot] = stack;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		Item driver = Driver.driverFor(stack,host.getClass());
		if(driver!=null && (driver.slot(stack)==this.getSlotType(slot) || this.getSlotType(slot) == Slot.Any) && driver.tier(stack) <= this.getSlotTier(slot))
			return true;
		return false;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	public String getSlotType(int slot){
		return Slot.Any;
	}
	
	public int getSlotTier(int slot){
		return Tier.Any();
	}
	
	public Node node(){
		return this.host.machine().node();
	}
	
	public void updateComponents() {
		if(!this.updatingCompoents.isEmpty()){
			Iterator<ManagedEnvironment> list = this.updatingCompoents.iterator();
			while(list.hasNext()){
				list.next().update();
			}
		}
	}
	
	public ManagedEnvironment getSlotComponent(int slot) {
		if(slot < this.getSizeInventory()) return this.components[slot];
		return null;
	}
	
	public void connectComponents(){
		for(int slot=0;slot<this.getSizeInventory();slot+=1){
			ItemStack stack = this.getStackInSlot(slot);
			if(stack!=null && this.components[slot]==null && this.isComponentSlot(slot, stack)){
				Item drv = Driver.driverFor(stack,host.getClass());
				if(drv!=null){
					ManagedEnvironment env = drv.createEnvironment(stack, host);
					if(env!=null){
						try{
							env.load(dataTag(drv,stack));
						}
						catch(Throwable e){
							OCMinecart.logger.warn("An item component of type"+env.getClass().getName()+" (provided by driver "+drv.getClass().getName()+") threw an error while loading.",e);
						}
						this.components[slot] = env;
						if(env.canUpdate() && !this.updatingCompoents.contains(env)){
							this.updatingCompoents.add(env);
						}				
					}
				}
			}
		}
		
		API.network.joinNewNetwork(this.node());
		for(int i=0;i<this.components.length;i+=1){
			if(this.components[i]!=null && this.components[i].node()!=null){
				this.connectItemNode(this.components[i].node());
			}
		}
	}
	
	public void disconnectComponents(){
		for(int i=0;i<this.components.length;i+=1){
			if(this.components[i]!=null) this.components[i].node().remove();
		}
	}
	
	public void connectItemNode(Node node){
		if(this.node()!=null && node!=null && this.node().network()!=null){
			this.node().connect(node);
		}
	}
	
	synchronized protected void onItemAdded(int slot, ItemStack stack){
		Item drv = Driver.driverFor(stack,host.getClass());
		if(drv!=null){
			ManagedEnvironment env = drv.createEnvironment(stack, host);
			if(env!=null){
				try{
					env.load(this.dataTag(drv, stack));
				}
				catch(Throwable e){
					OCMinecart.logger.warn("An item component of type"+env.getClass().getName()+" (provided by driver "+drv.getClass().getName()+") threw an error while loading.",e);
				}
				
				this.components[slot] = env;
				this.connectItemNode(env.node());
				if(env.canUpdate() && !this.updatingCompoents.contains(env)){
					this.updatingCompoents.add(env);
				}
				this.save(env, drv, stack);
			}
		}
	}
	
	synchronized protected void onItemRemoved(int slot, ItemStack stack){
		if(this.components[slot]!=null && FMLCommonHandler.instance().getEffectiveSide().isServer()){
			ManagedEnvironment component = this.components[slot];
			this.components[slot]=null;
			if(this.updatingCompoents!=null && this.updatingCompoents.contains(component)) this.updatingCompoents.remove(component);
			component.node().remove();
			this.save(component, Driver.driverFor(stack), stack);
			component.node().remove();
		}
	}
	
	private NBTTagCompound dataTag(Item driver, ItemStack stack){
		NBTTagCompound tag = null;
		if(driver!=null) driver.dataTag(stack);
		if(tag==null){
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound nbt = stack.getTagCompound();
			if (!nbt.hasKey(Settings.OC_Namespace + "data")) {
				nbt.setTag(Settings.OC_Namespace + "data", new NBTTagCompound());
			}
			tag = nbt.getCompoundTag(Settings.OC_Namespace + "data");
		}
		return tag;
	}
	
	public boolean isComponentSlot(int slot, ItemStack stack){return true;}
	
	public void save(ManagedEnvironment component, Item driver, ItemStack stack){
		try{
			NBTTagCompound tag = this.dataTag(driver, stack);
			
			Set<String> tags = tag.func_150296_c();
			String[] list = tags.toArray(new String[tags.size()]);
			for(int i=0; i<list.length; i+=1){
				tag.removeTag(list[i]);
			}
			component.save(tag);
		}
		catch(Throwable e){
			OCMinecart.logger.warn("An item component of type "+component.getClass().getName()+" (provided by driver "+driver.getClass().getName()+") threw an error while loading.",e);
		}
	}
	
	public NBTTagList writeNTB(){
		NBTTagList nbt  = new NBTTagList();
		for(byte slot=0;slot < this.getSizeInventory(); slot+=1){
			NBTTagCompound invslot = new NBTTagCompound();
			NBTTagCompound item = new NBTTagCompound();
			invslot.setByte("slot", slot);
			if(this.getStackInSlot(slot)!=null){
				this.getStackInSlot(slot).writeToNBT(item);
				invslot.setTag("item", item);
			}
			nbt.appendTag(invslot);
		}
		return nbt;
	}
	
	public void readNBT(NBTTagList nbt){
		for(int i=0;i < nbt.tagCount(); i+=1){
			NBTTagCompound invslot = nbt.getCompoundTagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(invslot.getCompoundTag("item"));
			byte slot = invslot.getByte("slot");
			if(slot>=0 && slot<this.getSizeInventory()) this.updateSlot(slot, stack);
		}
	}
	
	public Iterable<ManagedEnvironment> getComponents(){
		ArrayList<ManagedEnvironment> list = new ArrayList<ManagedEnvironment>();
		for(int i=0;i<this.getSizeInventory();i+=1){
			if(this.components[i]!=null){
				list.add(this.components[i]);
			}
		}
		return list;
	}
	
	public void saveComponents() {
	    for (int slot=0; slot< this.getSizeInventory(); slot+=1) {
	    	ItemStack stack = this.getStackInSlot(slot);
	    	if (stack != null && this.components[slot]!=null) {
	    		this.save(this.components[slot], Driver.driverFor(stack,host.getClass()), stack);
	    	}
	     }
	}
	
	public ItemStack getContainer(int index){
		if(index >= 0 && index <= 2 && this.getStackInSlot(index)!=null){
			Item it = Driver.driverFor(this.getStackInSlot(index));
			if(it instanceof Container) return this.getStackInSlot(index);
		}
		return null;
	}
	
	@Override
	public void onConnect(Node node) {}

	@Override
	public void onDisconnect(Node node) {}

	@Override
	public void onMessage(Message message) {}
}
