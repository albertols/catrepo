package com.parser.utils;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.log.LogEnum;
import com.log.Logger;

/**
 * 
 * Wraps up Math functionalities: Byte operations, Calendar conversions, etc.
 * 
 * @author alopez
 */
public final class MathUtils
{
	public static String DATE_FORMAT = "dd-MM-yy HH:mm:ss.SSS";
	

	public static byte[] toByteArray_4_bf(int value)
	{
	     return  ByteBuffer.allocate(4).putInt(value).array();
	}
	
	
	public static byte[] toByteArray_4(int value)
	{
	    return new byte[] { 
	        (byte)(value >> 24),
	        (byte)(value >> 16),
	        (byte)(value >> 8),
	        (byte)value };
	}

	
	/**
	 * packing an array of 4 bytes to an int, big endian
	 * @param bytes
	 * @return
	 */
	public static int fromByteArray_4(byte[] bytes) {
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	
	/**
	 * Resverses input array.
	 * 
	 * @param input byte[] array
	 * @return reversed byte[]
	 */
	public static byte[] reverseArray (byte[] array)
	{
		int size = array.length;
		byte temp;

		for (int i = 0; i < size/2; i++)
		{
			temp = array[i];
			array[i] = array[size-1 - i];
			array[size-1 - i] = temp;
		}
		return array;
	}
	
	/**
	 * WATCH OUT: reverses array AND uses little-endian (MSB=LSB).Gets array of bits (BitSet) from List<Byte> bytes.
	 * 
	 * @param input List<Byte> bytes
	 * @return BitSet
	 */
	public static MyBitSet getBitSetFromReveseLittleEndianBytes (List<Byte> bytes)
	{
		// WATCH OUT: reverses array AND uses little-endian (MSB=LSB)
		return getBitSetFromLittleEndianBytes(reverseArray(byteArrayConversor(bytes)));
	}

	
	/**
	 * USES little endian issue when reading bytes BitSet.valueOf(ByteBuffer.wrap(ba)!
	 * 
	 * 1) gets for instance: [0,85,-58,-88]
	 * 2) gets rid off signed mask for signed int: 11111111 11111111
	 * 3) WATCH OUT, it uses little endian: 00000000 10101010 01100011 00010101 (rather than 00000000 01010101 11000110 10101000)
	 * 
	 * @param data
	 * @param index
	 * @param end
	 * @return
	 */
	public static MyBitSet getBitSetFromLittleEndianBytes (byte[] ba)
	{
		// gets for instance: [0,85,-58,-88]
		//System.out.println("\n\nVS. (UnsignedInt)");
		
		// WATCH OUT! little endian: 00000000 10101010 01100011 00010101 (rather than 00000000 01010101 11000110 10101000)
		MyBitSet set = new MyBitSet((ba.length)*8, BitSet.valueOf(ByteBuffer.wrap(ba)));
		
		// shows 00000000 10101010 01100011 00010101
		//System.out.println(set.toStringWith_8_Bits_append());		
		
		// checks string
		//set.checkerBimaryString (getBitSetFromStringBoolanBytes(ba).originalString);
		return set;
	}
	
	/**
	 * 1) From primitive bytes, e.g: [0,85,-58,-88].
	 * 2) builds up string: 00000000 01010101 11000110 10101000
	 * 3) Creates MyBitSet from binary String
	 * 
	 * @param data
	 * @param index
	 * @param end
	 * @return
	 */
	public static MyBitSet getBitSetFromStringBoolanBytes (byte[] ba)
	{
		// builds up: 00000000 01010101 11000110 10101000 From primitive bytes: [0,85,-58,-88]
		StringBuilder sb = new StringBuilder();
		for (int x=0; x<ba.length; x++)
		{
			//System.out.println(x+"="+ba[x]);
			// 0 must be the LSB
			StringBuilder reverse = new StringBuilder(StringUtils.binaryStringFromUnsignedByte(ba[x])).reverse();
			sb.append(reverse);
		}
		
		// creates MyBitSet from binary String
		return new MyBitSet(sb.toString());
	}
	
	
	
	/**
	 * Converts a byte into a binary string. E.g: 129 ===> 10000001
	 * @param b
	 * @return
	 */
	public static String byteToBinaryString (byte b)
	{
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}
	
	/**
	 * Caluclates Least significant "byte"
	 * 
	 * @param value
	 * @return
	 */
	public static byte getLSB (byte value)
	{
		return (byte) (value & 0xFF);           // Least significant "byte"
	}
	
	/**
	 * Caluclates Least significant "byte"
	 * 
	 * @param value
	 * @return
	 */
	public static byte getLSB (Integer value)
	{
		return (byte) (value & 0xFF);           // Least significant "byte"
	}
	
	/**
	 * 
	 * Caluclates Most significant "byte"
	 * 
	 * @param value
	 * @return
	 */
	public static byte getMSB (byte value)
	{
		return (byte) ((value & 0xFF00) >> 8); // Most significant "byte"
	}
	
	/**
	 * Creates a Map<Integer, String> for input array od strings like 1=on-line 2=standby, etc... (INT=String) type
	 * 
	 * @param arrayString
	 * @return
	 */
	public static Map<Integer, String> createIntValMap (String[] arrayString)
	{
		Map<Integer, String> enumMap = new HashMap<>();
		for (int x=0; x<arrayString.length; x++)
		{
			String patternRule = "([0-9]+)[=](.*)";
			Pattern rRule = Pattern.compile(patternRule);
			Matcher m = rRule.matcher(arrayString[x]);
			while (m.find())
			{
				enumMap.put(Integer.parseInt(m.group(1)), m.group(2));
			}
		}
		return enumMap;
	}
	
	/**
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static String getHexStringFromBytesArray (List<Byte> byteList)
	{
		String s=null;
		s = byteList.toString();
		return s;
	}

	/**
	 * 
	 * Converts an String(with format String[]: [1,0,3,6]) into an int array, int []
	 * 
	 * @param s
	 * @param int size
	 * @return int [] array from string
	 */
	public static int[] getIntArrayFromString (String s, int size)
	{
		int [] retArr = new int[size];
		retArr= Arrays.stream(s.replaceAll("[\\[\\] ]", "").split(",")).mapToInt(Integer::parseInt).toArray();
		return retArr;
	}

	/**
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static int getIntFromBytesArray_4 (List<Byte> byteList)
	{
		// 1
		return byteList.get(0) << 24 | (byteList.get(1) & 0xFF) << 16 | (byteList.get(2) & 0xFF) << 8 | (byteList.get(3) & 0xFF);
		
		// 2
		//return new BigInteger(byteArrayConversor(byteList)).intValue();
		
		// 3
		//return ByteBuffer.wrap(byteArrayConversor(byteList)).getInt();
	}
	

	/**
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static int getIntFromBytesArray_2 (List<Byte> byteList)
	{
		return byteList.get(0)<<8 &0xFF00 | byteList.get(1)&0xFF;
	}
	
	/**
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static int getIntFromBytesArray_1 (List<Byte> byteList)
	{
		return byteList.get(0).intValue();// TODO: sort out unsigned and bit mask 
	}
	
	public static String byteArrayToHex(byte[] a) {
		   StringBuilder sb = new StringBuilder(a.length * 2);
//		   for(byte b: a)
//		      sb.append(String.format("%02x", b));
		   
		   for (int x=a.length-1; x>=0; x--)
		   {
			   sb.append(String.format("%02x", a[x]));
		   }
		   return sb.toString();
	}
	
	/**
	 * 
	 * @param int x, 23
	 * @return byte[] array, 1700
	 */
	public static byte[] shortToByteArray (short x)
	{
		byte[] array = new byte[2];
		array[0] = (byte)(x & 0xff);
		array[1] = (byte)((x >> 8) & 0xff);
		return array;
	}

	/**
	 * 
	 * @param List<Byte> byteList
	 * @return concatenated String ASCII values from an ArrayOfbytes
	 */
	public static String getAsciiStringFromBytesArray (List<Byte> byteList)
	{
		StringBuilder sb =  new StringBuilder();
		byte[] arr = new byte[byteList.size()];
		int j=0;
		for(Byte b: byteList)
		{
			arr[j++] = b.byteValue();
			//Logger.log(LogEnum.VAR,"\t"+b.intValue() +" - "+ (char)b.intValue());
			sb.append((char)b.intValue());
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * Converts a date format into dd-MM-yy HH:mm:SS.ssss; originally retrieved from a string with format 1702160547.5527 (YYMMDDHHMM.SShh )=(2017-02-16 05:47:55 270) where hh is hundreds of seconds
	 * 
	 * @param String s
	 * @return concatenated String ASCII values from an ArrayOfbytes
	 */
	public static String convertsDateFormatFromAsciiString (String s) throws Exception
	{
		char[] ch=s.toCharArray();
		StringBuilder sb = new StringBuilder();
		
		//dd
		int day_offset = Integer.parseInt(Character.toString(ch[4])+Character.toString(ch[5]));
		sb.append(Integer.toString(day_offset)+"-");
		
		//MM
		sb.append(Character.toString(ch[2])+Character.toString(ch[3])+"-");
		
		//yy
		sb.append(Character.toString(ch[0])+Character.toString(ch[1])+" ");

		//HH
		sb.append(Character.toString(ch[6])+Character.toString(ch[7])+":");
		
		//mm
		sb.append(Character.toString(ch[8])+Character.toString(ch[9])+":");
		
		//checking .
		if ((".").equals(ch[10]))
		{
			Logger.log(LogEnum.ERROR, "Not present dot \".\" in position 11 ch[10] from date String: " + s);
			return null;
		}
		
		//ss
		sb.append(Character.toString(ch[11])+Character.toString(ch[12])+".");
		
//		//SSS
		sb.append(Character.toString(ch[13])+Character.toString(ch[14])+"0");
		
		//checking .
		if ((" ").equals(ch[15]))
		{
			Logger.log(LogEnum.ERROR, "Not present espace/null \".\" in position 16 ch[15] from date String: " + s);
			return null;
		}
		
		return sb.toString();
	}
	
	/**
	 * Gets the current Local Date and Time stamp
	 * @return
	 */
	public static String getCurrentLocalDateTimeStamp()
	{
	    return LocalDateTime.now()
	       .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
	}
	
	/**
	 * 
	 * @param List<Byte> byteList
	 * @return String value from an ArrayOfbytes
	 */
	public static Date getDateFromString (String dateString)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date d = new Date();
		try
		{
			d = sdf.parse(dateString);
		}
		catch (ParseException e)
		{
			Logger.log(LogEnum.ERROR, "Parsing date "+dateString  + " into format ["+DATE_FORMAT+"]");
			e.printStackTrace();
		}
		//String date=sdf.format(d);
		//Logger.log(LogEnum.VAR,"Converted date: " +date);
		return d;
	}
	
	
	
	/**
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static double getDoubleFromBytesArray (List<Byte> byteList)
	{
		ByteBuffer bf = ByteBuffer.wrap(byteArrayConversor (byteList));
		return bf.getDouble();
	}
	
	/**
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static boolean getBooleanFromBytesArray (List<Byte> byteList)
	{
		ByteBuffer bf = ByteBuffer.wrap(byteArrayConversor (byteList));
		int num = bf.getInt();
		//Logger.log(LogEnum.VAR,num);
		switch (num)
		{
		case 1:
			return true;
		case 0:
			return false;
		default:
			Logger.log(LogEnum.ERROR, "Value of List<Byte> has NOT been converted into a boolean:"+num);
			return null != null;
		}

	}
	
	/**
	 * 
	 * Gets rid off signed mask for signed int: 11111111 11111111 and Converts List<Byte> into byte []. 
	 * 
	 * @param List<Byte> byteList
	 * @return integer value from an ArrayOfbytes
	 */
	public static byte[] byteArrayConversor (List<Byte> byteList)
	{
		// declares size for byte []
		byte[] arr = new byte[byteList.size()];
		
		// checks suitable size
		if (byteList.size()<=16)
		{
			// convert List<Byte> into byte []
			int j=0;
			for(Byte b: byteList)
			{
				arr[j++] = b.byteValue();
			}
		}
		else
		{
			Logger.log(LogEnum.ERROR, "SIZE of List<Byte> higher than 16, implement BIginteger feature to wrap up byte[higher than 4]");
		}
		
		return arr;
	}
	
	/**
	 * 
	 * Gets a List of Byte from a data byte[] at certain index and count
	 * 
	 * @param data
	 * @param index
	 * @param count
	 * @return
	 */
	public static List<Byte> getDataBytesList (byte[] data, int index, int count)
	{
		List<Byte> l = new ArrayList<>(count);
		for (int x=index;x<=count;x++)
		{
			l.add(data[x]);
		}
		return l;
	}
	
	/**
	 * 
	 * Gets a primitive data array from data between index and end
	 * 
	 * @param byte[] data, input array of bytes(primitive type)
	 * @param int index, offset position of data extraction
	 * @param int end position position of data extraction
	 * @return arrays of bytes from index to end
	 */
	public static byte[] getDataPrimitiveBytesList (byte[] data, int index, int end)
	{
		byte[] l =  new byte [(end-index)+1];
		int i=0;
		for (int x=index;x<=end;x++)
		{
			//Logger.log(LogEnum.VAR,i+". " + String.format("%02x", data[x]));
			l[i]=data[x];
			i++;
		}
		return l;
		
	}
	
	
	/**
	 *
	 * Checks if a string ends with null
	 * 
	 * @param String s
	 * @return boolean
	 */
	public static boolean isStringEndedWithNULL (String s)
	{
		if (s.endsWith("\0") || s.endsWith(" ") || s.endsWith("null") || s.endsWith(null) || s.endsWith("NUL") || s.endsWith("NUL"))
		{
			return true;
		}
		else return false;
	}
	
	/**
	 *
	 * Checks if an int is within a range of min and max values
	 * 
	 * @param String varName
	 * @param int value
	 * @param int max
	 * @param int min
	 * @return boolean
	 */
	public static boolean isInRange (String varName,int value,int min, int max)
	{
		if (isInRangeAssessment (varName, value, min, max))
		{
			rangeOK (varName, value);
			return true;
		}
		else 
			return false;
		
	}
	
	/**
	 * 
	 * Checks if a String can be converted into Integer
	 *
	 * @param String s
	 * @return boolean
	 */
	public static boolean isConvertedIntoInteger (String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
		
	}
	
	/**
	 *
	 * Checks if an int is within a range of min and max values
	 * 
	 * @param String varName
	 * @param int value
	 * @param int max
	 * @param int min
	 * @return boolean
	 */
	public static boolean isInRangeAssessment (String varName,int value,int min, int max)
	{
		if (value<min || value>max)
		{
			rangeError (varName, min, max, value);
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 *
	 * Checks if an int is within a contained range in a list 
	 * 
	 * @param String varName
	 * @param int value
	 * @param List<Object> ol
	 * @return boolean
	 */
	public static boolean containsInteger (String varName,int value, List<Integer> ol)
	{
		if (!ol.contains(value))
		{
			Logger.log(LogEnum.ERROR, "["+varName+"] is NOT in the range:"+value);
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Shows error for a certain var name in a certain range
	 * 
	 * @param String variable name
	 * @param int minimum value
	 * @param int max value
	 * @param int realValue
	 */
	public static void rangeError (String var, int min, int max, int realValue)
	{
		Logger.log(LogEnum.ERROR, "["+var+"] is NOT in the range ("+min+"-"+max+"):"+realValue);
	}
	
	/**
	 * Shows error for a certain var name in a certain range
	 * 
	 * @param String variable name
	 * @param int minimum value
	 * @param int max value
	 * @param int realValue
	 */
	public static void rangeOK (String var, int realValue)
	{
		Logger.log(LogEnum.DEBUG, "\t"+var+":"+realValue);
	}
	
	/**
	 * Gets the String with Time and Date of Calendar with milliseconds accuracy
	 * 
	 * @param Calendar cal
	 * @retrun String with Date and Time in milliseconds
	 */
	public static String getDateAndTime(Calendar cal)
	{
		return cal.getTime()+" " + cal.get(Calendar.MILLISECOND)+ "(ms)";
	}
	
	/**
	 * Shows primitive bytes.
	 * 
	 * @param byte[] bytes
	 */
	public static void showPrimitiveBytes(byte[] bytes)
	{
		for (int x=0; x<bytes.length; x++)
		{
			System.out.println("byte["+x+"]="+bytes[x]);
		}
	}
	
	
	public static void showBytesList(List<Byte> bytes)
	{
		StringBuilder sb =  new StringBuilder();
		for (int x=0; x<bytes.size(); x++)
		{
			sb.append("["+x+"]"+bytes.get(x)+" ");
		}
		System.out.println(sb.toString());
	}
	

}
