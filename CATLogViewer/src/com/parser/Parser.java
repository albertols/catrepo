package com.parser;

import com.log.LogEnum;
import com.log.Logger;

/**
 * 
 * Sets up input and output directories besides @FileHandler
 * 
 * @author alopez
 */
public class Parser extends FileHandler
{
	public final static String DEFAULT_INPUT_PATH ="input\\";
	public final static String DEFAULT_OUTPUT_PATH ="output\\";
	private String input;
	private String output;
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
	public Parser()
	{
		super();
	}

	public Parser(String filePath)
	{
		super (filePath);
		createDir (DEFAULT_INPUT_PATH);
		createDir (DEFAULT_OUTPUT_PATH);
	}
	
	public Parser(String filePath, String input, String output, boolean loadString)
	{
		super (filePath, loadString);
		createInAndOutDirs (input, output);
	}
	
	/**
	 * 
	 * 
	 * Creates Input and output directories
	 * 
	 * @param input
	 * @param output
	 */
	public void createInAndOutDirs (String input, String output)
	{
		// sorts out input dirs
		createDir (DEFAULT_INPUT_PATH);
		setInput(createDir (DEFAULT_INPUT_PATH+input));

		// sorts out  output dirs
		createDir (DEFAULT_OUTPUT_PATH);
		setOutput(createDir(DEFAULT_OUTPUT_PATH+output));
	}
	
	public void exec ()
	{
		Logger.log(LogEnum.JAR, "[**** Executing " +getClass().getSimpleName() + " ****]");
	}
}
