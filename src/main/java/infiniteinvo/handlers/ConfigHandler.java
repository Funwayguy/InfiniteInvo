package infiniteinvo.handlers;

import infiniteinvo.core.II_Settings;
import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
	public static void initConfigs(File file)
	{
		Configuration config = new Configuration(file);
		
		config.load();
		
		II_Settings.invoSize = config.getInt("Max Invo Size", Configuration.CATEGORY_GENERAL, 54, 0, Integer.MAX_VALUE - 100, "Maximum size of the player's inventory (not including hotbar). MUST BE IDENTICAL TO SERVERSIDE!");
		II_Settings.xpUnlock = config.getBoolean("Pay to Unlock", Configuration.CATEGORY_GENERAL, false, "Whether or not players will need to spend XP levels to unlock additional slots");
		II_Settings.unlockedSlots = config.getInt("No. Start Unlocked", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE - 100, "How many slots are initially unlocked (Recommend to keep at least 27 slots for)");
		II_Settings.unlockCost = config.getInt("Unlock cost", Configuration.CATEGORY_GENERAL, 10, 1, Integer.MAX_VALUE, "How many XP levels are required to unlock a slot");
		II_Settings.keepUnlocks = config.getBoolean("Persistent Unlocks", Configuration.CATEGORY_GENERAL, false, "Whether players will keep their unlocked slots upon death");
		II_Settings.unlockIncrease = config.getInt("Cost Increase", Configuration.CATEGORY_GENERAL, 2, 0, Integer.MAX_VALUE, "How much the unlock cost while increase per slot");
		
		II_Settings.extraRows = config.getInt("Extra Rows", Configuration.CATEGORY_GENERAL, 3, 0, 6, "How many extra rows are displayed in the inventory screen");
		II_Settings.extraColumns = config.getInt("Extra Colums", Configuration.CATEGORY_GENERAL, 3, 0, 9, "How many extra columns are displayed in the inventory screen");
		
		config.save();
		
		II_Settings.SaveToCache();
	}
}
