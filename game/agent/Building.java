package game.agent;

import game.Bot;
import game.agent.Unit.UnitType;
import game.map.Field;

import io.command.Command;

import java.awt.Point;

import config.Config;

/**
 * represents a building in the game
 * 
 * @author farzad
 * 
 */
public abstract class Building extends Agent
{
	private Agent capturer;
	
	public Building(Field map, Point position, Bot owner, int health, int viewRadius2)
	{
		super(map, position, owner, health, viewRadius2);
		
		this.capturer = null;
		
		if(map.getTileAt(position).getBuilding() == null)
			map.getTileAt(position).setBuilding(this);
	}
	
	public void setCapturer(Agent agent)
	{
		this.capturer = agent;
	}
	
	public Agent getCapturer()
	{
		return this.capturer;
	}

	/**
	 * represents the HeadQuarters (main building)
	 * 
	 * @author farzad
	 * 
	 */
	public static final class HeadQuarters extends Building
	{
		public HeadQuarters(Field map, int x, int y, Bot owner)
		{
			super(map, new Point(x, y), owner, Config.Building.HeadQuarters.maxHealth,
					Config.Building.HeadQuarters.viewRadius2);
			
			setAgentTypeID('0');
		}
		
		public String toString()
		{
			return String.format("b %d %d %d %d %d", getX(), getY(), getTeamNumber(), getHealth(), getAgentID());
		}
		
		/**
		 * orders this headquarters building to spawn a unit in its current position.
		 * @param unitType
		 */
		public Unit spawnUnit(UnitType unitType, int agentID)
		{
			if(!isAlive())
				return null;
			
			if(!isActionsLeft())
				return null;
			cripple();
			
			if(getActiveGameField().getTileAt(getPosition()).getUnit() != null)
				return null;
			
			if(getOwner().getSuppliesAmount() < 10)
				return null;
			
			getOwner().consumeSupplies(10);
			
			Unit unit = null;
			
			switch(unitType)
			{
				case MELEE:
					unit = new Unit.Melee(getActiveGameField(), getX(), getY(), getOwner());
					unit.setAgentID(agentID);
					break;
				case RANGED:
					unit = new Unit.Ranged(getActiveGameField(), getX(), getY(), getOwner());
					unit.setAgentID(agentID);
					break;				
			}
			
			this.setCommand(new Command.Spawn(getPosition(), unitType));
			
			return unit;
		}
	}
}
