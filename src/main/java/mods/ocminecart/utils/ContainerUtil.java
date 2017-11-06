package mods.ocminecart.utils;

import net.minecraft.inventory.Slot;

import java.util.Collection;

public final class ContainerUtil {

    public static Slot findSlotAt(int mouseX, int mouseY, Collection<Slot> slots){
        for(Slot slot : slots){
            if(mouseX > slot.xDisplayPosition && mouseX < slot.xDisplayPosition + 16 && mouseY > slot.yDisplayPosition && mouseY < slot.yDisplayPosition + 16){
                return slot;
            }
        }
        return null;
    }

    private ContainerUtil(){
    }
}
