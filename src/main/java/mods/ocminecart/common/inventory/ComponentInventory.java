package mods.ocminecart.common.inventory;

import li.cil.oc.api.driver.Item;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.common.component.TextBuffer;
import li.cil.oc.integration.opencomputers.DriverScreen;
import li.cil.oc.server.component.GraphicsCard;
import li.cil.oc.server.component.Keyboard;
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

import javax.annotation.Nullable;
import java.util.*;


public class ComponentInventory implements IInventory, Environment {

	private final ItemStack slots[];
	private final MachineHost host;

	private final List<ManagedEnvironment> updatingComponents = new LinkedList<>();
	private final ManagedEnvironment[] components;

	public ComponentInventory(MachineHost host, int size) {
		this.host = host;
		this.slots = new ItemStack[size];
		this.components = new ManagedEnvironment[size];
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

	private void connectItem(int slot) {
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

	private void connectNode(Node node) {
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

	private void disconnectItem(int slot) {
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
				if(drv.getClass().equals(DriverScreen.class)){
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
				disconnectItem(index);
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
				disconnectItem(index);
			}

			if (ItemStackUtil.isStackEmpty(stack)) {
				slots[index] = ItemStackUtil.getEmptyStack();
			} else {
				stack = stack.copy();
				if (stack.stackSize > 1) {
					stack.stackSize = 1;
				}
				slots[index] = stack;
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
		return false;
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
				disconnectItem(i);
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
