package mods.ocminecart.common.component;

import java.util.ArrayList;

import li.cil.oc.api.API;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.util.InventoryUtil;
import mods.ocminecart.common.util.ItemUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;


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
	
	@Callback(direct = true,doc="function():boolean -- Get the status of the break.")
	public Object[] getBreak(Context context, Arguments arguments){
		return new Object[]{this.cart.getBreakState()};
	}
	
	@Callback(direct = true,doc="function():number -- Get engine speed")
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
	
	@Callback(direct = true,doc="function():number -- Get light color")
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
	
	@Callback(direct = true,doc="function():boolean -- Check if the cart is on a rail")
	public Object[] onRail(Context context, Arguments arguments){
		return new Object[]{this.cart.onRail()};
	}
	
	@Callback(direct = true,doc="function():boolean -- Check if the cart is connected to a network rail")
	public Object[] hasNetworkRail(Context context, Arguments arguments){
		return new Object[]{this.cart.hasNetRail()};
	}
	
	@Callback(direct = true,doc="function():boolean -- Check if the cart is locked on a track")
	public Object[] isLocked(Context context, Arguments arguments){
		return new Object[]{this.cart.isLocked()};
	}
	
	/*--------Component-Functions-Inventory--------*/
		
	@Callback(doc = "function():number -- The size of this device's internal inventory.")
	public Object[] inventorySize(Context context, Arguments arguments){
		return new Object[]{this.cart.getInventorySpace()};
	}
	
	@Callback(doc = "function([slot:number]):number -- Get the currently selected slot; set the selected slot if specified.")
	public Object[] select(Context context, Arguments args){
		int slot = args.optInteger(0, 0);
		if(slot > 0 && slot <= this.cart.maininv.getMaxSizeInventory()){
			this.cart.setSelectedSlot(slot-1);
		}
		else if(args.count() > 0){
			throw new IllegalArgumentException("invalid slot");
		}
		return new Object[]{this.cart.selectedSlot()+1};
	}
	
	@Callback(direct = true, doc = "function([slot:number]):number -- Get the number of items in the specified slot, otherwise in the selected slot.")
	public Object[] count(Context context, Arguments args){
		int slot = args.optInteger(0, -1);
		int num = 0;
		slot = (args.count() > 0) ? slot-1 : this.cart.selectedSlot();
		if(slot > 0 && slot <= this.cart.getInventorySpace()){
			if(this.cart.mainInventory().getStackInSlot(slot-1)!=null){
				num = this.cart.mainInventory().getStackInSlot(slot-1).stackSize;
			}
		}
		else{
			throw new IllegalArgumentException("invalid slot");
		}
		return new Object[]{num};
	}
	
	@Callback(direct = true, doc = "function([slot:number]):number -- Get the remaining space in the specified slot, otherwise in the selected slot.")
	public Object[] space(Context context, Arguments args){
		int slot = args.optInteger(0, -1);
		int num = 0;
		slot = (args.count() > 0) ? slot-1 : this.cart.selectedSlot();
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
			throw new IllegalArgumentException("invalid slot");
		}
		return new Object[]{num};
	}
	
	@Callback(doc = "function(otherSlot:number):boolean -- Compare the contents of the selected slot to the contents of the specified slot.")
	public Object[] compareTo(Context context, Arguments args){
		int slotA = args.checkInteger(0) - 1;
		int slotB = this.cart.selectedSlot();
		boolean result;
		if(slotA>=0 && slotA<this.cart.mainInventory().getSizeInventory() && slotB>=0 && slotB<this.cart.mainInventory().getSizeInventory()){
			ItemStack stackA = this.cart.mainInventory().getStackInSlot(slotA);
			ItemStack stackB = this.cart.mainInventory().getStackInSlot(slotB);
			result = (stackA==null && stackB==null) || 
					(stackA!=null && stackB!=null && stackA.isItemEqual(stackB));
			return new Object[]{result};
		}
		throw new IllegalArgumentException("invalid slot");
	}
	
	@Callback(doc = "function(toSlot:number[, amount:number]):boolean -- Move up to the specified amount of items from the selected slot into the specified slot.")
	public Object[] transferTo(Context context, Arguments args){
		int tslot = args.checkInteger(0) - 1;
		int number = args.optInteger(1, this.cart.mainInventory().getInventoryStackLimit());
		if(!(tslot>=0 && tslot<this.cart.mainInventory().getSizeInventory()))
			throw new IllegalArgumentException("invalid slot");
		if(!(this.cart.selectedSlot()>=0 && this.cart.selectedSlot()<this.cart.mainInventory().getSizeInventory()))
			throw new IllegalArgumentException("invalid slot");
		
		ItemStack stackT = this.cart.mainInventory().getStackInSlot(tslot);
		ItemStack stackS = this.cart.mainInventory().getStackInSlot(this.cart.selectedSlot());
		if(!(stackT==null || (stackS!=null && stackT!=null && stackT.isItemEqual(stackS))) || stackS == null)
			return new Object[]{false};
		
		int items = 0;
		int maxStack = Math.min(this.cart.mainInventory().getInventoryStackLimit(), stackS.getMaxStackSize());
		items = maxStack - ((stackT!=null) ? stackT.stackSize : 0);
		items = Math.min(items, number);
		if(items<=0) return new Object[]{false};
		ItemStack dif = this.cart.mainInventory().decrStackSize(this.cart.selectedSlot(), items);
		if(stackT!=null){
			stackT.stackSize+=dif.stackSize;
		}
		else{
			this.cart.mainInventory().setInventorySlotContents(tslot, dif);
		}
		return new Object[]{true};
	}
	
	/*--------Component-Functions-Tank--------*/
	
	 @Callback(doc = "function():number -- The number of tanks installed in the device.")
	 public Object[] tankCount(Context context, Arguments args){
		 return new Object[]{ this.cart.tankcount() };
	 }
	 
	 @Callback(doc = "function([index:number]):number -- Select a tank and/or get the number of the currently selected tank.")
	 public Object[] selectTank(Context context, Arguments args){
		 int index = args.optInteger(0, 0);
		 if(index > 0 && index <=this.cart.tankcount())
			 this.cart.setSelectedTank(index);
		 else if(args.count() > 0)
			 throw new IllegalArgumentException("invalid tank index");
		 return new Object[]{this.cart.selectedTank()};
	 }
	 
	 @Callback(direct = true, doc = "function([index:number]):number -- Get the fluid amount in the specified or selected tank.")
	 public Object[] tankLevel(Context context, Arguments args){
		 int index = args.optInteger(0, 0);
		 index = (args.count()>0) ? index : this.cart.selectedTank();
		 if(!(index>0 && index<=this.cart.tankcount()))
			 throw new IllegalArgumentException("invalid tank index");
		 return new Object[]{ this.cart.getTank(index).getFluidAmount() };
	 }
	 
	 @Callback(direct = true, doc = "function([index:number]):number -- Get the remaining fluid capacity in the specified or selected tank.")
	 public Object[] tankSpace(Context context, Arguments args){
		 int index = args.optInteger(0, 0);
		 index = (args.count()>0) ? index : this.cart.selectedTank();
		 if(!(index>0 && index<=this.cart.tankcount()))
			 throw new IllegalArgumentException("invalid tank index");
		 IFluidTank tank = this.cart.getTank(index);
		 return new Object[]{ tank.getCapacity() - tank.getFluidAmount() };
	 }
	 
	 @Callback(doc = "function(index:number):boolean -- Compares the fluids in the selected and the specified tank. Returns true if equal.")
	 public Object[] compareFluidTo(Context context, Arguments args){
		 int tankA = args.checkInteger(0);
		 int tankB = this.cart.selectedTank();
		 if(!(tankA>0 && tankA<=this.cart.tankcount()))
			 throw new IllegalArgumentException("invalid tank index");
		 if(!(tankB>0 && tankB<=this.cart.tankcount()))
			 throw new IllegalArgumentException("invalid tank index");
		 
		 FluidStack stackA = this.cart.getTank(tankA).getFluid();
		 FluidStack stackB = this.cart.getTank(tankB).getFluid();
		 boolean res = (stackA==null && stackB==null);
		 if(!res && stackA!=null && stackB!=null)
			 res = stackA.isFluidEqual(stackB);
		 return new Object[]{ res };
	 }
	 
	 @Callback(doc = "function(index:number[, count:number=1000]):boolean -- Move the specified amount of fluid from the selected tank into the specified tank.")
	 public Object[] transferFluidTo(Context context, Arguments args){
		 int tankA = args.checkInteger(0);
		 int tankB = this.cart.selectedTank();
		 int count = args.optInteger(1, 1000);
		 if(!(tankA>0 && tankA<=this.cart.tankcount()))
			 throw new IllegalArgumentException("invalid tank index");
		 if(!(tankB>0 && tankB<=this.cart.tankcount()))
			 throw new IllegalArgumentException("invalid tank index");
		 IFluidTank tankT = this.cart.getTank(tankA);
		 IFluidTank tankS = this.cart.getTank(tankB);
		 
		 if(tankS.getFluid()==null || (tankT!=null && tankS.getFluid().isFluidEqual(tankT.getFluid())))
			 return new Object[]{ false };
		 
		 FluidStack sim = tankS.drain(count, false);	//Simulate the transfer to get the max. moveable amount.
		 int move = tankT.fill(sim, false);
		 
		 if(move<=0) return new Object[]{ false };
		 
		 FluidStack mv = tankS.drain(move, true);
		 int over = tankT.fill(mv, true);
		 over-=mv.amount;
		 if(over>0){	//Just in case we drained too much.
			 FluidStack ret = mv.copy();
			 ret.amount = over;
			 tankS.fill(ret, true);
		 }
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
				throw new IllegalArgumentException("invalid slot");
		 
		 ForgeDirection dir = this.cart.toGlobal(ForgeDirection.getOrientation(side));
		 int x = (int)Math.floor(this.cart.xPosition())+dir.offsetX;
		 int y = (int)Math.floor(this.cart.yPosition())+dir.offsetY;
		 int z = (int)Math.floor(this.cart.zPosition())+dir.offsetZ;
		 
		 ItemStack dstack = this.cart.mainInventory().getStackInSlot(sslot);
		 if(dstack == null) return new Object[]{ false };
		 
		 if(this.cart.world().getBlock(x,y,z) == Blocks.air){
			 ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
			 
			 int mov = Math.min(dstack.stackSize, amount);
			 ItemStack dif = dstack.splitStack(mov);
			 if(dstack.stackSize < 1) 
				 this.cart.mainInventory().setInventorySlotContents(sslot, null);
			 drop.add(dif);
			 ItemUtil.dropItemList(drop, this.cart.world(), x+0.5D, y+0.5D, z+0.5D, false);
			 return new Object[]{ true };
		 }
		 else{
			 int moved = InventoryUtil.dropItemInventoryWorld(dstack.copy(),  this.cart.world(), x, y, z, dir.getOpposite(), amount);
			 if(moved < 1) return new Object[]{ false };
			 if(dstack.stackSize > moved) dstack.stackSize-=moved;
			 else this.cart.mainInventory().setInventorySlotContents(sslot, null);
			 return new Object[]{ true };
		 }
	 }
}
