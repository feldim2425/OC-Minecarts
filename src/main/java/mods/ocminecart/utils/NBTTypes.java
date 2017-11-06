package mods.ocminecart.utils;

import net.minecraft.nbt.*;

public enum NBTTypes {
	BYTE((byte) 1, NBTTagByte.class),   // Byte is also used for Booleans
	SHORT((byte) 2, NBTTagShort.class),
	INT((byte) 3, NBTTagInt.class),
	LONG((byte) 4, NBTTagLong.class),
	FLOAT((byte) 5, NBTTagFloat.class),
	DOUBLE((byte) 6, NBTTagDouble.class),
	BYTE_ARRAY((byte) 7, NBTTagByteArray.class),
	STRING((byte) 8, NBTTagString.class),
	TAG_LIST((byte) 9, NBTTagList.class),
	TAG_COMPOUND((byte) 10, NBTTagCompound.class),
	INT_ARRAY((byte) 11, NBTTagIntArray.class);

	private byte type;
	private Class<? extends NBTBase> clazz;

	NBTTypes(byte type, Class<? extends NBTBase> clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	public byte getTypeID() {
		return type;
	}

	public Class<? extends NBTBase> getTypeClass() {
		return clazz;
	}

	public boolean isType(NBTBase base) {
		return base.getId() == type;
	}
}
