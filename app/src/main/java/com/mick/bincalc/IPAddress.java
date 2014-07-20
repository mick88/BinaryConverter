package com.mick.bincalc; 


public class IPAddress
{
	/*Class for storing and converting IP addresses*/
	
	byte [] bytes = new byte[4]; //least significant byte first
	boolean isCorrect=false;
	
	public IPAddress(String FromString)
	{
		String [] parts = FromString.split("\\.");
		bytes = new byte[4];
		isCorrect = parts.length == bytes.length;
		
		for (int i=0; i < Math.min(bytes.length, parts.length); i++)
		{
			int val;
			try
			{
				val = Integer.valueOf(parts[i]);
			}
			catch (Exception ex)
			{
				val=0;
			}
			if (val > 255) 
			{
				isCorrect=false;
				val = 255;
			}
			else if (val < 0)
			{
				isCorrect=false;
				val = 0;
			}
			
			bytes[bytes.length-i-1] = (byte)(val);
		}		
	}
	
	public IPAddress(long FromInteger)
	{
		for (int i=0; i < 4; i++)
		{
			bytes[i] = (byte) ((FromInteger >> (i*8)/*shift by 8 bits*/) & 0xFF);
		}
	}
	
	public String toString()
	{
		return String.format("%d.%d.%d.%d", bytes[3] & 0xFF, bytes[2]& 0xFF, bytes[1]& 0xFF, bytes[0]& 0xFF);
	}
	
	public int toInteger()
	{
		int result=0;
		for (int i=0; i < 4; i++)
		{
			result |= ((int)bytes[i]) << (i*8 /* shift by 8 bits*/);
		}
		return result;
	}
}
