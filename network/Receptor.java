package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Receptor
{
	public boolean running;

	private ArrayList<Communicator> clients;	
	private ServerSocket server;

	public Receptor(int port)
	{
		try
		{
			this.server = new ServerSocket(port);

			running = true;
		} catch (IOException e)
		{
			running = false;
		}
	}

	public Communicator acceptClient()
	{
		try
		{
			return new Communicator(server.accept());
		} catch (IOException e)
		{
			System.err.println("failed to accept a client...");
			return null;
		}
	}
	
	public int accept(int clientsCount)
	{
		if(!running)
			return -1;

		int count = 0;

		for(count = 0 ; count < clientsCount ; count++)
		{
			try
			{
				clients.add(new Communicator(server.accept()));
			}
			catch (IOException e){};
		}

		return count;
	}
	
	public ArrayList<Communicator> getClients()
	{
		return this.clients;
	}
	
	public void close()
	{
		running = false;
		try
		{
			server.close();
			System.out.println("server shut-down was successful.");
		}
		catch(IOException e)
		{
			System.err.println("failed to shut the server down");
		}
	}

	public boolean isRunning()
	{
		return this.running;
	}
}
