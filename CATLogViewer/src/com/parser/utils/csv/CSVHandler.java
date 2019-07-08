package com.parser.utils.csv;

import java.io.IOException;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.FileHandler;

public class CSVHandler
{
	/**
	 * .csv for RecordType exports
	 */
	public FileHandler csv = null;
	
	/**
	 * contains path for report file Name (.txt/.csv)
	 */
	public String path;
	
	private String csvHeader = null;
	

	public CSVHandler(String fileName, String csvHeader)
	{
		initHeader(csvHeader);
		this.path = fileName;
		this.csv = initCSV();
	}
	
	/**
	 * 
	 * New line in .csv, it it has been initialised and enabled by exportConfig.csv
	 * 
	 * @param String csvLine
	 */
	public void newLineCSV(String csvLine)
	{
		if (csv!=null)
		{
			try
			{
				csv.writer.write("\n"+csvLine);
			}
			catch (IOException e)
			{
				
				Logger.log(LogEnum.WARNING,csvLine +" cold not be written on .csv: " + csv.getFile().getName());
			}
		}
	}
	
	public void closesCSV ()
	{
		try
		{
			csv.writer.close();
		}
		catch (IOException e)
		{
			Logger.log(LogEnum.ERROR,this.getClass().getSimpleName()+" cold not close .csv: " + csv.getFile().getName());
		}
	}
	
	private String initHeader(String csvHeader)
	{
		this.csvHeader = csvHeader;
		checkCSVHeader ();
		return null;
	}

	/**
	 * .csv report
	 */
	private FileHandler initCSV ()
	{
		String csvName = this.path+".csv";
		FileHandler exportCSV  =null;
		exportCSV = new FileHandler(csvName);
		exportCSV.initWriter();
		
		// writes header if csv does not exist
		if (this.csv==null && checkCSVHeader ())
		{
			try
			{
				exportCSV.writer.write(csvHeader);
			}
			catch (IOException e)
			{
				Logger.log(LogEnum.ERROR, ".CSV could not be initialized: "+ csvName);
				e.printStackTrace();
			}
		}
		
		return exportCSV;
	}
	
	private boolean checkCSVHeader ()
	{
		if (this.csvHeader==null)
		{
			Logger.log(LogEnum.ERROR, ".CSV header has NOT been properly composed");
		}
		return (this.csvHeader==null) ? false : true;
	}
	
	

}
