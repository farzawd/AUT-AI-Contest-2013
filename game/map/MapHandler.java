package game.map;

import java.awt.Point;

import config.Config;

import game.Bot;
import game.agent.Agent;
import game.agent.Building;
import game.map.Field.Tile;

public class MapHandler
{
	private Field map;

	private int width;
	private int height;

	public MapHandler(Field map)
	{
		this.map = map;

		this.width = map.getWidth();
		this.height = map.getHeight();
	}

	/**
	 * cleans the map by removing corpses
	 * @return number of corpses
	 */
	public int cleanCorpsesUp()
	{
		int corpseCount = 0;

		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				if(map.getTileAt(col, row).getBuilding() != null && !map.getTileAt(col, row).getBuilding().isAlive())
				{
					if(map.getTileAt(col, row).getBuilding().getCapturer() != null)
					{
						Bot killer = map.getTileAt(col, row).getBuilding().getCapturer().getOwner();
						map.getTileAt(col, row).getBuilding().setOwner(killer);
						map.getTileAt(col, row).getBuilding().setCapturer(null);
						map.getTileAt(col, row).getBuilding().revive();
					}
					else
					{
						map.getTileAt(col, row).setBuilding(null);
					}
					corpseCount++;
				}
				if(map.getTileAt(col, row).getUnit() != null && !map.getTileAt(col, row).getUnit().isAlive())
				{
					map.getTileAt(col, row).setUnit(null);
					corpseCount++;
				}
			}

		return corpseCount;
	}

	public int cleanSuppliesUp()
	{
		int suppliesCount = 0;

		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				if(map.getTileAt(col, row).hasSupplies())
				{
					if(!map.getTileAt(col, row).isWalkable() || (map.getTileAt(col, row).getBuilding() != null))
					{
						suppliesCount++;
						map.getTileAt(col, row).removeSupplies();
					}
				}
			}

		return suppliesCount;
	}

	/**
	 * re-enables all units in the game to perform an action
	 */
	public void resetAgentActions()
	{
		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				Agent tempAgent = map.getTileAt(col, row).getUnit();
				if(tempAgent != null)
				{
					tempAgent.resetActions();
					tempAgent.setCommand(null);
					tempAgent.setLastPosition(tempAgent.getPosition());
				}

				tempAgent = map.getTileAt(col, row).getBuilding();
				if(tempAgent != null)
				{
					tempAgent.resetActions();
					tempAgent.setCommand(null);
					tempAgent.setLastPosition(tempAgent.getPosition());

					if(((Building)tempAgent).getCapturer() == null)
						tempAgent.heal(Config.Building.HeadQuarters.healthRegen);
					else if(tempAgent.isAlive())
						((Building)tempAgent).setCapturer(null);
				}
			}
	}

	public char[][] getPrintableMap()
	{
		char[][] tempMap = new char [width][height];

		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				Tile tempTile = map.getTileAt(col, row);

				if(tempTile.getUnit() != null)
					tempMap[col][row] = (char)(tempTile.getUnit().getAgentTypeID() + tempTile.getUnit().getTeamNumber());
				else if(tempTile.getBuilding() != null)
					tempMap[col][row] = (char)(tempTile.getBuilding().getTeamNumber() + '0');
				else if(tempTile.hasSupplies())
					tempMap[col][row] = 'X';
				else if(!tempTile.isWalkable())
					tempMap[col][row] = '#';
				else
					tempMap[col][row] = '.';
			}

		return tempMap;
	}

	private void fillCircle(boolean[][] grid, int x, int y, int radius2)
	{		
		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				int distance2 = (int)Point.distanceSq(x, y, col, row);

				if(distance2 <= radius2)
					grid[col][row] = true;
			}
	}

	public String getTransmittableMapFor(int teamNumber)
	{
		boolean[][] isVisible = new boolean[width][height];

		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
				isVisible[col][row] = false;

		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				Agent tempAgent = map.getTileAt(col, row).getBuilding();
				if(tempAgent != null)
					if(tempAgent.getTeamNumber() == teamNumber)
						fillCircle(isVisible, col, row, tempAgent.getViewRadius2());

				tempAgent = map.getTileAt(col, row).getUnit();
				if(tempAgent != null)
					if(tempAgent.getTeamNumber() == teamNumber)
						fillCircle(isVisible, col, row, tempAgent.getViewRadius2());
			}

		String tempStr = "";

		//add agents data
		tempStr += Config.Communicator.Tags.agentsStartTag + "\n";
		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				if(!isVisible[col][row])
					continue;

				Agent tempAgent = map.getTileAt(col, row).getBuilding();
				if(tempAgent != null)
					tempStr += tempAgent.toString() + "\n";

				tempAgent = map.getTileAt(col, row).getUnit();
				if(tempAgent != null)
					tempStr += tempAgent.toString() + "\n";
			}
		tempStr += Config.Communicator.Tags.agentsEndTag + "\n";


		//add tiles data
		tempStr += Config.Communicator.Tags.tilesStartTag + "\n";
		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				if(!isVisible[col][row])
					continue;

				Tile tempTile = map.getTileAt(col, row);

				if(!tempTile.isWalkable())
					tempStr += String.format("l %d %d\n", col, row);
				else if(tempTile.hasSupplies())
					tempStr += String.format("s %d %d\n", col, row);
			}
		tempStr += Config.Communicator.Tags.tilesEndTag + "\n";

		return tempStr;
	}

	public String getTransmittableMap()
	{
		String tempStr = "";

		//add agents data
		tempStr += Config.Communicator.Tags.agentsStartTag + "\n";
		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				Agent tempAgent = map.getTileAt(col, row).getBuilding();
				if(tempAgent != null)
					tempStr += tempAgent.toString() + "\n";

				tempAgent = map.getTileAt(col, row).getUnit();
				if(tempAgent != null)
					tempStr += tempAgent.toString() + "\n";
			}
		tempStr += Config.Communicator.Tags.agentsEndTag + "\n";


		//add tiles data
		tempStr += Config.Communicator.Tags.tilesStartTag + "\n";
		for(int col = 0 ; col < width ; col++)
			for(int row = 0 ; row < height ; row++)
			{
				Tile tempTile = map.getTileAt(col, row);

				if(!tempTile.isWalkable())
					tempStr += String.format("l %d %d\n", col, row);
				else if(tempTile.hasSupplies())
					tempStr += String.format("s %d %d\n", col, row);
			}
		tempStr += Config.Communicator.Tags.tilesEndTag + "\n";

		return tempStr;
	}
}
