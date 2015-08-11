package mods.ocminecart.common;


import org.apache.logging.log4j.Level;

import mods.ocminecart.common.blocks.ModBlocks;
import mods.ocminecart.common.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import li.cil.oc.api.API;
import li.cil.oc.api.detail.ItemAPI;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes {
	
	public static void init(){
		ItemAPI items  = API.items;
		
		GameRegistry.addShapedRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.block_NetworkRailBase),1), 
		"CSC",
		"MBM",
		"IKI", 'S',items.get("lanCard").createItemStack(1), 'C',items.get("chamelium").createItemStack(1), 'I', new ItemStack(Items.iron_ingot),
		       'M',items.get("chip2").createItemStack(1), 'K',Item.getItemFromBlock(items.get("cable").block()), 'B',items.get("printedCircuitBoard").createItemStack(1));
	
	
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.block_NetworkRail ), Item.getItemFromBlock(items.get("cable").block()), new ItemStack(Items.redstone), Item.getItemFromBlock(Blocks.detector_rail));
	
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.item_ComputerCartCase,1,0),"X","Y","Z",'X',Item.getItemFromBlock(items.get("case1").block()), 'Y', Items.minecart, 'Z', Item.getItemFromBlock(items.get("cable").block()));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.item_ComputerCartCase,1,1),"X","Y","Z",'X',Item.getItemFromBlock(items.get("case2").block()), 'Y', Items.minecart, 'Z', Item.getItemFromBlock(items.get("cable").block()));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.item_ComputerCartCase,1,2),"X","Y","Z",'X',Item.getItemFromBlock(items.get("case3").block()), 'Y', Items.minecart, 'Z', Item.getItemFromBlock(items.get("cable").block()));
	}
}
