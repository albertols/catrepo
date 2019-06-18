package com.log;

/**
 * 
 * POJO class for Log level and message
 */
public class LogSingle
{
	private LogEnum logLevel;
	private String logMsg;
	
	public LogSingle(LogEnum logLevel, String logMsg)
	{
		setLogLevel(logLevel);
		setLogMsg(logMsg);
	}

	public LogEnum getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogEnum logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}
	
	
}
