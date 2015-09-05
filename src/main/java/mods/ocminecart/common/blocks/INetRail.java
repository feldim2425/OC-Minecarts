package mods.ocminecart.common.blocks;

import li.cil.oc.api.network.Environment;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

public interface INetRail {
	
	Environment getResponseEnvironment(World world, int x, int y, int z);
	
	boolean isValid(World world, int x, int y, int z, EntityMinecart cart);
	
}
