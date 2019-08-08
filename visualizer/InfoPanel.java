package visualizer;

import game.Bot;
import game.GameAnalyzer;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InfoPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2L;
	private int graphicalWidth = 200;
	private int graphicalHeight = 600;

	private JScrollPane scroll;
	private JTextArea cycleInfo;

	public InfoPanel()
	{
		this.setSize(graphicalWidth, graphicalHeight);

		this.setLayout(null);
		this.setBackground(Color.WHITE);

		cycleInfo = new JTextArea("Cycle: 0");
		cycleInfo.setLocation(10, 10);
//		cycleInfo.setSize(200, 800);
		
		cycleInfo.setSize(0, 0);

		cycleInfo.setEditable(false);
		cycleInfo.setCursor(null);
		cycleInfo.setOpaque(false);
		cycleInfo.setFocusable(false);

		cycleInfo.setWrapStyleWord(true);
		cycleInfo.setLineWrap(true);
		cycleInfo.setVisible(true);

		scroll = new JScrollPane(cycleInfo);
//		scroll.setLayout(null);
		scroll.setAutoscrolls(true);
		scroll.setSize(200, 800);
		scroll.setLocation(0, 0);
		scroll.setVisible(true);
		
		this.add(scroll);
	}

	public void update(int cycleNumber, ArrayList<Bot> bots)
	{
		String info = String.format("Cycle: %5d\n\n", cycleNumber);

		for(Bot bot : bots)
		{
			if(bot.isLost() || bot.getCommunicator().isDisconnected())
				continue;
			
			info += String.format("Bot #%d (%s)\n", bot.getTeamNumber(), bot.getName());
			info += String.format("Su: %d\n", bot.getSuppliesAmount());
			info += String.format("HQ: %d\n", GameAnalyzer.getBuildingsCountFor(bot.getGameField(), bot));
			info += String.format("Un: %d\n", GameAnalyzer.getUnitsCountFor(bot.getGameField(), bot));
			info += String.format("Sc: %d\n", GameAnalyzer.getScoreFor(bot.getGameField(), bot));
			
			info += "---------------------\n";
		}

		this.cycleInfo.setText(info);

		this.repaint();
	}
}
