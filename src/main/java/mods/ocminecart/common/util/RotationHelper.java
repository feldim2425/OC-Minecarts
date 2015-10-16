package mods.ocminecart.common.util;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class RotationHelper {
	public static ForgeDirection[] dir = { 
		ForgeDirection.SOUTH,
		ForgeDirection.WEST,
		ForgeDirection.NORTH,
		ForgeDirection.EAST
	};
	
	public static ForgeDirection calcLocalDirection(ForgeDirection value, ForgeDirection face){
		int n = indexHelperArray(face);
		int d = indexHelperArray(value);
		if(n<0 || d<0) return value;
		return dir[(d+n+4)%4];
	}
	
	public static ForgeDirection calcGlobalDirection(ForgeDirection value, ForgeDirection face){
		int n = indexHelperArray(face);
		int d = indexHelperArray(value);
		if(n<0 || d<0) return value;
		return dir[(d-n+4)%4];
	}
	
	public static int indexHelperArray(ForgeDirection direction){
		for(int i=0;i<dir.length;i+=1){
			if(dir[i] == direction) return i;
		}
		return -1;
	}
	
	//http://jabelarminecraft.blogspot.co.at/p/minecraft-forge-172-finding-block.html
	public static ForgeDirection directionFromYaw(double yaw){
		yaw+=45;
		yaw = (yaw+360)%360;
		int di = MathHelper.floor_double((yaw * 4.0D / 360D) + 0.5D);
		di=(di+4)%4;
		return RotationHelper.dir[di];
	}
}
