package mods.ocminecart.common.recipe;


import li.cil.oc.api.API;
import li.cil.oc.api.detail.ItemAPI;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.blocks.ModBlocks;
import mods.ocminecart.common.items.ItemComputerCart;
import mods.ocminecart.common.items.ModItems;
import mods.ocminecart.common.recipe.event.ComputerCartRomCrafting;
import mods.ocminecart.common.recipe.event.CraftingHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes {
	
	public static void init(){
		ItemAPI items  = API.items;
		
		RecipeSorter.register(OCMinecart.MODID + ":romcrafting", RomCrafting.class , Category.SHAPELESS, "after:minecraft:shaped");
		
		GameRegistry.addShapedRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.block_NetworkRailBase),1), 
		"CSC",
		"MBM",
		"IKI", 'S',items.get("lanCard").createItemStack(1), 'C',items.get("chamelium").createItemStack(1), 'I', new ItemStack(Items.iron_ingot),
		       'M',items.get("chip2").createItemStack(1), 'K',Item.getItemFromBlock(items.get("cable").block()), 'B',items.get("printedCircuitBoard").createItemStack(1));
	
	
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.block_NetworkRail ), Item.getItemFromBlock(items.get("cable").block()), new ItemStack(Items.redstone), Item.getItemFromBlock(Blocks.detector_rail));
	
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.item_ComputerCartCase,1,0),"X","Y","Z",'X',Item.getItemFromBlock(items.get("case1").block()), 'Y', Items.minecart, 'Z', Item.getItemFromBlock(items.get("cable").block()));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.item_ComputerCartCase,1,1),"X","Y","Z",'X',Item.getItemFromBlock(items.get("case2").block()), 'Y', Items.minecart, 'Z', Item.getItemFromBlock(items.get("cable").block()));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.item_ComputerCartCase,1,2),"X","Y","Z",'X',Item.getItemFromBlock(items.get("case3").block()), 'Y', Items.minecart, 'Z', Item.getItemFromBlock(items.get("cable").block()));
		
		GameRegistry.addRecipe(new RomCrafting(new ItemStack(ModItems.item_ComputerCart) , ItemComputerCart.SLOT_ROM));
		
		CraftingHandler.registerNewHandler(new ComputerCartRomCrafting());
	}
}
