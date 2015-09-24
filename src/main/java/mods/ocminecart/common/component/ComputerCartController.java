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
		if(nbt.hasKey("color")) cart.setLightColor(nbt.getInteger("color"));
	}

	@Override
	public void save(NBTTagCompound nbt) {
		NBTTagCompound node = new NBTTagCompound();
		((Component)this.node).save(node);
		nbt.setTag("node", node);
		
		nbt.setInteger("color", cart.getLightColor());
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void update() {}
	
	/*--------Component-Functions--------*/
	
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
	
	@Callback(doc="function():boolean -- Set enginespeed")
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
}
