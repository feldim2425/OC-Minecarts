package mods.ocminecart.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.ocminecart.OCMinecart;
import mods.ocminecart.common.util.IPlugable;
import mods.ocminecart.common.util.Plug;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.movable.IMovableTile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(iface="appeng.api.movable.IMovableTile", modid="appliedenergistics2", striprefs=true)
public class NetworkRailBaseTile extends TileEntity implements ISidedInventory, SidedEnvironment, IPlugable, Analyzable, IMovableTile{
	
	
	private Plug rail;	//Environment for the Cart
	private Plug side;	//Environment for Cables, Computers, ...
	
	private boolean firstupdate = true; //First call of updateEntity()
	
	private ItemStack camoItem = null;
	private ItemStack camoItemOld = null;

	private IIcon camoTop = null;
	
	/*
	 * 0 = Connect: Network,Power
	 * 1 = Connect: Network
	 * 2 = Connect: Power
	 */
	private int Mode = 0;
	private boolean moving=false;
	
	public NetworkRailBaseTile(){
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()){
			rail = new Plug(this);
			side = new Plug(this);
			
			rail.setNode(Network.newNode(rail, Visibility.Network).withConnector().create());
			side.setNode(Network.newNode(side,Visibility.Network).withConnector(500D).create());
		}
		this.markDirty();
	}
	
	/*---------NBT/Sync--------*/
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if(nbt.hasKey("conMode")) Mode = nbt.getInteger("conMode");
		
		camoItem=ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt.getTag("CamoItem"));
		
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()){
			 if(nbt.hasKey("Plug_1"))side.load((NBTTagCompound) nbt.getTag("Plug_1"));
			 if(nbt.hasKey("Plug_2"))rail.load((NBTTagCompound) nbt.getTag("Plug_2"));
		}
		
		this.markDirty();
	}
		
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("conMode", Mode);
		
		if(!this.worldObj.isRemote){
			NBTTagCompound plug1 = new NBTTagCompound();
			NBTTagCompound plug2 = new NBTTagCompound();
			
			side.save(plug1);
			nbt.setTag("Plug_1",plug1);
			
			rail.save(plug2);
			nbt.setTag("Plug_2", plug2);
		}
		
		NBTTagCompound item = new NBTTagCompound();
		if(camoItem!=null)camoItem.writeToNBT(item);
		nbt.setTag("CamoItem",item);
	}
	
	   @Override
	   public Packet getDescriptionPacket()
	   {
	       NBTTagCompound syncData = new NBTTagCompound();
	       this.writeSyncableDataToNBT(syncData);
	       return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	   }
	   
	   @Override
	   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	   {
	       readSyncableDataFromNBT(pkt.func_148857_g());
	   }
	
	private void writeSyncableDataToNBT(NBTTagCompound syncData){
		NBTTagCompound item = new NBTTagCompound();
		if(camoItem!=null)camoItem.writeToNBT(item);
		syncData.setTag("CamoItem",item);
	}
	
	private void readSyncableDataFromNBT(NBTTagCompound syncData) {
		camoItem=ItemStack.loadItemStackFromNBT((NBTTagCompound) syncData.getTag("CamoItem"));
		if(camoItem !=camoItemOld) updateCamo();
	}
	
	private void forcesync(){
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		this.markDirty();
	}
	
	public void updateEntity(){
		super.updateEntity();
		if (!this.worldObj.isRemote && !this.moving) {
			if(firstupdate){
				Network.joinOrCreateNetwork(this);
				Network.joinNewNetwork(this.rail.node());
				firstupdate=false;
			}
			
			if(Mode==0 || Mode ==2){
				Connector con1=(Connector)side.node();
				Connector con2=(Connector)rail.node();
				if(con2.globalBuffer()<con2.globalBufferSize()){
					double need = con2.globalBufferSize() - con2.globalBuffer();
					double provide = 0.0;
					
					if(need > 100) need = 100;
					
					provide = need + con1.changeBuffer(-need);
					con2.changeBuffer(provide);
				}
			}
			
		}
	}
	/*------END-NBT/Sync------*/
	
	/*------Tile-Update-------*/
	
	private void updateCamo(){
		if(this.worldObj.isRemote){
			camoItemOld=(camoItem!=null) ? camoItem.copy() : null;
			if(camoItem!=null){
				if(camoItem.getItem() instanceof ItemBlock){
					Block block=Block.getBlockFromItem(camoItem.getItem());
					camoTop=block.getIcon(ForgeDirection.UP.ordinal(), camoItem.getItem().getMetadata(camoItem.getItemDamage()));
				}
				else camoTop=null;
			}	
			else camoTop=null;
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}
	 
	public void onChunkUnload() {
		 super.onChunkUnload();
		 if(rail != null){
			 rail.node().remove();
		 }
		 if(side!=null){
			 side.node().remove();
		 }
	 }
	 
	public void invalidate() {
		 super.invalidate();
		 if(rail != null){
			 rail.node().remove();
		 }
		 if(side!=null){
			 side.node().remove();
		 }
	 }
	
	/*-----END-Tile-Update-----*/
	
	/*------Inventory-------*/	//way too much code for only one ghost slot ;)
	public int getSizeInventory() {
		return 1;
	}

	public ItemStack getStackInSlot(int slot) {
		if(slot==0) return camoItem;
		return null;
	}
	
	public ItemStack decrStackSize(int slot, int itemnum) {
		if(camoItem != null && slot == 0){
			ItemStack stack;
			
			if(camoItem.stackSize <= itemnum){
				stack=camoItem;
				camoItem=null;
				this.forcesync();
				return stack;
			}
			else{
				stack=camoItem.splitStack(itemnum);
				
				if(camoItem.stackSize <= 0) camoItem = null;
				this.forcesync();
				return stack;
			}
		}
		return null;
	}

	public ItemStack getStackInSlotOnClosing(int slot) {
		if(camoItem!=null && slot == 0){
			ItemStack item = camoItem;
			camoItem=null;
			return item;
		}
		return null;
	}

	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot==0 && stack!=null) {
			camoItem= stack.copy();
			
			
			if(stack.stackSize > this.getInventoryStackLimit()){
				stack.stackSize = stack.stackSize - this.getInventoryStackLimit();
			}
			this.forcesync();
		}
	}

	public String getInventoryName() {
		return StatCollector.translateToLocal("gui."+OCMinecart.MODID+".networkrailbase.title");
	}

	public boolean hasCustomInventoryName() { return false; }

	public int getInventoryStackLimit() {return 1;}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void openInventory() {}
	
	public void closeInventory() {}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(slot == 0){
			if(stack.getItem() instanceof ItemBlock)
				if(Block.getBlockFromItem(stack.getItem()).renderAsNormalBlock()) return true;
			
			return false;
		}
		return false;
	}

	public int[] getAccessibleSlotsFromSide(int p_94128_1_){ return new int[]{}; }

	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,int p_102007_3_) { return false;}

	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,int p_102008_3_) {return false;}
	
	/*-----END-Inventory----*/
	
	/*-----OC-Network------*/
	
	@Override
	public Node sidedNode(ForgeDirection side) {
		if(!side.equals(ForgeDirection.UP)) return this.side.node();
		return null;
	}
	
	@Override
	public boolean canConnect(ForgeDirection side) {
		if(!side.equals(ForgeDirection.UP)) return true;
		return false;
	}

	@Override
	public void onPlugMessage(Plug plug, Message message) {
		if(Mode==0 || Mode==1){
			if(message.name()=="network.message" && this.side.node()!= message.source() && this.rail.node()!=message.source()){
				if(plug==rail) side.node().sendToReachable("network.message", message.data());
				else if(plug==side) rail.node().sendToReachable("network.message", message.data());
			}
		}
	}

	@Override
	public void onPlugConnect(Plug plug, Node node) {
	}

	@Override
	public void onPlugDisconnect(Plug plug, Node node) {
	}

	public int getMode() {
		return this.Mode;
	}

	/*-----END-OC-Network----*/
	
	public void onButtonPress(int buttonID) {
		if(buttonID==0){
			this.Mode += 1;
			if(this.Mode > 2) this.Mode = 0;
		}
	}
	
	public void setMode(int Mode){ this.Mode=Mode; }
	
	@SideOnly(Side.CLIENT)
	public IIcon getTopIcon(){ return this.camoTop; }
	
	public boolean checkRailNode(Node node){
		if(node!=null && !node.canBeReachedFrom(this.rail.node())){
			OCMinecart.logger.info("Connected");
			node.connect(this.rail.node());
			return true;
		}
		else if(node!=null && node.network()!=null && node.canBeReachedFrom(this.rail.node())) return true;
		else return false;
	}

	public Node[] onAnalyze(EntityPlayer player, int side, float hitX, float hitY, float hitZ) { return null; }

	/*-------AE2-Spatial-Storage-Handler------*/

	public void doneMoving() {
		this.moving=false;
		Network.joinOrCreateNetwork(this);
		this.forcesync();
	}
	
	public boolean prepareToMove() {
		this.moving=true;
		return true;
	}
	
	/*-------END-AE2-Spatial-Storage-Handler------*/
	

}