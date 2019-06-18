package com.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;

/**
 * 
 * Provides functionalities for a File: path, string, List of string in a file, writer, file to bytes, display content, match rules, creation time stamp etc
 * 
 * @author alopez
 */
public class FileHandler
{
	public FileWriter writer = null;
	
	/**
	 * Stores the File data in bytes []
	 */
	public byte[] fileData;
	
	/**
	 * Contains initialization time stamp of the read file
	 */
	public String timeStampString;
	
	/**
	 * Stores all the File in a List of Strings
	 */
	protected List<String> fileStringsList;
	
	private File file;
	private String fileString;
	private String filePath;

	/**
	 * Decides if Strings for File are going to be loaded or not
	 */
	private boolean loadStrings=true;

	public FileHandler()
	{
		refreshTimeStamp ();
	}
	
	public FileHandler(String filePath)
	{
		setFilePath(filePath);
		init(new File(filePath));
	}
	
	public FileHandler(String filePath, boolean loadString)
	{
		this.loadStrings=loadString;
		setFilePath(filePath);
		init(new File(filePath));
	}
	
	public FileHandler(File file)
	{
		init(file);
	}
	
	public static void copyFiles(String source, String dest)
	{
		try
		{
			Files.copy(new File(source).toPath(), new File(dest).toPath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.log(LogEnum.ERROR, "Copying " + source + " into "+ dest);
		}
	}
	
	public static void createFile (File file)
	{
		try
		{
			if (!file.getParentFile().mkdirs() && !file.getParentFile().exists())
			{
				Logger.log(LogEnum.ERROR, "Creating parent dirs: " +file.getPath());
			}
			
			if (Files.createFile(Paths.get(file.getPath())) == null)
			{
				Logger.log(LogEnum.ERROR, "Creating " +file.getPath());
			}
		}
		catch (IOException e)
		{
			if (!file.getParentFile().exists())
			{
				Logger.log(LogEnum.ERROR, "While Creating " +file.getPath());
			}
			else
			{
				Logger.log(LogEnum.WARNING, "Already exists " +file.getPath());
			}
		}
	}
	
	public static boolean deleteDir(File dir)
	{
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
	
	
	
	public FileWriter getWriter() {
		return writer;
	}

	public void setWriter(FileWriter writer) {
		this.writer = writer;
	}

	/**
	 * Gets rid off all \\n \\r \\f \\r\\n
	 */
	public void trimNewLinesToOne ()
	{
		setFileString(StringUtils.trimAllNewLinesToOne (getFileString(), getFile().getParentFile().getName() + "/"+ getFile().getName()));
		
//		FileHandler bak = new FileHandler(getFilePath()+".backup");
//		bak.flushFileHandlerWriter(bak.filePath, getFileString());
	}
	
	// getters and setters
	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getFileString()
	{
		return fileString;
	}

	public void setFileString(String wholeFileString)
	{
		this.fileString = wholeFileString;
	}
	
	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	/**
	 * Displays File once has been converted into a String
	 */
	public void showFile()
	{
		Logger.log(LogEnum.DEBUG,getFileString());
		Logger.log(LogEnum.INFO,"\n **** NUMBER OF LINES: " + fileStringsList.size());
	}
	
	public void refreshTimeStamp ()
	{
		timeStampString =StringUtils.currentTimeString();
	}
	
	/**
	 * 
	 * Flushes a new filePath with certain content
	 * 
	 * @param String filePath
	 * @param String content
	 */
	public void flushFileHandlerWriter(String filePath, String content)// throws IOException
	{
		FileHandler fh=new FileHandler(filePath);
		fh.modifiesNameWithTimeStamp();
		fh.initWriter();
		try
		{
			fh.writer.write(content);
			fh.writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.log(LogEnum.FATAL, "Wrtiring "+fh.getFilePath()+" for "+fh.getFile().getName()+"."+e.toString());
		}
		
	}
	
	/**
	 * 
	 * Flushes a new filePath with certain content
	 * 
	 * @param String filePath
	 * @param String content
	 */
	public void flushFileHandlerWriter()// throws IOException
	{
		initWriter();
		try
		{
			writer.write(getFileString());
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.log(LogEnum.FATAL, "Wrtiring "+getFilePath()+" for "+getFile().getName()+"."+e.toString());
		}
		
	}
	
	/**
	 * 
	 * Flushes a new filePath with wholeFileString
	 * 
	 * @param String filePath
	 */
	public void flushFileHandlerWriter(String filePath)
	{
		FileHandler fh=new FileHandler(filePath);
		fh.initWriter();
		try
		{
			fh.writer.write(this.getFileString());
			fh.writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.log(LogEnum.FATAL, "Wrtiring "+fh.getFilePath()+" for "+fh.getFile().getName()+"."+e.toString());
		}
		
	}
	
	/**
	 * @param String
	 *            stringToMatch
	 * @return Matcher rule2matcher
	 */
	public Matcher ruleMatcher(String patternRule, String stringToMatch)
	{
		Pattern rRule = Pattern.compile(patternRule);
		Matcher mRule = rRule.matcher(stringToMatch);
		return mRule;
	}
	
	
	/**
	 * Modifies File name by including the time stamp string
	 */
	public void modifiesNameWithTimeStamp()
	{
		setFilePath(getFilePath()+"_"+timeStampString);
		file.renameTo(new File(getFilePath()));
	}

	/**
	 * Initializes writer
	 */
	public void initWriter()
	{
		try
		{
			writer = new FileWriter(getFilePath());
		}
		catch (IOException e) 
		{
			Logger.log(LogEnum.ERROR, "Initializing File writer for" + getFile().getAbsolutePath());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Closes writer
	 */
	public void closeWriter()
	{
		try
		{
			writer.close();
		}
		catch (IOException e) 
		{
			Logger.log(LogEnum.ERROR, "Closing Writer");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Creates driectory in path
	 * 
	 * @param String path
	 * @return String path
	 */
	public String createDir (String path)
	{
		if (!existsDir(path))
		{
			new File (path).mkdirs();
		}
		return existsDir(path) ? path:"";
	}
	
	public boolean existsDir(String path)
	{
		return new File (path).exists() ? true:false;
	}
	
	/**
	 * Convert the file in a an Array of bytes.
	 */
	protected void fileToBytes() throws IOException
	{
		Path path = Paths.get(getFilePath());
		fileData = Files.readAllBytes(path);
		Logger.log(LogEnum.INFO,"FILE BYTES SIZE: " + fileData.length);
	}
	
	/**
	 * Convert the file's Array of bytes into a String.
	 */
	protected void fileBytesIntoString() throws IOException
	{
		int numberOfColumns = 8;
		Logger.log(LogEnum.INFO, "Converting file's Array of bytes into a String...");
		StringBuilder sb = new StringBuilder();
		for (int x=0;x<fileData.length;x++)
		{
			sb.append(String.format("%02X",fileData[x]));
			//System.out.print(String.format("%02X",fileData[x]));
			System.out.print(String.format("%02X",fileData[x]));
			
			System.out.print("["+Integer.toBinaryString(fileData[x])+"]");
			//System.exit(0);
			if (((x+1)%(numberOfColumns*2))==0)
			{
				System.out.print("\n");
				sb.append("\n");
			}
			else if ((x+1)%(numberOfColumns/4)==0)
			{
				System.out.print(" ");
				sb.append(" ");
			}
		}
		
		System.exit(0);
		setFileString(sb.toString());
		PrintWriter writer= new PrintWriter("copy_binary_SHR1");
		writer.print(sb.toString());
		//writer.print(getFileString());
		writer.flush();
		writer.close();
		
	}
	
	/**
	 * Shows File's Array of bytes: .
	 */
	protected void showFileBytes() throws IOException
	{
		int numberOfColumns = 8;
		Logger.log(LogEnum.INFO, "File's Array of bytes: ");
		for (int x=0;x<fileData.length;x++)
		{
			System.out.print(String.format("%02X",fileData[x]));
			if (((x+1)%(numberOfColumns*2))==0)
			{
				System.out.print("\n");
			}
			else if ((x+1)%(numberOfColumns/4)==0)
			{
				System.out.print(" ");
			}
		}
		
	}
	
	/**
	 * 
	 * Converts file from hexadecimal to ASCII
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void hexToAscii() throws FileNotFoundException, IOException
	{
		// to read a single line from the file
		BufferedReader br = new BufferedReader(new FileReader(getFilePath()));
		int read;
		
		// to store the string obtained from buffered reader
		String src= new String();     
		PrintWriter writer= new PrintWriter("fileOutput");
		
		// read an input line from the file
		src=br.readLine();             

		while(src!=null)
		{
			// Trim out the spaces
			src=src.replace(" ", "");   
			for(int i=0;i<src.length();i+=2)
			{
				// convert the String to hex integer
				read=Integer.parseInt(src.substring(i,i+2), 16);
				
				// convert hex to char and write into file
				writer.print((char)read);                           
			}
			src=br.readLine();
		}
		setFileString(writer.toString());
		writer.flush();
	}
	
	protected void hexToAscii2() throws IOException
	{
//		int numberOfColumns = 16;
//	    long streamPtr=0;
//	    InputStream inputStream = new FileInputStream(getFilePath());
//	    StringBuilder sb = new StringBuilder();
//		while (inputStream.available() > 0)
//	    { 
//	        final long col = streamPtr++ % numberOfColumns;
//	        sb.append(String.format("%02X",inputStream.read()));
//	        System.out.printf("%02X",inputStream.read());
//	        if (col == (numberOfColumns-1)) 
//	        {
//	        	sb.append("\n");
//	        }
//	    }
//		setFileString(sb.toString());
		
	}
	
	
	/**
	 * 
	 * Initializes file's time stamp, sets file and works out and sets the file's string
	 * 
	 * @param File file, input file
	 * @throws IOException 
	 */
	private void init(File file)
	{
		// initializes time stamp
		refreshTimeStamp ();
		
		// sets file
		setFile(file);
		
		try
		{
			// works out and sets the file's string
			if (file.exists() && loadStrings)
			{
				setFileString(loadFileToString());
				Logger.log(LogEnum.INFO, "Successfully loaded String for file: " + getFile().getAbsolutePath());
			}
			else if (loadStrings)
			{
				Logger.log(LogEnum.WARNING, "Attempting loading string from file that does NOT exist: " + file);

			}
		}
		catch (IOException e)
		{
			Logger.log(LogEnum.ERROR, "ERROR fetching into string: " + getFile().getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Works out and returns and String with the entire file as well as the number of lines.
	 * 
	 * @return String with whole file's lines
	 */
	private String loadFileToString() throws IOException
	{
		FileReader in = new FileReader(getFile());
		BufferedReader br = new BufferedReader(in);
		String line;
		StringBuilder filetoString = new StringBuilder();
		fileStringsList = new ArrayList<>();

		while ((line = br.readLine()) != null)
		{
			// Logger.log(LogEnum.VAR,line);
			filetoString.append(line);
			filetoString.append("\n");
			fileStringsList.add(line);
		}
		in.close();
		return filetoString.toString();
	}
}
