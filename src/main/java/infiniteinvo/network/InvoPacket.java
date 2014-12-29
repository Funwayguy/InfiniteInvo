package infiniteinvo.network;

import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.handlers.EventHandler;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class InvoPacket implements IMessage
{
	NBTTagCompound tags = new NBTTagCompound();
	
	public InvoPacket()
	{
	}
	
	public InvoPacket(NBTTagCompound tags)
	{
		this.tags = tags;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		tags = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, tags);
	}
	
	public static class HandleServer implements IMessageHandler<InvoPacket,IMessage>
	{
		@Override
		public IMessage onMessage(InvoPacket message, MessageContext ctx)
		{
			if(message.tags.hasKey("ID"))
			{
				if(message.tags.getInteger("ID") == 0)
				{
					WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.tags.getInteger("World"));
					EntityPlayer player = world.getPlayerEntityByName(message.tags.getString("Player"));
					if(player.experienceLevel >= II_Settings.unlockCost)
					{
						int unlocked = player.getEntityData().getInteger("INFINITE_INVO_UNLOCKED") + 1;
						player.getEntityData().setInteger("INFINITE_INVO_UNLOCKED", unlocked);
						player.addExperienceLevel(-II_Settings.unlockCost);
						
						EventHandler.unlockCache.put(player.getCommandSenderName(), unlocked);
						
						NBTTagCompound replyTags = new NBTTagCompound();
						replyTags.setInteger("ID", 0);
						replyTags.setString("Player", player.getCommandSenderName());
						replyTags.setInteger("Unlocked", unlocked);
						return new InvoPacket(replyTags);
						
					}
				} else if(message.tags.getInteger("ID") == 1)
				{
					WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.tags.getInteger("World"));
					EntityPlayer player = world.getPlayerEntityByName(message.tags.getString("Player"));
					
					int unlocked = 0;
					
					if(II_Settings.keepUnlocks && !player.getEntityData().hasKey("INFINITE_INVO_UNLOCKED") && EventHandler.unlockCache.containsKey(player.getCommandSenderName()))
					{
						unlocked = EventHandler.unlockCache.get(player.getCommandSenderName());
						player.getEntityData().setInteger("INFINITE_INVO_UNLOCKED", unlocked);
					} else
					{
						unlocked = player.getEntityData().getInteger("INFINITE_INVO_UNLOCKED");
						EventHandler.unlockCache.put(player.getCommandSenderName(), unlocked);
					}
					
					NBTTagCompound reply = new NBTTagCompound();
					reply.setInteger("ID", 0);
					reply.setString("Player", player.getCommandSenderName());
					reply.setInteger("Unlocked", unlocked);
					reply.setTag("Settings", II_Settings.cachedSettings);
					return new InvoPacket(reply);
				}
			}
			return null;
		}
		
	}
	
	public static class HandleClient implements IMessageHandler<InvoPacket,IMessage>
	{
		@Override
		public IMessage onMessage(InvoPacket message, MessageContext ctx)
		{
			if(message.tags.hasKey("ID"))
			{
				if(message.tags.getInteger("ID") == 0)
				{
					EntityPlayer player = Minecraft.getMinecraft().thePlayer;
					player.getEntityData().setInteger("INFINITE_INVO_UNLOCKED", message.tags.getInteger("Unlocked"));
					
					if(message.tags.hasKey("Settings"))
					{
						InfiniteInvo.logger.log(Level.INFO, "Loading serverside settings...");
						II_Settings.LoadFromTags(message.tags.getCompoundTag("Settings"));
					}
				}
			}
			return null;
		}
		
	}
}
