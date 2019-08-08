package game;

import game.map.Field;

import java.util.ArrayList;

public class GameHandler implements Runnable
{
	private ArrayList<Bot> winners;
	private boolean finished;
	
	private ArrayList<Bot> bots;
	private int gameSeed;
	private Field map;
	private int playersCount;
	
	public GameHandler(int playersCount, Field map, int gameSeed, ArrayList<Bot> bots)
	{
		finished = false;
		winners = null;
		
		this.playersCount = playersCount;
		this.map = map;
		this.gameSeed = gameSeed;
		this.bots = bots;
	}
	
	@Override
	public void run()
	{
		winners = Game.runGame(playersCount, map, gameSeed, bots);
		finished = true;
	}
	
	public ArrayList<Bot> getWinners()
	{
		return this.winners;
	}
	
	public boolean isFinished()
	{
		return finished;
	}

}
