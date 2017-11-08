package mods.ocminecart.common.inventory;

import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Container;
import li.cil.oc.api.internal.Keyboard;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.integration.opencomputers.DriverKeyboard$;
import li.cil.oc.integration.opencomputers.DriverScreen$;
import li.cil.oc.server.component.GraphicsCard;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.driver.CustomDriverRegistry;
import mods.ocminecart.utils.ItemStackUtil;
import mods.ocminecart.utils.NBTTypes;
import mods.ocminecart.utils.NbtUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import scala.tools.cmd.gen.AnyVals;

import javax.annotation.Nullable;
import java.util.*;


public class ComponentInventory implements IInventory, Environment {

	private final ItemStack slots[];
	private final MachineHost host;
	private final Map<Integer, Integer> containerSlots;

	private final List<ManagedEnvironment> updatingComponents = new LinkedList<>();
	private final ManagedEnvironment[] components;

	public ComponentInventory(MachineHost host, int size, int[] containerSlots) {
		this.host = host;
		this.slots = new ItemStack[size];
		this.components = new ManagedEnvironment[size];
		this.containerSlots = new HashMap<>();
		if(containerSlots.length % 2 != 0){
			throw new IllegalArgumentException("containerSlots requires a even number of entries");
		}
		for(int i=0; i<containerSlots.length; i+=2){
			this.containerSlots.put(containerSlots[i], containerSlots[i+1]);
		}
		clear();
	}

	@Override
	public Node node() {
		return this.host.machine() == null ? null : this.host.machine().node();
	}

	@Override
	public void onConnect(Node node) {

	}

	@Override
	public void onDisconnect(Node node) {

	}

	@Override
	public void onMessage(Message message) {

	}

	public void connectAllComponents() {
		for (int i = 0; i < this.slots.length; i++) {
			if (!ItemStackUtil.isStackEmpty(slots[i])) {
				connectItem(i);
			}
		}
	}

	public void disconnectAllComponents() {
		for (int i = 0; i < this.slots.length; i++) {
			if (this.components[i] != null) {
				this.components[i].node().remove();
			}
		}
	}

	public void saveAllComponents() {
		for (int i = 0; i < this.slots.length; i++) {
			if (this.components[i] != null) {
				this.save(this.components[i], CustomDriverRegistry.driverFor(this.slots[i]), this.slots[i]);
			}
		}
	}

	protected void connectItem(int slot) {
		ItemStack stack = slots[slot];
		if (stack != null && this.components[slot] == null) {//&& this.isComponentSlot(slot, stack)){
			Item drv = CustomDriverRegistry.driverFor(stack, host.getClass());
			if (drv != null) {
				ManagedEnvironment env = drv.createEnvironment(stack, host);
				if (env != null) {
					try {
						env.load(CustomDriverRegistry.dataTag(drv, stack));
					} catch (Throwable e) {
						OCMinecart.getLogger().warn("An item component of type" + env.getClass().getName() + " (provided by driver " + drv.getClass().getName() + ") threw an error while loading.", e);
					}
					this.components[slot] = env;
					this.connectNode(env.node());
					if (env.canUpdate() && !this.updatingComponents.contains(env)) {
						this.updatingComponents.add(env);
					}
					this.save(env, drv, stack);
				}
			}
		}
	}

	protected void connectNode(Node node) {
		if (this.node() != null && node != null) {
			this.node().connect(node);

			if (node.host() instanceof TextBuffer) {
				Arrays.asList(this.components).forEach((comp) -> {
					if (comp instanceof Keyboard) {
						node.connect(comp.node());
					} else if (comp instanceof GraphicsCard) {
						node.connect(comp.node());
					}
				});
			} else if (node.host() instanceof Keyboard) {
				Arrays.asList(this.components).forEach((comp) -> {
					if (comp instanceof TextBuffer) {
						node.connect(comp.node());
					}
				});
			}
		}
	}

	protected void disconnectItem(int slot) {
		if (this.components[slot] != null && !this.host.world().isRemote) {
			ManagedEnvironment component = this.components[slot];
			this.components[slot] = null;
			if (this.updatingComponents != null && this.updatingComponents.contains(component)) {
				this.updatingComponents.remove(component);
			}
			component.node().remove();
			this.save(component, CustomDriverRegistry.driverFor(this.slots[slot]), this.slots[slot]);
			component.node().remove();
		}
	}

	protected void addedItem(int slot){
		if(!this.host.world().isRemote && this.host.machine().node() != null) {
			connectItem(slot);
		}
	}

	protected void removedItem(int slot){
		if(!this.host.world().isRemote) {
			disconnectItem(slot);
		}
	}

	private void save(ManagedEnvironment component, Item driver, ItemStack stack) {
		NBTTagCompound tag = CustomDriverRegistry.dataTag(driver, stack);
		NbtUtil.clearTag(tag);
		component.save(tag);
	}

	public void updateComponents() {
		for(ManagedEnvironment env : updatingComponents){
			env.update();
		}
	}

	public Map<Integer, Integer> getContainerSlotConnections(){
		return Collections.unmodifiableMap(this.containerSlots);
	}

	public int getComponentSlot(String address){
		for (int i = 0; i < components.length; i++) {
			if(address.equals(components[i].node().address())){
				return i;
			}
		}
		return -1;
	}

	public ManagedEnvironment getComponent(int slot){
		if(slot < components.length){
			return components[slot];
		}
		return null;
	}

	public Container getContainer(int slot){
		if(!containerSlots.containsKey(slot)){
			return null;
		}
		ItemStack containerStack = getStackInSlot(this.containerSlots.get(slot));
		if(ItemStackUtil.isStackEmpty(containerStack)){
			return null;
		}
		Item driver = CustomDriverRegistry.driverFor(containerStack);
		return (driver instanceof Container) ? (Container) driver : null;
	}

	//===== NBT READ/WRITE ====//

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList nbtList = new NBTTagList();
		for (int i = 0; i < slots.length; i++) {
			ItemStack stack = this.slots[i];
			if(ItemStackUtil.isStackEmpty(stack)){
				continue;
			}
			if (this.components[i] != null) {
				this.save(this.components[i], CustomDriverRegistry.driverFor(stack), stack);
			}
			NBTTagCompound slotNBT = new NBTTagCompound();
			NBTTagCompound itemNBT = new NBTTagCompound();
			stack.writeToNBT(itemNBT);
			slotNBT.setInteger("slot", i);
			slotNBT.setTag("item", itemNBT);
			nbtList.appendTag(slotNBT);
		}
		nbt.setTag("slots", nbtList);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("slots", NBTTypes.TAG_LIST.getTypeID())) {
			NBTTagList nbtList = (NBTTagList) nbt.getTag("slots");
			for (int i = 0; i < nbtList.tagCount(); i++) {
				NBTTagCompound slotNBT = nbtList.getCompoundTagAt(i);
				if (!slotNBT.hasKey("item", NBTTypes.TAG_COMPOUND.getTypeID()) || !slotNBT.hasKey("slot", NBTTypes.INT.getTypeID())) {
					continue;
				}
				ItemStack stack = ItemStack.loadItemStackFromNBT(slotNBT.getCompoundTag("item"));
				int slot = slotNBT.getInteger("slot");
				if(slot < this.slots.length) {
					this.slots[slot] = stack;
				}
			}
		}
	}

	public Map<Integer, ItemStack> copyItemMap(){
		Map<Integer, ItemStack> map = new HashMap<>();
		for(int i=0;i<this.slots.length;i++){
			if(ItemStackUtil.isStackEmpty(this.slots[i])){
				continue;
			}
			ItemStack stack = this.slots[i].copy();
			ManagedEnvironment env = this.components[i];
			Item drv = CustomDriverRegistry.driverFor(stack);
			if(env!=null){
				if(drv.getClass().equals(DriverScreen$.class)){
					NbtUtil.clearTag(CustomDriverRegistry.dataTag(drv, stack));
				}
				else {
					this.save(env, drv, stack);
				}
			}
			map.put(i, stack);
		}
		return map;
	}

	public void loadItemMap(Map<Integer, ItemStack> map){
		for(Map.Entry<Integer, ItemStack> entry : map.entrySet()){
			ItemStack stack = entry.getValue();
			int slot = entry.getKey();
			if(slot >= this.slots.length || ItemStackUtil.isStackEmpty(stack)){
				continue;
			}
			this.slots[slot] = stack.copy();
		}
	}

	public List<ItemStack> internalComponents(){
		LinkedList<ItemStack> list = new LinkedList<>();
		for(int i = 0;i<slots.length ;i++){
			if(components[i]!=null){
				list.add(slots[i]);
			}
		}
		return list;
	}

	//===== INVENTORY ====//

	@Override
	public int getSizeInventory() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index < slots.length) {
			return slots[index];
		}
		return ItemStackUtil.getEmptyStack();
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (count > 0) {
			return removeStackFromSlot(index);
		}
		return ItemStackUtil.getEmptyStack();
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (index < slots.length) {
			ItemStack stack = slots[index];
			if (!ItemStackUtil.isStackEmpty(stack)) {
				removedItem(index);
			}
			slots[index] = ItemStackUtil.getEmptyStack();
			return stack;
		}
		return ItemStackUtil.getEmptyStack();
	}

	@Override
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		if (index < slots.length) {
			ItemStack currentStack = slots[index];
			if (!ItemStackUtil.isStackEmpty(currentStack)) {
				removedItem(index);
			}

			if (ItemStackUtil.isStackEmpty(stack)) {
				slots[index] = ItemStackUtil.getEmptyStack();
			} else {
				stack = stack.copy();
				if (stack.stackSize > 1) {
					stack.stackSize = 1;
				}
				slots[index] = stack;
				addedItem(index);
				connectItem(index);
			}
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(this.containerSlots.containsKey(index)){
			Container cDriver = getContainer(index);
			if(cDriver == null){
				return false;
			}
			ItemStack containerStack = getStackInSlot(this.containerSlots.get(index));
			Item drv = CustomDriverRegistry.driverFor(stack, host.getClass());
			if(drv == null || DriverKeyboard$.class.isAssignableFrom(drv.getClass()) || DriverScreen$.class.isAssignableFrom(drv.getClass())){
				return false;
			}

			return drv.slot(stack).equals(cDriver.providedSlot(containerStack)) || drv.slot(stack).equals(li.cil.oc.api.driver.item.Slot.Any) && drv.tier(stack) <= cDriver.providedTier(containerStack);
		}
		else {

			return true;
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.slots.length; i++) {
			if (!ItemStackUtil.isStackEmpty(slots[i])) {
				removedItem(i);
			}
			slots[i] = ItemStackUtil.getEmptyStack();
		}
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
}
