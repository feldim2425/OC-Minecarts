package mods.ocminecart.common.component;

import li.cil.oc.api.API;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public class ComputerCartController implements ManagedEnvironment{
	
	private Node node;
	private ComputerCart cart;
	
	public ComputerCartController(ComputerCart cart){
		this.cart = cart;
		node = API.network.newNode(this, Visibility.Neighbors).withComponent("computercart").create();
	}
	
	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onConnect(Node node) {
	}

	@Override
	public void onDisconnect(Node node) {
	}

	@Override
	public void onMessage(Message message) {}

	@Override
	public void load(NBTTagCompound nbt) {
		if(nbt.hasKey("node")) ((Component)this.node).load(nbt.getCompoundTag("node"));
		if(nbt.hasKey("color")) cart.setLightColor(nbt.getInteger("color")); //TODO: Remove this in the next version.
	}

	@Override
	public void save(NBTTagCompound nbt) {
		NBTTagCompound node = new NBTTagCompound();
		((Component)this.node).save(node);
		nbt.setTag("node", node);
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void update() {}
	
	/*--------Component-Functions-Cart--------*/
	
	@Callback(doc="function(set:boolean):boolen, string -- Enable/Disable the break. String for errors")
	public Object[] setBreak(Context context, Arguments arguments){
		boolean state = arguments.checkBoolean(0);
		if(this.cart.getSpeed() > this.cart.getMaxCartSpeedOnRail() && state){
			return new Object[]{this.cart.getBreakState(), "too fast"};
		}
		this.cart.setBreakState(state);
		return new Object[]{state, null};
	}
	
	@Callback(doc="function():boolean -- Get the status of the break.")
	public Object[] getBreak(Context context, Arguments arguments){
		return new Object[]{this.cart.getBreakState()};
	}
	
	@Callback(doc="function():boolean -- Get engine speed")
	public Object[] getEngineSpeed(Context context, Arguments arguments){
		return new Object[]{this.cart.getEngineState()};
	}
	
	@Callback(doc="function():number -- Get current speed of the cart. -1 if there is no rail")
	public Object[] getCartSpeed(Context context, Arguments arguments){
		double speed = -1;
		if(this.cart.onRail()){
			speed = this.cart.getSpeed();
		}
		return new Object[]{speed};
	}
	
	@Callback(doc="function(speed:number):number -- Set the engine speed.")
	public Object[] setEngineSpeed(Context context, Arguments arguments){
		double speed = Math.min(arguments.checkDouble(0), this.cart.getMaxCartSpeedOnRail());
		this.cart.setEngineState(speed);
		return new Object[]{speed};
	}
	
	@Callback(doc="function():number -- Get the maximal cart speed")
	public Object[] getMaxSpeed(Context context, Arguments arguments){
		return new Object[]{this.cart.getMaxCartSpeedOnRail()};
	}
	
	@Callback(doc="function(color:number):number -- Set light color")
	public Object[] setLightColor(Context context, Arguments arguments){
		int color = arguments.checkInteger(0);
		this.cart.setLightColor(color);
		return new Object[]{color};
	}
	
	@Callback(doc="function():number -- Get light color")
	public Object[] getLightColor(Context context, Arguments arguments){
		return new Object[]{this.cart.getLightColor()};
	}
	
	@Callback(doc="function() -- Rotate the cart")
	public Object[] rotate(Context context, Arguments arguments){
		float yaw = this.cart.rotationYaw + 180F;
		if(yaw >= 360F) yaw -= 360F;
		this.cart.rotationYaw = yaw;
		return new Object[]{};
	}
	
	/*--------Component-Functions-Inventory--------*/
		
	@Callback(doc = "function():number -- The size of this device's internal inventory.")
	public Object[] inventorySize(Context context, Arguments arguments){
		return new Object[]{this.cart.getInventorySpace()};
	}
	
	@Callback(doc = "function([slot:number]):number -- Get the currently selected slot; set the selected slot if specified.")
	public Object[] select(Context context, Arguments args){
		int slot = args.optInteger(0, -1);
		if(slot > 0 && slot <= this.cart.maininv.getMaxSizeInventory()){
			this.cart.setSelectedSlot(slot-1);
		}
		return new Object[]{this.cart.selectedSlot()+1};
	}
	
	@Callback(direct = true, doc = "function([slot:number]):number -- Get the number of items in the specified slot, otherwise in the selected slot.")
	public Object[] count(Context context, Arguments args){
		int slot = args.optInteger(0, -1);
		int num = 0;
		slot = (slot != -1) ? slot-1 : this.cart.selectedSlot();
		if(slot > 0 && slot <= this.cart.getInventorySpace()){
			if(this.cart.mainInventory().getStackInSlot(slot-1)!=null){
				num = this.cart.mainInventory().getStackInSlot(slot-1).stackSize;
			}
		}
		else{
			return new Object[]{null};
		}
		return new Object[]{num};
	}
	
	@Callback(direct = true, doc = "function([slot:number]):number -- Get the remaining space in the specified slot, otherwise in the selected slot.")
	public Object[] space(Context context, Arguments args){
		int slot = args.optInteger(0, -1);
		int num = 0;
		slot = (slot != -1) ? slot-1 : this.cart.selectedSlot();
		if(slot > 0 && slot <= this.cart.getInventorySpace()){
			ItemStack stack = this.cart.mainInventory().getStackInSlot(slot-1);
			if(stack!=null){
				int maxStack = Math.min(this.cart.mainInventory().getInventoryStackLimit(), stack.getMaxStackSize());
				num = maxStack-stack.stackSize;
			}
			else{
				num = this.cart.mainInventory().getInventoryStackLimit();
			}
		}
		else{
			return new Object[]{null};
		}
		return new Object[]{num};
	}
	
	@Callback(doc = "function(otherSlot:number):boolean -- Compare the contents of the selected slot to the contents of the specified slot.")
	public Object[] compareTo(Context context, Arguments args){
		int slotA = args.checkInteger(0) - 1;
		int slotB = this.cart.selectedSlot();
		boolean result;
		if(slotA>=0 && slotA<this.cart.mainInventory().getSizeInventory()){
			ItemStack stackA = this.cart.mainInventory().getStackInSlot(slotA);
			ItemStack stackB = this.cart.mainInventory().getStackInSlot(slotB);
			result = stackA!=null && stackB!=null && stackA.isItemEqual(stackB);
			return new Object[]{result};
		}
		return new Object[]{null};
	}
	
	@Callback(doc = "function(toSlot:number[, amount:number]):boolean -- Move up to the specified amount of items from the selected slot into the specified slot.")
	public Object[] transferTo(Context context, Arguments args){
		int tslot = args.checkInteger(0) - 1;
		int number = args.optInteger(1, this.cart.mainInventory().getInventoryStackLimit());
		if(!(tslot>=0 && tslot<this.cart.mainInventory().getSizeInventory())) return new Object[]{null};
		
		ItemStack stackT = this.cart.mainInventory().getStackInSlot(tslot);
		ItemStack stackS = this.cart.mainInventory().getStackInSlot(this.cart.selectedSlot());
		if(!(stackT==null || (stackS!=null && stackT!=null && stackT.isItemEqual(stackS))) || stackS == null)
			return new Object[]{false};
		
		int items = 0;
		int maxStack = Math.min(this.cart.mainInventory().getInventoryStackLimit(), stackS.getMaxStackSize());
		items = maxStack - ((stackT!=null) ? stackT.stackSize : 0);
		items = Math.min(items, number);
		if(items==0) return new Object[]{items==number};
		ItemStack dif = this.cart.mainInventory().decrStackSize(this.cart.selectedSlot(), items);
		if(stackT!=null){
			stackT.stackSize+=dif.stackSize;
		}
		else{
			this.cart.mainInventory().setInventorySlotContents(tslot, dif);
		}
		return new Object[]{true};
	}
}
