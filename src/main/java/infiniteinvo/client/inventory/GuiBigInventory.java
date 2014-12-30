package infiniteinvo.client.inventory;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.inventory.BigContainerPlayer;
import infiniteinvo.network.InvoPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiBigInventory extends GuiInventory
{
	BigContainerPlayer container;
	GuiButton unlock;
	public boolean redoButtons = false;
	
	public GuiBigInventory(EntityPlayer player)
	{
		super(player);
		container = player.inventoryContainer instanceof BigContainerPlayer? (BigContainerPlayer)player.inventoryContainer : null;
		this.xSize = 169 + (18 * II_Settings.extraColumns) + 15;
		this.ySize = 137 + (18 * II_Settings.extraRows) + 29;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		unlock = new GuiButton(100, this.guiLeft + 87, this.guiTop + 7, 74, 18, "");
		unlock.enabled = this.container.invo.player.experienceLevel >= II_Settings.unlockCost && II_Settings.xpUnlock;
		unlock.displayString = unlock.enabled? "Unlock Slot" : this.container.invo.player.experienceLevel + " / " + II_Settings.unlockCost + " XP";
		unlock.visible = II_Settings.xpUnlock;
		this.buttonList.add(unlock);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("infiniteinvo", "textures/gui/adjustable_gui.png"));
        int k = this.guiLeft;
        int l = this.guiTop;
        this.drawTexturedModalRect(k, l, 0, 0, 169, 137);
        
        for(int i = 0; i < II_Settings.extraColumns; i++)
        {
            this.drawTexturedModalRect(k + 169 + (18 * i), l, 169, 0, 18, 137);
        }
        
        for(int i = 0; i < II_Settings.extraRows; i++)
        {
            this.drawTexturedModalRect(k, l + 137 + (i * 18), 0, 119, 169, 18);
        }
        
        for(int i = 0; i < II_Settings.extraColumns; i++)
        {
        	for(int j = 0; j < II_Settings.extraRows; j++)
        	{
                this.drawTexturedModalRect(k + 169 + (i * 18), l + 137 + (j * 18), 7, 83, 18, 18);
        	}
        }
        
        this.drawTexturedModalRect(k + 169 + (II_Settings.extraColumns * 18), l, 187, 0, 15, 119);
        
        for(int i = 0; i < II_Settings.extraRows; i++)
        {
            this.drawTexturedModalRect(k + 169 + (II_Settings.extraColumns * 18), l + 119 + (i * 18), 187, 101, 15, 18);
        }
        
        this.drawTexturedModalRect(k + 169 + (II_Settings.extraColumns * 18), l + 119 + (II_Settings.extraRows * 18), 187, 119, 15, 18);
        
        this.drawTexturedModalRect(k, l + 137 + (II_Settings.extraRows * 18), 0, 137, 169, 29);
        
        for(int i = 0; i < II_Settings.extraColumns; i++)
        {
            this.drawTexturedModalRect(k + 169 + (i * 18), l + 137 + (II_Settings.extraRows * 18), 169, 137, 18, 29);
        }
        
        this.drawTexturedModalRect(k + 169 + (II_Settings.extraColumns * 18), l + 137 + (II_Settings.extraRows * 18), 187, 137, 16, 29);
        
        func_147046_a(k + 51, l + 75, 30, (float)(k + 51) - (float)p_146976_2_, (float)(l + 75 - 50) - (float)p_146976_3_, this.mc.thePlayer);
        
        if(redoButtons)
        {
        	redoButtons = false;
			for(int i = 1; i < buttonList.size(); i++)
			{
				GuiButton button = (GuiButton)buttonList.get(i);
				
				if(button.xPosition > this.width/2)
				{
					button.xPosition += (II_Settings.extraColumns * 9) + 4;
				} else if(button.xPosition < this.width/2)
				{
					button.xPosition -= (II_Settings.extraColumns * 9) + 4;
				}
				
				if(button.yPosition > this.height/2)
				{
					button.yPosition += (II_Settings.extraRows * 9);
				} else if(button.yPosition < this.height/2)
				{
					button.yPosition -= (II_Settings.extraRows * 9);
				}
			}
        }
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
        this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 87, 32, 4210752);
		
		if(container != null)
		{
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(new ResourceLocation("infiniteinvo", "textures/gui/adjustable_gui.png"));
			
	        int maxPos = MathHelper.ceiling_float_int((float)II_Settings.invoSize/(float)(9 + II_Settings.extraColumns)) - (3 + II_Settings.extraRows);
			int barPos = maxPos > 0? MathHelper.floor_float((float)container.scrollPos / (float)maxPos * (18F * (3F + (float)II_Settings.extraRows) - 8F)) : 0;
	        this.drawTexturedModalRect(this.xSize - 13, 83 + barPos, 8, 184, 8, 8);
	        
	        // Draw the empty/locked slot icons
	        for(int j = 0; j < 3 + II_Settings.extraRows; j++)
	        {
	        	for(int i = 0; i < 9 + II_Settings.extraColumns; i++)
	        	{
	        		if(i + (j + container.scrollPos) * (9 + II_Settings.extraColumns) >= II_Settings.invoSize)
	        		{
	        			this.drawTexturedModalRect(7 + i * 18, 83 + j * 18, 0, 166, 18, 18);
	        		} else if(i + (j + container.scrollPos) * (9 + II_Settings.extraColumns) >= container.invo.getUnlockedSlots() - 9)
	        		{
	        			this.drawTexturedModalRect(7 + i * 18, 83 + j * 18, 18, 166, 18, 18);
	        		}
	        	}
	        }
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 100 && button.enabled)
		{
			if(this.container.invo.player.experienceLevel >= II_Settings.unlockCost)
			{
				NBTTagCompound tags = new NBTTagCompound();
				tags.setInteger("ID", 0);
				tags.setInteger("World", this.container.invo.player.worldObj.provider.dimensionId);
				tags.setString("Player", this.container.invo.player.getCommandSenderName());
				tags.setInteger("InvoSize", II_Settings.invoSize);
				InfiniteInvo.instance.network.sendToServer(new InvoPacket(tags));
			}
		}
	}
    
    public void handleMouseInput()
    {
    	super.handleMouseInput();
    	
    	if(container != null)
    	{
        	int scrollDir = (int)Math.signum(Mouse.getEventDWheel());
        	
        	if(container.scrollPos - scrollDir < 0)
        	{
        		container.scrollPos = 0;
        	} else if(scrollDir != 0)
        	{
        		container.scrollPos -= scrollDir;
        	}
        	
        	container.UpdateScroll();
    		unlock.enabled = this.container.invo.player.experienceLevel >= II_Settings.unlockCost && II_Settings.xpUnlock;
    		unlock.displayString = unlock.enabled? "Unlock Slot" : this.container.invo.player.experienceLevel + " / " + II_Settings.unlockCost + " XP";
    		unlock.visible = II_Settings.xpUnlock;
    	}
    }
}
