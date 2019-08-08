package network;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import config.Config;


/**
 * Communicator is used for transmitting data between Client and Server
 * @author farzad
 *
 */
public class Communicator
{
	private Socket socket;

	private boolean isDisconnected;

	private BufferedReader input;
	private BufferedWriter output;

	public Communicator(Socket socket) throws IOException
	{
		this.socket = socket;

		output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		isDisconnected = false;
	}

	/**
	 * reads a single line from the socket's input stream
	 * @return
	 */
	private String readNextLine()
	{
		try
		{
			return input.readLine();
		} catch (IOException e)
		{
			System.err.println("failed to receive data...");
			isDisconnected = true;
			return null;
		}
	}

	/**
	 * packs and sends the given string to the server. do not modify.
	 * @param data
	 */
	public void sendData(String data)
	{
		if(isDisconnected)
			return;

		try
		{
			output.write("\n" + Config.Communicator.Tags.packetStartTag + "\n" + data + "\n" + Config.Communicator.Tags.packetEndTag + "\n");
			output.flush();
		}
		catch (IOException e)
		{
			System.err.println("could not send data (IOException)");
			isDisconnected = true;
		}
	}

	/**
	 * waits for the server to send it's next packet, and returns the received data. do not modify
	 * @return
	 */
	public String recvData()
	{
		if(isDisconnected)
			return null;

		String tempMessage = "";
		String tempLine = "";

		do
			tempLine = readNextLine();
		while(tempLine != null && !tempLine.equals(Config.Communicator.Tags.packetStartTag));

		tempLine = "";

		while(tempLine != null && !tempLine.equals(Config.Communicator.Tags.packetEndTag))
		{
			tempMessage += tempLine  + "\n";
			tempLine = readNextLine();
		}

		if(isDisconnected)
			return null;
		
		return tempMessage.trim().toLowerCase();
	}
	
	public void close()
	{
		isDisconnected = true;
		
		try
		{
			input.close();
			output.close();
			socket.close();
		}
		catch(IOException e)
		{
			System.err.println("failed to close a socket.");
		}
	}
	
	public boolean isDisconnected()
	{
		return this.isDisconnected;
	}
}
