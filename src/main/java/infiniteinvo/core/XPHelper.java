package infiniteinvo.core;

import net.minecraft.entity.player.EntityPlayer;

public class XPHelper
{
	public static void AddXP(EntityPlayer player, int xp)
	{
		System.out.println("XP Before: " + getPlayerXP(player));
		int experience = getPlayerXP(player) + xp;
		player.experienceTotal = experience;
		player.experienceLevel = getXPLevel(experience);
		int expForLevel = getLevelXP(player.experienceLevel);
		player.experience = (float)(experience - expForLevel) / (float)player.xpBarCap();
		System.out.println("XP After: " + getPlayerXP(player));
	}
	
	public static int getPlayerXP(EntityPlayer player)
	{
		return getLevelXP(player.experienceLevel) + (int)(player.experience * player.xpBarCap());
	}
	
	public static int getXPLevel(int xp)
	{
		int i = 0;
		
		while (getLevelXP(i) <= xp)
		{
			i++;
		}
		
		return i - 1;
	}
	
	public static int getLevelXP(int level)
	{
		if(level < 0)
		{
			return 0;
		}
		
		if(level < 16)
		{
			return level * 17;
		} else if(level > 15 && level < 31)
		{
			return (int)(1.5 * Math.pow(level, 2) - 29.5 * level + 360);
		} else
		{
			return (int)(3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
		}
	}
}
