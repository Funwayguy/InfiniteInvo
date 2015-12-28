package infiniteinvo.core;

import infiniteinvo.achievements.InvoAchievements;
import infiniteinvo.core.proxies.CommonProxy;
import infiniteinvo.handlers.ConfigHandler;
import infiniteinvo.item.ItemLockedSlot;
import infiniteinvo.item.ItemUnlockSlot;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.config.Configuration;
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
	public static Item unlock;
    
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
    	unlock = new ItemUnlockSlot();
    	GameRegistry.registerItem(unlock, "unlock_slot");
    	ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(unlock, 0, 1, 3, 2));
    	
    	proxy.registerRenderers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
