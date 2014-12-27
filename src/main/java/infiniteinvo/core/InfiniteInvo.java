package infiniteinvo.core;

import org.apache.logging.log4j.Logger;
import infiniteinvo.core.proxies.CommonProxy;
import infiniteinvo.handlers.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = InfiniteInvo.MODID, version = InfiniteInvo.VERSION, name = InfiniteInvo.NAME)
public class InfiniteInvo
{
    public static final String MODID = "infiniteinvo";
    public static final String VERSION = "II_VER_KEY";
    public static final String NAME = "InfiniteInvo";
    public static final String PROXY = "infiniteinvo.core.proxies";
	
	@Instance(MODID)
	public static InfiniteInvo instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	ConfigHandler.initConfigs(event.getSuggestedConfigurationFile());
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
