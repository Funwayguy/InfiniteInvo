package infiniteinvo.item;

import infiniteinvo.achievements.InvoAchievements;
import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.handlers.EventHandler;
import infiniteinvo.network.InvoPacket;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemUnlockSlot extends Item
{
	public ItemUnlockSlot()
	{
		this.setUnlocalizedName("infiniteinvo.unlock");
		//this.setTextureName("infiniteinvo:unlock_slot");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(world.isRemote)
        {
			return stack;
        }
        
		int unlocked = player.getEntityData().getInteger("INFINITE_INVO_UNLOCKED");
		
		if(!player.capabilities.isCreativeMode)
		{
			stack.stackSize--;
		}
		
		unlocked++;
		player.getEntityData().setInteger("INFINITE_INVO_UNLOCKED", unlocked);
		
		EventHandler.unlockCache.put(player.getCommandSenderName(), unlocked);
		
		if(unlocked > 0 || !II_Settings.xpUnlock)
		{
			player.addStat(InvoAchievements.unlockFirst, 1);
		}
		
		if(unlocked + II_Settings.unlockedSlots == II_Settings.invoSize || !II_Settings.xpUnlock)
		{
			player.addStat(InvoAchievements.unlockAll, 1);
		}
		
		if(player instanceof EntityPlayerMP)
		{
			NBTTagCompound replyTags = new NBTTagCompound();
			replyTags.setInteger("ID", 0);
			replyTags.setString("Player", player.getCommandSenderName());
			replyTags.setInteger("Unlocked", unlocked);
			InfiniteInvo.instance.network.sendTo(new InvoPacket(replyTags), (EntityPlayerMP)player);
		}
		
        world.playSoundAtEntity(player, "random.levelup", 1F, 1F);
        
        return stack;
    }
}
