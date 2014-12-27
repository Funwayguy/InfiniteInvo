package infiniteinvo.inventory;

import infiniteinvo.core.II_Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BigInventoryPlayer extends InventoryPlayer
{
	public BigInventoryPlayer(EntityPlayer player)
	{
		super(player);
		this.mainInventory = new ItemStack[II_Settings.invoSize + 9];
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
        this.mainInventory = new ItemStack[II_Settings.invoSize + 9];
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

                if (j >= 100 && j < this.armorInventory.length + 100)
                {
                    this.armorInventory[j - 100] = itemstack;
                }
            }
        }
    }
}
