package mods.ocminecart.common.container.slots;

import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Container;
import mods.ocminecart.client.texture.SlotIcons;
import mods.ocminecart.common.driver.CustomDriverRegistry;
import mods.ocminecart.utils.ItemStackUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotComponent extends Slot {
    private int tier;
    private String type;

    public SlotComponent(IInventory inventory, int index, int xpos, int ypos, int tier, String type) {
        super(inventory, index, xpos, ypos);
        this.tier = tier;
        this.type = type;
    }

    public SlotComponent(IInventory inventory, int index, int xpos, int ypos, ItemStack container) {
        super(inventory, index, xpos, ypos);

        if(ItemStackUtil.isStackEmpty(container)) {
            this.tier = -1;
            this.type = li.cil.oc.api.driver.item.Slot.None;
        }
        else {
            Item driver = CustomDriverRegistry.driverFor(container);
            if (driver != null && (driver instanceof Container)) {
                this.tier = ((Container) driver).providedTier(container);
                this.type = ((Container) driver).providedSlot(container);
            } else {
                this.tier = -1;
                this.type = li.cil.oc.api.driver.item.Slot.None;
            }
        }

    }

    @Override
    public TextureAtlasSprite getBackgroundSprite() {
        return SlotIcons.fromTier(this.tier);
    }

    public String getSlotType() {
        return this.type;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (this.type.equals(li.cil.oc.api.driver.item.Slot.None) || this.tier == -1) {
            return false;
        } else if (this.type.equals(li.cil.oc.api.driver.item.Slot.Any) && this.tier == Integer.MAX_VALUE) {
            return true;
        }
        return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
    }

    @Override
    public boolean canBeHovered() {
        return super.canBeHovered() && !type.equals(li.cil.oc.api.driver.item.Slot.None) && tier != -1;
    }

    public int getTier() {
        return this.tier;
    }

}
