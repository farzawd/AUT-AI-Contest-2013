package game.agent;

import game.Bot;
import game.map.Field;
import io.command.Command;

import java.awt.Point;

/**
 * represents an agent in the game
 * 
 * @author farzad
 * 
 */
public abstract class Agent
{
	private int		viewRadius2;

	private int		maxHealth;
	private int		health;
	private boolean	alive;

	private Field	activeGameField;
	private Point	position;
	private Point	lastPosition;

	private Bot		owner;
	private int		teamNumber;

	private Command	command;

	private boolean	actionsLeft;

	private char	agentTypeID;
	private int		agentID;

	public Agent(Field map, Point position, Bot owner, int health,
			int viewRadius2)
	{
		setOwner(owner);

		agentID = -1;

		this.activeGameField = map;
		this.position = position;
		this.lastPosition = new Point(position);

		this.maxHealth = health;
		this.health = health;

		if(health > 0)
			alive = true;
		else
			alive = false;

		this.viewRadius2 = viewRadius2;

		actionsLeft = false;
	}

	public int getMaxHealth()
	{
		return this.maxHealth;
	}

	public void setAgentID(int id)
	{
		this.agentID = id;
	}

	public int getAgentID()
	{
		return this.agentID;
	}

	public char getAgentTypeID()
	{
		return this.agentTypeID;
	}

	public void setAgentTypeID(char id)
	{
		this.agentTypeID = id;
	}

	/**
	 * checks if this agent can see the specified point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public final boolean isInViewRange(int x, int y)
	{
		int dx2 = (this.position.x - x) * (this.position.x - x);
		int dy2 = (this.position.y - y) * (this.position.y - y);

		int d2 = dx2 + dy2;

		return (d2 <= viewRadius2);
	}

	/**
	 * 
	 * @return the agents position.x
	 */
	public int getX()
	{
		return position.x;
	}

	public boolean isActionsLeft()
	{
		return actionsLeft;
	}

	/**
	 * enables this agent to do a job
	 */
	public void resetActions()
	{
		this.actionsLeft = true;
	}

	/**
	 * unables this agent to perform any action
	 */
	public void cripple()
	{
		this.actionsLeft = false;
	}

	/**
	 * 
	 * @return the agents position.y
	 */
	public int getY()
	{
		return position.y;
	}

	public Point getPosition()
	{
		return this.position;
	}

	public void setPosition(Point position)
	{
		this.lastPosition.x = this.position.x;
		this.lastPosition.y = this.position.y;

		this.position.x = position.x;
		this.position.y = position.y;
	}

	public void setLastPosition(Point lastPosition)
	{
		this.lastPosition.x = lastPosition.x;
		this.lastPosition.y = lastPosition.y;
	}

	public Point getLastPosition()
	{
		return this.lastPosition;
	}

	/**
	 * 
	 * @return this agent's team number
	 */
	public int getTeamNumber()
	{
		return teamNumber;
	}

	/**
	 * sets this agent's team number
	 * 
	 * @param teamNumber
	 */
	public Agent setTeamNumber(int teamNumber)
	{
		this.teamNumber = teamNumber;

		return this;
	}

	public Bot getOwner()
	{
		return this.owner;
	}

	public void setOwner(Bot owner)
	{
		this.owner = owner;

		if(owner != null)
			this.teamNumber = owner.getTeamNumber();
		else
			this.teamNumber = -1;
	}

	/**
	 * 
	 * @return the remaining health of this agent
	 */
	public int getHealth()
	{
		return health;
	}

	public void heal(int amount)
	{
		if(amount <= 0)
			return;

		health += amount;

		if(health > maxHealth)
		{
			health = maxHealth;
		}
	}

	public void damageHealth(int amount)
	{
		if(amount <= 0)
			return;

		health -= amount;

		if(health <= 0)
		{
			health = 0;
			alive = false;
		}
	}

	public boolean isAlive()
	{
		return this.alive;
	}

	public void revive()
	{
		this.alive = true;
	}

	public Field getActiveGameField()
	{
		return activeGameField;
	}

	public int getViewRadius2()
	{
		return this.viewRadius2;
	}

	/**
	 * 
	 * @return the command that this agent must execute
	 */
	public Command getCommand()
	{
		return command;
	}

	public Agent setCommand(Command command)
	{
		this.command = command;

		return this;
	}
}
