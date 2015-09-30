package mods.ocminecart.common.minecart;

import cpw.mods.fml.common.Loader;
import mods.ocminecart.Settings;
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

	protected boolean enableBreak = false; // enable break
	protected double engineSpeed = 0.0D;

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
		// Free DataWatcher 2-16, 23-32
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("enginespeed", this.engineSpeed);
		tag.setBoolean("break", this.enableBreak);
		nbt.setTag("advcart", tag);
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("advcart")){
			NBTTagCompound tag = (NBTTagCompound) nbt.getTag("advcart");
			if(tag.hasKey("enginespeed")) this.engineSpeed = tag.getDouble("enginespeed");
			if(tag.hasKey("break")) this.enableBreak = tag.getBoolean("break");
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
        if(!this.enableBreak){
			this.motionX *= 0.9699999785423279D;
			this.motionY *= 0.0D;
        	this.motionZ *= 0.9699999785423279D;
        	
        	if(this.engineSpeed!=0){
        		double yaw = this.rotationYaw * Math.PI / 180.0;
        		
        		this.motionX += Math.cos(yaw) * 10;
        		this.motionZ += Math.sin(yaw) * 10;
        		
        		double nMotionX = Math.min( Math.abs(this.motionX) , this.engineSpeed);
        		double nMotionZ = Math.min( Math.abs(this.motionZ) , this.engineSpeed);
        		
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
        else if (this.enableBreak){
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
		return (!this.enableBreak || !onRail());
	}


}
