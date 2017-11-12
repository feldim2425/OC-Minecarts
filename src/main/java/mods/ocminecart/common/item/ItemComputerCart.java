package mods.ocminecart.common.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.client.ModCreativeTab;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.common.item.base.IModelItem;
import mods.ocminecart.common.item.data.DataComputerCart;
import mods.ocminecart.utils.MinecartUtil;
import mods.ocminecart.utils.NbtUtil;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import org.lwjgl.input.Keyboard;

import java.util.List;


public class ItemComputerCart extends Item implements IModelItem {

	private static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {

		@Override
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
			BlockPos railPos = source.getBlockPos().offset(enumfacing);
			World world = source.getWorld();

			IBlockState state = world.getBlockState(railPos);
			if (BlockRailBase.isRailBlock(state)) {
				ItemComputerCart.summonEntity(state, world, railPos, stack);
				stack.splitStack(1);
				return stack;
			}
			IBlockState state2 = world.getBlockState(railPos.down());
			if (state.getMaterial() == Material.AIR && BlockRailBase.isRailBlock(state2)) {
				ItemComputerCart.summonEntity(state2, world, railPos.down(), stack);
				stack.splitStack(1);
				return stack;
			}
			return super.dispenseStack(source, stack);
		}

	};

	public ItemComputerCart() {
		super();
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSER_BEHAVIOR);
		setUnlocalizedName(OCMinecart.MOD_ID + ".computercart");
		setCreativeTab(ModCreativeTab.TAB);
		setMaxStackSize(1);
	}

	public static void summonEntity(IBlockState state, World world, BlockPos pos, ItemStack stack) {
		if (world.isRemote) {
			return;
		}
		Vec3d posVec = MinecartUtil.correctMinecartPosition(pos, state);
		EntityComputerCart entity = new EntityComputerCart(world, posVec.xCoord, posVec.yCoord, posVec.zCoord, ((ItemComputerCart) stack.getItem()).getData(stack));
		world.spawnEntityInWorld(entity);
	}

	@Override
	public void registerModel(String modId) {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(modId + ":computer_cart", "inventory"));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return this.getDisplayString(stack, false);
	}

	public String getDisplayString(ItemStack stack, boolean hasColor) {
		ChatFormatting color;
		String tier;
		DataComputerCart data = getData(stack);
		tier = I18n.translateToLocal("tooltip." + OCMinecart.MOD_ID + ".tier" + (data.getTier() + 1));
		switch (data.getTier()) {
			case 0:
				color = ChatFormatting.WHITE;
				break;
			case 1:
				color = ChatFormatting.YELLOW;
				break;
			case 2:
				color = ChatFormatting.AQUA;
				break;
			case 3:
				color = ChatFormatting.LIGHT_PURPLE;
				break;
			default:
				color = ChatFormatting.DARK_RED;
				tier = "ERROR!";
				break;
		}
		if (!hasColor) {
			color = ChatFormatting.RESET;
		}
		return color + super.getItemStackDisplayName(stack) + " " + tier;
	}

	public DataComputerCart getData(ItemStack stack) {
		return DataComputerCart.loadFromNBT(stack.getSubCompound(OCMinecart.MOD_ID + "_data", true));
	}

	public void setData(ItemStack stack, DataComputerCart data) {
		NBTTagCompound stackNBT = NbtUtil.getOrCreateTag(stack);
		NBTTagCompound dataNBT = new NBTTagCompound();
		data.writeToNBT(dataNBT);
		stackNBT.setTag(OCMinecart.MOD_ID + "_data", dataNBT);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
		super.addInformation(stack, player, list, adv);

		list.clear();
		list.add(this.getDisplayString(stack, true));

		DataComputerCart data = getData(stack);

		if (data == null) return;

		String eloc = I18n.translateToLocal("tooltip." + OCMinecart.MOD_ID + ".storedenergy");
		list.add(ChatFormatting.WHITE + eloc + ": " + ChatFormatting.GREEN + String.format("%.0f", data.getEnergy()));

		if (!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
			String key = GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
			String formkey = "[" + ChatFormatting.WHITE + key + ChatFormatting.GRAY + "]";
			list.add(I18n.translateToLocalFormatted("tooltip." + OCMinecart.MOD_ID + ".viewcomponents", formkey));
		}
		else {
			list.add(I18n.translateToLocal("tooltip." + OCMinecart.MOD_ID + ".instcomponents") + ":");
			for (ItemStack compStack : data.getComponents().values()) {
				list.add("- " + compStack.getDisplayName());
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		if (BlockRailBase.isRailBlock(state)) {
			ItemStack remain = stack;
			if (!playerIn.capabilities.isCreativeMode) {
				remain = stack.splitStack(1);
			}
			summonEntity(state, worldIn, pos, remain);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
