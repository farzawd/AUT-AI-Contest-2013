package game;

import game.map.Field;

public class GameAnalyzer
{
	public static int getUnitsCountFor(Field map, Bot bot)
	{
		int unitCount = 0;
		
		for(int col = 0 ; col < map.getWidth() ; col++)
			for(int row = 0 ; row < map.getHeight() ; row++)
			{
				if(map.getTileAt(col, row).getUnit() != null && map.getTileAt(col, row).getUnit().isAlive())
				{
					if(map.getTileAt(col, row).getUnit().getTeamNumber() == bot.getTeamNumber())
						unitCount++;
				}
			}
		
		return unitCount;
	}
	
	public static int getBuildingsCountFor(Field map, Bot bot)
	{
		int buildingCount = 0;
		
		for(int col = 0 ; col < map.getWidth() ; col++)
			for(int row = 0 ; row < map.getHeight() ; row++)
			{
				if(map.getTileAt(col, row).getBuilding() != null && map.getTileAt(col, row).getBuilding().isAlive())
				{
					if(map.getTileAt(col, row).getBuilding().getTeamNumber() == bot.getTeamNumber())
						buildingCount++;
				}
			}
		
		return buildingCount;
	}
	
	public static int getScoreFor(Field map, Bot bot)
	{
		if(bot.isLost())
			return -1;
		
		return bot.getSuppliesAmount() + (getUnitsCountFor(map, bot) * 10) + (getBuildingsCountFor(map, bot) * 50);
	}
}
