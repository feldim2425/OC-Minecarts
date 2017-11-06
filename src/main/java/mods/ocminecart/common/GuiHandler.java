package mods.ocminecart.common;

import mods.ocminecart.client.gui.GuiComputerCart;
import mods.ocminecart.common.container.ContainerComputerCart;
import mods.ocminecart.common.entity.EntityComputerCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID){
			case 0:
				Entity entity = world.getEntityByID(x);
				if(entity instanceof EntityComputerCart){
					return new ContainerComputerCart((EntityComputerCart)entity, player);
				}
				return null;

			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID){
			case 0:
				Entity entity = world.getEntityByID(x);
				if(entity instanceof EntityComputerCart){
					return new GuiComputerCart((EntityComputerCart)entity, player);
				}
				return null;

			default:
				return null;
		}
	}

}
