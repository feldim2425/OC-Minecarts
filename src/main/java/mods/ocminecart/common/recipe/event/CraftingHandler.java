package mods.ocminecart.common.recipe.event;

import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CraftingHandler {
	private static ArrayList<ICraftingToolHandler> handlers = new ArrayList<ICraftingToolHandler>();
	
	public static void registerNewHandler(ICraftingToolHandler handler){
		if(handlers.contains(handler)) return;
		handlers.add(handler);
	}
	
	public static void onCraftingEvent(ItemCraftedEvent event){
		Iterator<ICraftingToolHandler> list = handlers.iterator();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		while(list.hasNext()){
			ICraftingToolHandler handler = list.next();
			if(handler.match(event.craftMatrix, event.crafting)){
				List<ItemStack> ls = handler.getItems(event.craftMatrix, event.crafting);
				if(ls!=null) items.addAll(ls);
			}
		}
		
		Iterator<ItemStack> itemlist = items.iterator();
		while(itemlist.hasNext()){
			event.player.inventory.addItemStackToInventory(itemlist.next());
		}
	}
}
