package mods.ocminecart.interaction.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.minecart.ComputerCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ComputerCartDataProvider implements IWailaEntityProvider {

	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return accessor.getEntity();
	}

	@Override
	public List<String> getWailaHead(Entity entity, List<String> currenttip,
			IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound tag = accessor.getNBTData();
		String stier;
		EnumChatFormatting color;
    	switch(tag.getInteger("tier")){
    	case 0:
    		color = EnumChatFormatting.WHITE;
    		stier = "(Tier 1)";
    		break;
    	case 1:
    		color = EnumChatFormatting.YELLOW;
    		stier = "(Tier 2)";
    		break;
    	case 2:
    		color = EnumChatFormatting.AQUA;
    		stier = "(Tier 3)";
    		break;
    	case 3:
    		color = EnumChatFormatting.LIGHT_PURPLE;
    		stier = "(Creative)";
    		break;
    	default:
    		color = EnumChatFormatting.DARK_RED;
    		stier = "ERROR!";
    		break;
    	}
    	currenttip.set(0, color+StatCollector.translateToLocal("entity.ocminecart.computercart.name")+" "+stier);
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> currenttip,
			IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound tag = accessor.getNBTData();
		if(config.getConfig("oc.address") && tag.hasKey("address")){
			String loc = StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".address");
			currenttip.add(EnumChatFormatting.GOLD+loc+": "+EnumChatFormatting.WHITE+tag.getString("address"));
		}
		
		if(config.getConfig("oc.energy") && tag.hasKey("energy") && tag.hasKey("maxenergy")){
			String loc = StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".storedenergy");
			currenttip.add(EnumChatFormatting.GOLD+loc+": "+EnumChatFormatting.WHITE+tag.getInteger("energy")
					+"/"+tag.getInteger("maxenergy"));
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(Entity entity, List<String> currenttip,
			IWailaEntityAccessor accessor, IWailaConfigHandler config) {    //Unused
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent,NBTTagCompound tag, World world) {
		NBTTagCompound newtag = new NBTTagCompound();
		if(ent instanceof ComputerCart){
			ComputerCart cart = (ComputerCart) ent;
			newtag.setInteger("tier", cart.tier());
			newtag.setInteger("energy", (int)Math.floor(cart.getCurEnergy()+0.5D));
			newtag.setInteger("maxenergy", (int)Math.floor(cart.getMaxEnergy()));
			newtag.setString("address", cart.node().address());
		}
		return newtag;
	}

}
