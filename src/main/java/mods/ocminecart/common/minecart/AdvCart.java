package mods.ocminecart.common.minecart;

import com.jcraft.jogg.Packet;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class AdvCart extends EntityMinecart{
	
	public AdvCart(World p_i1713_1_, double p_i1713_2_, double p_i1713_4_,double p_i1713_6_) {
		super(p_i1713_1_, p_i1713_2_, p_i1713_4_, p_i1713_6_);
		this.setDisplayTileData(0);
	}
	
	public AdvCart(World p_i1713_1) {
		super(p_i1713_1);
		this.setDisplayTileData(0);
	}
	
	protected void entityInit(){
		super.entityInit();
		this.dataWatcher.addObject(24, new Integer(0));
	}
	
	@Override
	public void killMinecart(DamageSource p_94095_1_){
		this.setDead();
        ItemStack itemstack = this.getCartItem();

        if (this.func_95999_t() != null)
        {
            itemstack.setStackDisplayName(this.func_95999_t());
        }

        this.entityDropItem(itemstack, 0.0F);
	}

	@Override
	public int getMinecartType() {
		return -1;
	}
	
	public int getMode(){
		return this.dataWatcher.getWatchableObjectInt(24);
	}
	
	public void setMode(int mode){
		this.dataWatcher.updateObject(24, mode);
	}
	
	//This is just debug Code to test minecart break
	/*public boolean interactFirst(EntityPlayer p){   
		String msg="";
		int m;
		
		m=getMode();
		
		++m;
		if(m>2)m=0;
		if(this.worldObj.isRemote){	
			switch(m){
			case 0:
				msg="Set Mode to NORMAL";
				break;
			case 1:
				msg="Set Mode to DRIVE";
				break;
			case 2:
				msg="Set Mode to BREAK";
				break;
			default:
				m=0;
				msg="ERROR:Try Again!";
				break;
			}
			p.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD+msg));
		}
		setMode(m);
		return true;
	}*/
	
	public boolean onRail(){
		int x=MathHelper.floor_double(this.posX);
		int y=MathHelper.floor_double(this.posY);
		int z=MathHelper.floor_double(this.posZ);
		return BlockRailBase.func_150049_b_(this.worldObj, x, y, z);
	}
	
	public void onUpdate(){
		super.onUpdate();
		if(this.motionX!=0 || this.motionZ!=0){
			if(getMode()==2 && onRail()){
				this.setPosition(this.lastTickPosX, this.posY, this.lastTickPosZ);	//Fix: Bug on Booster Tracks  (Reset Position)
				this.setVelocity(0,0,0);
			}
		}
		
		
	}
	
	 public boolean canBePushed(){
		 return (getMode()!=2 || !onRail());
	 }
}
