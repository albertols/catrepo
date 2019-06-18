package com.parser.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.log.LogEnum;
import com.log.Logger;

/**
 * 
 * Wraps up String functionalities.
 * 
 * @author alopez
 */
public class StringUtils
{
	public static final String MDC_DATE_STRING_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	public static String friendlyTimeDiff(Calendar start, Calendar end)
	{
		if (start.getTimeInMillis() < end.getTimeInMillis())
		{
			long timeDifferenceMilliseconds = end.getTimeInMillis() - start.getTimeInMillis();
			long diffSeconds = timeDifferenceMilliseconds / 1000;
			long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
			long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
			long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
			long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
			long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
			long diffYears = timeDifferenceMilliseconds / ((long)60 * 60 * 1000 * 24 * 365);

			if (diffSeconds < 1) {
				return "< s";
			} else if (diffMinutes < 1) {
				return diffSeconds + "s";
			} else if (diffHours < 1) {
				return diffMinutes + "m " + diffSeconds + "s";
			} else if (diffDays < 1) {
				return diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
			} else if (diffWeeks < 1) {
				return diffDays + "d " + diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
			} else if (diffMonths < 1) {
				return diffWeeks + "w " + diffDays + "d " + diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
			} else if (diffYears < 1) {
				return diffMonths + "m " + diffWeeks + "w " + diffDays + "d " + diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
			} else {
				return diffYears + "y " + diffMonths + "m " + diffWeeks + "w " + diffDays + "d " + diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
			}
		}
		return "null";
	}
	
//	public static Date stringToDate(String stringDate, String format) throws ParseException{
//
////		// this actually works, got rid of the original code idea
////		String[] splitDate = date.split("/");
////		int days = Integer.parseInt(splitDate[0]);
////		int month = (Integer.parseInt(splitDate[1]) - 1);
////		int year = Integer.parseInt(splitDate[2]);
////
////		// dates go in properly
////		GregorianCalendar dateConverted = new GregorianCalendar(year, month, days);
////		String finalDate = format(dateConverted);
////		return dateConverted;
//		
//		DateFormat sdf = new SimpleDateFormat(format);
//		Date date = sdf.parse(stringDate);
//		//System.out.println(date.toString());
//		return date;
//	}
	
	public static LocalDateTime stringToLocalTime(String stringDate, String format) throws ParseException
	{
		DateTimeFormatter f = DateTimeFormatter.ofPattern(format, Locale.FRANCE) ;
		LocalDateTime ldt = LocalDateTime.parse(stringDate, f) ;
		return ldt;
	}
	
	public static Calendar stringToCal(String stringDate, String format) throws ParseException
	{
//		// this actually works, got rid of the original code idea
//		String[] splitDate = date.split("/");
//		int days = Integer.parseInt(splitDate[0]);
//		int month = (Integer.parseInt(splitDate[1]) - 1);
//		int year = Integer.parseInt(splitDate[2]);

		// OPTION 1
//		Calendar cal = Calendar.getInstance();
//		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
//		cal.setTime(sdf.parse(stringDate));
		
		// OPTION 2: New modern way in Java 8 and later.
		LocalDateTime ldt = stringToLocalTime(stringDate, format);
		Calendar cal = Calendar.getInstance();
		cal.set(ldt.getYear(), ldt.getMonthValue()-1, ldt.getDayOfMonth(),ldt.getHour(), ldt.getMinute(), ldt.getSecond());
		return cal;

	}

	public static String calendarToString(Calendar date, String format)
	{
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		fmt.setCalendar(date);
		String dateFormatted = fmt.format(date.getTime());
		return dateFormatted;
	}
	
	/**
	 * From byte 85 returns a binarty string 01010101
	 * @param num, input byte
	 * @return binary string
	 */
	public static String binaryStringFromByte (byte num)
	{
		return String.format("%8s", Integer.toBinaryString(num)).replace(' ', '0');
	}
	
	/**
	 * From byte -88 returns a binarty string 10101000 (rather than 11111111111111111111111110101000)
	 * 
	 * @param num, input byte
	 * @return binary string
	 */
	public static String binaryStringFromUnsignedByte (byte num)
	{
		return String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(num))).replace(' ', '0');
	}
	
	/**
	 * Gets String with dateTime format (YYYY-MM-DDThh:mm:ss.nnn±OO:oo)
	 * where: YYYY-MM-DD includes the date; 
	 * hh:mm:ss includes hour:minute:second; 
	 * nnn is used for milliseconds; 
	 * ±OO:oo provides the Time Zone offset (+ or -)
	 * 
	 * @param hANDLED_RECORD_TIME_CALENDAR
	 * @return String
	 */
	public static String getMDCFormatTime (Calendar cal)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(MDC_DATE_STRING_FORMAT);
		String mdcTime= dateFormat.format(cal.getTime());
		mdcTime = mdcTime +getTimeZoneOffset(cal);
		return mdcTime;
	}
	
	/**
	 * Provides ±OO:oo according to the Time Zone offset (+ or -)
	 * @param Calendar cal
	 * @return String with Calendar timeZone offset in format ±OO:oo
	 */
	public static String getTimeZoneOffset(Calendar cal)
	{
	    TimeZone tz = TimeZone.getDefault();  
	    int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

	    String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
	    offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

	    return offset;
	}
	
	/**
	 * Converts ms into String time format "HH:mm:ss:SSS"
	 * 
	 * @param long ms
	 * @return time in format "HH:mm:ss:SSS"
	 */
	 public static String milisecondsToTimeFormat (long millis)
	 {
		 long second = (millis / 1000) % 60;
		 long minute = (millis / (1000 * 60)) % 60;
		 long hour = (millis / (1000 * 60 * 60)) % 24;

		return String.format("%02d:%02d:%02d %d(ms)", hour, minute, second, millis);
	 }
	
	/**
	 * 
	 * Gets a two decimal X.xx % String between the division off two longs
	 * 
	 * @param long n num
	 * @param long d den
	 * @return string type X.xx %
	 */
	public static String twoDecimalPercentage (long n, long d)
	{
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(1);
		return df.format(((n * 100.00f) / d));
	}
	/**
	 * 
	 * Converts bytes into human readble format
	 * 
	 * @param bytes to be converted
	 * @param boolean is (International System) can be 1000 or 1024
	 * @return
	 */
	public static String humanReadableByteCount(long bytes, boolean is) {
	    int unit = is ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (is ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (is ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	/**
	 * Gets a String with current time in format "dd-MM-yyyy_HH-mm-ss"
	 * @return current time String
	 */
	public static String currentTimeString ()
	{
		return new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Works out and returns a List of String from an inputString split by chars \\r\\n
	 * 
	 * @param inputString
	 * @return List<String>
	 * @throws IOException
	 */
	public static List<String> getListStringFromLineString(String inputString)
	{
		List<String> fileStringsList = Arrays.asList(inputString.split("[\\r\\n]+"));
		return fileStringsList;
	}
	
	/**
	 * 
	 * Trims string with only one space, removing tabs and spaces
	 * 
	 * @param inputString
	 * @return String with only one space
	 */
	public static String onlyOneSpace(String inputString)
	{
		return inputString.trim().replaceAll("\\s+", " ");
	}
	
	/**
	 * 
	 * Strips all characters but letters and numbers
	 * 
	 * @param inputString
	 * @return stripped String
	 */
	public static String onlyLetters(String inputString)
	{
		String s=null;
		s=inputString.replaceAll("[^a-zA-Z]","");
		return s;
	}
	
	/**
	 * 
	 * Strips all characters but letters and numbers
	 * 
	 * @param inputString
	 * @return stripped String
	 */
	public static String onlyNumAndLetters(String inputString)
	{
		String s=null;
		s=inputString.replaceAll("[^a-zA-Z0-9]","");
		return s;
	}
	
	/**
	 * 
	 * Strips all characters but  numbers
	 * 
	 * @param inputString
	 * @return stripped String
	 */
	public static String onlyNum(String inputString)
	{
		String s=null;
		s=inputString.replaceAll("[^\\d.]","");
		return s;
	}
	
	public static boolean isOnlyNum(String str)  
	{  
		try  
		{  
			Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}
	
	/**
	 * 
	 * Trimms a String without leading and trailing white spces and tabs
	 * 
	 * @param String s
	 * @return s without header and trailling white spces
	 */
	public static String trimmLeadingTraillingWhitespaces (String s)
	{
		return s.replaceAll("^\\s+|\\s+$", "");
	}
	
	/**
	 * 
	 * Trimms a String without white spaces
	 * 
	 * @param String s
	 * @return s without white spaces
	 */
	public static String trimmSpaces (String s)
	{
		return s.replaceAll("\\s", "");
	}
	

	/**
	 * 
	 * @param String objectString
	 * @return String[] with the split words by white space 
	 */
	public static String[] getWords(String objectString)
	{
		return objectString.split("\\s+");
	}
	
	/**
	 * 
	 * Check if a String contains more than certain numbers in a String
	 * 
	 * @param string
	 * @param maxNum
	 * @return
	 */
	public static boolean moreNumbersInString(String string, int maxNum)
	{
	    int numberOfNumbers = 0;
	    for (int i = 0; i < string.length(); i++)
	    {
	        if (Character.isDigit(string.charAt(i)))
	        {
	            numberOfNumbers++;
	            if (numberOfNumbers > maxNum)
	            {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	/**
	 * 
	 * @param boolean checkName, if the file name is going to be checked
	 * @param File path
	 * @param String encoding, type of encoding
	 * @param String containName, part of the name to be checked
	 * @param String extension, file's extension
	 * @throws IOException
	 */
	public static void convertFilesIntoEncoding (boolean checkName, String path, String encoding, String containName, String extension) throws IOException
	{
		File inPath = new File (path);
		if (inPath.exists())
		{
			for (File f:inPath.listFiles())
			{
				if (isTheFile(f, containName, extension) || checkName)
				{

					InputStreamReader isr = new InputStreamReader(new FileInputStream (f.getPath()));
					Reader in = new BufferedReader(isr);
					StringBuffer buffer = new StringBuffer();

					int ch;
					while ((ch = in.read()) > -1)
					{
						buffer.append((char)ch);
					}
					in.close();


					FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
					Writer out = new OutputStreamWriter(fos, encoding);
					out.write(buffer.toString());
					out.close();
					Logger.log(LogEnum.DEBUG, "SUCCESS converting into encoding " + encoding + ": " + f.getName());
				}
			}
		}

	}
	
	/**
	 * Identifies a file with certain name and extension
	 * @param f
	 * @param containName
	 * @param extension
	 * @return
	 */
	public static boolean isTheFile (File f, String containName, String extension)
	{
		if (f.getName().contains(containName) && f.getName().endsWith(extension))
		{
			return true;
		}
		return false;
	}

	/**
	 * Gets rid off all \\n \\r \\f \\r\\n
	 * @param String fileString, content itself
	 * @param fileName, optional (is it gonna be a file)
	 * @return String with only one /n for line
	 */
	public static String  trimAllNewLinesToOne(String fileString, String fileName)
	{
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new StringReader(fileString)))
		{
			String line = reader.readLine();
			while (null != line)
			{
				if (line.isEmpty())
				{
					while (null != line && (line.isEmpty() || line.equals("\f")))
					{
						line = reader.readLine();
					}
				}
				
				String newLine = line+"\n";
				sb.append(newLine);
				//System.out.println(newLine);

				// last line
				if (null != line)
				{
					line = reader.readLine();
				}
					
			}
			reader.close();
		}
		catch (IOException e)
		{
			Logger.log(LogEnum.ERROR,"Removing \\n \\r \\f \\r\\n in file" + fileName);
			e.printStackTrace();
		}
		return sb.toString();
	}

}
