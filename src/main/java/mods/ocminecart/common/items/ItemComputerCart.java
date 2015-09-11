package mods.ocminecart.common.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import li.cil.oc.common.Tier;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.minecart.ComputerCart;
import mods.ocminecart.common.util.ComputerCartData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemComputerCart extends MinecartItem{

    private static final String __OBFID = "CL_00000049";

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
		//I will add a prebuild creative cart, but not yet. :P
	}
    
    public static ComputerCartData getData(ItemStack stack){
    	if(stack.getItem() instanceof ItemComputerCart){
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
    	switch(data.getTier()){
    	case 0:
    		color = EnumChatFormatting.WHITE;
    		tier = "(Tier 1)";
    		break;
    	case 1:
    		color = EnumChatFormatting.YELLOW;
    		tier = "(Tier 2)";
    		break;
    	case 2:
    		color = EnumChatFormatting.AQUA;
    		tier = "(Tier 3)";
    		break;
    	case 3:
    		color = EnumChatFormatting.LIGHT_PURPLE;
    		tier = "(Creative)";
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
    	
    	String eloc = StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".storedenergy");
    	list.add(EnumChatFormatting.WHITE+eloc+": "+EnumChatFormatting.GREEN+data.getEnergy());
    	
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
}



