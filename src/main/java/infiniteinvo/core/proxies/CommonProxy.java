package infiniteinvo.core.proxies;

import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.handlers.EventHandler;
import infiniteinvo.handlers.II_UpdateNotification;
import infiniteinvo.network.InvoPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void registerHandlers()
	{
		EventHandler handler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
		FMLCommonHandler.instance().bus().register(new II_UpdateNotification());
    	InfiniteInvo.instance.network.registerMessage(InvoPacket.HandleServer.class, InvoPacket.class, 0, Side.SERVER);
	}

	public void registerRenderers()
	{
	}
}
