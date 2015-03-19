package infiniteinvo.client.inventory;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.inventory.BigInventoryPlayer;
import infiniteinvo.inventory.SlotLockable;
import infiniteinvo.network.InvoPacket;
import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class InvoScrollBar extends GuiButton
{
	int refresh = 0;
	boolean creative = false;
	int maxScroll = 0;
	int scrollPos = 0;
	Container container;
	GuiContainer gui;
	int guiLeft = 0;
	int guiTop = 0;
	int[][] slotPos = new int[27][2];
	Slot[] invoSlots = new Slot[27];
	int[][] slotIndex = new int[2][27];
	
	int scrollX = 0;
	int scrollY = Integer.MAX_VALUE;
	
	boolean firstSync = false;
	
	public InvoScrollBar(int id, int posX, int posY, int width, int height, String title, Container container, GuiContainer gui)
	{
		super(id, posX, posY, width, height, title);
		this.container = container;
		this.gui = gui;
		this.creative = gui instanceof GuiContainerCreative;
		this.enabled = false;
		refresh = 0;
		
		if(this.creative)
		{
			enabled = this.InitCreative();
		} else
		{
			enabled = this.InitDefault();
		}
		
		UpdateGuiPos();
	}
	
	@SuppressWarnings("unchecked")
	public boolean InitDefault()
	{
		if(refresh > 0)
		{
			return enabled;
		} else
		{
			refresh = 15;
		}
		
		int index = 0;
		for(int i = 0; i < container.inventorySlots.size() && index < 27; i++)
		{
			Slot s = (Slot)container.inventorySlots.get(i);
			
			if(s.inventory instanceof InventoryPlayer && s.getSlotIndex() >= 9 && s.getSlotIndex() < II_Settings.invoSize + 9)
			{
				if(s.getClass() != Slot.class && s.getClass() != SlotLockable.class)
				{
					InfiniteInvo.logger.log(Level.WARN, "Container " + container.getClass().getSimpleName() + " is not supported by InfiniteInvo! Reason: Custom Slots (" + s.getClass() + ") are being used!");
					return false;
				}
				
				Slot r = new SlotLockable(s.inventory, index + 9, s.xDisplayPosition, s.yDisplayPosition);
				
				// Replace the local slot with our own tweaked one so that locked slots are handled properly
				container.inventorySlots.set(i, r);
				r.slotNumber = s.slotNumber;
				s = r;
				container.inventoryItemStacks.set(i, r.getStack());
				r.onSlotChanged();
				
				invoSlots[index] = s;
				slotIndex[0][index] = s.getSlotIndex();
				slotIndex[1][index] = i;
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
		
		return true;
	}
	
	public boolean InitCreative()
	{
		if(refresh > 0)
		{
			return enabled;
		} else
		{
			refresh = 15;
		}
		
		ArrayList<Slot> slotList = new ArrayList<Slot>();
		ArrayList<int[]> indexList = new ArrayList<int[]>();
		for(int i = 0; i < container.inventorySlots.size(); i++)
		{
			Slot s = (Slot)container.inventorySlots.get(i);
			
			if(s.inventory instanceof InventoryPlayer && s.getSlotIndex() >= 9 && s.getSlotIndex() < II_Settings.invoSize + 27)
			{
				if(s.getSlotIndex() >= 36 && s.getSlotIndex() < 36 + 9)
				{
					// This is the (oddly indexed) hotbar
					continue;
				}
				slotList.add(s);
				int[] tmp = new int[]{s.getSlotIndex(), i};
				indexList.add(tmp);
				
				if(s.getSlotIndex() >= 36)
				{
					s.xDisplayPosition = -2000;
					s.yDisplayPosition = -2000;
				} else
				{
					if(s.xDisplayPosition - 1 > scrollX)
					{
						scrollX = s.xDisplayPosition - 1;
					}
					
					if(s.yDisplayPosition - 1 < scrollY)
					{
						scrollY = s.yDisplayPosition - 1;
					}
				}
			}
		}
		
		this.invoSlots = slotList.toArray(new Slot[]{});
		this.slotIndex = new int[indexList.size()][2];
		for(int i = 0; i < indexList.size(); i++)
		{
			slotIndex[i] = indexList.get(i);
		}
		
		if(invoSlots.length <= 27)
		{
			maxScroll = 0;
		} else
		{
			maxScroll = MathHelper.ceiling_float_int((float)(invoSlots.length - 27)/9F);
		}
		
		return true;
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
	
	public void drawButton(Minecraft mc, int mx, int my)
    {
		if(refresh > 0)
		{
			refresh--;
		}
		
		if(!enabled)
		{
			return;
		}
		
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		UpdateGuiPos();
		int scrollDX = (int)Math.signum(Mouse.getDWheel());
		
		if(scrollDX != 0)
		{
			doScroll(scrollDX);
		} else if(Mouse.isButtonDown(0))
    	{
    		int sx = this.guiLeft + scrollX + 20;
    		int sy = this.guiTop + scrollY;
    		
    		boolean flag = mx >= sx && my >= sy && mx < sx + 8 && my < sy + (18 * 3);
    		
    		if((flag || dragging == 1) && dragging != -1)
    		{
    			dragging = 1;
    			int preScroll = scrollPos;
    			int tmpPos = MathHelper.clamp_int(Math.round((float)(my - sy) / (float)(18 * 3) * (float)maxScroll), 0, maxScroll);
    			if(preScroll - tmpPos != 0)
    			{
    				doScroll(preScroll - tmpPos);
    			}
    		} else
    		{
    			dragging = -1;
    		}
    	} else
    	{
    		dragging = 0;
    	}
		
        mc.getTextureManager().bindTexture(new ResourceLocation("infiniteinvo", "textures/gui/adjustable_gui.png"));
        
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
        	
    		if(this.enabled && (invoSlots.length <= 0 || invoSlots[0] == null))
    		{
    			if(creative)
    			{
    				enabled = this.InitCreative();
    			} else
    			{
    				enabled = this.InitDefault();
    			}
    			
    			if(enabled && !(invoSlots.length <= 0 || invoSlots[0] == null))
    			{
    				doScroll(0);
    			}
    			return;
    		} else if(!firstSync && !creative)
    		{
    			firstSync = true;
    			scrollPos = 0;
    			doScroll(0);
    			return;
    		}
    		
    		for(int i = 0; i < this.invoSlots.length; i++)
    		{
    			Slot s = this.invoSlots[i];
    			
    			if(s == null)
    			{
    				continue;
    			}
    			
    			if(creative)
    			{
    				if(!container.inventorySlots.contains(s))
    				{
    					this.InitCreative();
    					return;
    				}
    				int j = slotIndex[i][1] - (9 * scrollPos) - (s.getSlotIndex() >= 36? 9 : 0);
    				int k = j - 9;
                    int l = k % 9;
                    int i1 = k / 9;
                    s.xDisplayPosition = 9 + l * 18;

                    if (j >= 36 || j < 9)
                    {
                    	s.yDisplayPosition = -2000;
                    	s.xDisplayPosition = -2000;
                    } else
                    {
                        s.yDisplayPosition = 54 + i1 * 18;
                    }
    			} else
    			{
    				if(!container.inventorySlots.contains(s))
    				{
    					this.InitDefault();
    					return;
    				}
    				
        			if(s.getSlotIndex() - 9 >= II_Settings.invoSize)
        			{
        				s.xDisplayPosition = -999;
        				s.yDisplayPosition = -999;
        				this.drawTexturedModalRect(slotPos[i][0] + guiLeft - 1, slotPos[i][1] + guiTop - 1, 0, 166, 18, 18);
        			} else if(pinvo.getUnlockedSlots() <= s.getSlotIndex())
        			{
        				s.xDisplayPosition = -999;
        				s.yDisplayPosition = -999;
        				this.drawTexturedModalRect(slotPos[i][0] + guiLeft - 1, slotPos[i][1] + guiTop - 1, 18, 166, 18, 18);
        			} else
        			{
        				s.xDisplayPosition = slotPos[i][0];
        				s.yDisplayPosition = slotPos[i][1];
        			}
    			}
    			
    			// Debug stuffs that renders the slot IDs
    			//this.drawString(mc.fontRenderer, "" + s.getSlotIndex(), s.xDisplayPosition + this.guiLeft, s.yDisplayPosition + this.guiTop + 4, 16777120);
    	        //mc.getTextureManager().bindTexture(new ResourceLocation("infiniteinvo", "textures/gui/adjustable_gui.png"));
                //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	}
        }
    }
	
	public void doScroll(int scrollDX)
	{
		if(!enabled)
		{
			return;
		}
		
		scrollPos -= (int)Math.signum(scrollDX);
		
		scrollPos = MathHelper.clamp_int(scrollPos, 0, maxScroll);
		
		if(!creative)
		{
			for(int i = 0; i < invoSlots.length; i++)
			{
				Slot s = invoSlots[i];
				
				if(s != null && s instanceof SlotLockable)
				{
					((SlotLockable)s).slotIndex = slotIndex[0][i] + (scrollPos * 9);
					s.onSlotChanged();
				}
			}
			
			NBTTagCompound scrollTags = new NBTTagCompound();
			scrollTags.setInteger("ID", 2);
			scrollTags.setString("Player", Minecraft.getMinecraft().thePlayer.getCommandSenderName());
			scrollTags.setInteger("World", Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId);
			scrollTags.setInteger("Scroll", scrollPos);
			scrollTags.setIntArray("Indexes", slotIndex[0]);
			scrollTags.setIntArray("Numbers", slotIndex[1]);
			scrollTags.setInteger("Container ID", this.container.windowId);
			InfiniteInvo.instance.network.sendToServer(new InvoPacket(scrollTags));
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mx, int my)
    {
		return false;
    }
}
