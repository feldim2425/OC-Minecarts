package mods.ocminecart.common.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import li.cil.oc.common.Tier;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.minecart.ComputerCart;
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
		return ComputerCart.create(w, x, y, z, ItemComputerCart.getComponents(stack),ItemComputerCart.getTier(stack));
    }
    
    @Override
	public void getSubItems(Item item, CreativeTabs tab, List list){
		ItemStack stack = new ItemStack(item);
		ItemComputerCart.setTags(stack, null, 3);
    	list.add(stack);
	}
    
    public static ItemStack setTags(ItemStack stack, Iterable<Pair<Integer,ItemStack>> components, int tier){
    	if(stack.getItem() == ModItems.item_ComputerCart){
    		NBTTagCompound tag = new NBTTagCompound();
    		NBTTagList itemlist = new NBTTagList();
    		Iterator<Pair<Integer,ItemStack>> list = (components!=null) ? components.iterator() : new ArrayList<Pair<Integer,ItemStack>>().iterator();
    		
    		while(list.hasNext()){
    			Pair<Integer,ItemStack> p = list.next();
    			NBTTagCompound invslot = new NBTTagCompound();
    			NBTTagCompound item = new NBTTagCompound();
    			ItemStack buf = p.getRight();
    			invslot.setInteger("slot", p.getLeft());
    			if(buf!=null){
    				buf.writeToNBT(item);
    				invslot.setTag("item", item);
    			}
    			itemlist.appendTag(invslot);
    		}
    		tag.setTag("componentinv", itemlist);
    		tag.setInteger("Tier", tier);
    		stack.setTagCompound(tag);
    		
    		return stack;
    	}
    	
    	return null;
    }
    
    public static Iterable<Pair<Integer,ItemStack>> getComponents(ItemStack stack){
    	ArrayList<Pair<Integer,ItemStack>> components = new ArrayList<Pair<Integer,ItemStack>>();
    	if(stack.getItem() == ModItems.item_ComputerCart){
    		NBTTagCompound tag = stack.getTagCompound();
    		if(tag!=null && tag.hasKey("componentinv")){
    			NBTTagList list=(NBTTagList) tag.getTag("componentinv");
    			for(int i=0;i<list.tagCount();i+=1){
    				NBTTagCompound invslot = list.getCompoundTagAt(i);
    				components.add(Pair.of(invslot.getInteger("slot"), ItemStack.loadItemStackFromNBT(invslot.getCompoundTag("item"))));
    			}
    		}
    	}
    	return components;
    }
    
    public static int getTier(ItemStack stack){
    	if(stack.getItem() == ModItems.item_ComputerCart){
    		NBTTagCompound tag = stack.getTagCompound();
    		if(tag!=null && tag.hasKey("Tier")){
    			return tag.getInteger("Tier");
    		}
    	}
    	return Tier.None();
    }
    
    public String getItemStackDisplayName(ItemStack stack){
    	EnumChatFormatting color;
    	String tier;
    	switch(ItemComputerCart.getTier(stack)){
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
    		color = EnumChatFormatting.WHITE;
    		tier = "";
    		break;
    	}
    	return color+super.getItemStackDisplayName(stack)+" "+tier;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
    	super.addInformation(stack, player, list, adv);
    	if(!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())){
    		String key = GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
    		String formkey = "[" + EnumChatFormatting.WHITE + key + EnumChatFormatting.GRAY + "]";
    		list.add(StatCollector.translateToLocalFormatted("tooltip."+OCMinecart.MODID+".viewcomponents", formkey));
    	}
    	else{
    		list.add(StatCollector.translateToLocal("tooltip."+OCMinecart.MODID+".instcomponents")+":");
    		Iterator<Pair<Integer,ItemStack>> components = ItemComputerCart.getComponents(stack).iterator();
    		while(components.hasNext()){
    			list.add("- "+components.next().getRight().getDisplayName());
    		}
    	}
    }
}



