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
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;

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
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
