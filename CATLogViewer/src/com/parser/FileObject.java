package com.parser;

/**
 * 
 * Parent class to save retrieved objects from files that are used by @Parser
 * 
 * @author alopez
 */
public class FileObject 
{
	private String foType;
	public FileObject()
	{
		foType=this.getClass().getSimpleName();
	}
	public String getFoType() {
		return foType;
	}
	@Override
	public String toString() {
		return foType + " ";
	}
	
	
	
	

}
