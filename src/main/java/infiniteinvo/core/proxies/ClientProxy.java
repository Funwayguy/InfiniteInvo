package infiniteinvo.core.proxies;

import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.network.InvoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

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
	
	@Override
	public void registerRenderers()
	{
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		
		mesher.register(InfiniteInvo.unlock, 0, new ModelResourceLocation("infiniteinvo:unlock_slot", "inventory"));
		mesher.register(InfiniteInvo.locked, 0, new ModelResourceLocation("infiniteinvo:locked_slot", "inventory"));
	}
}
