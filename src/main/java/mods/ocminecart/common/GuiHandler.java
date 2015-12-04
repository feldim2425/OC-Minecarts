package mods.ocminecart.common;

import mods.ocminecart.client.gui.ComputerCartGui;
import mods.ocminecart.client.gui.NetworkRailBaseGui;
import mods.ocminecart.client.gui.RemoteModuleGui;
import mods.ocminecart.common.container.ComputerCartContainer;
import mods.ocminecart.common.container.NetworkRailBaseContainer;
import mods.ocminecart.common.container.RemoteModuleContainer;
import mods.ocminecart.common.entityextend.RemoteExtenderRegister;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(y!=-10){			//if y is -10 then we send the Entity ID with the par. x
			TileEntity entity = world.getTileEntity(x, y, z);
			if(entity!=null){
				switch(ID){
					case 0:
						if(entity instanceof NetworkRailBaseTile) 
							return new NetworkRailBaseContainer(player.inventory,(NetworkRailBaseTile) entity);
				}
			}
		}
		else{
			Entity entity  = world.getEntityByID(x);
			if(entity!=null){
				switch(ID){
				case 1:
					if(entity instanceof ComputerCart)
						return new ComputerCartContainer(player.inventory,(ComputerCart) entity);
					break;
				case 2:
					if((entity instanceof EntityMinecart) && RemoteExtenderRegister.isRemoteEnabled((EntityMinecart) entity)){
						return new RemoteModuleContainer((EntityMinecart) entity);
					}
					break;
				}
			}
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(y!=-10){			//if y is -10 then we send the Entity ID with the par. x
			TileEntity entity = world.getTileEntity(x, y, z);
			if(entity!=null){
				switch(ID){
					case 0:
						if(entity instanceof NetworkRailBaseTile) 
						return new NetworkRailBaseGui(player.inventory,(NetworkRailBaseTile) entity);
				}
			}
		}
		else{
			Entity entity = world.getEntityByID(x);
			if(entity!=null){
				switch(ID){
					case 1:
						if(entity instanceof ComputerCart)
							return new ComputerCartGui(player.inventory,(ComputerCart) entity);
						break;
					case 2:
						if(entity instanceof EntityMinecart){
							return new RemoteModuleGui();
						}
				}
			}
		}
		
		return null;
	}

}
