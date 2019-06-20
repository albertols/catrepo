package com.parser.utils.csv;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

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
			//Logger.log(LogEnum.DEBUG, AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK8 + "=8 - 7=" + AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK7);
			this.cal = StringUtils.stringToCal(ts.trim(), AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK8);
			this.calTs = StringUtils.calendarToString(cal, AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK7);
			//Logger.log(LogEnum.DEBUG,"\tts "+ts+" vs. "+ calTs);
		}
		catch (ParseException e)
		{
			Logger.log(LogEnum.ERROR,"WRONG Date conversion for "+ toString());
			e.printStackTrace();
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
