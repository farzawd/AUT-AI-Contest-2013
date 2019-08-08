package game.map;

import game.agent.Building;
import game.agent.Unit;
import game.map.Field;
import game.map.MapHandler;
import game.map.Field.TileType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MapManager
{
	public static Field loadMap(String path) throws IOException
	{
		File f = new File(path);
		FileInputStream fis = new FileInputStream(f);
		BufferedReader input = new BufferedReader(new InputStreamReader(fis));

		int width = Integer.parseInt(input.readLine());
		int height = Integer.parseInt(input.readLine());
		
		char[][] charMap = new char[width][height];
		Field map = new Field(width, height);

		for(int i = 0 ; i < height ; i++)
		{
			String temp = input.readLine();
			for(int j = 0 ; j < width ; j++)
			{
				charMap[j][i] = temp.charAt(j);
			}
		}
		
		for(int i = 0 ; i < height ; i++)
		{
			for(int j = 0 ; j < width ; j++)
			{
				char c = charMap[j][i];
				switch(c)
				{
					case '#':
						map.setTileType(j, i, TileType.LAVA);
						break;
						
					case 'X':
						map.getTileAt(j, i).putSupplies();
						break;
						
					case '.':
						break;
						
					default:
						int teamNumber;
						if(c >= 'a' && c <= 'j') //melee
						{
							teamNumber = c - 'a';
							new Unit.Melee(map, j, i, null).setTeamNumber(teamNumber);
						}
						else if(c >= 'A' && c <= 'J') //ranged
						{
							teamNumber = c - 'A';
							new Unit.Ranged(map, j, i, null).setTeamNumber(teamNumber);
						}
						else if(c >= '0' && c <= '9') //HQs
						{
							teamNumber = c - '0';
							new Building.HeadQuarters(map, j, i, null).setTeamNumber(teamNumber);
						}
				}
			}
		}
		
		input.close();
		fis.close();
		f = null;
		
		return map;
	}
	
	public static void saveMap(Field map, String path) throws IOException
	{
		File f = new File(path);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fos));

		int width = map.getWidth();
		int height = map.getHeight();
		
		MapHandler mapH = new MapHandler(map);
		char[][] charMap = mapH.getPrintableMap();
		
		output.write(String.format("%d\n", width));
		output.write(String.format("%d\n", height));
		
		for(int i = 0 ; i < height ; i++)
		{
			for(int j = 0 ; j < width ; j++)
			{
				char c = charMap[j][i];
				
				output.write(String.format("%c", c));
			}
			
			output.write("\n");
		}
		
		output.flush();
		output.close();
		fos.close();
		
		f = null;
	}
}
