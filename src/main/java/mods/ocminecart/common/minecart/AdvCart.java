package mods.ocminecart.common.minecart;

import mods.ocminecart.Settings;
import mods.ocminecart.common.util.BitUtil;
import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.api.electricity.IElectricMinecart;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.common.emblems.EmblemToolsServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

//Is the Base for a solid, self powered cart with a break.
//Later I will add the Railcraft integration here
@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.railcraft.api.carts.IEnergyTransfer", modid = "Railcraft") ,
	@Optional.Interface(iface = "mods.railcraft.api.electricity.IElectricMinecart", modid = "Railcraft")
})
public abstract class AdvCart extends EntityMinecart implements IEnergyTransfer, IElectricMinecart {
	
	private ChargeHandler charge;
	
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
		
		if(Loader.isModLoaded("Railcraft") && FMLCommonHandler.instance().getEffectiveSide().isServer())
			charge = new ChargeHandler(this, ChargeHandler.Type.USER, Settings.ComputerCartETrackBuf, Settings.ComputerCartETrackLoss);
		
		this.dataWatcher.addObject(3, (byte)0);
		this.dataWatcher.addObject(4, 0.0F);
		this.dataWatcher.addObject(5, "");
		// Free DataWatcher 6-16, 23-32
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
		if(Loader.isModLoaded("Railcraft")){
			NBTTagCompound rctag = new NBTTagCompound();
			rctag.setBoolean("locked", BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 1));
			if(this.charge!=null) this.charge.writeToNBT(rctag);
			String emblem = this.dataWatcher.getWatchableObjectString(5);
			if(emblem!=null && emblem!="") rctag.setString("emblem_id", emblem);
			tag.setTag("railcraft", rctag);
		}
		nbt.setTag("advcart", tag);
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("advcart")){
			NBTTagCompound tag = (NBTTagCompound) nbt.getTag("advcart");
			if(tag.hasKey("enginespeed")) this.dataWatcher.updateObject(4, (float)tag.getDouble("enginespeed"));
			if(tag.hasKey("break")) 
				this.dataWatcher.updateObject(3, BitUtil.setBit(tag.getBoolean("break"), this.dataWatcher.getWatchableObjectByte(3), 0));
			if(tag.hasKey("railcraft") && Loader.isModLoaded("Railcraft")){
				NBTTagCompound rctag = tag.getCompoundTag("railcraft");
				this.dataWatcher.updateObject(3, BitUtil.setBit(rctag.getBoolean("locked"), this.dataWatcher.getWatchableObjectByte(3), 1));
				if(this.charge!=null) this.charge.readFromNBT(rctag);
				if(rctag.hasKey("emblem_id")) this.dataWatcher.updateObject(5, rctag.getString("emblem_id"));
			}
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
		if (this.worldObj.isRemote) return;
		
		if(charge!=null && Loader.isModLoaded("Railcraft")){
			this.charge.tick();
			double mv = this.addEnergy(this.charge.getCharge() * Settings.OC_IC2PWR, true);  //Get max. energy we can load to the node
			mv = Math.min(mv, Settings.ComputerCartETrackLoad * Settings.OC_IC2PWR); //Check if the movable energy is higher than the limit.
			mv = this.charge.removeCharge(mv / Settings.OC_IC2PWR) * Settings.OC_IC2PWR; //Remove the charge from the buffer
			this.addEnergy(mv , false);	//Add the removed energy to the node network
		}
	}
	
	@Override
    protected void func_145821_a(int trackX, int trackY, int trackZ, double maxSpeed, double slopeAdjustement, Block trackBlock, int trackMeta) {
        super.func_145821_a(trackX, trackY, trackZ, maxSpeed, slopeAdjustement, trackBlock, trackMeta);
        if (this.worldObj.isRemote) return;
        if(charge!=null && Loader.isModLoaded("Railcraft")){
        	this.charge.tickOnTrack(trackX, trackY, trackZ);
        }
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
	
	protected abstract double addEnergy(double amount, boolean simulate);
	
	/*-------Railcraft-------*/
	public void lockdown(boolean lock){
		if(lock != BitUtil.getBit(this.dataWatcher.getWatchableObjectByte(3), 1))
			this.dataWatcher.updateObject(3, BitUtil.setBit(lock, this.dataWatcher.getWatchableObjectByte(3), 1));
	}

	@Override
	public boolean canExtractEnergy() { return false; }

	@Override
	public boolean canInjectEnergy() { return false; }

	@Override
	public double extractEnergy(Object arg0, double arg1, int arg2, boolean arg3, boolean arg4, boolean arg5) { return 0; }

	@Override
	public int getCapacity() {
		return (int) this.charge.getCapacity();
	}

	@Override
	public double getEnergy() {
		return charge.getCharge();
	}

	@Override
	public int getTier() { return 1; }

	@Override
	public int getTransferLimit() { return (int)(Settings.ComputerCartETrackLoad * 1.1); }

	@Override
	public double injectEnergy(Object arg0, double arg1, int arg2, boolean arg3, boolean arg4, boolean arg5) { return 0; }
	
	@Override
	public ChargeHandler getChargeHandler() {
		return this.charge;
	}
	
	public boolean setEmblem(ItemStack stack){
		if(!Loader.isModLoaded("Railcraft")) return false;
		return setEmblem(EmblemToolsServer.getEmblemIdentifier(stack));
	}
	
	public boolean setEmblem(String emblem){
		if(!Loader.isModLoaded("Railcraft")) return false;
		if(emblem==this.dataWatcher.getWatchableObjectString(5)) return false;
		if(emblem==null) emblem="";
		this.dataWatcher.updateObject(5, emblem);
		return true;
	}
	
	public String getEmblem(){
		if(!Loader.isModLoaded("Railcraft")) return null;
		return this.dataWatcher.getWatchableObjectString(5);
	}
	
	@Optional.Method(modid="Railcraft")		
	public ResourceLocation getEmblemIcon(){	//Optional.Method because of speed (used in the renderer)
		String id = this.dataWatcher.getWatchableObjectString(5);
		if(id==null || id=="") return null;
		return EmblemToolsClient.packageManager.getEmblemTextureLocation(id);
	}

}
