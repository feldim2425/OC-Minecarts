package mods.ocminecart.common.container;


import li.cil.oc.api.driver.Item;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.integration.opencomputers.DriverKeyboard;
import li.cil.oc.integration.opencomputers.DriverKeyboard$;
import mods.ocminecart.common.container.slots.SlotComponent;
import mods.ocminecart.common.driver.CustomDriverRegistry;
import mods.ocminecart.common.entity.EntityComputerCart;
import mods.ocminecart.common.inventory.ComponentInventory;
import mods.ocminecart.utils.ItemStackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerComputerCart extends Container {

	public static final int YSIZE_SCR = 256;
	public static final int YSIZE_NOSCR = 108;
	public static final int XSIZE = 256;
	public static final int DELTA = YSIZE_SCR - YSIZE_NOSCR;

	private EntityComputerCart computerCart;
	private EntityPlayer player;

	private boolean hasScreen;

	public ContainerComputerCart(EntityComputerCart computerCart, EntityPlayer player) {
		super();
		this.computerCart = computerCart;
		this.player = player;
		this.hasScreen = findScreen() != null;

		ComponentInventory compInv = computerCart.getComponentInventory();

		this.addSlotToContainer(new SlotComponent(compInv, 20 , 188,232 - ((this.hasScreen) ? 0 : DELTA), compInv.getStackInSlot(0)));
		this.addSlotToContainer(new SlotComponent(compInv, 21 , 206,232 - ((this.hasScreen) ? 0 : DELTA), compInv.getStackInSlot(1)));
		this.addSlotToContainer(new SlotComponent(compInv, 22 , 224,232 - ((this.hasScreen) ? 0 : DELTA), compInv.getStackInSlot(2)));

		addPlayerInv(6, 174 - ((this.hasScreen) ? 0 : DELTA) ,player.inventory);
	}

	public TextBuffer findScreen(){
		ComponentInventory compInv = this.computerCart.getComponentInventory();
		for(int i=0; i<compInv.getSizeInventory(); i++){
			ManagedEnvironment environment = compInv.getComponent(i);
			if(environment instanceof TextBuffer){
				return (TextBuffer) environment;
			}
		}
		return null;
	}

	public boolean hasKeyboard(){
		ComponentInventory compInv = this.computerCart.getComponentInventory();
		for(int i=0; i<compInv.getSizeInventory(); i++){
			ItemStack stack = compInv.getStackInSlot(i);
			if(ItemStackUtil.isStackEmpty(stack)){
				continue;
			}

			Item driver = CustomDriverRegistry.driverFor(stack, EntityComputerCart.class);
			if(driver != null && DriverKeyboard$.class.isAssignableFrom(driver.getClass())){
				return true;
			}
		}
		return false;
	}

	public EntityComputerCart getComputerCart() {
		return computerCart;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return !computerCart.isDead && playerIn.worldObj.equals(computerCart.worldObj) && playerIn.getDistanceSqToEntity(this.computerCart) <= (16*16);
	}

	private void addPlayerInv(int x,int y, InventoryPlayer inventory){
		for(int i=0;i<9;i++){
			this.addSlotToContainer(new Slot(inventory, i, x+i*18, y+58));
		}

		for(int i=0;i<3;i++){
			for(int j=0;j<9;j++){
				this.addSlotToContainer(new Slot(inventory, j+i*9+9, x+j*18, y+i*18));
			}
		}
	}
}
