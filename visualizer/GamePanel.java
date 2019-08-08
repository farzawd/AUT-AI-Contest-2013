package visualizer;

import game.agent.Agent;
import game.agent.Building;
import game.agent.Unit;
import game.map.Field;
import io.command.Command;
import io.command.Command.CommandType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import config.Config;

public class GamePanel extends JPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private int					gridWidth;
	private int					gridHeight;

	private int					graphicalWidth		= 600;
	private int					graphicalHeight		= 600;

	private int					cellEdge;
	private int					startX;
	private int					startY;

	private Field				map;

	private final Color			BROWN				= new Color(0xa08010);
	private final Color			TP_RED				= new Color(0xa0ffff00,
															true);
	private final Color			DK_GREEN			= Color.LIGHT_GRAY;	// =
	
	private int					frameNum;
	private int					maxFrames;
	
	private JPanel backGround;

	public GamePanel(int width, int height)
	{
		this.gridWidth = width;
		this.gridHeight = height;

		this.setSize(graphicalWidth, graphicalWidth);

		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);

		this.map = new Field(width, height);

		this.setScaling();
		
		
		backGround = new JPanel()
		{
			/**
			 * 
			 */
			private static final long	serialVersionUID	= 8404934288774862506L;

			@Override
			public void paint(Graphics g)
			{
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 0, graphicalWidth, graphicalHeight);
				
				g.setColor(DK_GREEN);
				g.fillRect(startX, startY, cellEdge * gridWidth, cellEdge * gridHeight);

				drawWalls(g);
			}
		};
		
		backGround.setLayout(null);
		backGround.setSize(graphicalWidth, graphicalWidth);
		backGround.setLocation(0, 0);
		backGround.setBackground(Color.LIGHT_GRAY);
		
		this.add(backGround);
		backGround.repaint();
		
//		walls = fetchWalls();
	}

	public void setFrameNum(int frameNum)
	{
		this.frameNum = frameNum;
	}

	public void setMaxFrames(int maxFrames)
	{
		this.maxFrames = maxFrames;
	}

	private void setScaling()
	{
		cellEdge = Math.min(graphicalHeight / gridHeight, graphicalWidth
				/ gridWidth);

		startX = (graphicalWidth - (gridWidth * cellEdge)) / 2;
		startY = (graphicalHeight - (gridHeight * cellEdge)) / 2;
	}

	public synchronized void setMap(Field map)
	{
		this.map = map;//.clone();
	}

	public static Color getColorByNum(int num)
	{
		num %= 10;

		switch(num)
		{
			case 0:
				return Color.GREEN;
			case 1:
				return Color.WHITE;
			case 2:
				return Color.BLUE;
			case 3:
				return Color.RED;
			case 4:
				return Color.CYAN;
			case 5:
				return Color.MAGENTA;
			case 6:
				return Color.GRAY;
			case 7:
				return Color.YELLOW;
			case 8:
				return Color.BLACK;
			default:
				return Color.PINK;

		}
	}
	
//	private void drawGrid(Graphics g)
//	{
//		g.setColor(Color.GRAY);
//		
//		for(int i = 0 ; i < gridWidth ; i++)
//			for(int j = 0 ; j < gridHeight ; j++)
//			{
//				int tempX = startX + (i * cellEdge);
//				int tempY = startY + (j * cellEdge);
//				
//				g.drawRect(tempX, tempY, cellEdge, cellEdge);
//			}
//	}

	private void drawSupplies(Graphics g, int x, int y)
	{
		int tempX = startX + (x * cellEdge);
		int tempY = startY + (y * cellEdge);

		g.setColor(BROWN);
		g.fillRect(tempX + (cellEdge / 4), tempY + (cellEdge / 4),
				cellEdge / 2, cellEdge / 2);

		g.setColor(Color.BLACK);
		g.drawRect(tempX + (cellEdge / 4), tempY + (cellEdge / 4),
				cellEdge / 2, cellEdge / 2);
	}

	private void drawActiveCell(Graphics g)
	{
		try
		{
			Point p = this.getMousePosition();

			g.setColor(Color.BLACK);
			g.drawRect(startX + ((p.x - startX) / cellEdge) * cellEdge, startY
					+ ((p.y - startY) / cellEdge) * cellEdge, cellEdge,
					cellEdge);
		}
		catch(Exception e)
		{
		}
	}

	// private void drawWall(Graphics g, int x, int y)
	// {
	// int tempX = startX + (x * cellEdge);
	// int tempY = startY + (y * cellEdge);
	//
	// g.setColor(Color.ORANGE);
	// g.fillRect(tempX, tempY, cellEdge, cellEdge);
	// }


	private void drawWalls(Graphics g)
	{
		g.setColor(Color.ORANGE);
		
		for(int x = 0 ; x < gridWidth ; x++)
			for(int y = 0 ; y < gridHeight ; y++)
				if(!map.getTileAt(x, y).isWalkable())
				{
					int tempX = startX + (x * cellEdge);
					int tempY = startY + (y * cellEdge);
					
					g.fillRect(tempX, tempY, cellEdge, cellEdge);
				}
	}

	private void drawMeleeUnit(Graphics g, int x, int y, Color color)
	{
		int[] xs =
		{ x, x + (cellEdge / 2), x + cellEdge, x + (cellEdge / 2) };
		int[] ys =
		{ y + (cellEdge / 2), y, y + (cellEdge / 2), y + cellEdge };

		g.setColor(color);
		g.fillPolygon(xs, ys, 4);
		
		g.setColor(Color.BLACK);
		g.drawPolygon(xs, ys, 4);
	}

	private void drawRangedUnit(Graphics g, int x, int y, Color color)
	{
		g.setColor(color);
		g.fillOval(x, y, cellEdge, cellEdge);
		
		g.setColor(Color.BLACK);
		g.drawOval(x, y, cellEdge, cellEdge);
	}

	private void drawHQ(Graphics g, int x, int y, Color color, int health)
	{
		int tempX = startX + (x * cellEdge);
		int tempY = startY + (y * cellEdge);

		g.setColor(color);
		g.fillRect(tempX, tempY, cellEdge, cellEdge);

		int r = (int) (Math.sqrt(Config.Building.HeadQuarters.viewRadius2)
				* cellEdge * 2 * health) / 3600 + cellEdge;
		int d = r / 2 - cellEdge / 2;
		g.drawOval(tempX - d, tempY - d, r, r);
		g.setColor(Color.BLACK);
		g.drawOval(tempX - d - 1, tempY - d - 1, r + 2, r + 2);

		g.setColor(Color.BLACK);
		g.drawRect(tempX, tempY, cellEdge, cellEdge);

		g.drawLine(tempX, tempY, tempX + cellEdge, tempY + cellEdge);
		g.drawLine(tempX + cellEdge, tempY, tempX, tempY + cellEdge);
	}

	private void drawAnimAttackLine(Graphics g, Point from, Point to)
	{
		int tempXS = from.x + (cellEdge / 2);
		int tempYS = from.y + (cellEdge / 2);

		int tempXE = to.x + (cellEdge / 2);
		int tempYE = to.y + (cellEdge / 2);

		g.setColor(TP_RED);
		g.fillOval(to.x, to.y, cellEdge, cellEdge);
		g.setColor(Color.BLACK);
		g.drawLine(tempXS, tempYS, tempXE, tempYE);
	}

	private void drawSpawnFlash(Graphics g, Color color, Command cmd)
	{
		int x = cmd.getArgs()[0];
		int y = cmd.getArgs()[1];

		int tempX = startX + (x * cellEdge);
		int tempY = startY + (y * cellEdge);

		int r = cellEdge * 2;
		int d = r / 2 - cellEdge / 2;
		g.setColor(Color.BLACK);
		g.drawOval(tempX - d - 1, tempY - d, r, r);
		g.setColor(color);
		g.drawOval(tempX - d - 1, tempY - d, r - 2, r - 2);
	}

	private synchronized void paintTerrain(Graphics graphics, int frameNum,
			int maxFrames)
	{
		if(frameNum < 0)
			frameNum = 0;
		if(frameNum > maxFrames)
			frameNum = maxFrames;

		Graphics2D g = (Graphics2D) graphics;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		// Graphics g=graphics;

		
		
//		drawGrid(g);
		
		
		ArrayList<Unit> rangedUnits = new ArrayList<Unit>();
		ArrayList<Unit> meleeUnits = new ArrayList<Unit>();

		for(int i = 0 ; i < gridWidth ; i++)
		{
			for(int j = 0 ; j < gridHeight ; j++)
			{
				Unit tempUnit = map.getTileAt(i, j).getUnit();
				
				Building tempBuilding = map.getTileAt(i, j).getBuilding();

				// if(!map.getTileAt(i, j).isWalkable()) //walls
				// {
				// drawWall(g, i, j);
				// }

				if(tempBuilding != null) // HQ
				{
					drawHQ(g, i, j, getColorByNum(tempBuilding.getTeamNumber()), tempBuilding.getHealth());
				}

				if(tempUnit != null)
				{
					if(tempUnit.getAgentTypeID() == 'a') // melee
					{
						meleeUnits.add(tempUnit);
					}

					if(tempUnit.getAgentTypeID() == 'A') // ranged
					{
						rangedUnits.add(tempUnit);
					}
				}

				if(map.getTileAt(i, j).hasSupplies()) // supplies
				{
					drawSupplies(g, i, j);
				}
			}
		}

		g.setColor(Color.BLACK);
		g.drawRect(startX, startY, cellEdge * gridWidth, cellEdge * gridHeight);

		drawActiveCell(g);

		for(Unit unit : meleeUnits)
		{
			Point animGP = getAnimGP(unit, frameNum, maxFrames);

			drawMeleeUnit(g, animGP.x, animGP.y,
					getColorByNum(unit.getTeamNumber()));
		}

		for(Unit unit : rangedUnits)
		{
			Point animGP = getAnimGP(unit, frameNum, maxFrames);

			drawRangedUnit(g, animGP.x, animGP.y,
					getColorByNum(unit.getTeamNumber()));
		}

		for(Unit unit : meleeUnits)
		{
			Command tempCommand = unit.getCommand();
			if(tempCommand != null)
			{
				if(tempCommand.getType() == CommandType.ATTACK)
				{
					Agent target = tempCommand.getTarget();
					if(target != null)
					{
						Point animGP = getAnimGP(unit, frameNum, maxFrames);
						drawAnimAttackLine(g, animGP,
								getAnimGP(target, frameNum, maxFrames));
					}
				}
			}

			// g.drawRect(animGP.x, animGP.y, cellEdge * 2, cellEdge*2);
		}

		for(Unit unit : rangedUnits)
		{
			Command tempCommand = unit.getCommand();
			if(tempCommand != null)
			{
				if(tempCommand.getType() == CommandType.ATTACK)
				{
					Agent target = tempCommand.getTarget();
					if(target != null)
					{
						Point animGP = getAnimGP(unit, frameNum, maxFrames);
						drawAnimAttackLine(g, animGP,
								getAnimGP(target, frameNum, maxFrames));
					}
				}
			}
		}

		for(int i = 0 ; i < gridWidth ; i++)
		{
			for(int j = 0 ; j < gridHeight ; j++)
			{
				Building tempBuilding = map.getTileAt(i, j).getBuilding();
				Command tempCommand;

				if(tempBuilding != null)
				{
					tempCommand = tempBuilding.getCommand();
					if(tempCommand != null)
					{
						if(tempCommand.getType() == CommandType.SPAWN)
						{
							drawSpawnFlash(
									g,
									getColorByNum(tempBuilding.getTeamNumber()),
									tempCommand);
						}
					}
				}
			}
		}
	}

	private Point getAnimGP(Agent agent, int frameNum, int maxFrames)
	{
		Point lastGP = new Point(startX + cellEdge * agent.getLastPosition().x,
				startY + cellEdge * agent.getLastPosition().y);
		Point currentGP = new Point(startX + cellEdge * agent.getX(), startY
				+ cellEdge * agent.getY());

		Point animGP = new Point();
		animGP.x = lastGP.x + ((currentGP.x - lastGP.x) * frameNum) / maxFrames;
		animGP.y = lastGP.y + ((currentGP.y - lastGP.y) * frameNum) / maxFrames;

		return animGP;
	}

	public synchronized void paint(Graphics g)
	{
		super.paint(g);

		paintTerrain(g, frameNum, maxFrames);
	}
}
