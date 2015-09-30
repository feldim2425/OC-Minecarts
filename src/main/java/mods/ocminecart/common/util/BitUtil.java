package mods.ocminecart.common.util;

public class BitUtil {
	public static boolean getBit(byte b, int pos){
		if(pos<0 && pos >7) return false;
		return (b & (1<<pos)) != 0;
	}
	
	public static byte setBit(boolean value, byte b, int pos){
		if(pos<0 && pos >7) return 0;
		if(value){
			return (byte)(b | (1<<pos));
		}
		else{
			return (byte)(b & ~(1<<pos));
		}
	}
}
