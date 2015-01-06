package infiniteinvo.client.inventory;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.inventory.BigInventoryPlayer;
import infiniteinvo.inventory.SlotLockable;
import infiniteinvo.network.InvoPacket;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;

public class InvoScrollBar extends GuiButton
{
	int maxScroll = 0;
	int scrollPos = 0;
	Container container;
	GuiContainer gui;
	int guiLeft = 0;
	int guiTop = 0;
	int[][] slotPos = new int[27][2];
	Slot[] invoSlots = new Slot[27];
	int[] slotIndex = new int[27];
	
	int scrollX = 0;
	int scrollY = Integer.MAX_VALUE;
	
	public InvoScrollBar(int id, int posX, int posY, int width, int height, String title, Container container, GuiContainer gui)
	{
		super(id, posX, posY, width, height, title);
		this.container = container;
		this.gui = gui;
		
		int index = 0;
		for(int i = 0; i < container.inventorySlots.size() && index < 27; i++)
		{
			Slot s = (Slot)container.inventorySlots.get(i);
			
			if(s.inventory instanceof InventoryPlayer && s.getSlotIndex() >= 9 && s.getSlotIndex() < 36)
			{
				if(s.getClass() != Slot.class && s.getClass() != SlotLockable.class)
				{
					InfiniteInvo.logger.log(Level.WARN, "Container " + container.getClass().getSimpleName() + " is not supported by InfiniteInvo! Reason: Custom Slots are being used!");
					return;
				}
				Slot r = new SlotLockable(s.inventory, s.getSlotIndex(), s.xDisplayPosition, s.yDisplayPosition);
				
				// Replace the local slot with our own tweaked one so that locked slots are handled properly
				container.inventorySlots.set(i, r);
				r.slotNumber = i;
				s = r;
				container.inventoryItemStacks.set(i, r.getStack());
				r.onSlotChanged();
				
				invoSlots[index] = s;
				slotIndex[index] = s.getSlotIndex();
				slotPos[index][0] = s.xDisplayPosition;
				slotPos[index][1] = s.yDisplayPosition;
				
				if(s.xDisplayPosition - 1 > scrollX)
				{
					scrollX = s.xDisplayPosition - 1;
				}
				
				if(s.yDisplayPosition - 1 < scrollY)
				{
					scrollY = s.yDisplayPosition - 1;
				}
				
				index++;
			}
		}
		
		container.detectAndSendChanges();
		
		if(II_Settings.invoSize <= 27)
		{
			maxScroll = 0;
		} else
		{
			maxScroll = MathHelper.ceiling_float_int((float)(II_Settings.invoSize - 27)/9F);
		}
		
		NBTTagCompound scrollTags = new NBTTagCompound();
		scrollTags.setInteger("ID", 2);
		scrollTags.setString("Player", Minecraft.getMinecraft().thePlayer.getCommandSenderName());
		scrollTags.setInteger("World", Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId);
		scrollTags.setInteger("Scroll", 0);
		scrollTags.setIntArray("Indexes", slotIndex);
		scrollTags.setBoolean("Reset", true);
		InfiniteInvo.instance.network.sendToServer(new InvoPacket(scrollTags));
		
		UpdateGuiPos();
	}
	
	public void UpdateGuiPos()
	{
		Field f1;
		Field f2;
		
		try
		{
			f1 = GuiContainer.class.getDeclaredField("field_147003_i");
			f2 = GuiContainer.class.getDeclaredField("field_147009_r");
		} catch(Exception e1)
		{
			try
			{
				f1 = GuiContainer.class.getDeclaredField("guiLeft");
				f2 = GuiContainer.class.getDeclaredField("guiTop");
			} catch(Exception e2)
			{
				InfiniteInvo.logger.log(Level.ERROR, "Unable to get gui positioning for GUI: ", e2);
				return;
			}
		}
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		
		try
		{
			guiLeft = f1.getInt(gui);
			guiTop = f2.getInt(gui);
		} catch(Exception e)
		{
			InfiniteInvo.logger.log(Level.ERROR, "Unable to get gui positioning for GUI: ", e);
			return;
		}
	}
	
	int dragging = 0;
	
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
		int scrollDX = (int)Math.signum(Mouse.getDWheel());
		
		if(scrollDX != 0)
		{
			doScroll(scrollDX);
		} else if(Mouse.isButtonDown(0))
    	{
			Minecraft mc = Minecraft.getMinecraft();
            final ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            int mouseX = Mouse.getX() * i / mc.displayWidth;
            int mouseY = height - Mouse.getY() * j / mc.displayHeight - 1;
    		int sx = this.guiLeft + scrollX + 20;
    		int sy = this.guiTop + scrollY;
    		
    		boolean flag = mouseX >= sx && mouseY >= sy && mouseX < sx + 8 && mouseY < sy + (18 * 3);
    		
    		if((flag || dragging == 1) && dragging != -1)
    		{
    			dragging = 1;
    			int preScroll = scrollPos;
    			scrollPos = MathHelper.clamp_int(Math.round((float)(mouseY - sy) / (float)(18 * 3) * (float)maxScroll), 0, maxScroll);
    			if(scrollPos - preScroll != 0)
    			{
    				doScroll(scrollPos - preScroll);
    			}
    		} else
    		{
    			dragging = -1;
    		}
    	} else
    	{
    		dragging = 0;
    	}
		
        p_146112_1_.getTextureManager().bindTexture(new ResourceLocation("infiniteinvo", "textures/gui/adjustable_gui.png"));
        
        if(maxScroll > 0)
        {
			this.drawTexturedModalRect(this.guiLeft + scrollX + 20, this.guiTop + scrollY, 52, 166, 8, 18);
			this.drawTexturedModalRect(this.guiLeft + scrollX + 20, this.guiTop + scrollY + 18, 44, 166, 8, 18);
			this.drawTexturedModalRect(this.guiLeft + scrollX + 20, this.guiTop + scrollY + 36, 36, 166, 8, 18);
			
			this.drawTexturedModalRect(this.guiLeft + scrollX + 20, this.guiTop + scrollY + (Math.round((float)scrollPos / (float)maxScroll * 46F)), 60, 166, 8, 8);
        }
        
        if(Minecraft.getMinecraft().thePlayer.inventory instanceof BigInventoryPlayer)
        {
        	BigInventoryPlayer pinvo = (BigInventoryPlayer)Minecraft.getMinecraft().thePlayer.inventory;
        	
        	if(pinvo.getUnlockedSlots() - 9 < 27)
        	{
        		for(int i = 0; i < this.invoSlots.length; i++)
        		{
        			Slot s = this.invoSlots[i];
        			
        			if(s == null)
        			{
        				continue;
        			}
        			
        			if(s.getSlotIndex() - 9 >= II_Settings.invoSize)
        			{
        				s.xDisplayPosition = -99;
        				s.yDisplayPosition = -99;
        				this.drawTexturedModalRect(slotPos[i][0] + guiLeft - 1, slotPos[i][1] + guiTop - 1, 0, 166, 18, 18);
        			} else if(pinvo.getUnlockedSlots() <= s.getSlotIndex())
        			{
        				s.xDisplayPosition = -99;
        				s.yDisplayPosition = -99;
        				this.drawTexturedModalRect(slotPos[i][0] + guiLeft - 1, slotPos[i][1] + guiTop - 1, 18, 166, 18, 18);
        			} else
        			{
        				s.xDisplayPosition = slotPos[i][0];
        				s.yDisplayPosition = slotPos[i][1];
        			}
        		}
        	}
        }
    }
	
	public void doScroll(int scrollDX)
	{
		UpdateGuiPos();
		
		int preScroll = scrollPos;
		
		scrollPos -= (int)Math.signum(scrollDX);
		
		scrollPos = MathHelper.clamp_int(scrollPos, 0, maxScroll);
		
		if(preScroll != scrollPos)
		{
			for(int i = 0; i < invoSlots.length; i++)
			{
				Slot s = invoSlots[i];
				
				if(s instanceof SlotLockable)
				{
					((SlotLockable)s).slotIndex = slotIndex[i] + (scrollPos * 9);
				}
				
				s.onSlotChanged();
			}
			
			container.detectAndSendChanges();
			
			NBTTagCompound scrollTags = new NBTTagCompound();
			scrollTags.setInteger("ID", 2);
			scrollTags.setString("Player", Minecraft.getMinecraft().thePlayer.getCommandSenderName());
			scrollTags.setInteger("World", Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId);
			scrollTags.setInteger("Scroll", scrollPos);
			scrollTags.setIntArray("Indexes", slotIndex);
			scrollTags.setBoolean("Reset", false);
			InfiniteInvo.instance.network.sendToServer(new InvoPacket(scrollTags));
		}
	}
}
