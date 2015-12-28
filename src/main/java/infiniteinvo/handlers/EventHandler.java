package infiniteinvo.handlers;

import infiniteinvo.achievements.InvoAchievements;
import infiniteinvo.client.inventory.GuiBigInventory;
import infiniteinvo.client.inventory.GuiButtonUnlockSlot;
import infiniteinvo.client.inventory.InvoScrollBar;
import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.inventory.BigContainerPlayer;
import infiniteinvo.inventory.BigInventoryPlayer;
import infiniteinvo.inventory.InventoryPersistProperty;
import infiniteinvo.network.InvoPacket;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class EventHandler
{
	public static File worldDir;
	public static HashMap<String, Integer> unlockCache = new HashMap<String, Integer>();
	public static HashMap<String, Container> lastOpened = new HashMap<String, Container>();
	
	@SubscribeEvent
	public void onEntityConstruct(EntityConstructing event) // More reliable than on entity join
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			
			if(InventoryPersistProperty.get(player) == null)
			{
				InventoryPersistProperty.Register(player);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			
			if(InventoryPersistProperty.get(player) != null)
			{
				InventoryPersistProperty.get(player).onJoinWorld();
			}
			
			if(event.world.isRemote)
			{
				NBTTagCompound requestTags = new NBTTagCompound();
				requestTags.setInteger("ID", 1);
				requestTags.setInteger("World", event.world.provider.getDimensionId());
				requestTags.setString("Player", player.getCommandSenderName());
				InfiniteInvo.instance.network.sendToServer(new InvoPacket(requestTags));
			} else
			{
				II_Settings.LoadFromCache();
			}
		} else if(event.entity instanceof EntityItem)
		{
			EntityItem itemDrop = (EntityItem)event.entity;
			
			if(itemDrop.getEntityItem() != null && itemDrop.getEntityItem().getItem() == InfiniteInvo.locked)
			{
				itemDrop.setDead();
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event)
	{
		if(event.entityItem.getEntityItem() != null)
		{
			if(Item.itemRegistry.getNameForObject(event.entityItem.getEntityItem().getItem()).equals("exnihilo:silkworm"))
			{
				event.player.addStat(InvoAchievements.wormDrops, 1);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent event)
	{
		if(event.pickedUp != null && event.pickedUp.getEntityItem() != null && event.pickedUp.getEntityItem().getItem() == Items.bone && !event.pickedUp.worldObj.isRemote)
		{
			if(!event.player.getCommandSenderName().equals(event.pickedUp.getThrower()));
			{
				if(event.pickedUp.getThrower() == null || event.pickedUp.getThrower().isEmpty())
				{
					return;
				}
				
				EntityPlayer player = event.pickedUp.worldObj.getPlayerEntityByName(event.pickedUp.getThrower());
				
				if(player != null)
				{
					player.addStat(InvoAchievements.boneSanta, 1);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityLiving(LivingUpdateEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
			boolean flag = true;
			for(int i = 9; i < player.inventory.mainInventory.length; i++)
			{
				ItemStack stack = player.inventory.mainInventory[i];
				
				if(player.inventory instanceof BigInventoryPlayer && (i >= ((BigInventoryPlayer)player.inventory).getUnlockedSlots() || i - 9 >= II_Settings.invoSize) && !event.entityLiving.worldObj.isRemote && !player.capabilities.isCreativeMode)
				{
					if(stack != null && stack.getItem() != InfiniteInvo.locked)
					{
						player.entityDropItem(stack.copy(), 0);
						player.inventory.setInventorySlotContents(i, null);
						player.inventory.markDirty();
						stack = null;
					}
					
					if(stack == null)
					{
						player.inventory.setInventorySlotContents(i, new ItemStack(InfiniteInvo.locked));
						player.inventory.markDirty();
					}
					flag = false;
					continue;
				}
				
				if(stack != null && stack.getItem() == Items.cooked_porkchop && stack.stackSize >= stack.getMaxStackSize())
				{
					continue;
				} else
				{
					flag = false;
				}
			}
			
			if(!event.entityLiving.isEntityAlive())
			{
				if(!II_Settings.keepUnlocks && !event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
				{
					unlockCache.remove(event.entityLiving.getCommandSenderName());
					unlockCache.remove(event.entityLiving.getUniqueID().toString());
				}
			}
			
			if(flag)
			{
				player.addStat(InvoAchievements.bacon, 1);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
		{
			if(!II_Settings.keepUnlocks && !event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
			{
				unlockCache.remove(event.entityLiving.getCommandSenderName());
				unlockCache.remove(event.entityLiving.getUniqueID().toString());
			}
			
			if(!event.entityLiving.worldObj.isRemote && event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
			{
				InventoryPersistProperty.keepInvoCache.put(event.entityLiving.getUniqueID(), ((EntityPlayer)event.entityLiving).inventory.writeToNBT(new NBTTagList()));
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		if(event.gui != null && event.gui.getClass() == GuiInventory.class && !(event.gui instanceof GuiBigInventory))
		{
			event.gui = new GuiBigInventory(Minecraft.getMinecraft().thePlayer);
		} else if(event.gui == null && Minecraft.getMinecraft().thePlayer.inventoryContainer instanceof BigContainerPlayer)
		{
			// Reset scroll and inventory slot positioning to make sure it doesn't screw up later
			((BigContainerPlayer)Minecraft.getMinecraft().thePlayer.inventoryContainer).scrollPos = 0;
			((BigContainerPlayer)Minecraft.getMinecraft().thePlayer.inventoryContainer).UpdateScroll();
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseInput(MouseInputEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;
		KeyBinding pickBlock = mc.gameSettings.keyBindPickBlock;
		
		if(pickBlock.isPressed() && mc.objectMouseOver != null && II_Settings.invoSize > 27)
		{
			KeyBinding.setKeyBindState(pickBlock.getKeyCode(), false);
			
			if (!net.minecraftforge.common.ForgeHooks.onPickBlock(mc.objectMouseOver, player, mc.theWorld))
			{
				return;
			}
			
			if(player.capabilities.isCreativeMode)
			{
                int j = 36 + player.inventory.currentItem;
                mc.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), j);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiPostInit(InitGuiEvent.Post event)
	{
		if(event.gui instanceof GuiBigInventory)
		{
			((GuiBigInventory)event.gui).redoButtons = true;
		} else if(event.gui instanceof GuiContainer)
		{
			GuiContainer gui = (GuiContainer)event.gui;
			Container container = gui.inventorySlots;
			
			event.buttonList.add(new InvoScrollBar(256, 0, 0, 1, 1, "", container, gui));
			
			if(event.gui instanceof GuiInventory)
			{
				final ScaledResolution scaledresolution = new ScaledResolution(event.gui.mc);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
				event.buttonList.add(new GuiButtonUnlockSlot(event.buttonList.size(), i/2 - 50, j - 40, 100, 20, event.gui.mc.thePlayer));
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(!event.world.isRemote && worldDir == null && MinecraftServer.getServer().isServerRunning())
		{
			MinecraftServer server = MinecraftServer.getServer();
			
			if(InfiniteInvo.proxy.isClient())
			{
				worldDir = server.getFile("saves/" + server.getFolderName());
			} else
			{
				worldDir = server.getFile(server.getFolderName());
			}

			new File(worldDir, "data/").mkdirs();
			LoadCache(new File(worldDir, "data/SlotUnlockCache"));
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		if(!event.world.isRemote && worldDir != null && MinecraftServer.getServer().isServerRunning())
		{
			new File(worldDir, "data/").mkdirs();
			SaveCache(new File(worldDir, "data/SlotUnlockCache"));
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		if(!event.world.isRemote && worldDir != null && !MinecraftServer.getServer().isServerRunning())
		{
			new File(worldDir, "data/").mkdirs();
			SaveCache(new File(worldDir, "data/SlotUnlockCache"));
			
			worldDir = null;
			unlockCache.clear();
			InventoryPersistProperty.keepInvoCache.clear();
		}
	}
	
	public static void SaveCache(File file)
	{
		try
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(unlockCache);
			
			oos.close();
			fos.close();
		} catch(Exception e)
		{
			InfiniteInvo.logger.log(Level.ERROR, "Failed to save slot unlock cache", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void LoadCache(File file)
	{
		try
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			FileInputStream fis = new FileInputStream(file);
			
			if(fis.available() <= 0)
			{
				fis.close();
				return;
			}
			
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			unlockCache = (HashMap<String,Integer>)ois.readObject();
			
			ois.close();
			fis.close();
		} catch(Exception e)
		{
			InfiniteInvo.logger.log(Level.ERROR, "Failed to load slot unlock cache", e);
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID.equals(InfiniteInvo.MODID))
		{
			ConfigHandler.config.save();
			ConfigHandler.initConfigs();
		}
	}
}
