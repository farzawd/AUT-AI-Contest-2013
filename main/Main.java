package main;

import java.io.IOException;

import config.Config;

import game.GameFactory;

public class Main
{

	public static void printHelpMessage()
	{
		System.out.println("use the following arguments to run a game, or simply run the game without ant arguments to use the default settings:");
		System.out.println("-port\t\t[portNumber]\t\tsets the servers port number");
		System.out.println("-seed\t\t[seed]\t\t\tsets the game's seed(number)");
		System.out.println("-flat\t\t[flatness]\t\tsets the flatness of generated maps");
		System.out.println("-width\t\t[width]\t\t\tsets the mapSize.width");
		System.out.println("-height\t\t[height]\t\tsets the mapSize.height");
		System.out.println("-players\t[playersCount]\t\tsets the number of players in the game");
		System.out.println("-hqs\t\t[hqsCount]\t\tsets the number of HQs per players");
		System.out.println("-delay\t\t[delay(ms)]\t\tsets the amount of time between cycles");
		System.out.println("-cycle\t\t[max cycles]\t\tsets the cycle in which the game is force-finished");
		System.out.println("-map [\"path to map file\"]\t\tloads a specific map");
	}	

	private static enum ArgType
	{
		MAP, PORT, HELP, SEED, FLATNESS, MAP_WIDTH, MAP_HEIGHT, PLAYERS, HQS, DELAY, CYCLE, INVALID;
	}

	public static void main(String[] args)
	{
		int playersCount = 2;
		int mapWidth = 60;
		int mapHeight = 60;
		int gameSeed = 10000;
		int flatness = 8;
		int hqs = 1;
		String mapPath = null;

		try
		{
			ArgType type = ArgType.INVALID;
			for(String arg : args)
			{			
				switch(arg)
				{
					case "-help":
					case "-h":
					case "/?":
						type = ArgType.HELP;
						printHelpMessage();
						System.exit(0);
						break;
						
					case "-map":
						type = ArgType.MAP;
						break;

					case "-port":
						type = ArgType.PORT;
						break;

					case "-players":
						type = ArgType.PLAYERS;
						break;

					case "-hqs":
						type = ArgType.HQS;
						break;

					case "-width":
						type = ArgType.MAP_WIDTH;
						break;

					case "-height":
						type = ArgType.MAP_HEIGHT;
						break;
					case "-seed":
						type = ArgType.SEED;
						break;
						
					case "-flat":
						type = ArgType.FLATNESS;
						break;
						
					case "-delay":
						type = ArgType.DELAY;
						break;
						
					case "-cycle":
						type = ArgType.CYCLE;
						break;

					default:
						switch(type)
						{
							case HQS:
								hqs = Integer.parseInt(arg);
								Config.Game.startingSupplies = hqs * Config.Game.suppliesPerPack;
								break;
							case PLAYERS:
								playersCount = Integer.parseInt(arg);
								break;
							case PORT:
								Config.Game.serverPort = Integer.parseInt(arg);
								break;
							case MAP_HEIGHT:
								mapHeight = Integer.parseInt(arg);
								break;
							case MAP_WIDTH:
								mapWidth = Integer.parseInt(arg);
								break;
							case SEED:
								gameSeed = Integer.parseInt(arg);
								break;
							case FLATNESS:
								flatness = Integer.parseInt(arg);
								break;
							case DELAY:
								Config.Game.cycleTimeLimit = Integer.parseInt(arg);
								break;
							case CYCLE:
								Config.Game.cycleCountLimit = Integer.parseInt(arg);
								break;
							case MAP:
								mapPath = arg;
								break;
							case INVALID:
								printHelpMessage();
								System.exit(1);
								break;
							default:
								break;
						}

						type = ArgType.INVALID;
				}
			}
		}
		catch(Exception e)
		{
			printHelpMessage();
			System.exit(2);
		}
		
		System.out.println("AIC Server Version 2.2.2 - Use commandline argument \"-h\" for more info");
		System.out.println(String.format("Starting the server on port %d...", Config.Game.serverPort));
		System.out.println("Games' seed is set to " + gameSeed);
		System.out.println(String.format("Games will end after reaching cycle #%d. Thinking time between cycles is %dms.", Config.Game.cycleCountLimit, Config.Game.cycleTimeLimit));
		
		try
		{
			if(mapPath == null)
			{
				System.out.println(String.format("(%d,  %d) maps will be generated using %dx wall removals", mapWidth, mapHeight, flatness));
				System.out.println("There will be " + playersCount + " bots per game, with each one having " + hqs + " HQ(s)");
				new GameFactory(mapWidth, mapHeight, hqs, playersCount, gameSeed, flatness).startGames(1);
			}
			else
			{
				System.out.println("maps will be loaded from:\n\t" + mapPath);
				System.out.println("There will be " + playersCount + " bots per game.");
				new GameFactory(playersCount, mapPath).startGames(1);
			}
		}
		catch(IOException e)
		{
			System.out.println("Map file not found!");
		}
		catch(NullPointerException e)
		{
			System.err.println("The server has encountered a fatal error... Please check if port #" + Config.Game.serverPort + " is open on your system, and try again.");
		}
	}
}
