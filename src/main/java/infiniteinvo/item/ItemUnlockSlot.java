package infiniteinvo.item;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.network.InvoPacket;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemUnlockSlot extends Item
{
	public ItemUnlockSlot()
	{
		this.setUnlocalizedName("infiniteinvo.item.unlock");
		this.setTextureName("infiniteinvo:unlock_slot");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(world.isRemote)
        {
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger("ID", 0);
			tags.setInteger("World", player.worldObj.provider.dimensionId);
			tags.setString("Player", player.getCommandSenderName());
			tags.setInteger("InvoSize", II_Settings.invoSize);
			tags.setBoolean("UseItem", true);
			InfiniteInvo.instance.network.sendToServer(new InvoPacket(tags));
        } else
        {
            world.playSoundAtEntity(player, "random.levelup", 1F, 1F);
        }
        
        return stack;
    }
}
