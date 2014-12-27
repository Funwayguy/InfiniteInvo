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
		
		II_Settings.invoSize = config.getInt("Max Invo Size", Configuration.CATEGORY_GENERAL, 54, 27, Integer.MAX_VALUE - 100, "Maximum size of the player's inventory (not including hotbar). MUST BE IDENTICAL TO SERVERSIDE!");
		
		config.save();
	}
}
