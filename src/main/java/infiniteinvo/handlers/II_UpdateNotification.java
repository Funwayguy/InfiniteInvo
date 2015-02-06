package infiniteinvo.handlers;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.Level;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class II_UpdateNotification
{
	boolean hasChecked = false;
	@SuppressWarnings("unused")
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(!InfiniteInvo.proxy.isClient() || hasChecked)
		{
			return;
		}
		
		hasChecked = true;
		
		if(InfiniteInvo.VERSION == "II_VER_" + "KEY")
		{
			event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "THIS COPY OF INFINITEINVO IS NOT FOR PUBLIC USE!"));
			return;
		}
		
		try
		{
			String[] data = getNotification("http://bit.ly/1Brrt22", true);
			
			if(II_Settings.hideUpdates)
			{
				return;
			}
			
			String version = data[0].trim();
			String link = data[1].trim();
			
			int verStat = compareVersions(InfiniteInvo.VERSION, version);
			
			if(verStat == -1)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Update " + version + " of InfiniteInvo available!"));
				event.player.addChatMessage(new ChatComponentText("Download & Changelog:"));
				event.player.addChatMessage(new ChatComponentText("" + EnumChatFormatting.BLUE + EnumChatFormatting.UNDERLINE + link));
				
				for(int i = 2; i < data.length; i++)
				{
					if(i > 5)
					{
						event.player.addChatMessage(new ChatComponentText("and " + (data.length - 6) + " more..."));
						break;
					} else
					{
						event.player.addChatMessage(new ChatComponentText(data[i].trim()));
					}
				}
			} else if(verStat == 0)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "InfiniteInvo " + version + " is up to date"));
			} else if(verStat == 1)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "InfiniteInvo " + version + " is a debug build"));
			} else if(verStat == -2)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured while checking InfiniteInvo version!"));
			}
			
		} catch(Exception e)
		{
			event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured while checking InfiniteInvo version!"));
			e.printStackTrace();
			return;
		}
	}
	
	public int compareVersions(String ver1, String ver2)
	{
		int[] oldNum;
		int[] newNum;
		String[] oldNumString;
		String[] newNumString;
		
		try
		{
			oldNumString = ver1.split("\\.");
			newNumString = ver2.split("\\.");
			
			oldNum = new int[]{Integer.valueOf(oldNumString[0]), Integer.valueOf(oldNumString[1]), Integer.valueOf(oldNumString[2])};
			newNum = new int[]{Integer.valueOf(newNumString[0]), Integer.valueOf(newNumString[1]), Integer.valueOf(newNumString[2])};
		} catch(Exception e)
		{
			return -2;
		}
		
		for(int i = 0; i < 3; i++)
		{
			if(oldNum[i] < newNum[i])
			{
				return -1; // New version available
			} else if(oldNum[i] > newNum[i])
			{
				return 1; // Debug version ahead of release
			}
		}
		
		return 0;
	}
	
	private String[] getNotification(String link, boolean doRedirect) throws Exception
	{
		URL url = new URL(link);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setDoOutput(false);
		con.setReadTimeout(20000);
		con.setRequestProperty("Connection", "keep-alive");
		
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");
		((HttpURLConnection)con).setRequestMethod("GET");
		con.setConnectTimeout(5000);
		BufferedInputStream in = new BufferedInputStream(con.getInputStream());
		int responseCode = con.getResponseCode();
		if(responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_MOVED_PERM)
		{
			InfiniteInvo.logger.log(Level.WARN, "Update request returned response code: " + responseCode + " " + con.getResponseMessage());
		} else if(responseCode == HttpURLConnection.HTTP_MOVED_PERM)
		{
			if(doRedirect)
			{
				try
				{
					return getNotification(con.getHeaderField("location"), false);
				} catch(Exception e)
				{
					throw e;
				}
			} else
			{
				throw new Exception();
			}
		}
		StringBuffer buffer = new StringBuffer();
		int chars_read;
		//	int total = 0;
		while((chars_read = in.read()) != -1)
		{
			char g = (char)chars_read;
			buffer.append(g);
		}
		final String page = buffer.toString();
		
		String[] pageSplit = page.split("\\n");
		
		return pageSplit;
	}
}