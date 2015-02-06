package infiniteinvo.core;

import infiniteinvo.achievements.InvoAchievements;
import infiniteinvo.core.proxies.CommonProxy;
import infiniteinvo.handlers.ConfigHandler;
import infiniteinvo.item.ItemLockedSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

@Mod(modid = InfiniteInvo.MODID, version = InfiniteInvo.VERSION, name = InfiniteInvo.NAME, guiFactory = "infiniteinvo.handlers.ConfigGuiFactory")
public class InfiniteInvo
{
    public static final String MODID = "infiniteinvo";
    public static final String VERSION = "II_VER_KEY";
    public static final String NAME = "InfiniteInvo";
    public static final String PROXY = "infiniteinvo.core.proxies";
    public static final String CHANNEL = "I_INVO_CHAN";
	
	@Instance(MODID)
	public static InfiniteInvo instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public SimpleNetworkWrapper network ;
	public static Logger logger;
	
	/**
	 * Purely used for returning faking filled slots
	 */
	public static Item locked;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    	ConfigHandler.config = new Configuration(event.getSuggestedConfigurationFile(), true);
    	ConfigHandler.initConfigs();
    	
    	proxy.registerHandlers();
    	
    	InvoAchievements.InitAchievements();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	locked = new ItemLockedSlot();
    	GameRegistry.registerItem(locked, "locked_slot");
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
    		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(locked, 0, new ModelResourceLocation("infiniteinvo:locked", "inventory"));
    	}
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}