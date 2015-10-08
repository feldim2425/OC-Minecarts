package mods.ocminecart.common.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TankUtil {
	
	public static IFluidHandler getFluidHandler(World w, int x, int y, int z){
		if(w.isRemote) return null;
		TileEntity entity = w.getTileEntity(x, y, z);
		if(entity == null || !(entity instanceof IFluidHandler)) return null;		
		return (IFluidHandler)entity;
	}
	
	public static int getSpaceForFluid(IFluidHandler tank, FluidStack stack, ForgeDirection side){
		FluidTankInfo[] inf = tank.getTankInfo(side);
		int space = 0;
		for(int i=0;i<inf.length;i+=1){
			if(inf[i].fluid == null)
				space += inf[i].capacity;
			else if(inf[i].fluid.isFluidEqual(stack))
				space += inf[i].capacity - inf[i].fluid.amount;
		}
		return space;
	}
	
	public static boolean hasFluid(IFluidHandler tank, FluidStack stack, ForgeDirection side){
		FluidTankInfo[] inf = tank.getTankInfo(side);
		for(int i=0;i<inf.length;i+=1){
			if(inf[i].fluid.isFluidEqual(stack))
				return true;
		}
		return false;
	}
	
}
