package infiniteinvo.achievements;

import infiniteinvo.core.InfiniteInvo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class InvoAchievements
{
	public static AchievementPage page;

	public static Achievement boneSanta;
	public static Achievement wormDrops;
	public static Achievement bacon;
	public static Achievement unlockFirst;
	public static Achievement unlockAll;
	
	public static void InitAchievements()
	{
		boneSanta = new Achievement("infiniteinvo.bonesanta", "infiniteinvo.bonesanta", -1, 1, Items.bone, null).setSpecial().registerStat();
		wormDrops = new Achievement("infiniteinvo.wormdrops", "infiniteinvo.wormdrops", 0, 1, Blocks.leaves, null).setSpecial().registerStat();
		bacon = new Achievement("infiniteinvo.baconpriorities", "infiniteinvo.baconpriorities", -1, 0, Items.cooked_porkchop, unlockAll).registerStat();
		unlockFirst = new Achievement("infiniteinvo.morespace", "infiniteinvo.morespace", 0 ,0, Blocks.chest, null).registerStat();
		unlockAll = new Achievement("infiniteinvo.allspace", "infiniteinvo.allspace", 1, 0, Blocks.ender_chest, unlockFirst).registerStat();
		
		page = new AchievementPage(InfiniteInvo.NAME, wormDrops, bacon, unlockFirst, unlockAll, boneSanta);
		AchievementPage.registerAchievementPage(page);
	}
}
