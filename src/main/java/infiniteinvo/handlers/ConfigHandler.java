package infiniteinvo.handlers;

import infiniteinvo.core.II_Settings;
import infiniteinvo.core.InfiniteInvo;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public class ConfigHandler
{
	public static Configuration config;
	
	public static void initConfigs()
	{
		if(config == null)
		{
			InfiniteInvo.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
			return;
		}
		
		config.load();
		
		II_Settings.invoSize = config.getInt("Max Invo Size", Configuration.CATEGORY_GENERAL, 54, 0, Integer.MAX_VALUE - 100, "Maximum size of the player's inventory (not including hotbar). MUST BE IDENTICAL TO SERVERSIDE!");
		II_Settings.xpUnlock = config.getBoolean("Pay to Unlock", Configuration.CATEGORY_GENERAL, false, "Whether or not players will need to spend XP levels to unlock additional slots");
		II_Settings.unlockedSlots = config.getInt("No. Start Unlocked", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE - 100, "How many slots are initially unlocked (Recommend to keep at least 27 slots for)");
		II_Settings.unlockCost = config.getInt("Unlock cost", Configuration.CATEGORY_GENERAL, 10, 1, Integer.MAX_VALUE, "How many XP levels are required to unlock a slot");
		II_Settings.keepUnlocks = config.getBoolean("Persistent Unlocks", Configuration.CATEGORY_GENERAL, false, "Whether players will keep their unlocked slots upon death");
		II_Settings.unlockIncrease = config.getInt("Cost Increase", Configuration.CATEGORY_GENERAL, 2, 0, Integer.MAX_VALUE, "How much the unlock cost while increase per slot");
		II_Settings.useOrbs = config.getBoolean("Use Orbs", Configuration.CATEGORY_GENERAL, false, "Unlock cost is in orbs instead of levels");
		
		II_Settings.extraRows = config.getInt("Extra Rows", Configuration.CATEGORY_GENERAL, 3, 0, Integer.MAX_VALUE, "How many extra rows are displayed in the inventory screen");
		II_Settings.extraColumns = config.getInt("Extra Columns", Configuration.CATEGORY_GENERAL, 3, 0, Integer.MAX_VALUE, "How many extra columns are displayed in the inventory screen");
		
		II_Settings.IT_Patch = config.getBoolean("ITweaks Patch", Configuration.CATEGORY_GENERAL, false, "A patch for Inventory Tweaks Shift + Space crash. Has some side effects!");
		
		II_Settings.hideUpdates = config.getBoolean("Hide Updates", Configuration.CATEGORY_GENERAL, false, "Hides the one-time update notifications");
		
		config.save();
		
		II_Settings.SaveToCache();
		
		InfiniteInvo.logger.log(Level.INFO, "Loaded configs...");
	}
}
