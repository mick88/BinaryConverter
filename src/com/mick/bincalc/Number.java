package com.mick.bincalc;

import java.util.Locale;


public class Number
{
	/*class for storing data in any format*/
	private Long number=0L;
	
	private boolean correctInput=true;
	private int originalBase=10;
	private final int CHAR_SIZE = 128;
	
	public static final int minBase = 2,
			maxBase=36;
	
	public Number(int fromDecimal)
	{
		originalBase = 10;
		number = ((long)fromDecimal & 0xFFFFFFFF);
	}
	
	public Number()
	{
		
	}
	
	public long toDecimal()
	{
		return number;
	}
	
	public boolean isZero()
	{
		return number == 0l;
	}
	
	public String toChar()
	{
		/*should return more than 1 char if number is more than 8bit*/

		char result = (char)(number % CHAR_SIZE);
		
		if (result <= 32 || result > 126) return null;
		else return new String(new char[]{result});
	}
	
	public void fromChar(char character)
	{
		number = (long)(character & 0xFFFFFFFF);
	}
	
	public void fromChar(String character)
	{
		if (character == null || character.length() == 0) number = 0l;
		fromChar(character.charAt(0));
	}
	
	void strcpy(String to, String from)
	{
		to = from;
	}
	
	public String getCharDescription()
	{
		if (number >= CHAR_SIZE) return null;
		char result = (char)(number % CHAR_SIZE);
		
		switch(result)
		{
			case 0: return "NUL";
			case 1: return "SOH";
			case 2: return "STX";
			case 3: return "ETX";
			case 4: return "EOT";
			case 5: return "ENQ";
			case 6: return "ACK";
			case 7: return "BEL";
			case 8: return "BS";
			case 9: return "TAB";
			case 10: return "LF";
			case 11: return "VT";
			case 12: return "FF";
			case 13: return "CR";
			case 14: return "SO";
			case 15: return "SI";
			case 16: return "DLE";
			case 17: return "DC1";
			case 18: return "DC2";
			case 19: return "DC3";
			case 20: return "DC4";
			case 21: return "NAK";
			case 22: return "SYN";
			case 23: return "ETB";
			case 24: return "CAN";
			case 25: return "EM";
			case 26: return "SUB";
			case 27: return "ESC";
			case 28: return "FS";
			case 29: return "GS";
			case 30: return "RS";
			case 31: return "US";
			case 32: return "SPC";
			case 127: return "DEL";
			default: return null;
		}
	}
	
	//convert number from any base
	public Number(String fromString, int base)
	{
		int len = fromString.length();
		originalBase=base;
		
		if (fromString == null || len == 0 || base < minBase || base > maxBase)
		{
			number = 0L;
		}
		else
		{
			int multiplier=1;
			for (int i=len-1; i >= 0; i--)
			{
				char c = fromString.charAt(i);
				int val;
				
				if (c >= '0' && c <= '9') val = c-'0';
				else if (c >= 'A' && c <= 'Z') val = c-'A'+10;
				else if (c >= 'a' && c <= 'a') val = c-'a'+10;
				else //unexpected character
				{
					correctInput=false;
					continue;
				}
				
				if (val >= base) //character is not present in selected base
				{
					correctInput = false;
					continue;
				}
				
				number += (long)val * (long)multiplier;
				multiplier *= base;
			}
		}
	}
	
	public String toAnyBase(int base)
	{
		if (number == 0) return "0";
		StringBuilder sb = new StringBuilder();
		long n = number;
		while(n > 0)
		{
			char num = (char)(n % base);
			if (num > 9) num += 'A'-10;
			else num += '0';
			sb.append(num);
			n /= base;
		}
		return sb.reverse().toString();
	}
	
	int getOctet(int octet)
	{
		return (int)((number >> (octet * 8)) & 0xFF);
	}
	
	public String toIpAddress()
	{		
		return String.format(Locale.ENGLISH,"%d.%d.%d.%d", getOctet(3), getOctet(2), getOctet(1), getOctet(0));
	}
	
	public void fromIpAddress(String ip)
	{
		int dots=0;
		number = 0L;
		correctInput = true;
		for (int i=0; i < ip.length(); i++)
		{
			char c =ip.charAt(i);
			if (c == '.') dots++;
			else if (c < '0' || c > '9') correctInput = false;
		}
		if (dots != 3) correctInput = false;
		
		String[] parts = ip.split("\\.");
		
		if (parts.length != 4) correctInput = false;
		
		int i=0;
		for (String s : parts)
		{
			int n;
			try
			{
				n = Integer.valueOf(s);
			}
			catch (Exception e)
			{
				n=0;
			}
			if (n >= 256 || n < 0) 
			{
				if (n > 255) n = 255;
				else if (n < 0) n = 0;
				correctInput=false;
			}
			
			number |= (long)n << ((3-i) * 8);

			if (++i == 4) break; 	
		}
		
		
	}
	
	public boolean isInputCorrect()
	{
		return correctInput;
	}
	
	public int getOriginalBase()
	{
		return originalBase;
	}
	
	public int getBitSize()
	{
		int bits = 0;
		long n =number;
		while(Math.abs(n) > 0)
		{
			n /= 2;
			bits++;
		}
		return bits;
	}
	
}
