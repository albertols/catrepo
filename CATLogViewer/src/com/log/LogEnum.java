package com.log;

/**
 * Enum for Logger
 * 
 * @param @author alopez
 */
public enum LogEnum
{
	ERROR("ERROR"),
	INFO("INFO"),
	JAR("JAR_OUTPUT"),
	WARNING("WARNING"),
	WARNING_VARIABLE("WARNING_VARIABLE"),
	DEBUG("DEBUG"),
	FATAL("FATAL");
	
	private String logType;
	
	LogEnum(String logType)
	{
		this.logType =  logType;
	}
	
	public String getLogType()
	{
		return logType;
	}

}
