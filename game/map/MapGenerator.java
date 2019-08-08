package game.map;

import game.agent.Building;
import game.map.Field.*;

import java.awt.Point;
import java.util.Random;


/**
 * used to generate maps.
 * @author farzad
 *
 */
public class MapGenerator
{
	Field map;

	int width;
	int height;

	int seed;
	Random rand;

	public MapGenerator(Field map, int seed)
	{
		this.map = map;
		this.seed = seed;

		rand = new Random(seed);

		this.width = map.getWidth();
		this.height = map.getHeight();
	}

	/**
	 * fills the entire map with the given filler
	 * @param filler
	 */
	public Field fillMap(TileType filler)
	{
		for (int col = 0; col < width; col++)
		{
			for (int row = 0; row < height; row++)
			{
				map.setTileType(col, row, filler);
			}
		}

		return this.map;
	}

	/**
	 * generates a random map using the given seed and flatness
	 * @param seed
	 * @param flatness specifies the amount of open (non-obstacle) tiles
	 */
	public Field generateRandomMap(int flatness)
	{
		TileType tempTileType;

		fillMap(TileType.LAVA);

		for (int reRun = 0 ; reRun < flatness ; reRun++)
		{
			for (int col = 0; col < width; col++)
			{
				for (int row = 0; row < height; row++)
				{
					if (rand.nextBoolean())
						tempTileType = TileType.ROCK;
					else
						tempTileType = TileType.LAVA;

					if (map.getTileType(col, row) != TileType.ROCK)
						map.setTileType(col, row, tempTileType);
				}
			}
		}

		return this.map;
	}

	private Tile getRandomTile()
	{
		int tempX = rand.nextInt(width);
		int tempY = rand.nextInt(height);

		return map.getTileAt(tempX, tempY);
	}

	public boolean placeRandomSupplies()
	{
		Tile tempTile;

		int attempts = 0;
		int maxAttempts = width * height * 3;

		do
		{
			tempTile = getRandomTile();

			attempts++;
		}
		while ((!tempTile.isWalkable() || tempTile.hasSupplies()
				|| tempTile.getUnit() != null || tempTile.getBuilding() != null)
				&& attempts <= maxAttempts);

		if (attempts <= maxAttempts)
		{
			tempTile.putSupplies();
			return true;
		}
		else
		{
			return false;
		}
	}

	private Building getClosestHQ(Point p)
	{
		int minDistance = width + height;
		Building tempBuilding = null;
		Building targetBuilding = null;

		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				if((tempBuilding = map.getTileAt(col, row).getBuilding()) != null)
					if(p.distance(col, row) < minDistance)
					{
						minDistance = (int)p.distance(col, row);
						targetBuilding = tempBuilding;
					}
			}

		return targetBuilding;		
	}

	public void placeHQs(int botsCount, int hqPerBot)
	{
		int hqsCount = botsCount * hqPerBot;
		int hqsPerLine = (int)Math.sqrt(hqsCount) + 2;

		int horizontalDist = (width / hqsPerLine);
		int verticalDist = (height / hqsPerLine);

		int minHDist = (horizontalDist * 3) / 4;
		int maxHDist = (horizontalDist * 5) / 4;

		int minVDist = (verticalDist * 3) / 4;
		int maxVDist = (verticalDist * 5) / 4;

		int maxRetries = width * height * 3;
		
		for(int botNum = 0 ; botNum < botsCount ; botNum++)
			for(int hqNum = 0 ; hqNum < hqPerBot ; hqNum++)
			{
				Building closestHQ;
				Point tempPos = new Point();

				int xDist, yDist;
				int retry = 0;

				do
				{
					tempPos.x = rand.nextInt(width);
					tempPos.y = rand.nextInt(height);

					closestHQ = getClosestHQ(tempPos);
					if(closestHQ == null)
					{
						xDist = horizontalDist;
						yDist = verticalDist;
						
//						tempPos.x = horizontalDist;
//						tempPos.y = verticalDist;
					}
					else
					{
						xDist = Math.abs(closestHQ.getX() - tempPos.x);
						yDist = Math.abs(closestHQ.getY() - tempPos.y);
					}
					
					if(tempPos.x > (width - minVDist) || tempPos.x < minVDist)
					{
						xDist = -1;
						yDist = -1;
					}
					
					if(tempPos.y > (height - minHDist) || tempPos.y < minHDist)
					{
						xDist = -1;
						yDist = -1;
					}
					
					retry++;
				}
				while(((xDist > maxHDist || xDist < minHDist) && (yDist > maxVDist || yDist < minVDist)) && (retry < maxRetries));
				
				if(retry == maxRetries)
					return;
				
				map.setTileType(tempPos.x, tempPos.y, TileType.ROCK);
				new Building.HeadQuarters(map, tempPos.x, tempPos.y, null).setTeamNumber(botNum);
			}
	}
}
