package mods.ocminecart.common.blocks;

import li.cil.oc.api.network.Environment;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class NetworkRail extends BlockRailBase{

	protected NetworkRail() {
		super(false);
		this.setBlockTextureName(OCMinecart.MODID+":netrail");
		this.setBlockName(OCMinecart.MODID+".networkrail");
		this.setHardness(0.7F);
	}
	
	public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z){
		return false;
	}
	
	public boolean canMakeSlopes(IBlockAccess world, int x, int y, int z){
		return false;
	}
	
	public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z)
    {
		if((world.getTileEntity(x, y-1, z) instanceof NetworkRailBaseTile) && (cart instanceof Environment)){
			NetworkRailBaseTile tile = (NetworkRailBaseTile) world.getTileEntity(x, y-1, z);
			tile.checkRailNode(((Environment) cart).node());
		}
    }
}
