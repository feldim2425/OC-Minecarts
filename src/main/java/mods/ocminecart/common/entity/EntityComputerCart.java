package mods.ocminecart.common.entity;

import li.cil.oc.api.API;
import li.cil.oc.api.driver.item.Container;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.MachineHost;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Node;
import mods.ocminecart.ConfigSettings;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.inventory.ComponentInventory;
import mods.ocminecart.common.item.ItemComputerCart;
import mods.ocminecart.common.item.ModItems;
import mods.ocminecart.common.item.data.DataComputerCart;
import mods.ocminecart.network.ISyncObject;
import mods.ocminecart.network.ModNetwork;
import mods.ocminecart.network.messages.MessageNbtSyncRequest;
import mods.ocminecart.network.messages.MessageNbtSyncResponse;
import mods.ocminecart.utils.ItemStackUtil;
import mods.ocminecart.utils.NBTTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class EntityComputerCart extends EntityMinecart implements MachineHost, Analyzable, ISyncObject {

	public static final DataParameter<Boolean> PARAM_MACHINE_RUNNING = EntityDataManager.createKey(EntityComputerCart.class, DataSerializers.BOOLEAN);

	private ComponentInventory compInventory = new ComponentInventory(this, 24, new int[]{20, 0, 21, 1, 22, 2}){
		@Override
		protected void addedItem(int slot) {
			super.addedItem(slot);
			if(!EntityComputerCart.this.world().isRemote){
				Container cDriver = getContainer(slot);
				if(cDriver == null){
					return;
				}

				if(Slot.Floppy.equals(cDriver.providedSlot(getStackInSlot(slot)))){
					worldObj.playSound(null, posX, posY, posZ, new SoundEvent(new ResourceLocation("opencomputers", "floppy_insert")), SoundCategory.BLOCKS , ConfigSettings.oc_soundvol,1);
				}
			}
		}

		@Override
		protected void removedItem(int slot) {
			super.removedItem(slot);
			if(!EntityComputerCart.this.world().isRemote){
				Container cDriver = getContainer(slot);
				if(cDriver == null){
					return;
				}
				if(Slot.Floppy.equals(cDriver.providedSlot(getStackInSlot(slot)))){
					worldObj.playSound(null, posX, posY, posZ, new SoundEvent(new ResourceLocation("opencomputers", "floppy_eject")), SoundCategory.BLOCKS , ConfigSettings.oc_soundvol,1);
				}
			}
		}

		@Override
		public void markDirty() {
			super.markDirty();
			if(!EntityComputerCart.this.world().isRemote){
				ModNetwork.getWrapper().sendToServer(new MessageNbtSyncResponse(EntityComputerCart.this));
			}
		}
	};

	private Machine machine;
	private int tier;
	private boolean setup = false;
	private boolean isChangingDimension = false;

	public EntityComputerCart(World worldIn) {
		super(worldIn);
	}

	public EntityComputerCart(World worldIn, double x, double y, double z, DataComputerCart data) {
		super(worldIn, x, y, z);
		tier = data.getTier();

		((Connector) this.machine.node()).changeBuffer(data.getEnergy());
		compInventory.loadItemMap(data.getComponents());
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		if (!this.worldObj.isRemote) {
			machine = li.cil.oc.api.Machine.create(this);
			this.machine.setCostPerTick(ConfigSettings.cart_energy_usage);
			((Connector) this.machine.node()).setLocalBufferSize(ConfigSettings.cart_energy_buffer);
		}

		this.dataManager.register(PARAM_MACHINE_RUNNING, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(worldObj.isRemote){
			if(!setup){
				setup = true;
				ModNetwork.getWrapper().sendToServer(new MessageNbtSyncRequest(this));
			}
		}
		else {
			if(!setup) {
				setup = true;
				wireUp();
			}
			else {
				if(machine.isRunning()) {
					machine.update();
					compInventory.updateComponents();
				}
				if(this.tier > 2){
					((Connector) this.machine.node()).changeBuffer(999999);
				}
			}

			if(machine.isRunning() != dataManager.get(PARAM_MACHINE_RUNNING)){
				dataManager.set(PARAM_MACHINE_RUNNING, machine.isRunning());
			}
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		if (!worldObj.isRemote && !isChangingDimension) {
			machine.stop();
			machine.node().remove();
			compInventory.disconnectAllComponents();
			compInventory.saveAllComponents();
		}
	}

	@Nullable
	@Override
	public Entity changeDimension(int dimensionIn) {
		Entity newEntity;
		try {
			isChangingDimension = true;
			newEntity = super.changeDimension(dimension);
		}
		finally {
			isChangingDimension = false;
			setDead();
		}
		return newEntity;
	}

	private void wireUp(){
		API.network.joinNewNetwork(machine.node());
		compInventory.connectAllComponents();
	}

	public DataComputerCart getData(){
		DataComputerCart data = new DataComputerCart();
		data.setComponents(compInventory.copyItemMap());
		data.setTier(this.tier);
		data.setEnergy((float) ((Connector) this.machine.node()).localBuffer());
		return data;
	}

	@Override
	public ItemStack getCartItem() {
		return ItemStackUtil.getEmptyStack();
	}

	@Override
	public Type getType() {
		return Type.RIDEABLE;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if(this.worldObj.isRemote)
		{
			return;
		}

		if(compound.hasKey("tier", NBTTypes.INT.getTypeID())){
			tier = compound.getInteger("tier");
		}
		if(compound.hasKey("components", NBTTypes.TAG_COMPOUND.getTypeID())){
			compInventory.readFromNBT(compound.getCompoundTag("components"));
		}

		machine.onHostChanged();
		if(compound.hasKey("machine", NBTTypes.TAG_COMPOUND.getTypeID())){
			machine.load(compound.getCompoundTag("machine"));
		}

		wireUp();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if(this.worldObj.isRemote)
		{
			return;
		}

		compound.setInteger("tier", tier);

		NBTTagCompound nbtCompInv = new NBTTagCompound();
		compInventory.saveAllComponents();
		compInventory.writeToNBT(nbtCompInv);
		compound.setTag("components", nbtCompInv);

        NBTTagCompound nbtMachine = new NBTTagCompound();
        machine.save(nbtMachine);
        compound.setTag("machine", nbtMachine);
	}

	@Override
	public void writeSyncData(NBTTagCompound data) {
		NBTTagCompound nbtCompInv = new NBTTagCompound();
		compInventory.saveAllComponents();
		compInventory.writeToNBT(nbtCompInv);
		data.setTag("components", nbtCompInv);
	}

	@Override
	public void readSyncData(NBTTagCompound data) {
		if(data.hasKey("components", NBTTypes.TAG_COMPOUND.getTypeID())){
			compInventory.readFromNBT(data.getCompoundTag("components"));
			compInventory.connectAllComponents();
		}
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand) {
		if(!this.worldObj.isRemote && !player.isSneaking() && machine.canInteract(player.getName())){
			player.openGui(OCMinecart.getInstance(), 0 ,this.worldObj, this.getEntityId(), 0, 0);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	public ComponentInventory getComponentInventory(){
		return compInventory;
	}

	@Override
	public Machine machine() {
		return this.machine;
	}

	@Override
	public Iterable<ItemStack> internalComponents() {
		return compInventory.internalComponents();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public void killMinecart(DamageSource dms){
		this.setDead();

		if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
		{
			List<ItemStack> drop = new ArrayList<>();

			for(int cslot : compInventory.getContainerSlotConnections().keySet()){
				ItemStack compStack = compInventory.removeStackFromSlot(cslot);
				if(!ItemStackUtil.isStackEmpty(compStack)){
					drop.add(compStack);
				}
			}

			ItemStack cartItemStack = new ItemStack(ModItems.Items.COMPUTER_CART.get(), 1);
			((ItemComputerCart) cartItemStack.getItem()).setData(cartItemStack, getData());
			drop.add(cartItemStack);

			ItemStackUtil.dropItemList(drop, this.worldObj, this.posX, this.posY, this.posZ,true);
		}
	}

	@Override
	public int componentSlot(String address) {
		return compInventory.getComponentSlot(address);
	}


	@Override
	public void onMachineConnect(Node node) {

	}

	@Override
	public void onMachineDisconnect(Node node) {

	}

	@Override
	public Node[] onAnalyze(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		return new Node[]{this.machine().node()};
	}

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

	}

	public boolean isRunning() {
		return dataManager.get(PARAM_MACHINE_RUNNING);
	}

	public int getLightColor() {
		return 0xffffff;
	}
}
