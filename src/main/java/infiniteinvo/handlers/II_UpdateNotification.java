package infiniteinvo.handlers;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

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
		
		if(InfiniteInvo.HASH == "CI_MOD_" + "HASH")
		{
			event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "THIS COPY OF " + InfiniteInvo.NAME.toUpperCase() + " IS NOT FOR PUBLIC USE!"));
			return;
		}
		
		try
		{
			String[] data = getNotification("http://bit.ly/1Brrt22", true).split("\\n");
			
			if(II_Settings.hideUpdates)
			{
				return;
			}
			
			ArrayList<String> changelog = new ArrayList<String>();
			boolean hasLog = false;
			
			for(String s : data)
			{
				if(s.equalsIgnoreCase("git_branch:" + InfiniteInvo.BRANCH))
				{
					if(!hasLog)
					{
						hasLog = true;
						changelog.add(s);
						continue;
					} else
					{
						break;
					}
				} else if(s.toLowerCase().startsWith("git_branch:"))
				{
					if(hasLog)
					{
						break;
					} else
					{
						continue;
					}
				} else if(hasLog)
				{
					changelog.add(s);
				}
			}
			
			if(!hasLog || data.length < 2)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured while checking " + InfiniteInvo.NAME + " version!"));
				InfiniteInvo.logger.log(Level.ERROR, "An error has occured while checking " + InfiniteInvo.NAME + " version! (hasLog: " + hasLog + ", data: " + data.length + ")");
				return;
			} else
			{
				// Only the relevant portion of the changelog is preserved
				data = changelog.toArray(new String[0]);
			}
			
			String hash = data[1].trim();
			
			boolean hasUpdate = !InfiniteInvo.HASH.equalsIgnoreCase(hash);
			
			if(hasUpdate)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Update for " + InfiniteInvo.NAME + " available!"));
				event.player.addChatMessage(new ChatComponentText("Download: http://minecraft.curseforge.com/projects/better-questing"));
				
				for(int i = 2; i < data.length; i++)
				{
					if(i > 5)
					{
						event.player.addChatMessage(new ChatComponentText("and " + (data.length - 5) + " more..."));
						break;
					} else
					{
						event.player.addChatMessage(new ChatComponentText("- " + data[i].trim()));
					}
				}
			}
			
		} catch(Exception e)
		{
			event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error has occured while checking " + InfiniteInvo.NAME + " version!"));
			InfiniteInvo.logger.log(Level.ERROR, "An error has occured while checking " + InfiniteInvo.NAME + " version!", e);
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
	
	private String getNotification(String link, boolean doRedirect) throws Exception
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
		HttpURLConnection.setFollowRedirects(true);
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
		
		return page;
	}
}
