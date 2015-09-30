package mods.ocminecart.common.util;

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
		return dir[(d+n)%4];
	}
	
	public static ForgeDirection calcGlobalDirection(ForgeDirection value, ForgeDirection face){
		int n = indexHelperArray(face);
		int d = indexHelperArray(value);
		if(n<0 || d<0) return value;
		return dir[(d-n)%4];
	}
	
	public static int indexHelperArray(ForgeDirection direction){
		for(int i=0;i<dir.length;i+=1){
			if(dir[i] == direction) return i;
		}
		return -1;
	}
}
