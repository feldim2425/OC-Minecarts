package mods.ocminecart.asm.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;


public final class ASMUtil {

	public static ClassNode readClass(byte[] clazz, int flags) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(clazz);
		reader.accept(node, flags);
		return node;
	}

	public static ClassNode readClass(byte[] clazz) {
		return readClass(clazz, 0);
	}

	public static byte[] writeClass(ClassNode node, int flags) {
		ClassWriter cw = new ClassWriter(flags);
		node.accept(cw);
		return cw.toByteArray();
	}

	public static byte[] writeClass(ClassNode node) {
		return writeClass(node, 0);
	}

	public static boolean isLabelOrLine(AbstractInsnNode node) {
		return node.getType() == AbstractInsnNode.LABEL || node.getType() == AbstractInsnNode.LINE;
	}

	public static AbstractInsnNode findNextInstruction(AbstractInsnNode lastNode) {
		AbstractInsnNode node = lastNode;
		do {
			node = node.getNext();
		}
		while (node != null && isLabelOrLine(node));
		return node;
	}

	public static AbstractInsnNode findNextInstructionWithOpcode(AbstractInsnNode lastNode, int opcode) {
		AbstractInsnNode node = lastNode;
		do {
			node = node.getNext();
		}
		while (node != null && node.getOpcode() != opcode);
		return node;
	}

	public static AbstractInsnNode findPrevInstruction(AbstractInsnNode lastNode) {
		AbstractInsnNode node = lastNode;
		do {
			node = node.getPrevious();
		}
		while (node != null && isLabelOrLine(node));
		return node;
	}

	public static AbstractInsnNode findPrevInstructionWithOpcode(AbstractInsnNode lastNode, int opcode) {
		AbstractInsnNode node = lastNode;
		do {
			node = node.getPrevious();
		}
		while (node != null && node.getOpcode() != opcode);
		return node;
	}

	private ASMUtil() {

	}
}
