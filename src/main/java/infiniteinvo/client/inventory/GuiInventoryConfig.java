package infiniteinvo.client.inventory;

import infiniteinvo.core.InfiniteInvo;
import infiniteinvo.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiInventoryConfig extends GuiConfig
{
	public GuiInventoryConfig(GuiScreen parent)
	{
		super(parent, new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), InfiniteInvo.MODID, false, false, InfiniteInvo.NAME);
	}
}
