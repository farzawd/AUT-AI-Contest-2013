package game.agent;

import game.Bot;
import game.map.Field;
import io.command.Command;

import java.awt.Point;

import config.Config;


public abstract class Unit extends Agent
{
	private int			maxAttackRange2;

	private int			maxDamage;

	public Unit(Field map, Point position, Bot owner ,int health, int viewRadius2, int maxAttackRange2, int maxDamage)
	{
		super(map, position, owner, health, viewRadius2);

		this.maxAttackRange2 = maxAttackRange2;

		this.maxDamage = maxDamage;

		if(map.getTileAt(position).getUnit() == null)
			map.getTileAt(position).setUnit(this);
	}

	/**
	 * 
	 * @return the maximum damage for each of this unit's attacks
	 */
	public int getMaxDamage()
	{
		return this.maxDamage;
	}

	/**
	 * checks if this agent can attack the specified point
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInAttackRange(int x, int y)
	{
		int dx2 = (this.getX() - x) * (this.getX() - x);
		int dy2 = (this.getY() - y) * (this.getY() - y);

		int d2 = dx2 + dy2;

		return (d2 <= maxAttackRange2);
	}

	/**
	 * checks if this agent can attack the specified enemy
	 * @param enemy
	 * @return
	 */
	public boolean isInAttackRange(Agent enemy)
	{
		return isInAttackRange(enemy.getX(), enemy.getY());
	}

	/**
	 * moves this agent
	 * @param dest
	 */
	public boolean move(Point dest)
	{
		if(!isActionsLeft())
			return false;
		cripple();
		
		if(getActiveGameField().isOutOfField(dest.x, dest.y))
			return false;
		
		if(!getActiveGameField().getTileAt(dest).isWalkable())
			return false;
		
		if(getActiveGameField().getTileAt(dest).getUnit() != null)
			return false;

		if(getActiveGameField().getTileAt(dest).getBuilding() != null && getActiveGameField().getTileAt(dest).getBuilding().getTeamNumber() != this.getTeamNumber())
			return false;
		
		int distX = Math.abs(dest.x - getX());
		int distY = Math.abs(dest.y - getY());
		
		int dist = distX + distY;
		
		if(dist > 1)
			return false;

		Point from = new Point(getX(), getY());

		getActiveGameField().getTileAt(dest).setUnit(this);
		getActiveGameField().getTileAt(from).setUnit(null);
		
		setCommand(new Command.Move(getPosition(), dest));
		
		setPosition(dest);
		if(getActiveGameField().getTileAt(getPosition()).hasSupplies())
		{
			getActiveGameField().getTileAt(getPosition()).removeSupplies();
			getOwner().addSupplies(Config.Game.suppliesPerPack);
		}
		
		return true;
	}

	/**
	 * orders this unit to attack the specified point
	 * @param to
	 */
	public boolean attack(Point to)
	{
		if(!isActionsLeft())
			return false;
		cripple();
		
		if(getActiveGameField().isOutOfField(to.x, to.y))
			return false;
		
		if(!isInAttackRange(to.x, to.y))
			return false;
		
		Agent target = null;
		
		
		boolean isBuilding = false;
		target = getActiveGameField().getTileAt(to).getUnit();
		if(target == null)
		{
			isBuilding = true;
			target = getActiveGameField().getTileAt(to).getBuilding();
		}
		
		if(target == null)
			return false;
		
		target.damageHealth(getMaxDamage());
		
		if(isBuilding)
			((Building)target).setCapturer(this);
		
		setCommand(new Command.Attack(getPosition(), to));
		getCommand().setTarget(target);
		
		return true;
	}

	/**
	 * orders this unit to attack the specified point
	 * @param x
	 * @param y
	 */
	public boolean attack(int x, int y)
	{
		return attack(new Point(x, y));
	}

	/**
	 * orders this unit to attack the specified enemy agent
	 * @param targetEnemy
	 */
	public boolean attack(Agent targetEnemy)
	{
		return attack(targetEnemy.getX(), targetEnemy.getY());
	}

	/**
	 * represents a melee unit
	 * @author farzad
	 *
	 */
	public static final class Melee extends Unit
	{
		public Melee(Field map, int x, int y, Bot owner)
		{
			super(map, new Point(x, y), owner, Config.Unit.Melee.maxHealth, Config.Unit.Melee.viewRadius2, Config.Unit.Melee.maxAttackRange2, Config.Unit.Melee.maxDamage);
			
			setAgentTypeID('a');
		}
		
		public String toString()
		{
			return String.format("m %d %d %d %d %d", getX(), getY(), getTeamNumber(), getHealth(), getAgentID());
		}
	}

	/**
	 * represents a ranged unit
	 * @author farzad
	 *
	 */
	public static final class Ranged extends Unit
	{
		public Ranged(Field map, int x, int y, Bot owner)
		{
			super(map, new Point(x, y), owner, Config.Unit.Ranged.maxHealth, Config.Unit.Ranged.viewRadius2, Config.Unit.Ranged.maxAttackRange2, Config.Unit.Ranged.maxDamage);
			
			setAgentTypeID('A');
		}
		
		public String toString()
		{
			return String.format("r %d %d %d %d %d", getX(), getY(), getTeamNumber(), getHealth(), getAgentID());
		}
	}

	/**
	 * represents a unit's type. do not change the order of the values, as it matters while spawning new units.
	 * @author farzad
	 *
	 */
	public enum UnitType
	{
		MELEE, RANGED;
	}
}