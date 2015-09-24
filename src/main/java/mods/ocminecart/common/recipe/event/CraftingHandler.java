package mods.ocminecart.common.recipe.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class CraftingHandler {
	private static ArrayList<IExtraItemHandler> handlers = new ArrayList<IExtraItemHandler>();
	
	public static void registerNewHandler(IExtraItemHandler handler){
		if(handlers.contains(handler)) return;
		handlers.add(handler);
	}
	
	public static void onCraftingEvent(ItemCraftedEvent event){
		Iterator<IExtraItemHandler> list = handlers.iterator();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		while(list.hasNext()){
			IExtraItemHandler handler = list.next();
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
