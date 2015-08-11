package mods.ocminecart.common.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class MinecartItem extends Item{
	
	protected static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem()
    {
        private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
        
        public ItemStack dispenseStack(IBlockSource iblock, ItemStack stack)
        {
            EnumFacing enumfacing = BlockDispenser.func_149937_b(iblock.getBlockMetadata());
            World world = iblock.getWorld();
            double d0 = iblock.getX() + (double)((float)enumfacing.getFrontOffsetX() * 1.125F);
            double d1 = iblock.getY() + (double)((float)enumfacing.getFrontOffsetY() * 1.125F);
            double d2 = iblock.getZ() + (double)((float)enumfacing.getFrontOffsetZ() * 1.125F);
            int i = iblock.getXInt() + enumfacing.getFrontOffsetX();
            int j = iblock.getYInt() + enumfacing.getFrontOffsetY();
            int k = iblock.getZInt() + enumfacing.getFrontOffsetZ();
            Block block = world.getBlock(i, j, k);
            double d3;

            if (BlockRailBase.func_150051_a(block))
            {
                d3 = 0.0D;
            }
            else
            {
                if (block.getMaterial() != Material.air || !BlockRailBase.func_150051_a(world.getBlock(i, j - 1, k)))
                {
                    return this.behaviourDefaultDispenseItem.dispense(iblock, stack);
                }

                d3 = -1.0D;
            }

            EntityMinecart entityminecart = ((MinecartItem)stack.getItem()).create(world, d0, d1 + d3, d2, stack);

            if (stack.hasDisplayName())
            {
                entityminecart.setMinecartName(stack.getDisplayName());
            }

            world.spawnEntityInWorld(entityminecart);
            stack.splitStack(1);
            return stack;
        }
     
		/**
         * Play the dispense sound from the specified block.
         */
        protected void playDispenseSound(IBlockSource p_82485_1_)
        {
            p_82485_1_.getWorld().playAuxSFX(1000, p_82485_1_.getXInt(), p_82485_1_.getYInt(), p_82485_1_.getZInt(), 0);
        }
    };
    
    protected MinecartItem(){
    	BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
    	this.maxStackSize = 1;
    }
    
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int xb, int yb, int zb, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if (BlockRailBase.func_150051_a(world.getBlock(xb, yb, zb)))
        {
            if (!world.isRemote)
            {
                EntityMinecart entityminecart = this.create(world, (float)xb+0.5F, (float)yb+0.5F, (float)zb+0.5F, stack);

                if (stack.hasDisplayName())
                {
                    entityminecart.setMinecartName(stack.getDisplayName());
                }

                world.spawnEntityInWorld(entityminecart);
            }

            --stack.stackSize;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public EntityMinecart create(World world, double d0, double d1, double d2, ItemStack stack) {
		return EntityMinecart.createMinecart(world, d0, d1, d2, 0);
	}
}
