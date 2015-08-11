package mods.ocminecart.common.blocks;

import mods.ocminecart.OCMinecart;
import net.minecraft.block.BlockRailBase;
import net.minecraft.world.IBlockAccess;

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
}
