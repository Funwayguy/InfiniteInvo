package infiniteinvo.core.proxies;

import net.minecraftforge.common.MinecraftForge;
import infiniteinvo.handlers.EventHandler;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
}
