package game;

import java.io.IOException;
import java.util.ArrayList;

import config.Config;
import network.Receptor;
import game.map.Field;
import game.map.MapGenerator;
import game.map.MapManager;

public class GameFactory
{
	private Receptor receptor;

	private int playersCount;

	private int hqPerBot;
	private int mapWidth;
	private int mapHeight;

	private int gameSeed;
	private int mapFlatness;
	
	private String mapPath;
	private boolean isMapFromFile;

	public GameFactory(int mapWidth, int mapHeight, int hqPerBot, int playersCount, int gameSeed, int flatness)
	{
		this.isMapFromFile = false;
		this.mapPath = null;
		
		this.hqPerBot = hqPerBot;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;

		this.gameSeed = gameSeed;
		this.mapFlatness = flatness;

		receptor = new Receptor(Config.Game.serverPort);

		this.playersCount = playersCount;
	}
	
	public GameFactory(int playersCount, String mapPath)
	{
		this.isMapFromFile = true;
		this.mapPath = mapPath;

		receptor = new Receptor(Config.Game.serverPort);

		this.playersCount = playersCount;
	}

	private void runOneGame(ArrayList<Bot> bots, Field map)
	{
		//		return Game.runGame(playersCount, map, gameSeed, bots);
		new Thread(new GameHandler(playersCount, map, gameSeed, bots)).start();
	}

	public void startGames(int gamesCount) throws IOException
	{
		for(int gameNum = 0 ; gameNum < gamesCount ; gameNum++)
		{
			ArrayList<Bot> bots = new ArrayList<Bot>();

			Field map;

			if(!isMapFromFile)
			{
				map = new Field(mapWidth, mapHeight);
				MapGenerator mapGen = new MapGenerator(map, gameSeed);

				mapGen.generateRandomMap(mapFlatness);
				mapGen.placeHQs(playersCount, hqPerBot);
			}
			else
			{
				map = MapManager.loadMap(mapPath);
			}

			System.out.println("Waiting for clients...");
			for(int i = 0 ; i < playersCount ; i++)
			{
				bots.add(new Bot(map, i, receptor.acceptClient()));
				System.out.println(String.format("\tBot #%d connected.", i));
			}

			runOneGame(bots, map);
		}

		receptor.close();
	}
}
