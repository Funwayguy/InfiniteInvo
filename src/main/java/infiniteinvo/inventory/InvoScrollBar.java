package infiniteinvo.inventory;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.network.InvoPacket;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class InvoScrollBar extends GuiButton
{
	int maxScroll = 0;
	int scrollPos = 0;
	Container container;
	Slot[] invoSlots = new Slot[27];
	int[] slotIndex = new int[27];
	
	public InvoScrollBar(int id, int posX, int posY, int width, int height, String title, Container container)
	{
		super(id, posX, posY, width, height, title);
		this.container = container;
		
		int index = 0;
		for(int i = 0; i < container.inventorySlots.size() && index < 27; i++)
		{
			Slot s = (Slot)container.inventorySlots.get(i);
			
			if(s.inventory instanceof InventoryPlayer && s.slotNumber >= 9 && !(s.slotNumber >= 36 && s.slotNumber < 45))
			{
				invoSlots[index] = s;
				slotIndex[index] = s.slotNumber;
				index++;
			}
		}
		
		System.out.println("Detected " + (index) + " slots");
		
		maxScroll = MathHelper.ceiling_float_int((float)invoSlots.length/9F);
	}
	
	public void UpdateSlots()
	{
		
	}
	
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
		int scrollDX = Mouse.getEventDWheel();
		
		if(scrollDX != 0)
		{
			doScroll(scrollDX);
		}
    }
	
	public void doScroll(int scrollDX)
	{
		int preScroll = scrollPos;
		
		scrollPos -= (int)Math.signum(scrollDX);
		
		scrollPos = MathHelper.clamp_int(scrollPos, 0, maxScroll);
		
		/*if(preScroll != scrollPos)
		{
			System.out.println("Scrolling to " + scrollPos);
			
			for(int i = 0; i < invoSlots.length; i++)
			{
				Slot s = invoSlots[i];
				s.slotNumber = slotIndex[i] + (scrollPos * 9);
			}
			
			NBTTagCompound scrollTags = new NBTTagCompound();
			scrollTags.setInteger("ID", 2);
			scrollTags.setString("Player", Minecraft.getMinecraft().thePlayer.getCommandSenderName());
			scrollTags.setInteger("World", Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId);
			scrollTags.setInteger("Scroll", scrollPos);
			InfiniteInvo.instance.network.sendToServer(new InvoPacket());
		}*/
	}
}
