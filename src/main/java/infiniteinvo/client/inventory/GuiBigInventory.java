package infiniteinvo.client.inventory;

import infiniteinvo.core.II_Settings;
import infiniteinvo.inventory.BigContainerPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiBigInventory extends GuiInventory
{
	BigContainerPlayer container;
	
	public GuiBigInventory(EntityPlayer player)
	{
		super(player);
		container = player.inventoryContainer instanceof BigContainerPlayer? (BigContainerPlayer)player.inventoryContainer : null;
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		
		if(container != null)
		{
			//this.fontRendererObj.drawString("[-]", 86, 72, 4210752);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(new ResourceLocation("infiniteinvo", "textures/gui/scroll_bar.png"));

	        this.drawTexturedModalRect(this.xSize, this.ySize/2, 8, 0, 8, 4);
	        
	        for(int i = 0; i < 46; i += 8)
	        {
	        	int space = 46 - i < 8? 46 - i : 8;
	        	
		        this.drawTexturedModalRect(this.xSize, this.ySize/2 + i + 4, 8, 4, 8, 4 + space);
	        }
	        
	        this.drawTexturedModalRect(this.xSize, this.ySize/2 + 50, 8, 12, 8, 4);
			
	        int maxPos = MathHelper.ceiling_float_int((float)II_Settings.invoSize/9F) - 3;
			int barPos = MathHelper.floor_float((float)container.scrollPos / (float)maxPos * 46F);
	        this.drawTexturedModalRect(this.xSize, this.ySize/2 + barPos, 0, 0, 8, 8);
	        
	        // Draw the empty slot icons
	        if(II_Settings.invoSize % 9 != 0 && container.scrollPos == maxPos)
	        {
	        	for(int i = II_Settings.invoSize % 9; i < 9; i++)
	        	{
	    	        this.drawTexturedModalRect(7 + (9 - i) * 18, 119, 16, 0, 18, 18);
	        	}
	        }
		}
	}
    
    public void handleMouseInput()
    {
    	super.handleMouseInput();
    	
    	if(container != null)
    	{
        	int scrollDir = (int)Math.signum(Mouse.getEventDWheel());
        	
        	if(container.scrollPos - scrollDir <= 0)
        	{
        		container.scrollPos = 0;
        	} else
        	{
        		container.scrollPos -= scrollDir;
        	}
        	
        	container.UpdateScroll();
    	}
    }
}
