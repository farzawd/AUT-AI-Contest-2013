package io.connector;

public class DataStore
{
	private String data;
	
	public DataStore()
	{
		data = "";
	}
	
	public synchronized void clearData()
	{
		data = "";
	}
	
	public synchronized String getData()
	{
		String dataClone = this.data;
		
		return dataClone;
	}
	
	public synchronized void setData(String data)
	{
		this.data = data;
	}
}
