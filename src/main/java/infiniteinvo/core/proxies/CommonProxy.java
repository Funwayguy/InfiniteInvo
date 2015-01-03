package infiniteinvo.core.proxies;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.handlers.EventHandler;
import infiniteinvo.network.InvoPacket;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		EventHandler handler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
    	InfiniteInvo.instance.network.registerMessage(InvoPacket.HandleServer.class, InvoPacket.class, 0, Side.SERVER);
	}
}
