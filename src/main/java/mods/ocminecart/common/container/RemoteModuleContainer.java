package mods.ocminecart.common.container;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.ocminecart.common.entityextend.RemoteCartExtender;
import mods.ocminecart.common.entityextend.RemoteExtenderRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import java.util.ArrayList;

public class RemoteModuleContainer extends Container{
	
	protected final boolean IS_SERVER = FMLCommonHandler.instance().getEffectiveSide().isServer(); 
	private RemoteCartExtender module;
	private EntityMinecart cart;
	
	private ArrayList<EntityPlayer> toBan;
	
	@SideOnly(Side.CLIENT)
	public int passstate = 0;
	@SideOnly(Side.CLIENT)
	public boolean perm = false;

	public boolean locked = false;
	
	public RemoteModuleContainer(){
		super();
	}
	
	public RemoteModuleContainer(EntityMinecart cart){
		super();
		
		if(IS_SERVER) toBan = new ArrayList<EntityPlayer>();
		
		this.cart=cart;
		this.module = RemoteExtenderRegister.getExtender(cart); 
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(IS_SERVER){
			if(this.toBan.size()>0 && this.toBan.contains(player)) return false; 
			return player.worldObj == cart.worldObj && player.getDistanceSq(cart.posX, cart.posY, cart.posZ) <= 64.0D && module.isEnabled();
		}
		return true;
	}

	public RemoteCartExtender getModule() {
		return module;
	}
	
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        for(int i=0;i<this.crafters.size();i+=1){
        	ICrafting craft = (ICrafting) this.crafters.get(i);
        	
        	if(this.module.isLocked()!=this.locked){
        		craft.sendProgressBarUpdate(this, 2, (module.isLocked())? 1:0);
        	}
        }
        
        this.locked = this.module.isLocked();
    }
	
	public void addCraftingToCrafters(ICrafting craft){
		 super.addCraftingToCrafters(craft);
		 if(!IS_SERVER) return;
		 craft.sendProgressBarUpdate(this, 0, 0);
		 if(craft instanceof EntityPlayer)
			 craft.sendProgressBarUpdate(this, 1, module.editableByPlayer((EntityPlayer) craft, true) ? 1:0);
		 else
			 craft.sendProgressBarUpdate(this, 1, 0);
		 craft.sendProgressBarUpdate(this, 2, (module.isLocked())? 1:0);
	}
	
	public void sendPassState(EntityPlayer p, int i){
		if(p instanceof ICrafting){
			((ICrafting)p).sendProgressBarUpdate(this, 0, i);
		}
	}
	
	public void lockGui(){
		 for(int i=0;i<this.crafters.size();i+=1){
	        ICrafting craft = (ICrafting) this.crafters.get(i);
	        if(!(craft instanceof EntityPlayer)) continue;
	        if(!module.editableByPlayer((EntityPlayer) craft, false)){
	        	this.toBan.add((EntityPlayer) craft);
	        }
	     }
	}
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int updateid, int value){
		switch(updateid){
		case 0:
			this.passstate=value;
			break;
		case 1:
			this.perm=(value==1);
			break;
		case 2:
			this.locked=(value==1);
			break;
		}
	}
}
