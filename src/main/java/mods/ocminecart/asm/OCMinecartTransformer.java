package mods.ocminecart.asm;

import mods.ocminecart.asm.utils.ASMUtil;
import mods.ocminecart.asm.utils.ObfuscationUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static mods.ocminecart.asm.OCMinecartFMLPlugin.logger;


public class OCMinecartTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.world.World")) {
			ClassNode node = ASMUtil.readClass(basicClass, ClassReader.EXPAND_FRAMES);
			transformWorld(node);
			return ASMUtil.writeClass(node, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		}
		else if (transformedName.equals("net.minecraft.entity.item.EntityMinecart")) {
			ClassNode node = ASMUtil.readClass(basicClass, ClassReader.EXPAND_FRAMES);
			transformEntityMinecart(node);
			return ASMUtil.writeClass(node, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		}
		return basicClass;
	}

	// ====== TRANSFORMER METHODS ====== //

	private void transformWorld(ClassNode cnode) {
		try {
			for (MethodNode mnode : cnode.methods) {
				// Inject Redstone Power Override
				if (mnode.name.equals(ObfuscationUtil.getString("getRedstonePower", "func_175651_c"))) {
					AbstractInsnNode start = mnode.instructions.getFirst();

					InsnList insert = new InsnList();
					LabelNode jump = new LabelNode();

					insert.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insert.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insert.add(new VarInsnNode(Opcodes.ALOAD, 2));
					insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mods/ocminecart/common/hooks/RedstonePower",
							"getOverrideRedstonePower",
							"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)I",
							false));
					insert.add(new VarInsnNode(Opcodes.ISTORE, 3));
					insert.add(new VarInsnNode(Opcodes.ILOAD, 3));
					insert.add(new JumpInsnNode(Opcodes.IFLT, jump));
					insert.add(new VarInsnNode(Opcodes.ILOAD, 3));
					insert.add(new InsnNode(Opcodes.IRETURN));
					insert.add(jump);
					mnode.instructions.insertBefore(start, insert);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARN, "Could not transform Minecraft World class!", e);
		}
	}

	private void transformEntityMinecart(ClassNode cnode) {
		try {
			for (MethodNode mnode : cnode.methods) {
				// Inject Event call
				if (mnode.name.equals(ObfuscationUtil.getString("killMinecart", "func_94095_a"))) {
					AbstractInsnNode start = mnode.instructions.getFirst();

					InsnList insert = new InsnList();
					LabelNode jump = new LabelNode();

					insert.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge",
							"EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
					insert.add(new TypeInsnNode(Opcodes.NEW, "mods/ocminecart/common/hooks/MinecartKillEvent"));
					insert.add(new InsnNode(Opcodes.DUP));
					insert.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insert.add(new VarInsnNode(Opcodes.ALOAD, 1));
					insert.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
							"mods/ocminecart/common/hooks/MinecartKillEvent", "<init>",
							"(Lnet/minecraft/entity/item/EntityMinecart;Lnet/minecraft/util/DamageSource;)V", false));
					insert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
							"net/minecraftforge/fml/common/eventhandler/EventBus", "post",
							"(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
					insert.add(new JumpInsnNode(Opcodes.IFEQ, jump));
					insert.add(new InsnNode(Opcodes.RETURN));
					insert.add(jump);
					mnode.instructions.insertBefore(start, insert);
				}
				// Jump over setDeath and always call killMinecart
				else if (mnode.name.equals(ObfuscationUtil.getString("attackEntityFrom", "func_70097_a"))) {
					AbstractInsnNode invoke = mnode.instructions.getFirst();
					do {
						invoke = ASMUtil.findNextInstructionWithOpcode(invoke, Opcodes.INVOKEVIRTUAL);
					}
					while (invoke != null && !((MethodInsnNode) invoke).name
							.equals(ObfuscationUtil.getString("hasCustomName", "func_145818_k_")));
					AbstractInsnNode aload0 = ASMUtil.findPrevInstruction(invoke);
					LabelNode jump = new LabelNode();
					mnode.instructions.insertBefore(aload0, new JumpInsnNode(Opcodes.GOTO, jump));

					AbstractInsnNode gotoN = ASMUtil.findNextInstructionWithOpcode(invoke, Opcodes.GOTO);
					mnode.instructions.insert(gotoN, jump);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARN, "Could not transform Minecraft EntityMinecart class!", e);
		}
	}

}
