package infiniteinvo.item;

import infiniteinvo.inventory.BigInventoryPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLockedSlot extends Item
{
	/**
	 * These are placed in locked slots to prevent item pickups and shift clicking even when the slot is not visible
	 */
	public ItemLockedSlot()
	{
		this.setMaxStackSize(1);
		//this.setTextureName("infiniteinvo:locked_slot");
		this.setUnlocalizedName("infiniteinvo.LOCKED");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if(!(entity instanceof EntityPlayer))
		{
			stack = null;
		} else 
		{
			EntityPlayer player = (EntityPlayer)entity;
			
			if(player.inventory instanceof BigInventoryPlayer)
			{
				BigInventoryPlayer invo = (BigInventoryPlayer)player.inventory;
				if(slot < invo.getUnlockedSlots() || player.capabilities.isCreativeMode)
				{
					invo.setInventorySlotContents(slot, null);
				}
			}
		}
	}
}
