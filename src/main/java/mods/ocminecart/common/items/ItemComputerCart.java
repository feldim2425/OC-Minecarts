package mods.ocminecart.common.items;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.API;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.items.interfaces.IComponentInventoryItem;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.util.ComputerCartData;
import mods.railcraft.client.emblems.Emblem;
import mods.railcraft.client.emblems.EmblemToolsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemComputerCart extends MinecartItem implements IComponentInventoryItem{

    private static final String __OBFID = "CL_00000049";
    public static final int SLOT_ROM = 18;
    
    public ItemComputerCart()
    {
    	super();
        this.setUnlocalizedName(OCMinecart.MODID+".itemcomputercart");
        this.setTextureName(OCMinecart.MODID+":computercartcase_1"); //random texture to prevent errors. You should not see this Icon because 3D Renderer.
        this.setHasSubtypes(true);
    }
    
    public EntityMinecart create(World w, double x,double y,double z,ItemStack stack){
		return ComputerCart.create(w, x, y, z, ItemComputerCart.getData(stack));
    }
    
    @Override
	public void getSubItems(Item item, CreativeTabs tab, List list){
    	if(tab!=OCMinecart.itemGroup || item!=ModItems.item_ComputerCart) return;
		ItemStack stack = new ItemStack(ModItems.item_ComputerCart,1);
		ComputerCartData data = new ComputerCartData();
		HashMap<Integer,ItemStack> inv = new HashMap<Integer,ItemStack>();
		//Container
		inv.put(0, API.items.get("cardContainer3").createItemStack(1)); 
		inv.put(1, API.items.get("upgradeContainer3").createItemStack(1));
		inv.put(2, API.items.get("diskDrive").createItemStack(1));
		//Upgrades
		inv.put(3, API.items.get("inventoryUpgrade").createItemStack(1)); 
		inv.put(4, API.items.get("inventoryUpgrade").createItemStack(1));
		inv.put(5, API.items.get("inventoryUpgrade").createItemStack(1));
		inv.put(6, API.items.get("inventoryUpgrade").createItemStack(1));
		inv.put(7, API.items.get("inventoryControllerUpgrade").createItemStack(1));
		inv.put(8, API.items.get("tankUpgrade").createItemStack(1));
		inv.put(9, API.items.get("tankControllerUpgrade").createItemStack(1));
		inv.put(10, API.items.get("screen1").createItemStack(1));
		inv.put(11, API.items.get("keyboard").createItemStack(1));
		//Cards
		inv.put(12, API.items.get("internetCard").createItemStack(1));
		inv.put(13, API.items.get("wlanCard").createItemStack(1));
		inv.put(14, null);
		//CPU - APU
		inv.put(15, API.items.get("apuCreative").createItemStack(1));
		//RAM
		inv.put(16, API.items.get("ram6").createItemStack(1));
		inv.put(17, API.items.get("ram6").createItemStack(1));
		//EEPROM
		inv.put(18, API.items.get("luaBios").createItemStack(1));
		//HDD
		inv.put(19, API.items.get("hdd3").createItemStack(1));
		data.setComponents(inv);
		data.setTier(3);
		data.setEnergy(20000);
		setData(stack,data);
		list.add(stack);
	}
    
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int xb, int yb, int zb, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_){
    	if(!world.isRemote && getData(stack)==null){
    		player.inventory.setInventorySlotContents(player.inventory.currentItem , null);
    		player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Sorry! Removed the invalid item"));
    		return false;
    	}
    	else{
    		return super.onItemUse(stack, player, world, xb, yb, zb, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_);
    	}
    }
    
    public static ComputerCartData getData(ItemStack stack){
    	if((stack.getItem() instanceof ItemComputerCart) && stack.hasTagCompound()){
    		ComputerCartData data = new ComputerCartData();
    		NBTTagCompound nbt = stack.getTagCompound();
    		if(nbt.hasKey("cartdata")){
    			data.loadItemData(nbt.getCompoundTag("cartdata"));
    		}
    		return data;
    	}
    	return null;
    }
    
    public static void setData(ItemStack stack,ComputerCartData data){
    	if(stack.getItem() instanceof ItemComputerCart){
    		NBTTagCompound nbt = (stack.hasTagCompound()) ? stack.getTagCompound() : new NBTTagCompound();
    		if(data !=null){
    			NBTTagCompound tag = new NBTTagCompound();
    			data.saveItemData(tag);
    			nbt.setTag("cartdata", tag);
    		}
    		if(!stack.hasTagCompound())stack.setTagCompound(nbt);
    	}
    }
    
    public String getItemStackDisplayName(ItemStack stack){
    	return this.getDisplayString(stack, false);
    }
    
    public String getDisplayString(ItemStack stack,boolean hasColor){
    	EnumChatFormatting color;
    	String tier;
    	ComputerCartData data = getData(stack);
    	tier=StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".tier"+(data.getTier()+1));
    	switch((data!=null) ? data.getTier() : -1){
    	case 0:
    		color = EnumChatFormatting.WHITE;
    		break;
    	case 1:
    		color = EnumChatFormatting.YELLOW;
    		break;
    	case 2:
    		color = EnumChatFormatting.AQUA;
    		break;
    	case 3:
    		color = EnumChatFormatting.LIGHT_PURPLE;
    		break;
    	default:
    		color = EnumChatFormatting.DARK_RED;
    		tier = "ERROR!";
    		break;
    	}
    	if(!hasColor) color = EnumChatFormatting.RESET;
    	return color+super.getItemStackDisplayName(stack)+" "+tier;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
    	super.addInformation(stack, player, list, adv);
    	
    	list.clear();
    	list.add(this.getDisplayString(stack, true));
    	
    	ComputerCartData data = getData(stack);
    	
    	if(data==null) return ;
    	
    	String eloc = StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".storedenergy");
    	list.add(EnumChatFormatting.WHITE+eloc+": "+EnumChatFormatting.GREEN+String.format("%.0f", data.getEnergy()));
    	
    	String emblemid = data.getEmblem();
    	if(emblemid!=null && emblemid!="" && Loader.isModLoaded("Railcraft"))
    	{
    		Emblem emblem = EmblemToolsClient.packageManager.getEmblem(emblemid);
    		if(emblem!=null){
    			list.add(EnumChatFormatting.GOLD+StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".emblem")+
    					EnumChatFormatting.WHITE+" "+emblem.displayName);
    		}
    	}
    	
    	if(!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())){
    		String key = GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
    		String formkey = "[" + EnumChatFormatting.WHITE + key + EnumChatFormatting.GRAY + "]";
    		list.add(StatCollector.translateToLocalFormatted("tooltip."+OCMinecart.MODID+".viewcomponents", formkey));
    	}
    	else{
    		list.add(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".instcomponents")+":");
    		Iterator<Entry<Integer, ItemStack>> components = data.getComponents().entrySet().iterator();
    		while(components.hasNext()){
    			list.add("- "+components.next().getValue().getDisplayName());
    		}
    	}
    }

	@Override
	public void setComponentList(ItemStack stack, Map<Integer, ItemStack> components) {
		ComputerCartData data = ItemComputerCart.getData(stack);
		if(data!=null){
			data.setComponents(components);
			ItemComputerCart.setData(stack, data);
		}
	}

	@Override
	public Map<Integer, ItemStack> getComponentList(ItemStack stack) {
		ComputerCartData data = ItemComputerCart.getData(stack);
		if(data!=null){
			return data.getComponents();
		}
		return null;
	}
}



