package game;

import game.agent.Agent;
import game.map.Field;
import game.map.MapGenerator;
import game.map.MapHandler;

import io.command.Command;
import io.command.CommandParser;
import io.command.CommandRunner;

import java.util.ArrayList;

import visualizer.GraphicalOutput;

import config.Config;

public class Director
{
	private ArrayList<Bot> bots;

	private Field gameField;
	private MapHandler mapHandler;

	private MapGenerator mapGenerator;

	private GraphicalOutput view;

	private int lastAgentID;

	public Director(ArrayList<Bot> bots, Field gameField, int gameSeed)
	{
		this.lastAgentID = 0;

		this.bots = bots;

		this.gameField = gameField;
		this.mapHandler = new MapHandler(gameField);
		this.mapGenerator = new MapGenerator(gameField, gameSeed);

		this.view = new GraphicalOutput(gameField);
		view.update(bots, 0);

		mapHandler.cleanCorpsesUp();
		mapHandler.cleanSuppliesUp();
		mapHandler.resetAgentActions();

		for(Bot bot : bots)
			lastAgentID += bot.gatherMatchingAgents(lastAgentID);

		for(int i = 0 ; i < gameField.getWidth() ; i++)
		{
			for(int j = 0 ; j < gameField.getHeight() ; j++)
			{
				Agent tempAgent = gameField.getTileAt(i, j).getBuilding();
				if(tempAgent != null && tempAgent.getAgentID() < 0)
				{
					tempAgent.setAgentID(lastAgentID);
					lastAgentID++;
				}
				
				tempAgent = gameField.getTileAt(i, j).getUnit();
				if(tempAgent != null && tempAgent.getAgentID() < 0)
				{
					tempAgent.setAgentID(lastAgentID);
					lastAgentID++;
				}
			}		
		}
	}

	private int countRemainingBots()
	{
		int cnt = 0;

		for(Bot bot : bots)
			if(!bot.isLost())
				cnt++;

		return cnt;
	}

	private boolean isGameOver(int cycleNumber)
	{
		if(cycleNumber >= Config.Game.cycleCountLimit)
			return true;

		if(countRemainingBots() <= 1)
			return true;

		return false;
	}

	private ArrayList<Bot> getWinners()
	{
		int maxScore = 0;
		ArrayList<Bot> winners = new ArrayList<Bot>();

		for(Bot bot : bots)
			if(bot.getScore() > maxScore)
			{
				maxScore = bot.getScore();
				winners.clear();
				winners.add(bot);
			}
			else if(bot.getScore() == maxScore)
				winners.add(bot);

		return winners;
	}

	public ArrayList<Bot> runGame()
	{
		int i = 0;
		while(!isGameOver(i))//the game is not over
		{
			runCycle(i);
			i++;
		}

		runCycle(i);

		return getWinners();
		//throw new UnsupportedOperationException();
	}

	private void runCycle(int cycleNumber)
	{
		if(cycleNumber % 50 == 0)
			System.out.println("Running cycle #" + cycleNumber);

//		//this should be here
//		//cleanup
//		mapHandler.cleanCorpsesUp();


		//send data
		for(Bot bot : bots)
		{
			bot.setCycleStartTime(System.currentTimeMillis());
			bot.setTransmittedData(getCycleDataFor(bot, cycleNumber));

		}

		//delay and graphics update
		view.update(bots, cycleNumber);

//		//this should not be here
//		//cleanup
		mapHandler.cleanCorpsesUp();
		
		//reset actions
		mapHandler.resetAgentActions();


		//gather commands
		String commandsStr = "";

		ArrayList<Command> commands = new ArrayList<Command>();

		for(Bot bot : bots)
		{
			commandsStr = bot.getDataStore().getData() + "\n";
			bot.getDataStore().clearData();

			ArrayList<Command> tmpCmds = CommandParser.parseAllCommands(commandsStr, bot.getTeamNumber());

			for(Command cmd : tmpCmds)
				commands.add(cmd);
		}


		//run commands

		//attack
		CommandRunner.runAttackCmds(gameField, commands);
		//move
		CommandRunner.runMoveCmds(gameField, commands);		
		//spawn
		lastAgentID += CommandRunner.runSpawnCmds(gameField, commands, lastAgentID);

		//put some supplies
		mapGenerator.placeRandomSupplies();

		//gather captured agents
		for(Bot bot : bots)
			lastAgentID += bot.gatherMatchingAgents(lastAgentID);
	}

	public String getCycleDataFor(Bot bot, int cycleNumber)
	{
		String tempStr = Config.Communicator.Tags.dataStartTag + "\n";

		tempStr += String.format("s %d\n", bot.getSuppliesAmount());
		tempStr += String.format("c %d\n", cycleNumber);

		tempStr += Config.Communicator.Tags.dataEndTag + "\n";

		tempStr += mapHandler.getTransmittableMapFor(bot.getTeamNumber());

		return tempStr;
	}

	public void close()
	{
		view.close();
	}
}
