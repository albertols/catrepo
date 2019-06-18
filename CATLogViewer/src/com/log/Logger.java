package com.log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Static class. Manages all the syso outputs. Used potentially for .txt output log files
 * 
 * @param @author alopez
 */
public class Logger
{
	/**
	 * tracks number of times the writer has been called
	 */
	public static int writerCounter =0;
	public static FileWriter writer=null;
	public static String logPath;
	public static List<LogSingle> loggerList =  new ArrayList<LogSingle>();
	
	/**
	 * enables syso
	 */
	public static boolean LOG_ENABLE = true;
	
	// critical
	public static boolean ERROR= true;
	public static boolean FATAL= true;
	public static boolean JAR= true;
	
	// less critical
	public static boolean INFO = false;
	public static boolean WARNING = false;
	public static boolean DEBUG= false;
	public static boolean WARNING_VAR= false;
	
	
	
	/**
	 * Shows log type with corresponding message
	 * 
	 * @param LogEnum log, Enum type of log
	 * @param String msg
	 */
	public static void log(LogEnum log, String msg)
	{
//		String outputString ="[" + log.getLogType() + "] " + msg;
//		if (LogEnum.SYSO.getLogType().equals(log.getLogType()))
//		{
//			outputString=msg;
//		}
//		System.out.println(outputString);
//		Logger.LogSingle logSingle = new Logger.LogSingle(log.getLogType(), msg);
		LogSingle logSingle = new LogSingle(log, msg);
		addLog(logSingle);
		
	}
	
	/**
	 * Adds LogSingle to the static list of logs "loggerList"
	 * 
	 * @param logSingle
	 */
	public static void addLog (LogSingle logSingle)
	{
		if (isLogLevelAllowed(logSingle.getLogLevel()))
		{
			loggerList.add(logSingle);
			
			// to show syso
			if (LOG_ENABLE || 
					(logSingle.getLogLevel().equals(LogEnum.JAR) || logSingle.getLogLevel().equals(LogEnum.FATAL) || logSingle.getLogLevel().equals(LogEnum.ERROR)))
			{
				System.out.println(("[" + logSingle.getLogLevel() + "] " +logSingle.getLogMsg()));
			}
		}
	}
	
	public static void _verboseLogs_noDEBUG ()
	{
		DEBUG= false;
		INFO = true;
		WARNING = true;
		WARNING_VAR= true;
	}
	

	public static void _verboseLogs_DEBUG ()
	{
		DEBUG= true;
		INFO = true;
		WARNING = true;
		WARNING_VAR= true;
	}
	
	/**
	 * Disable all logs
	 */
	public static void _offLogs ()
	{
		LOG_ENABLE = false;
		ERROR= false;
		FATAL= false;
		JAR= false;
		INFO = false;
		WARNING = false;
		DEBUG= false;
		WARNING_VAR= false;
	}
	
	/**
	 * Only for ERROR, FATAL, JAR
	 * 
	 */
	public static void _enableSysoCriticalLogs ()
	{
		LOG_ENABLE = true;
		ERROR= true;
		FATAL= true;
		JAR= true;
	}
	
	/**
	 * Shows log type with corresponding message
	 * 
	 * @param LogEnum log, Enum type of log
	 * @param String msg
	 */
	public static void log(LogEnum log, String msg, String headerMsg, boolean showLogLevel)
	{
//		String outputString = "";
//		if (showLogLevel)
//		{
//			outputString = headerMsg+"[" + log.getLogType() + "] " + msg;
//		}
//		else
//		{
//			outputString = headerMsg+msg;
//		}
		//System.out.println(outputString);
		LogSingle logSingle = new LogSingle(log, msg);
		addLog(logSingle);
	}
	
	/**
	 * Clears loggerList
	 */
	public static void clearLogList ()
	{
		loggerList =  new ArrayList<LogSingle>();
	}
	
	/**
	 * 
	 * Initializes logger on String logPath
	 * @param String logPath
	 */
	public static void initWriter (String logPath)
	{
		try
		{
			Logger.logPath=logPath;
			writer = new FileWriter(logPath, true);
		}
		catch (IOException e)
		{
			
			Logger.log(LogEnum.ERROR,"Initializing Logger");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Initializes the writer, counts the writer is being used, iterates over the @Logger.loggerList and  clears already written logs
	 * 
	 * @param String logPath for initializing the writer
	 */
	public static void appendFileForLogList (String logPath) throws IOException
	{
		String outputString = "";
		
		// only the first time Initializes the writer
		if(writerCounter==0)
		{
			initWriter (logPath);
		}
		else
		{
			writerCounter++;
		}
	
		// iterates over the @Logger.loggerList
		for(LogSingle single: loggerList)
		{
			if (isLogLevelAllowed(single.getLogLevel()))
			{
				outputString ="[" + single.getLogLevel() + "] " + single.getLogMsg() + "\n";
				writer.append(outputString);
			}

		}
		
		// closes writer
		Logger.writer.close();
		
		// clears already written logs
		clearLogList ();
	}
	
	/**
	 * check if a certain log level is allowed or not: DEBUG, ERROR, WARNING, INFO, JAR, etc
	 * 
	 * @param LogEnum logEnum
	 */
	public static boolean isLogLevelAllowed (LogEnum logEnum)
	{
		boolean isLevelAllowed =  false;
		switch (logEnum)
		{
		case DEBUG:
			isLevelAllowed = DEBUG; 
			break;
		
		case INFO:
			isLevelAllowed = INFO; 
			break;

		case FATAL:
			isLevelAllowed = FATAL; 
			break;

		case ERROR:
			isLevelAllowed = ERROR; 
			break;

		case JAR:
			isLevelAllowed = JAR; 
			break;

		case WARNING:
			isLevelAllowed = WARNING; 
			break;
		
		case WARNING_VARIABLE:
			isLevelAllowed = WARNING_VAR; 
			break;
			
		default:
			break;
		}
		return isLevelAllowed && LOG_ENABLE;
	}
}
