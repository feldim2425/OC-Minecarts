package mods.ocminecart.common.minecart;

import mods.ocminecart.OCMinecart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

//Is the Base for a solid, self powered cart with a break.
//Later I will add the Railcraft integration here
public class AdvCart extends EntityMinecart {

	protected boolean enableBreak = false; // enable break
	protected double motor = 0.0D;

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
		// Freie DataWatcher 2-16, 23-32
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
        	
        	if(this.motor!=0){
        		double yaw = rotationYaw * Math.PI / 180.0;
        		this.motionX += Math.cos(yaw) * 10;
        		this.motionZ += Math.sin(yaw) * 10;
        		
        		double nMotionX = Math.min( Math.abs(this.motionX) , this.motor);
        		double nMotionZ = Math.min( Math.abs(this.motionZ) , this.motor);
        		
        		if(this.motionX < 0) this.motionX = - nMotionX;
        		else this.motionX = nMotionX;
        		
        		if(this.motionZ < 0) this.motionZ = - nMotionZ;
        		else this.motionZ = nMotionZ;
        	}
        }
        else if (this.enableBreak){
        	this.motionX = 0;
        	this.motionZ = 0;
        	this.setPosition(this.lastTickPosX, this.posY, this.lastTickPosZ);  // Fix: Bug on Booster Tracks (Reset Position)
        }
    }

	public AxisAlignedBB getBoundingBox() {
		return this.getCollisionBox(this);
	}

	public boolean canBePushed() {
		return (!this.enableBreak || !onRail());
	}


}
