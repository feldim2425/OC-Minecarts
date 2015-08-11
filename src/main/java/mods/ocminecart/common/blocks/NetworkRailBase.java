package mods.ocminecart.common.blocks;

import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.tileentity.NetworkRailBaseTile;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetworkRailBase extends BlockContainer {
	
	private IIcon sideicon;
	private IIcon boticon;
	private IIcon topicon;
	
	protected NetworkRailBase() {
		super(Material.iron);
		this.setBlockName(OCMinecart.MODID+".networkrailbase");
		this.setHardness(2F);
		this.setResistance(5f);
	}
	
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz){
		player.openGui(OCMinecart.instance, 0, world, x, y, z);
		return true;
	}
	
	public boolean canBeReplacedByLeaves(IBlockAccess world,int x, int y ,int z){
		return false;
	}
	
	public boolean canCreatureSpawn(EnumCreatureType creature, IBlockAccess world, int x , int y, int z){
		return false;
	}
	
	public boolean canHarvestBlock(EntityPlayer player, int meta){
		return true;
	}

	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new NetworkRailBaseTile();
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register){
		sideicon=register.registerIcon(OCMinecart.MODID+":networkrailbase");
		boticon=register.registerIcon(OCMinecart.MODID+":genericbot");
		topicon=register.registerIcon(OCMinecart.MODID+":networkrailbaseTop");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		if(side==ForgeDirection.UP.ordinal()) return getCamoIcon(world,x,y,z);
		else return getStdIcon(side);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return getStdIcon(side);
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon getStdIcon(int side){
		if(side!=ForgeDirection.UP.ordinal() && side!=ForgeDirection.DOWN.ordinal()) return sideicon;
		else if (side==ForgeDirection.DOWN.ordinal()) return boticon;
		else if (side==ForgeDirection.UP.ordinal()) return topicon;
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon getCamoIcon(IBlockAccess world, int x, int y, int z){
		IIcon top = null;
		if(world!=null){
			if(world.getTileEntity(x, y, z)!=null){
				TileEntity tile=world.getTileEntity(x, y, z);
				if(tile instanceof NetworkRailBaseTile){
					top = ((NetworkRailBaseTile) tile).getTopIcon();
				}
			}
		}
		if(top==null) top=getStdIcon(ForgeDirection.UP.ordinal());
		return top;
	}
}
