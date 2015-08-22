package mods.ocminecart.common.items;

import java.util.List;

import mods.ocminecart.OCMinecart;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ComputerCartCase extends Item{
	
	private IIcon t1case;
	private IIcon t2case;
	private IIcon t3case;
	private IIcon t4case;
	
	ComputerCartCase(){
		super();
		this.setHasSubtypes(true);
		this.setUnlocalizedName(OCMinecart.MODID+".computercartcase");
		this.setMaxStackSize(1);
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        switch(damage){
        case 0: return t1case;
        case 1: return t2case;
        case 2: return t3case;
        case 3: return t4case; //reserved for creative case
        default: return null;
        }
    }
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register){
		t1case = register.registerIcon(OCMinecart.MODID+":computercartcase_1");
		t2case = register.registerIcon(OCMinecart.MODID+":computercartcase_2");
		t3case = register.registerIcon(OCMinecart.MODID+":computercartcase_3");
		t4case = register.registerIcon(OCMinecart.MODID+":computercartcase_4");
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(int i=0;i<=3;i+=1){
			list.add(new ItemStack(item,1,i));
		}
	}
	
	public String getUnlocalizedName(ItemStack stack){
		return "item."+OCMinecart.MODID + ".computercartcase_"+stack.getItemDamage();
	}
	
	public String getItemStackDisplayName(ItemStack stack)
    {
		EnumChatFormatting color = EnumChatFormatting.RESET;
		switch(stack.getItemDamage()){
		case 0:	//Tier 1
			color = EnumChatFormatting.WHITE;
			break;
		case 1: //Tier 2
			color = EnumChatFormatting.YELLOW;
			break;
		case 2:  //Tier 3
			color = EnumChatFormatting.AQUA;
			break;
		case 3://Creative (NOT IMPLEMENTED YET)
			color = EnumChatFormatting.LIGHT_PURPLE;
		}
        return color + super.getItemStackDisplayName(stack);
    }
}
