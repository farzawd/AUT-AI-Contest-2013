package visualizer;

import java.util.ArrayList;

import game.Bot;
import game.map.Field;

import javax.swing.JFrame;
import config.Config;

public class GraphicalOutput
{
	GamePanel gamePanel;
	InfoPanel infoPanel;
	JFrame gameFrame;

	private int animFPS = 16;
	private int animFPC;
	private int animDelay;
	
	private int gWidth = 800;
	private int gHeight = 635;
	
	Field map;

	public GraphicalOutput(Field map)
	{
		this.map = map;
		int width = map.getWidth();
		int height = map.getHeight();
		
		gameFrame = new JFrame(String.format("GameView (%d, %d)", width, height));
		gameFrame.setLayout(null);
		gameFrame.setResizable(false);
		gameFrame.setSize(gWidth, gHeight);

		gamePanel = new GamePanel(width, height);
		gamePanel.setLocation(0, 0);

		infoPanel = new InfoPanel();
		infoPanel.setLocation(gWidth - 200, 0);

		gameFrame.add(gamePanel);
		gameFrame.add(infoPanel);
		gameFrame.setVisible(true);

		animFPC = (animFPS * Config.Game.cycleTimeLimit) / 1000;

		if(animFPC == 0)
			animFPC = 1;


		//		animDelay = (1000 / animFPS);

		animDelay = (Config.Game.cycleTimeLimit / animFPC);
		gamePanel.setMaxFrames(animFPC);
		
		gamePanel.setMap(map);
	}

	public void update(ArrayList<Bot> bots, int cycleNumber)
	{
		infoPanel.update(cycleNumber, bots);

		double startingTime = System.currentTimeMillis();

		for(int frameNum = 1 ; frameNum <= animFPC ; frameNum++)
		{
			gamePanel.setFrameNum(frameNum);
			gamePanel.repaint();
			try
			{
				Thread.sleep(animDelay);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int passedTime = (int)(System.currentTimeMillis() - startingTime);

		int remainingTime = Config.Game.cycleTimeLimit - passedTime;
		
		if(remainingTime > animFPC)
		{
			animDelay += remainingTime / animFPC;
		}
		else if(remainingTime > 0)
		{
			try
			{
				Thread.sleep(remainingTime);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(remainingTime <= -animFPC && animDelay > 0)
			animDelay--;
		else if(remainingTime <= -animFPC && animFPC > 1)
		{
			System.err.println("WARNING! System Overload. Disabling Animations. " + remainingTime);
			
			animFPC = 1;
			animDelay = Config.Game.cycleTimeLimit;
			gamePanel.setMaxFrames(animFPC);
		}
	}

	public void close()
	{
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.dispose();

		//an alternative way:
		//		WindowEvent wev = new WindowEvent(gameFrame, WindowEvent.WINDOW_CLOSING);
		//		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
}
