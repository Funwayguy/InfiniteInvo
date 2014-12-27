package infiniteinvo.handlers;

import infiniteinvo.client.inventory.GuiBigInventory;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.inventory.BigContainerPlayer;
import infiniteinvo.inventory.BigInventoryPlayer;
import java.io.File;
import java.io.FileInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandler
{
	@SubscribeEvent
	public void onPlayerLoad(PlayerEvent.LoadFromFile event)
	{
		EntityPlayer player = event.entityPlayer;
		player.inventory = new BigInventoryPlayer(player);
		player.inventoryContainer = new BigContainerPlayer(player.inventory, !player.worldObj.isRemote, player);
		player.openContainer = player.inventoryContainer;
		
		// Reload NBT tags from file for the new inventory replacement
        NBTTagCompound nbttagcompound = null;

        try
        {
            File file1 = new File(event.playerDirectory, player.getUniqueID().toString() + ".dat");

            if (file1.exists() && file1.isFile())
            {
                nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
            }
        }
        catch (Exception exception)
        {
            InfiniteInvo.logger.warn("Failed to load player data for " + player.getCommandSenderName());
        }

        if (nbttagcompound != null)
        {
            player.inventory.readFromNBT(nbttagcompound.getTagList("Inventory", 10));
        }
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			
			if(!(player.inventory instanceof BigInventoryPlayer))
			{
				player.inventory = new BigInventoryPlayer(player);
				player.inventoryContainer = new BigContainerPlayer(player.inventory, !player.worldObj.isRemote, player);
				player.openContainer = player.inventoryContainer;
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		if(event.gui instanceof GuiInventory && !(event.gui instanceof GuiBigInventory))
		{
			event.gui = new GuiBigInventory(Minecraft.getMinecraft().thePlayer);
		}
	}
}
