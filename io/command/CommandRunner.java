package io.command;

import io.command.Command.CommandType;

import java.awt.Point;
import java.util.ArrayList;

import game.agent.Agent;
import game.agent.Building;
import game.agent.Unit.UnitType;
import game.agent.Unit;
import game.map.Field;

public class CommandRunner
{
	public static boolean runUnitCmd(Field map, Command cmd)
	{
		Unit unit;
		try
		{
			unit = (Unit)getCmdOwner(map, cmd);
		}
		catch(Exception e)
		{
			return false;
		}

		if(unit == null)
			return false;

		if(cmd.getOwner() != unit.getTeamNumber())
			return false;

		switch(cmd.getType())
		{
			case ATTACK:
				return unit.attack(new Point(cmd.getArgs()[2], cmd.getArgs()[3]));

			case MOVE:
				return unit.move(new Point(cmd.getArgs()[2], cmd.getArgs()[3]));

			default:
				return false;			
		}
	}

	public static boolean runBuildingCmd(Field map, Command cmd, int agentID)
	{
		Building.HeadQuarters building;
		try
		{
			building = (Building.HeadQuarters)getCmdOwner(map, cmd);
		}
		catch(Exception e)
		{
			return false;
		}

		if(building == null)
			return false;

		if(cmd.getOwner() != building.getTeamNumber())
			return false;

		switch(cmd.getType())
		{
			case SPAWN:
				if(building.spawnUnit(UnitType.values()[cmd.getArgs()[2]], agentID) != null)
					return true;
				return false;

			default:
				return false;
		}
	}

	public static Agent getCmdOwner(Field map, Command cmd)
	{
		switch(cmd.getType())
		{
			case ATTACK:
			case MOVE:
				return map.getTileAt(cmd.getArgs()[0], cmd.getArgs()[1]).getUnit();
			case SPAWN:
			case WAIT:
			default:
				return map.getTileAt(cmd.getArgs()[0], cmd.getArgs()[1]).getBuilding();
		}
	}

	public static int runSpawnCmds(Field map, ArrayList<Command> cmds, int startingUnitID)
	{
		int count = 0;

		for(Command cmd : cmds)
		{
			if(cmd.getType() == CommandType.SPAWN)
				if(runBuildingCmd(map, cmd, startingUnitID + count))
					count++;
		}

		return count;
	}

	public static int runMoveCmds(Field map, ArrayList<Command> cmds)
	{
		int count = 0;
		int totalCount = 0;

		do
		{
			count = 0;
			for(Command cmd : cmds)
			{
				Agent cmdOwner = getCmdOwner(map, cmd);
				if(cmdOwner == null)
					cmd.setDone(true);
				
				else if(!cmd.isDone() && cmd.getType() == CommandType.MOVE)
				{
					if(runUnitCmd(map, cmd))
					{
						count++;
						cmd.setDone(true);
					}
					else
						cmdOwner.resetActions();
				}
			}
			totalCount += count;
		}
		while(count != 0);

		return totalCount;
	}

	public static int runAttackCmds(Field map, ArrayList<Command> cmds)
	{
		int count = 0;

		for(Command cmd : cmds)
		{
			if(cmd.getType() == CommandType.ATTACK)
				if(runUnitCmd(map, cmd))
					count++;
		}

		return count;
	}
}
