package mods.ocminecart.common.components;

import li.cil.oc.api.API;
import li.cil.oc.api.Persistable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.utils.ItemStackUtil;
import mods.ocminecart.utils.NBTTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;


public class ComponentCCartController implements Environment, Persistable {

	private final EntityComputerCart cart;
	private final Node node;

	public ComponentCCartController(EntityComputerCart cart) {
		this.cart = cart;
		this.node = API.network.newNode(this, Visibility.Network).withComponent("computercart").create();
	}

	@Override
	public void load(NBTTagCompound nbt) {

		if(nbt.hasKey("node", NBTTypes.TAG_COMPOUND.getTypeID())) {
			node.load(nbt.getCompoundTag("node"));
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {

		NBTTagCompound nodeNBT = new NBTTagCompound();
		node.save(nodeNBT);
		nbt.setTag("node", nodeNBT);
	}

	@Override
	public Node node() {
		return this.node;
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

	public EntityComputerCart getComputerCart() {
		return cart;
	}

	@Callback(doc="function([color:number]):number -- Set or Get the light color")
	public Object[] lightColor(Context context, Arguments arguments){
		if(arguments.count() < 1) {
			int color = arguments.checkInteger(1);
			this.cart.setLightColor(color);
			return new Object[]{color};
		}
		else{
			return new Object[]{this.cart.getLightColor()};
		}
	}

	/*--------Component-Functions-Inventory--------*/

	@Callback(doc = "function():number -- The size of this device's internal inventory.")
	public Object[] inventorySize(Context context, Arguments arguments){
		return new Object[]{this.cart.getMainInventory().getSizeInventory()};
	}

	@Callback(doc = "function([slot:number]):number -- Get the currently selected slot; set the selected slot if specified.")
	public Object[] select(Context context, Arguments args){
		if(args.count() > 0) {
			int slot = args.optInteger(0, 0);
			if (this.cart.getMainInventory().checkSlot(slot)) {
				this.cart.getMainInventory().setSelectedSlot(slot - 1);
			}
			else {
				throw new IllegalArgumentException("invalid slot");
			}
		}
		return new Object[]{this.cart.getMainInventory().getSelectedSlot()+1};
	}

	@Callback(direct = true, doc = "function([slot:number]):number -- Get the number of items in the specified slot, otherwise in the selected slot.")
	public Object[] count(Context context, Arguments args){
		int slot = args.optInteger(0, this.cart.getMainInventory().getSelectedSlot() + 1) - 1;
		if(this.cart.getMainInventory().checkSlot(slot)){
			ItemStack stack = this.cart.getMainInventory().getStackInSlot(slot);
			if(ItemStackUtil.isStackEmpty(stack)) {
				return new Object[]{0};
			}
			else {
				return new Object[]{stack.stackSize};
			}
		}
		else{
			if(args.count() < 1) {
				return new Object[]{0, "no slot selected"};
			}
			throw new IllegalArgumentException("invalid slot");
		}
	}

	@Callback(direct = true, doc = "function([slot:number]):number -- Get the remaining space in the specified slot, otherwise in the selected slot.")
	public Object[] space(Context context, Arguments args){
		int slot = args.optInteger(0, this.cart.getMainInventory().getSelectedSlot() + 1) - 1;
		if(this.cart.getMainInventory().checkSlot(slot)){
			ItemStack stack = this.cart.getMainInventory().getStackInSlot(slot);
			if(ItemStackUtil.isStackEmpty(stack)) {
				return new Object[]{this.cart.getMainInventory().getInventoryStackLimit()};
			}
			else {
				return new Object[]{Math.min(this.cart.getMainInventory().getInventoryStackLimit(), stack.getMaxStackSize()) - stack.stackSize};
			}
		}
		else{
			if(args.count() < 1) {
				return new Object[]{0, "no slot selected"};
			}
			throw new IllegalArgumentException("invalid slot");
		}
	}

	@Callback(doc = "function(otherSlot:number):boolean -- Compare the contents of the selected slot to the contents of the specified slot.")
	public Object[] compareTo(Context context, Arguments args){
		int slotA = args.checkInteger(0) - 1;
		int slotB = this.cart.getMainInventory().getSelectedSlot();
		boolean result;
		if(this.cart.getMainInventory().checkSlot(slotB)) {
			return new Object[]{false, "no slot selected"};
		}
		if(this.cart.getMainInventory().checkSlot(slotA)){
			ItemStack stackA = this.cart.getMainInventory().getStackInSlot(slotA);
			ItemStack stackB = this.cart.getMainInventory().getStackInSlot(slotB);
			result = ItemStack.areItemsEqual(stackA, stackB);
			return new Object[]{result};
		}
		throw new IllegalArgumentException("invalid slot");
	}

	@Callback(doc = "function(toSlot:number[, amount:number]):boolean -- Move up to the specified amount of items from the selected slot into the specified slot.")
	public Object[] transferTo(Context context, Arguments args){
		int slotA = this.cart.getMainInventory().getSelectedSlot();
		int slotB = args.checkInteger(0) - 1;
		int number = args.optInteger(1, this.cart.getMainInventory().getInventoryStackLimit());
		if(!this.cart.getMainInventory().checkSlot(slotB)) {
			throw new IllegalArgumentException("invalid slot");
		}
		if(!this.cart.getMainInventory().checkSlot(slotA)) {
			return new Object[]{false, "no slot selected"};
		}

		ItemStack stackA = this.cart.getMainInventory().getStackInSlot(slotA);
		ItemStack stackB = this.cart.getMainInventory().getStackInSlot(slotB);
		if(ItemStackUtil.isStackEmpty(stackA) || !(ItemStack.areItemsEqual(stackA, stackB) || ItemStackUtil.isStackEmpty(stackB))) {
			return new Object[]{false};
		}

		int maxStack = Math.min(this.cart.getMainInventory().getInventoryStackLimit(), stackA.getMaxStackSize());
		int items = maxStack - ((ItemStackUtil.isStackEmpty(stackB)) ? 0 : stackB.stackSize);

		ItemStack dif = this.cart.getMainInventory().decrStackSize(slotA, Math.min(items, number));
		if(ItemStackUtil.isStackEmpty(dif)){
			return new Object[]{false};
		}
		else if(!ItemStackUtil.isStackEmpty(stackB)){
			stackB = stackB.copy();
			stackB.stackSize+=dif.stackSize;
		}
		else{
			this.cart.getMainInventory().setInventorySlotContents(slotB, dif);
		}
		return new Object[]{true};
	}

	/*--------Component-Functions-Tank--------*/

	@Callback(doc = "function():number -- The number of tanks installed in the device.")
	public Object[] tankCount(Context context, Arguments args){
		return new Object[]{ this.cart.getMutliTank().tankCount() };
	}

	@Callback(doc = "function([index:number]):number -- Select a tank and/or get the number of the currently selected tank.")
	public Object[] selectTank(Context context, Arguments args){
		if(args.count() > 0) {
			int index = args.checkInteger(0);
			if (this.cart.getMutliTank().checkIndex(index)) {
				this.cart.getMutliTank().setSelectedTank(index);
			}
			else {
				throw new IllegalArgumentException("invalid tank index");
			}
		}
		return new Object[]{this.cart.getMutliTank().getSelectedTank()};
	}

	@Callback(direct = true, doc = "function([index:number]):number -- Get the fluid amount in the specified or selected tank.")
	public Object[] tankLevel(Context context, Arguments args){
		int index = args.optInteger(0, this.cart.getMutliTank().getSelectedTank() + 1) - 1;
		if(!this.cart.getMutliTank().checkIndex(index)){
			if(args.count()<1) {
				return new Object[]{false, "no tank selected"};
			}
			throw new IllegalArgumentException("invalid tank index");
		}
		return new Object[]{ this.cart.getMutliTank().getFluidTank(index).getFluidAmount() };
	}

	@Callback(direct = true, doc = "function([index:number]):number -- Get the remaining fluid capacity in the specified or selected tank.")
	public Object[] tankSpace(Context context, Arguments args){
		int index = args.optInteger(0, this.cart.getMutliTank().getSelectedTank() + 1) - 1;
		if(!this.cart.getMutliTank().checkIndex(index)){
			if(args.count()<1) {
				return new Object[]{false, "no tank selected"};
			}
			throw new IllegalArgumentException("invalid tank index");
		}
		IFluidTank tank = this.cart.getMutliTank().getFluidTank(index);
		return new Object[]{ tank.getCapacity() - tank.getFluidAmount() };
	}

	@Callback(doc = "function(index:number):boolean -- Compares the fluids in the selected and the specified tank. Returns true if equal.")
	public Object[] compareFluidTo(Context context, Arguments args){
		int tankA = args.checkInteger(0) - 1;
		int tankB = this.cart.getMutliTank().getSelectedTank();
		if(!this.cart.getMutliTank().checkIndex(tankA)) {
			throw new IllegalArgumentException("invalid tank index");
		}
		if(!this.cart.getMutliTank().checkIndex(tankB)) {
			return new Object[]{false, "no tank selected"};
		}

		FluidStack stackA = this.cart.getMutliTank().getFluidTank(tankA).getFluid();
		FluidStack stackB = this.cart.getMutliTank().getFluidTank(tankB).getFluid();
		boolean res = (stackA==null && stackB==null);
		if(!res && stackA!=null && stackB!=null) {
			res = stackA.isFluidEqual(stackB);
		}
		return new Object[]{ res };
	}

	@Callback(doc = "function(index:number[, count:number=1000]):boolean -- Move the specified amount of fluid from the selected tank into the specified tank.")
	public Object[] transferFluidTo(Context context, Arguments args){
		int tankA = args.checkInteger(0) - 1;
		int tankB = this.cart.getMutliTank().getSelectedTank();
		int count = args.optInteger(1, 1000);
		if(!this.cart.getMutliTank().checkIndex(tankA)) {
			throw new IllegalArgumentException("invalid tank index");
		}
		if(!this.cart.getMutliTank().checkIndex(tankB)) {
			return new Object[]{false, "no tank selected"};
		}

		IFluidTank tankT = this.cart.getMutliTank().getFluidTank(tankA);
		IFluidTank tankS = this.cart.getMutliTank().getFluidTank(tankB);

		if(tankS == null || tankS.getFluid()==null || tankT == null || tankS.getFluid().isFluidEqual(tankT.getFluid())) {
			return new Object[]{false};
		}

		FluidStack sim = tankS.drain(count, false);	//Simulate the transfer to get the max. moveable amount.
		int move = tankT.fill(sim, false);
		if(move<=0){
			return new Object[]{ false };
		}
		FluidStack mv = tankS.drain(move, true);
		tankT.fill(mv, true);
		return new Object[]{ true };
	}

	//--------World-Inventory----------//

	@Callback(doc = "function(side:number[, count:number=64]):boolean -- Drops items from the selected slot towards the specified side.")
	public Object[] drop(Context context, Arguments args){
		int side = args.checkInteger(0);
		int amount = args.optInteger(1,64);
		if(side<0 || side > 5) throw new IllegalArgumentException("invalid side");
		int sslot = this.cart.selectedSlot();
		if(!(sslot>=0 && sslot<this.cart.mainInventory().getSizeInventory()))
			return new Object[]{ false , "no slot selected"};
		if(amount<1) return new Object[]{ false };

		ForgeDirection dir = this.cart.toGlobal(ForgeDirection.getOrientation(side));
		int x = (int)Math.floor(this.cart.xPosition())+dir.offsetX;
		int y = (int)Math.floor(this.cart.yPosition())+dir.offsetY;
		int z = (int)Math.floor(this.cart.zPosition())+dir.offsetZ;

		ItemStack dstack = this.cart.mainInventory().getStackInSlot(sslot);
		if(dstack == null) return new Object[]{ false };

		if(!(this.cart.world().getTileEntity(x, y, z) instanceof IInventory)){
			ArrayList<ItemStack> drop = new ArrayList<ItemStack>();

			int mov = Math.min(dstack.stackSize, amount);
			ItemStack dif = dstack.splitStack(mov);
			if(dstack.stackSize < 1)
				this.cart.mainInventory().setInventorySlotContents(sslot, null);
			drop.add(dif);
			ItemUtil.dropItemList(drop, this.cart.world(), x+0.5D, y+0.5D, z+0.5D, false);
			context.pause(Settings.OC_DropDelay);
			return new Object[]{ true };
		}
		else{
			int moved = InventoryUtil.dropItemInventoryWorld(dstack.copy(),  this.cart.world(), x, y, z, dir.getOpposite(), amount);
			if(moved < 1) return new Object[]{ false, "inventory full" };
			if(dstack.stackSize > moved) dstack.stackSize-=moved;
			else this.cart.mainInventory().setInventorySlotContents(sslot, null);
			context.pause(Settings.OC_DropDelay);
			return new Object[]{ true };
		}
	}

	@Callback(doc = "function(side:number[, count:number=64]):boolean -- Suck up items from the specified side.")
	public Object[] suck(Context context, Arguments args){
		int side = args.checkInteger(0);
		int amount = args.optInteger(1,64);
		if(side<0 || side > 5) throw new IllegalArgumentException("invalid side");
		int sslot = this.cart.selectedSlot();
		if(!(sslot>=0 && sslot<this.cart.mainInventory().getSizeInventory()))
			return new Object[]{ false ,"no slot selected"};
		if(amount<1) return new Object[]{ false };

		ForgeDirection dir = this.cart.toGlobal(ForgeDirection.getOrientation(side));
		int x = (int)Math.floor(this.cart.xPosition())+dir.offsetX;
		int y = (int)Math.floor(this.cart.yPosition())+dir.offsetY;
		int z = (int)Math.floor(this.cart.zPosition())+dir.offsetZ;

		if(!(this.cart.world().getTileEntity(x, y, z) instanceof IInventory)){
			int moved = 0;
			int[] acc = InventoryUtil.getAccessible(this.cart.mainInventory(), ForgeDirection.UNKNOWN);
			acc = InventoryUtil.prioritizeAccessible(acc, this.cart.selectedSlot());
			if(!ItemUtil.hasDroppedItems(this.cart.world(), x, y, z))
				return new Object[]{ false };
			for(int i=0;i<acc.length && moved<1;i+=1){
				ItemStack filter = this.cart.mainInventory().getStackInSlot(acc[i]);
				if(filter != null) acc = InventoryUtil.sortAccessible(this.cart.mainInventory(), acc, filter);
				acc = InventoryUtil.prioritizeAccessible(acc, this.cart.selectedSlot());
				int movable = InventoryUtil.spaceforItem(filter, this.cart.mainInventory(), acc);
				movable = Math.min(movable, Math.min((filter == null) ? 64 :filter.getMaxStackSize(), amount));
				ItemStack stack = ItemUtil.suckItems(this.cart.world(), x, y, z, filter, movable);
				if(stack!=null) moved = stack.stackSize;
				if(moved > 0) InventoryUtil.putInventory(stack, this.cart.mainInventory(), moved, ForgeDirection.UNKNOWN, acc);
			}
			if(moved>0) context.pause(Settings.OC_SuckDelay);
			return new Object[]{ (moved > 0) };
		}
		else{
			int[] mslots = InventoryUtil.getAccessible(this.cart.mainInventory(), ForgeDirection.UNKNOWN);
			int moved = InventoryUtil.suckItemInventoryWorld(this.cart.mainInventory(), mslots, this.cart.selectedSlot(), this.cart.worldObj,
					x, y, z, dir.getOpposite(), amount);
			if(moved > 0){
				context.pause(Settings.OC_SuckDelay);
				return new Object[]{ true };
			}
			return new Object[]{ false };
		}
	}

	//-------World-Tank-------//

	@Callback(doc = "function(side:number):boolean -- Compare the fluid in the selected tank with the fluid on the specified side. Returns true if equal.")
	public Object[] compareFluid(Context context, Arguments args){
		int side = args.checkInteger(0);
		if(side<0 || side > 5) throw new IllegalArgumentException("invalid side");
		int stank = this.cart.selectedTank();
		if(!(stank>0 && stank<=this.cart.tankcount()))
			return new Object[]{ false , "no tank selected"};

		ForgeDirection dir = this.cart.toGlobal(ForgeDirection.getOrientation(side));
		int x = (int)Math.floor(this.cart.xPosition())+dir.offsetX;
		int y = (int)Math.floor(this.cart.yPosition())+dir.offsetY;
		int z = (int)Math.floor(this.cart.zPosition())+dir.offsetZ;

		IFluidHandler t1 = TankUtil.getFluidHandler(this.cart.world(), x, y, z);
		FluidStack st = this.cart.getTank(stank).getFluid();
		if(t1 == null) return new Object[]{ false };
		return new Object[]{ TankUtil.hasFluid(t1, st, dir.getOpposite()) };
	}

	@Callback(doc = "function(side:number[, amount:number=1000]):boolean, number or string -- Drains the specified amount of fluid from the specified side. Returns the amount drained, or an error message.")
	public Object[] drain(Context context, Arguments args){
		int side = args.checkInteger(0);
		if(side<0 || side > 5) throw new IllegalArgumentException("invalid side");
		int amount = args.optInteger(1, 1000);
		int stank = this.cart.selectedTank();
		if(!(stank>0 && stank<=this.cart.tankcount()))
			return new Object[]{ false , "no tank selected"};

		ForgeDirection dir = this.cart.toGlobal(ForgeDirection.getOrientation(side));
		int x = (int)Math.floor(this.cart.xPosition())+dir.offsetX;
		int y = (int)Math.floor(this.cart.yPosition())+dir.offsetY;
		int z = (int)Math.floor(this.cart.zPosition())+dir.offsetZ;

		IFluidTank t1 = this.cart.getTank(stank);
		IFluidHandler t2 = TankUtil.getFluidHandler(this.cart.world(), x, y, z);
		if(t2 == null )  return new Object[]{ false , "no tank found"};

		FluidStack filter = t1.getFluid();
		FluidStack drain = null;
		if(filter == null)
			drain = t2.drain(dir.getOpposite(), amount, false);
		else
			drain = t2.drain(dir.getOpposite(), new FluidStack(filter,amount), false);

		if(drain == null) return new Object[]{ false, "incompatible or no fluid" };
		int moved = t1.fill(drain, false);
		if(moved < 1) return new Object[]{ false, "tank full" };

		t1.fill(t2.drain(dir.getOpposite(), new FluidStack(drain, moved), true), true);
		return new Object[]{ true,  moved};
	}

	@Callback(doc = "function(side:number[, amount:number=1000]):boolean, number or string -- Eject the specified amount of fluid to the specified side. Returns the amount ejected or an error message.")
	public Object[] fill(Context context, Arguments args){
		int side = args.checkInteger(0);
		if(side<0 || side > 5) throw new IllegalArgumentException("invalid side");
		int amount = args.optInteger(1, 1000);
		int stank = this.cart.selectedTank();
		if(!(stank>0 && stank<=this.cart.tankcount()))
			return new Object[]{ false , "no tank selected"};

		ForgeDirection dir = this.cart.toGlobal(ForgeDirection.getOrientation(side));
		int x = (int)Math.floor(this.cart.xPosition())+dir.offsetX;
		int y = (int)Math.floor(this.cart.yPosition())+dir.offsetY;
		int z = (int)Math.floor(this.cart.zPosition())+dir.offsetZ;

		IFluidTank t1 = this.cart.getTank(stank);
		IFluidHandler t2 = TankUtil.getFluidHandler(this.cart.world(), x, y, z);
		if(t2 == null )  return new Object[]{ false , "no tank found"};

		FluidStack drain = t1.drain(amount, false);
		if(drain == null)
			return new Object[]{ false, "tank is empty" };
		if(TankUtil.getSpaceForFluid(t2, drain, dir.getOpposite()) < 1)
			return new Object[]{ false, "incompatible or no fluid" };
		int moved = t2.fill(dir.getOpposite(),drain, false);
		if(moved < 1) return new Object[]{ false, "no space" };

		t2.fill(dir.getOpposite(),t1.drain(moved, true), true);
		return new Object[]{ true,  moved };
	}
}
