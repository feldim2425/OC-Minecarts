package mods.ocminecart.common.minecart;

import cpw.mods.fml.common.Loader;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.Settings;
import mods.ocminecart.common.util.BitUtil;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

//Is the Base for a solid, self powered cart with a break.
//Later I will add the Railcraft integration here
public class AdvCart extends EntityMinecart {

	//protected boolean enableBreak = false; // enable break
	//protected double engineSpeed = 0.0D;

	public AdvCart(World p_i1713_1_, double p_i1713_2_, double p_i1713_4_,
			double p_i1713_6_) {
		super(p_i1713_1_, p_i1713_2_, p_i1713_4_, p_i1713_6_);
		this.setDisplayTileData(0);
	}

	public AdvCart(World p_i1713_1) {
		super(p_i1713_1);
		this.setDisplayTileData(0);
	}

	protected void entityInit() {
		super.entityInit();
		
		this.dataWatcher.addObject(3, (byte)0);
		this.dataWatcher.addObject(4, 0.0F);
		// Free DataWatcher 5-16, 23-32
	}
	
	protected final void setBreak(boolean b){ 
		this.dataWatcher.updateObject(3, BitUtil.setBit(b, this.dataWatcher.getWatchableObjectByte(3), 0));
	}
	protected final boolean getBreak(){ return BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 0); }
	protected final void setEngine(double d){ this.dataWatcher.updateObject(4, (float)d); }
	protected final double getEngine(){ return this.dataWatcher.getWatchableObjectFloat(4); }
	public final boolean isLocked(){ return BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 1); }
	public final boolean isEngineActive(){ return this.getEngine()!=0 && !this.isLocked() && !this.getBreak() && this.onRail(); }
	
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("enginespeed", this.dataWatcher.getWatchableObjectFloat(4));
		tag.setBoolean("break", BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 0));
		nbt.setTag("advcart", tag);
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("advcart")){
			NBTTagCompound tag = (NBTTagCompound) nbt.getTag("advcart");
			if(tag.hasKey("enginespeed")) this.dataWatcher.updateObject(4, (float)tag.getDouble("enginespeed"));
			if(tag.hasKey("break")) 
				this.dataWatcher.updateObject(3, BitUtil.setBit(tag.getBoolean("break"), this.dataWatcher.getWatchableObjectByte(3), 0));
		}
	}

	@Override
	public void killMinecart(DamageSource p_94095_1_) {
		this.setDead();
		ItemStack itemstack = this.getCartItem();

		if (this.func_95999_t() != null) {
			itemstack.setStackDisplayName(this.func_95999_t());
		}

		this.entityDropItem(itemstack, 0.0F);
	}

	@Override
	public int getMinecartType() {
		return -1;
	}

	public boolean onRail() {
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.posY);
		int z = MathHelper.floor_double(this.posZ);
		return BlockRailBase.func_150049_b_(this.worldObj, x, y, z);
	}

	public void onUpdate() {
		super.onUpdate();
	}

    protected void applyDrag() {
        if(!(BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 0) || BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 1))){
			this.motionX *= 0.9699999785423279D;
			this.motionY *= 0.0D;
        	this.motionZ *= 0.9699999785423279D;
        	
        	if(this.dataWatcher.getWatchableObjectFloat(4)!=0){
        		double yaw = this.rotationYaw * Math.PI / 180.0;
        		
        		this.motionX += Math.cos(yaw) * 10;
        		this.motionZ += Math.sin(yaw) * 10;
        		
        		double nMotionX = Math.min( Math.abs(this.motionX) , this.dataWatcher.getWatchableObjectFloat(4));
        		double nMotionZ = Math.min( Math.abs(this.motionZ) , this.dataWatcher.getWatchableObjectFloat(4));
        		
        		if(this.motionX < 0) this.motionX = - nMotionX;
        		else this.motionX = nMotionX;
        		
        		if(this.motionZ < 0) this.motionZ = - nMotionZ;
        		else this.motionZ = nMotionZ;
        	}
        	
        	//Stop the cart if there is no speed. (below 0.0001 there are only sounds and no movement)
        	if(Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) < 0.0001){
        		this.motionX = 0;
            	this.motionZ = 0;
        	}
        }
        else if(!BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 1)){
        	this.motionX = 0;
        	this.motionZ = 0;
        	this.setPosition(this.lastTickPosX, this.posY, this.lastTickPosZ);  // Fix: Bug on Booster Tracks (Reset Position)
        }
    }
    
    public double getSpeed(){
    	return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
    }

	public AxisAlignedBB getBoundingBox() {
		if(Loader.isModLoaded("Railcraft") && Settings.GeneralFixCartBox)	//The Railcraft collision handler breaks some things
			return super.getBoundingBox();
		return this.getCollisionBox(this);
	}

	public boolean canBePushed() {
		return (!BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3),0) || !onRail());
	}
	
	/*-------Railcraft-------*/
	
	public void lockdown(boolean lock){
		this.dataWatcher.updateObject(3, BitUtil.setBit(lock, this.dataWatcher.getWatchableObjectByte(3), 1));
	}


}
