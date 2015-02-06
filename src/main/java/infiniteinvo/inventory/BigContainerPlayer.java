package infiniteinvo.inventory;

import infiniteinvo.core.II_Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class BigContainerPlayer extends ContainerPlayer
{
	public int scrollPos = 0;
	public BigInventoryPlayer invo;
	/**
	 * A more organised version of 'inventorySlots' that doesn't include the hotbar
	 */
	Slot[] slots = new Slot[MathHelper.clamp_int(II_Settings.invoSize, 27, Integer.MAX_VALUE - 100)];
	Slot[] hotbar = new Slot[9];
	Slot[] crafting = new Slot[4];
	Slot result;
	
	public BigContainerPlayer(BigInventoryPlayer invo, boolean isLocal, EntityPlayer player)
	{
		super(invo, isLocal, player);
		this.invo = (BigInventoryPlayer)invo;
		
		for(int i = 9; i < 36; i++)
		{
			// Add all the previous inventory slots to the organised array
			 Slot os = (Slot)this.inventorySlots.get(i);
			 
			 Slot ns = new SlotLockable(os.inventory, os.getSlotIndex(), os.xDisplayPosition, os.yDisplayPosition);
			 ns.slotNumber = os.slotNumber;
			 this.inventorySlots.set(i, ns);
			 ns.onSlotChanged();
			 slots[i - 9] = ns;
		}
		
		for(int i = 36; i < 45; i++)
		{
			// Get the hotbar for repositioning
			hotbar[i - 36] = (Slot)this.inventorySlots.get(i);
		}
		
		for(int i = 1; i < 5; i++)
		{
			crafting[i - 1] = (Slot)this.inventorySlots.get(i);
		}
		
		result = (Slot)this.inventorySlots.get(0);
		result.xDisplayPosition = 144;
		result.yDisplayPosition = 53;
		
		for(int i = 0; i < 4; i++)
		{
			Slot hs = crafting[i];
			hs.xDisplayPosition = 88 + ((i%2) * 18);
			hs.yDisplayPosition = 43 + ((i/2) * 18);
		}
		
		for(int i = 0; i < 9; i++)
		{
			Slot hs = hotbar[i];
			hs.xDisplayPosition = 8 + (i * 18);
			hs.yDisplayPosition = 142 + (18 * II_Settings.extraRows);
		}

        for (int i = 3; i < MathHelper.ceiling_float_int((float)II_Settings.invoSize/9F); ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
            	if(j + (i * 9) >= II_Settings.invoSize && II_Settings.invoSize > 27)
            	{
            		break;
            	} else
            	{
            		// Moved off screen to avoid interaction until screen scrolls over the row
            		Slot ns = new SlotLockable(invo, j + (i + 1) * 9, -999, -999);
            		slots[j + (i * 9)] = ns;
            		this.addSlotToContainer(ns);
            	}
            }
        }
        
        this.UpdateScroll();
	}
	
	public void UpdateScroll()
	{
		if(scrollPos > MathHelper.ceiling_float_int((float)II_Settings.invoSize/(float)(9 + II_Settings.extraColumns)) - (3 + II_Settings.extraRows))
		{
			scrollPos = MathHelper.ceiling_float_int((float)II_Settings.invoSize/(float)(9 + II_Settings.extraColumns)) - (3 + II_Settings.extraRows);
		}
		
		if(scrollPos < 0)
		{
			scrollPos = 0;
		}
		
		for(int i = 0; i < MathHelper.ceiling_float_int((float)MathHelper.clamp_int(II_Settings.invoSize, 27, Integer.MAX_VALUE)/(float)(9 + II_Settings.extraColumns)); i++)
		{
            for (int j = 0; j < 9 + II_Settings.extraColumns; ++j)
            {
            	int index = j + (i * (9 + II_Settings.extraColumns));
            	if(index >= II_Settings.invoSize && index >= 27)
            	{
            		break;
            	} else
            	{
            		if(i >= scrollPos && i < scrollPos + 3 + II_Settings.extraRows && index < invo.getUnlockedSlots() - 9 && index < II_Settings.invoSize)
            		{
            			Slot s = slots[index];
            			s.xDisplayPosition = 8 + j * 18;
            			s.yDisplayPosition = 84 + (i - scrollPos) * 18;
            		} else
            		{
            			Slot s = slots[index];
            			s.xDisplayPosition = -999;
            			s.yDisplayPosition = -999;
            		}
            	}
            }
		}
	}

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
	@Override
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
		int vLocked = invo.getUnlockedSlots() < 36? 36 - invo.getUnlockedSlots() : 0;
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ == 0) // Crafting result
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (p_82846_2_ >= 1 && p_82846_2_ < 5) // Crafting grid
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, false))
                {
                    return null;
                }
            }
            else if (p_82846_2_ >= 5 && p_82846_2_ < 9) // Armor
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack()) // Inventory to armor
            {
                int j = 5 + ((ItemArmor)itemstack.getItem()).armorType;

                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if ((p_82846_2_ >= 9 && p_82846_2_ < 36) || (p_82846_2_ >= 45 && p_82846_2_ < invo.getUnlockedSlots() + 9))
            {
                if (!this.mergeItemStack(itemstack1, 36, 45, false))
                {
                    return null;
                }
            }
            else if (p_82846_2_ >= 36 && p_82846_2_ < 45) // Hotbar
            {
                if (!this.mergeItemStack(itemstack1, 9, 36 - vLocked, false) && (invo.getUnlockedSlots() - 36 <= 0 || !this.mergeItemStack(itemstack1, 45, 45 + (invo.getUnlockedSlots() - 36), false)))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 9, invo.getUnlockedSlots() + 9, false)) // Full range
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }

        return itemstack;
    }
}