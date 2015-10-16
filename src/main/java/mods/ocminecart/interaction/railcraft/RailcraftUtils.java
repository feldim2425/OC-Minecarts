package mods.ocminecart.interaction.railcraft;

import mods.railcraft.api.core.items.IToolCrowbar;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.Loader;

public class RailcraftUtils {
	
	public static boolean isUsingChrowbar(EntityPlayer player){
		if(!Loader.isModLoaded("Railcraft")) return false;
		if(player.getCurrentEquippedItem()!=null && (player.inventory .getCurrentItem().getItem() instanceof IToolCrowbar))
			return true;
		return false;
	}
}
