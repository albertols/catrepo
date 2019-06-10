package com.parser.utils;

import java.util.BitSet;

import com.log.LogEnum;
import com.log.Logger;

/**
 * Wraps up bits. OPTION 1: BitSet. OPTION 2: boolean[] bitSetArray.
 * 
 * @author alopez
 *
 */
public class MyBitSet
{
	/**
	 * OPTION 1: BitSet Constructor
	 */
	public BitSet bitSet=null;
	
	/**
	 * OPTION 2: boolean [] Constructor
	 */
	public boolean [] bitSetArray;
	public String originalString;
	
	/**
	 * as legacy BitSet as length() returns the maximum set bit within BitSet
	 */
	int realSize;
	
	/**
	 * 
	 * OPTION 1: BitSet Constructor from BitSet and int realsize(as legacy BitSet as length() returns the maximum set bit within BitSet) 
	 * 
	 * @param int realsize
	 * @param input BitSet bitSet
	 */
    public MyBitSet(int realsize, BitSet bitSet)
    {
    	this.bitSet = bitSet;
        this.realSize=realsize;
    }

    /**
	 * OPTION 2: boolean [] Constructor from String
	 */
    public MyBitSet(String string)
    {
    	this.originalString = string;
    	this.realSize=string.length();
    	this.bitSetArray = new boolean [this.realSize];
    	fillsBooleanArray (string);
	}

	

	public int realSize()
    {
        return this.realSize;
    }
    
    @Override
    /**
     * represents bits from MSB to LSB
     */
    public String toString()
    {
    	StringBuilder s = new StringBuilder();
    	if(bitSet!=null)
    	{
    		 for( int i = realSize-1; i >=0 ;  i-- )
    	        {
    	           s.append( bitSet.get( i ) == true ? 1: 0 );
    	        }
    	}
    	
    	// OPTION 2
    	else if (bitSetArray.length>0)
    	{
    		for (int i=bitSetArray.length-1; i>=0; i--)
        	{
        		s.append(bitSetArray[i] == true ? 1: 0);
        	}
    	}
       
        return s.toString();
    }
    
    /**
     * Like toString() but without a space append every 8 bits (MSB-LSB)
     * 
     * @return
     */
    public String toStringWith_8_Bits_append()
    {
    	StringBuilder s = new StringBuilder();
    	
    	// OPTION 1: BitSet
    	if(bitSet!=null)
    	{
    		for (int i=realSize()-1; i>=0; i--)
        	{
        		s.append(bitSet.get(i) == true ? 1: 0);
        		if ((i+1)%8==0)
        		{
        			s.append(" ");
        		}
        	}
    	}
    	
    	// OPTION 2: boolean[]
    	else if (bitSetArray.length>0)
    	{
    		for (int i=0; i<bitSetArray.length; i++)
        	{
        		s.append(bitSetArray[i] == true ? 1: 0);
        		if ((i+1)%8==0)
        		{
        			s.append(" ");
        		}
        	}
    		s.reverse();
    	}
    	
    	return s.toString();
    }

    /**
     * Checks binary String with another input String
     * 
     * @param input
     */
	public void checkerBimaryString(String input)
	{
		if (!toString().equals(input))
		{
			Logger.log(LogEnum.WARNING, "Error Converting BitSet. " +  toString() + " != "+input );
		}
		
	}
	
	/**
	 * Checks bit for boolean[]. 
	 * @param x
	 * @return
	 */
	public boolean isSet (int pos)
	{
		if (bitSetArray.length>pos)
    	{
			return bitSetArray[pos];
    	}
		return false;
	}
	
	/**
	 * Checks bit for BitSet position.
	 */
	public boolean isSetBitSet (int pos)
	{
		if (bitSet!=null)
		{
			return bitSet.get(pos);
		}
		return false;
	}
	
	/**
	 * From MSB=0 to Fills bitSetArray with true or false depending on input string chars, '1' or '0'
	 * 
	 * @param string, input boolean string
	 */
	private void fillsBooleanArray(String string)
	{
		char[] booleanChar = string.toCharArray();
		for (int x=0; x<booleanChar.length; x++)
		{
			if (booleanChar[booleanChar.length-1-x]=='0')
			{
				bitSetArray[x]=false;
			}
			else if (booleanChar[booleanChar.length-1-x]=='1')
			{
				bitSetArray[x]=true;
			}
		}
		
	}
	


    
}
