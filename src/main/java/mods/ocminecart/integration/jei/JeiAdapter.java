package mods.ocminecart.integration.jei;

import mods.ocminecart.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public final class JeiAdapter {

    private static boolean enabled;

    /* default */ static void enableAdapter(){
        enabled = true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void drawHighlights(List<ItemStack> stacks){
        if(enabled){
            JeiAddon.getRuntime().getItemListOverlay().highlightStacks(stacks);
        }
    }

    public static ItemStack getStackUnderMouse(){
        if(enabled){
            return JeiAddon.getRuntime().getItemListOverlay().getStackUnderMouse();
        }
        return ItemStackUtil.getEmptyStack();
    }

    public static List<ItemStack> getVisibleStacks(){
        if(enabled){
            return JeiAddon.getRuntime().getItemListOverlay().getVisibleStacks();
        }
        return Collections.emptyList();
    }

    private JeiAdapter(){
    }
}
