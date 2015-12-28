package infiniteinvo.inventory;

import org.apache.logging.log4j.Level;
import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLockable extends Slot
{
	public int slotIndex;
	ItemStack lockedStack;
	public SlotLockable(IInventory invo, int index, int dispX, int dispY)
	{
		super(invo, index, dispX, dispY);
		slotIndex = index;
		lockedStack = new ItemStack(InfiniteInvo.locked);
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
    {
		if(stack != null && stack.getItem() == InfiniteInvo.locked)
		{
			stack = null;
		}
		
        this.onSlotChanged();
    }
	
	@Override
	public ItemStack getStack()
    {
    	if(!(this.inventory instanceof BigInventoryPlayer))
    	{
            return this.inventory.getStackInSlot(this.slotIndex);
    	} else if(isUnlocked((BigInventoryPlayer)this.inventory))
    	{
            return this.inventory.getStackInSlot(this.slotIndex);
    	} else
    	{
    		return lockedStack.copy();
    	}
    }
	
	@Override
	public boolean isItemValid(ItemStack p_75214_1_)
    {
    	if(!(this.inventory instanceof BigInventoryPlayer))
    	{
    		InfiniteInvo.logger.log(Level.WARN, "Modified slot used for non modified inventory!");
    		return true;
    	} else if(isUnlocked((BigInventoryPlayer)this.inventory))
    	{
    		return true;
    	} else
    	{
    		return false;
    	}
    }
	
	@Override
    public boolean canTakeStack(EntityPlayer p_82869_1_)
    {
    	if(!(this.inventory instanceof BigInventoryPlayer))
    	{
    		InfiniteInvo.logger.log(Level.WARN, "Modified slot used for non modified inventory!");
    		return true;
    	} else if(isUnlocked((BigInventoryPlayer)this.inventory))
    	{
    		return true;
    	} else
    	{
    		return false;
    	}
    }

    @Override
    public void putStack(ItemStack p_75215_1_)
    {
        this.inventory.setInventorySlotContents(this.slotIndex, p_75215_1_);
        this.onSlotChanged();
    }

    @Override
    public ItemStack decrStackSize(int p_75209_1_)
    {
        return this.inventory.decrStackSize(this.slotIndex, p_75209_1_);
    }

    @Override
    public boolean isHere(IInventory p_75217_1_, int p_75217_2_)
    {
        return p_75217_1_ == this.inventory && p_75217_2_ == this.slotIndex;
    }
	
	@Override
	public int getSlotIndex()
    {
        /** The index of the slot in the inventory. */
        return slotIndex;
    }

    /**
     * Returns if this slot contains a stack.
     */
	@Override
    public boolean getHasStack()
    {
        return this.getStack() != null && (this.getStack().getItem() != InfiniteInvo.locked || !II_Settings.IT_Patch);
    }
	
	public boolean isUnlocked(BigInventoryPlayer invo)
	{
		if(invo.player.capabilities.isCreativeMode)
		{
			return true;
		} else
		{
			return invo.getUnlockedSlots() > this.slotIndex && this.slotIndex - 9 < II_Settings.invoSize;
		}
	}
}
