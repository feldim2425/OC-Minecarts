package mods.ocminecart.common.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.client.ModCreativeTab;
import mods.ocminecart.common.item.base.IModelItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

import java.util.List;

public class ItemComputerCartCase extends Item implements IModelItem {

	public ItemComputerCartCase() {
		super();
		setCreativeTab(ModCreativeTab.TAB);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		switch (stack.getMetadata()) {
			case 0:
				return OCMinecart.MOD_ID + ".computer_cart_caste_t1";
			case 1:
				return OCMinecart.MOD_ID + ".computer_cart_caste_t2";
			case 2:
				return OCMinecart.MOD_ID + ".computer_cart_caste_t3";
			case 3:
				return OCMinecart.MOD_ID + ".computer_cart_caste_t4";
		}
		return OCMinecart.MOD_ID + ".computer_cart_caste_t1";
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		ChatFormatting color;
		switch (stack.getMetadata()) {
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
				break;
		}
		return color + super.getItemStackDisplayName(stack);
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
	}

	@Override
	public void registerModel(String modId) {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(modId + ":computer_cart_case_t1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(modId + ":computer_cart_case_t2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 2, new ModelResourceLocation(modId + ":computer_cart_case_t3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 3, new ModelResourceLocation(modId + ":computer_cart_case_t4", "inventory"));
	}
}
