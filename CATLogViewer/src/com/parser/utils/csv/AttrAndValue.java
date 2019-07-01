package com.parser.utils.csv;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;

/**
 * @author alopez
 *
 */
public class AttrAndValue implements Comparable<AttrAndValue>
{
	public String type;
	public Calendar cal;
	public String calTs ="null";
	public String ts;
	public String v;
	
	public AttrAndValue(String ts,String type, String v)
	{
		this.ts = ts;
		this.type = type;
		this.v = v;
		convertTimeStampToDate ();
	}
	
	@Override
	public String toString() {
		return "[type=" + type + ", ts=" + ts + ", v=" + v + "]";
	}
	
	public String toStringAndDate() {
		return "[type=" + type + ", ts=" + ts + ", v=" + v + ", Date=" + calTs+"]";
	}
	
	public boolean tsConversionChecker ()
	{
		return ts.equals(calTs)? true:false;
	}

	private void convertTimeStampToDate()
	{
		try
		{
			DateTime dt = DateTime.parse(ts.trim(), DateTimeFormat.forPattern(AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK7).withLocale(Locale.US));
			this.cal = dt.toGregorianCalendar();
			this.calTs = dt.toString(AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK7);
		}
		catch (Exception e)
		{
			Logger.log(LogEnum.ERROR,"WRONG Date conversion for "+ toString());
		}
		
		//if (tsConversionChecker ()!=true)
		{
			//Logger.log(LogEnum.WARNING,"WRONG Date conversion Check for "+ calTs + " != "+ toString());
			//System.exit(0);
		}
	}
	
	public Date getTime ()
	{
		return cal.getTime();
	}

	@Override
	public int compareTo(AttrAndValue a)
	{
		return getTime().compareTo(a.getTime());
	}
}
