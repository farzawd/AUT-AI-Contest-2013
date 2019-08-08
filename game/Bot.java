package game;

import network.Communicator;
import io.connector.DataStore;
import config.Config;
import game.agent.Agent;
import game.map.Field;

/**
 * 
 * @author farzad
 * 
 */
public class Bot implements Runnable
{
	private int				teamNumber;

	private Field			gameField;

	private int				suppliesStock;

	private Communicator	communicator;
	private DataStore		dataStore;
	private DataStore		gameState;

	private double			cycleStartTime;

	private boolean			stopped;
	
	private int totalCaps;
	private int totalKills;
	
	private int losingCycle;

	public Bot(Field activeGameField, int teamNumber, Communicator communicator)
	{
		this.communicator = communicator;

		this.teamNumber = teamNumber;

		this.gameField = activeGameField;

		this.suppliesStock = Config.Game.startingSupplies;

		this.dataStore = new DataStore();
		this.gameState = new DataStore();
	}

	/**
	 * sets the amount of supplies currently available. WARNING: never use this
	 * or you might corrupt your information
	 * 
	 * @param suppliesAmount
	 */
	public void setSuppliesAmount(int suppliesAmount)
	{
		this.suppliesStock = suppliesAmount;
	}

	/**
	 * 
	 * @return the amount of supplies currently available
	 */
	public int getSuppliesAmount()
	{
		return this.suppliesStock;
	}

	public void addSupplies(int amount)
	{
		if(amount <= 0)
			return;

		suppliesStock += amount;
	}

	public void setTransmittedData(String data)
	{
		gameState.setData(data);
	}

	public void consumeSupplies(int amount)
	{
		if(amount <= 0)
			return;

		if(suppliesStock - amount < 0)
			return;

		suppliesStock -= amount;
	}

	/**
	 * 
	 * @return the game field this bot is currently playing on
	 */
	public Field getGameField()
	{
		return gameField;
	}

	/**
	 * 
	 * @return this bot's team number
	 */
	public int getTeamNumber()
	{
		return this.teamNumber;
	}

	public DataStore getDataStore()
	{
		return this.dataStore;
	}

	public int gatherMatchingAgents(int startingAgentID)
	{
		Agent tempAgent;
		int counter = 0;
		
		for(int col = 0 ; col < gameField.getWidth() ; col++)
			for(int row = 0 ; row < gameField.getHeight() ; row++)
			{
				tempAgent = gameField.getTileAt(col, row).getUnit();
				if(tempAgent != null && tempAgent.getTeamNumber() == teamNumber && tempAgent.getOwner() != this)
				{
					tempAgent.setOwner(this);
					tempAgent.setAgentID(startingAgentID + counter);
					counter++;
				}

				tempAgent = gameField.getTileAt(col, row).getBuilding();
				if(tempAgent != null && tempAgent.getTeamNumber() == teamNumber && tempAgent.getOwner() != this)
				{
					tempAgent.setOwner(this);
					tempAgent.setAgentID(startingAgentID + counter);
					counter++;
				}
			}
		
		return counter;
	}


	public synchronized double getCycleStartTime()
	{
		return this.cycleStartTime;
	}

	public synchronized void setCycleStartTime(double cycleStartTime)
	{
		this.cycleStartTime = cycleStartTime;
	}

	@Override
	public void run()
	{
		while(!isLost() && !stopped)
		{
			//wait for the next cycle
			while(gameState.getData().isEmpty() && !stopped);

			//send data
			communicator.sendData(gameState.getData());
			gameState.clearData();


			//receive commands
			dataStore.setData(communicator.recvData());

			//delay, maybe?

			double cycleStartTime = this.getCycleStartTime();
			double passedTime = System.currentTimeMillis() - cycleStartTime;
			double remainingTime = Config.Game.cycleTimeLimit - passedTime; 
			if(remainingTime > 0)
			{
				try
				{
					Thread.sleep((long)remainingTime);
				}
				catch (InterruptedException e)
				{
					System.err.println("InterruptException in Bot#" + teamNumber);
				}
			}
			else
			{
				System.err.println("Warning! Bot #" + teamNumber + " is failing to give its orders in time.");
			}
		}

		System.out.println("Bot #" + teamNumber + " eliminated!");
		this.stopActivity();
	}

	public void startActivity()
	{
		stopped = false;

		new Thread(this, String.format("bot #%d", teamNumber)).start();
	}

	public void stopActivity()
	{
		stopped = true;
		communicator.close();
	}

	public String getName()
	{
		switch (teamNumber)
		{
			case 0:
				return "green";
			case 1:
				return "white";
			case 2:
				return "blue";
			case 3:
				return "red";
			case 4:
				return "cyan";
			case 5:
				return "magenta";
			case 6:
				return "gray";
			case 7:
				return "yellow";
			case 8:
				return "black";
			default:
				return "pink";

		}

	}


	public Communicator getCommunicator()
	{
		return this.communicator;
	}

	public boolean isLost()
	{
		if(GameAnalyzer.getUnitsCountFor(gameField, this) == 0)
		{
			if(suppliesStock < 10)
				return true;
			if(GameAnalyzer.getBuildingsCountFor(gameField, this) == 0)
				return true;
		}
		
		if(communicator.isDisconnected())
			return true;

		return false;
	}
	
	public int getTotalKills()
	{
		return this.totalKills;
	}
	
	public void addKill(int increment)
	{
		if(increment <= 0)
			return;
		
		this.totalKills += increment;
	}
	
	public int getTotalCaptures()
	{
		return this.totalCaps;
	}
	
	public void addCapture(int increment)
	{
		if(increment <= 0)
			return;
		
		totalCaps += increment;
	}
	
	public int getLosingCycle()
	{
		return this.losingCycle;
	}

	public int getScore()
	{
		return GameAnalyzer.getScoreFor(gameField, this);
	}
}
