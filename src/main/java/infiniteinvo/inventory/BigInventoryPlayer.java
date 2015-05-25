package infiniteinvo.inventory;

import infiniteinvo.core.II_Settings;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BigInventoryPlayer extends InventoryPlayer
{
    @SideOnly(Side.CLIENT)
    private ItemStack currentItemStack;
    
	public BigInventoryPlayer(EntityPlayer player)
	{
		super(player);
		this.mainInventory = new ItemStack[MathHelper.clamp_int(II_Settings.invoSize, 27, Integer.MAX_VALUE - 100) + 9];
		
		if(player.inventory != null)
		{
			ItemStack[] oldMain = player.inventory.mainInventory;
			ItemStack[] oldArmor = player.inventory.armorInventory;
			
			for(int i = 0; i < this.mainInventory.length && i < oldMain.length; i++)
			{
				this.mainInventory[i] = oldMain[i];
			}
			
			this.armorInventory = oldArmor;
		}
	}
	
	@Override
	public void dropAllItems()
	{
		super.dropAllItems();
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) // TODO: Creative inventory middle click fix
    {
		/*if(this.player.capabilities.isCreativeMode && this.player.worldObj.isRemote)
		{
            Minecraft.getMinecraft().playerController.sendSlotPacket(stack, slot);
		}*/
		
		super.setInventorySlotContents(slot, stack);
    }
	
	public int getUnlockedSlots()
	{
		int unlocked = II_Settings.xpUnlock && !this.player.capabilities.isCreativeMode? II_Settings.unlockedSlots + 9 + this.player.getEntityData().getInteger("INFINITE_INVO_UNLOCKED") : this.mainInventory.length;
		
		unlocked = unlocked <= this.mainInventory.length? unlocked : this.mainInventory.length;
		
		return unlocked;
	}
	
    private int func_146029_c(Item p_146029_1_)
    {
        for (int i = 0; i < this.getUnlockedSlots(); ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == p_146029_1_)
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * stores an itemstack in the users inventory
     */
    private int storeItemStack(ItemStack p_70432_1_)
    {
        for (int i = 0; i < this.getUnlockedSlots(); ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == p_70432_1_.getItem() && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getItemDamage() == p_70432_1_.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i], p_70432_1_))
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the first item stack that is empty.
     */
    @Override
    public int getFirstEmptyStack()
    {
        for (int i = 0; i < this.getUnlockedSlots(); ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    private int func_146024_c(Item p_146024_1_, int p_146024_2_)
    {
        for (int j = 0; j < this.getUnlockedSlots(); ++j)
        {
            if (this.mainInventory[j] != null && this.mainInventory[j].getItem() == p_146024_1_ && this.mainInventory[j].getItemDamage() == p_146024_2_)
            {
                return j;
            }
        }

        return -1;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void func_146030_a(Item p_146030_1_, int p_146030_2_, boolean p_146030_3_, boolean p_146030_4_)
    {
        this.currentItemStack = this.getCurrentItem();
        int k;

        if (p_146030_3_)
        {
            k = this.func_146024_c(p_146030_1_, p_146030_2_);
        }
        else
        {
            k = this.func_146029_c(p_146030_1_);
        }

        if (k >= 0 && k < 9)
        {
            this.currentItem = k;
        }
        else
        {
            if (p_146030_4_ && p_146030_1_ != null)
            {
                int j = this.getFirstEmptyStack();

                if (j >= 0 && j < 9)
                {
                    this.currentItem = j;
                }

                this.func_70439_a(p_146030_1_, p_146030_2_);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void func_70439_a(Item p_70439_1_, int p_70439_2_)
    {
        if (p_70439_1_ != null)
        {
            if (this.currentItemStack != null && this.currentItemStack.isItemEnchantable() && this.func_146024_c(this.currentItemStack.getItem(), this.currentItemStack.getItemDamageForDisplay()) == this.currentItem)
            {
                return;
            }

            int j = this.func_146024_c(p_70439_1_, p_70439_2_);

            if (j >= 0)
            {
                int k = this.mainInventory[j].stackSize;
                this.mainInventory[j] = this.mainInventory[this.currentItem];
                this.mainInventory[this.currentItem] = new ItemStack(p_70439_1_, k, p_70439_2_);
            }
            else
            {
                this.mainInventory[this.currentItem] = new ItemStack(p_70439_1_, 1, p_70439_2_);
            }
        }
    }

    /**
     * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of
     * left over items.
     */
    private int storePartialItemStack(ItemStack p_70452_1_)
    {
        Item item = p_70452_1_.getItem();
        int i = p_70452_1_.stackSize;
        int j;

        if (p_70452_1_.getMaxStackSize() == 1)
        {
            j = this.getFirstEmptyStack();

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.mainInventory[j] == null)
                {
                    this.mainInventory[j] = ItemStack.copyItemStack(p_70452_1_);
                }

                return 0;
            }
        }
        else
        {
            j = this.storeItemStack(p_70452_1_);

            if (j < 0)
            {
                j = this.getFirstEmptyStack();
            }

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.mainInventory[j] == null)
                {
                    this.mainInventory[j] = new ItemStack(item, 0, p_70452_1_.getItemDamage());

                    if (p_70452_1_.hasTagCompound())
                    {
                        this.mainInventory[j].setTagCompound((NBTTagCompound)p_70452_1_.getTagCompound().copy());
                    }
                }

                int k = i;

                if (i > this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize)
                {
                    k = this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize;
                }

                if (k > this.getInventoryStackLimit() - this.mainInventory[j].stackSize)
                {
                    k = this.getInventoryStackLimit() - this.mainInventory[j].stackSize;
                }

                if (k == 0)
                {
                    return i;
                }
                else
                {
                    i -= k;
                    this.mainInventory[j].stackSize += k;
                    this.mainInventory[j].animationsToGo = 5;
                    return i;
                }
            }
        }
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    @SuppressWarnings("rawtypes")
	@Override
    public boolean addItemStackToInventory(final ItemStack p_70441_1_)
    {
        if (p_70441_1_ != null && p_70441_1_.stackSize != 0 && p_70441_1_.getItem() != null)
        {
            try
            {
                int i;

                if (p_70441_1_.isItemDamaged())
                {
                    i = this.getFirstEmptyStack();

                    if (i >= 0)
                    {
                        this.mainInventory[i] = ItemStack.copyItemStack(p_70441_1_);
                        this.mainInventory[i].animationsToGo = 5;
                        p_70441_1_.stackSize = 0;
                        return true;
                    }
                    else if (this.player.capabilities.isCreativeMode)
                    {
                        p_70441_1_.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    do
                    {
                        i = p_70441_1_.stackSize;
                        p_70441_1_.stackSize = this.storePartialItemStack(p_70441_1_);
                    }
                    while (p_70441_1_.stackSize > 0 && p_70441_1_.stackSize < i);

                    if (p_70441_1_.stackSize == i && this.player.capabilities.isCreativeMode)
                    {
                        p_70441_1_.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return p_70441_1_.stackSize < i;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(p_70441_1_.getItem())));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(p_70441_1_.getItemDamage()));
                crashreportcategory.addCrashSectionCallable("Item name", new Callable()
                {
                    public String call()
                    {
                        return p_70441_1_.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Modified from the original to allow for more than 255 inventory slots
     */
    public NBTTagList writeToNBT(NBTTagList p_70442_1_)
    {
        int i;
        NBTTagCompound nbttagcompound;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("Slot", i);
                this.mainInventory[i].writeToNBT(nbttagcompound);
                p_70442_1_.appendTag(nbttagcompound);
            }
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("Slot", i + (Integer.MAX_VALUE - 100)); // Give armor slots the last 100 integer spaces
                this.armorInventory[i].writeToNBT(nbttagcompound);
                p_70442_1_.appendTag(nbttagcompound);
            }
        }
        
        return p_70442_1_;
    }

    /**
     * Modified from the original to allow for more than 255 inventory slots
     */
	@Override
    public void readFromNBT(NBTTagList p_70443_1_)
    {
        this.mainInventory = new ItemStack[MathHelper.clamp_int(II_Settings.invoSize, 27, Integer.MAX_VALUE - 100) + 9];
        this.armorInventory = new ItemStack[4];
        
        for (int i = 0; i < p_70443_1_.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = p_70443_1_.getCompoundTagAt(i);
            int j = nbttagcompound.getInteger("Slot");
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 0 && j < this.mainInventory.length)
                {
            		this.mainInventory[j] = itemstack;
                }

                if (j >= (Integer.MAX_VALUE - 100) && j <= Integer.MAX_VALUE)
                {
            		this.mainInventory[j] = itemstack;
                }
            }
        }
    }
}
