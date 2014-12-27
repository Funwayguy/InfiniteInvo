package infiniteinvo.inventory;

import infiniteinvo.core.II_Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.MathHelper;

public class BigContainerPlayer extends ContainerPlayer
{
	public int scrollPos = 0;
	public InventoryPlayer invo;
	/**
	 * A more organised version of 'inventorySlots' that doesn't include the hotbar
	 */
	Slot[] slots = new Slot[II_Settings.invoSize];
	
	public BigContainerPlayer(InventoryPlayer invo, boolean isLocal, EntityPlayer player)
	{
		super(invo, isLocal, player);
		this.invo = invo;
		
		for(int i = 9; i < 36; i++)
		{
			// Add all the previous inventory slots to the organised array
			slots[i - 9] = (Slot)this.inventorySlots.get(i);
		}

        for (int i = 3; i < MathHelper.ceiling_float_int((float)II_Settings.invoSize/9F); ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
            	if(j + (i * 9) >= II_Settings.invoSize)
            	{
            		break;
            	} else
            	{
            		// Moved off screen to avoid interaction until screen scrolls over the row
            		Slot ns = new Slot(invo, j + (i + 1) * 9, -99, -99);
            		slots[j + (i * 9)] = ns;
            		this.addSlotToContainer(ns);
            	}
            }
        }
	}
	
	public void UpdateScroll()
	{
		if(scrollPos >= MathHelper.ceiling_float_int((float)II_Settings.invoSize/9F) - 2)
		{
			scrollPos = MathHelper.ceiling_float_int((float)II_Settings.invoSize/9F) - 3;
		}
		
		for(int i = 0; i < MathHelper.ceiling_float_int((float)II_Settings.invoSize/9F); i++)
		{
            for (int j = 0; j < 9; ++j)
            {
            	if(j + (i * 9) >= II_Settings.invoSize)
            	{
            		break;
            	} else
            	{
            		if(i >= scrollPos && i < scrollPos + 3)
            		{
            			Slot s = slots[j + (i * 9)];
            			s.xDisplayPosition = 8 + j * 18;
            			s.yDisplayPosition = 84 + (i - scrollPos) * 18;
            		} else
            		{
            			Slot s = slots[j + (i * 9)];
            			s.xDisplayPosition = -99;
            			s.yDisplayPosition = -99;
            		}
            	}
            }
		}
	}
}
