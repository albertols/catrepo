package com.parser.utils.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.FileObject;
import com.parser.Parser;

/**
 * Reads a .csv file, checks its header and saves rows in an Array
 * @author alopez
 *
 */
public abstract class CSVConfig
{
	public String filePath;
	public String headerFormat;
	public int headerSize;
	public ArrayList<List<String>> rows;
	public List<FileObject> fosList= new ArrayList<>();
	public Parser parser;
	
	public CSVConfig(String filePath, String headerFormat)
	{
		this.filePath = filePath;
		this.headerFormat = headerFormat;
		this.headerSize = headerSizeCalculator(headerFormat);
		this.rows =  new ArrayList<>();
		this.parser = new Parser(filePath, "", "", true);
		if (this.parser.getFile().exists())
		{
			readConfig ();
		}
		else
		{
			Logger.log(LogEnum.ERROR,"There is NOT .CSV under "+ filePath);
		}
		
	}
	
	public static List<File> getRecursiveCSVFiles (String input, List<File> l)
	{
		if (null==l)
		{
			l = new ArrayList<>();
		}
		
		for (File f : new File(input).listFiles()) 
	    {
			if (f.isFile() && f.getAbsolutePath().endsWith(".csv"))
	        {
				l.add(f);
	        }
			if (f.isDirectory())
			{
				getRecursiveCSVFiles (f.getPath(), l);
			}
			
	    }
		return l;
	}

	public void showCSV ()
	{
		rows.forEach((row)->{
			Logger.log(LogEnum.INFO,row.toString());
		});
	}

	protected abstract void readConfig();
	
	
	protected boolean checkHeader (String header)
	{
		return (header.contains(headerFormat)) ? true : false;
	}

	// GETTERS and SETTERS
	public ArrayList<List<String>> getRows() {
		return rows;
	}

	public void setRows(ArrayList<List<String>> rows) {
		this.rows = rows;
	}
	
	private int headerSizeCalculator (String headerFormat)
	{
		int size = headerFormat.split(";").length;
		if (size==0)
		{
			Logger.log(LogEnum.ERROR,"headerFormat, size=0: "+ headerFormat);
		}
		return size;
	}


}
