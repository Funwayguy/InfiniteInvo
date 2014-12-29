package infiniteinvo.core.proxies;

import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.network.InvoPacket;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public void registerHandlers()
	{
		super.registerHandlers();
    	
    	InfiniteInvo.instance.network.registerMessage(InvoPacket.HandleClient.class, InvoPacket.class, 0, Side.CLIENT);
	}
}
