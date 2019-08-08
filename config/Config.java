package config;

/**
 * stores the game config data
 * 
 * @author farzad
 * 
 */
public class Config
{
	public static abstract class Communicator
	{
		public static abstract class Tags
		{
			public static String	packetStartTag	= "start";
			public static String	packetEndTag	= "end";

			public static String	configStartTag	= "config";
			public static String	tilesStartTag	= "tile";
			public static String	dataStartTag	= "data";
			public static String	agentsStartTag	= "agent";

			public static String	configEndTag	= "/config";
			public static String	tilesEndTag		= "/tile";
			public static String	dataEndTag		= "/data";
			public static String	agentsEndTag	= "/agent";
		}
	}

	public static abstract class Game
	{
		public static int		cycleTimeLimit	= 200;
		public static int		cycleCountLimit	= 2500;

		public static int		suppliesPerPack	= 10;
		public static int		startingSupplies = 40;

		public static String	serverAddress	= "127.0.0.1";
		public static int		serverPort		= 10000;
	}

	public static abstract class Unit
	{
		public static abstract class Melee
		{
			public static int	viewRadius2		= 18;
			public static int	maxAttackRange2	= 2;
			public static int	maxDamage		= 900;
			public static int	maxHealth		= 900;
		}

		public static abstract class Ranged
		{
			public static int	viewRadius2		= 2;
			public static int	maxAttackRange2	= 12;
			public static int	maxDamage		= 300;
			public static int	maxHealth		= 300;
		}
	}

	public static abstract class Building
	{
		public static class HeadQuarters
		{
			public static int	viewRadius2	= 30;
			public static int	maxHealth	= 3600;
			public static int	healthRegen	= 300;
		}
	}

}
