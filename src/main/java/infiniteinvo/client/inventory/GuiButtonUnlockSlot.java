package infiniteinvo.client.inventory;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.inventory.BigInventoryPlayer;
import infiniteinvo.network.InvoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class GuiButtonUnlockSlot extends GuiButton
{
	EntityPlayer player;
	
	public GuiButtonUnlockSlot(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, EntityPlayer player)
	{
		super(p_i1020_1_, p_i1020_2_, p_i1020_3_, "");
		this.player = player;
	}
	
	public GuiButtonUnlockSlot(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, EntityPlayer player)
	{
		super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, "");
		this.player = player;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mx, int my)
	{
		if(player.inventory instanceof BigInventoryPlayer)
		{
			this.enabled = player.experienceLevel >= (II_Settings.unlockCost + (player.getEntityData().getInteger("INFINITE_INVO_UNLOCKED") * II_Settings.unlockIncrease)) && II_Settings.xpUnlock && ((BigInventoryPlayer)player.inventory).getUnlockedSlots() - 9 < II_Settings.invoSize;
		} else
		{
			this.enabled = false;
		}
		
		this.displayString = this.enabled? StatCollector.translateToLocal("infiniteinvo.unlockslot") : player.experienceLevel + " / " + (II_Settings.unlockCost + (player.getEntityData().getInteger("INFINITE_INVO_UNLOCKED") * II_Settings.unlockIncrease)) + " XP";
		
		super.drawButton(mc, mx, my);
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mx, int my)
    {
		boolean flag = super.mousePressed(mc, mx, my);
		
		if(flag)
		{
			enabled = false;
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger("ID", 0);
			tags.setInteger("World", player.worldObj.provider.dimensionId);
			tags.setString("Player", player.getCommandSenderName());
			tags.setInteger("InvoSize", II_Settings.invoSize);
			InfiniteInvo.instance.network.sendToServer(new InvoPacket(tags));
		}
		
		return flag;
    }
}
