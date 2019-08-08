package io.command;

import game.agent.Unit.UnitType;
import java.awt.Point;
import java.util.ArrayList;

public class CommandParser
{
	public static Command parseCommand(String commandStr, int cmdOwner)
	{
		Command cmd = null;
		
		String[] commandParts = commandStr.split(" ");
		int[] args = new int[4];
		
		try
		{
			if(commandParts[0].equals("mov"))
			{
				args[0] = Integer.parseInt(commandParts[1]);
				args[1] = Integer.parseInt(commandParts[2]);
				args[2] = Integer.parseInt(commandParts[3]);
				args[3] = Integer.parseInt(commandParts[4]);
				
				Point from = new Point(args[0], args[1]);
				Point to = new Point(args[2], args[3]);
				
				cmd = new Command.Move(from, to);
			}
			else if(commandParts[0].equals("atk"))
			{
				args[0] = Integer.parseInt(commandParts[1]);
				args[1] = Integer.parseInt(commandParts[2]);
				args[2] = Integer.parseInt(commandParts[3]);
				args[3] = Integer.parseInt(commandParts[4]);
				
				Point from = new Point(args[0], args[1]);
				Point to = new Point(args[2], args[3]);
				
				cmd = new Command.Attack(from, to);
				
			}
			else if(commandParts[0].equals("spw"))
			{
				args[0] = Integer.parseInt(commandParts[1]);
				args[1] = Integer.parseInt(commandParts[2]);
				args[2] = Integer.parseInt(commandParts[3]);
				
				Point pos = new Point(args[0], args[1]);
				UnitType unitType = UnitType.values()[args[2]];
				
				
				cmd = new Command.Spawn(pos, unitType);				
			}
		}
		catch(ArrayIndexOutOfBoundsException | NumberFormatException e)
		{
			System.err.println("invalid arguments for the command <" + commandStr + ">");
			return null;
		}
		
		if(cmd != null)
			cmd.setOwner(cmdOwner);
		
		return cmd;
	}
	
	public static ArrayList<Command> parseAllCommands(String commandsStr, int cmdOwner)
	{
		ArrayList<Command> commands = new ArrayList<Command>();
		
		String[] commandLines = commandsStr.split("\n");
		
		for(String line : commandLines)
		{
			Command tmpCmd = parseCommand(line, cmdOwner);
			
			if(tmpCmd != null)
				commands.add(tmpCmd);
		}
		
		return commands;
	}
}
