package game;

import game.map.Field;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import config.Config;

public class Game
{
	
	/**
	 * 
	 * @param playersCount
	 * @param map
	 * @param gameSeed
	 * @param bots
	 * @return games winners
	 */
	public static ArrayList<Bot> runGame(int playersCount, Field map, int gameSeed, ArrayList<Bot> bots)
	{
		Director director;
		
		System.out.println("Configuring the bots...");
		for(Bot bot : bots)
		{
			//send configs
			System.out.print(String.format("\tSending configurations for bot #%d... ", bot.getTeamNumber()));
			bot.getCommunicator().sendData(getGameConfig(bot));
			System.out.println("done.");
		}
		
		System.out.println("Starting the bots...");
		int counter = 0;
		for(Bot bot : bots)
		{
			bot.startActivity();
			System.out.println(String.format("\tBot #%d is set.", counter));
			
			counter++;
		}
		
		director = new Director(bots, map, gameSeed);
		ArrayList<Bot> winners = director.runGame();
		
		String message = null;
		String scores = null;
		
		if(winners.size() == 0)
		{
			message = "The game finished with no winners.";
		}
		else
		{
			message = "The following bot(s) have won this match:\n";
			
			for(Bot bot : winners)
			{
				message += String.format("-Bot #%d (Score: %d)\n", bot.getTeamNumber(), bot.getScore());
			}
		}
		
		scores = "Scores are as following:\n";
		for(Bot bot : bots)
		{
			scores += String.format("-Bot #%d (%s): %d\n", bot.getTeamNumber(), bot.getName(), bot.getScore());
		}
		
		for(Bot bot : bots)
			bot.stopActivity();
		
		System.out.println(message);
		JOptionPane.showMessageDialog(null, message);
		
		System.out.println(scores);
		JOptionPane.showMessageDialog(null, scores);
		
		director.close();
		
		return winners;
	}
	
	private static String getGameConfig(Bot bot)
	{
		String tempStr = Config.Communicator.Tags.configStartTag + "\n";
		
		tempStr += String.format("w %d\n", bot.getGameField().getWidth());
		tempStr += String.format("h %d\n", bot.getGameField().getHeight());
		tempStr += String.format("o %d\n", bot.getTeamNumber());
		tempStr += String.format("t %d\n", Config.Game.cycleTimeLimit);
		tempStr += String.format("c %d\n", Config.Game.cycleCountLimit);
		
		tempStr += Config.Communicator.Tags.configEndTag + "\n";
		
		return tempStr;
	}
}
