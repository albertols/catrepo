package com.parser.utils.csv.ptu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;
import com.parser.utils.csv.AttrsAndValue;
import com.parser.utils.csv.CATInputCSV;

import application.chart.CalcValueChart;
import application.chart.EnumChart;
import application.chart.RawValueChart;

/**
 * Parser for an input .csv exported with PTU format (from .xlsx).
 * @author alopez
 *
 */
public class PTUInputCSV extends CATInputCSV
{
	public final static String CSV_PTU_PATH = "input/csv/PTU/S0X_STOPPING_NOMINAL_T1.PLD.csv";
//	public String CAT_DATE_STRING_FORMAT_JDK8 = "dd LLL uuuu HH:mm:ss:SSSS";
//	public String CAT_DATE_STRING_FORMAT_JDK7 = "dd MMMM yyyy HH:mm:ss:SSSS";
	public final static String CSV_PTU_PATH_1 = "input/csv/PTU/S0X_STOPPING_NOMINAL_T1.PLD(2).csv";
	public final static String CSV_PTU_PATH_2 = "input/csv/PTU/2.csv";
	public final static String CSV_PTU_PATH_3 = "input/csv/PTU/scr_uucm_vato_023_T1_PM_Speed.xls.csv";

	// static
	//public String CAT_DATE_STRING_FORMAT_JDK7 = "dd MMM yyyy HH:mm:ss:SSSS";
	public String CAT_DATE_STRING_FORMAT_JDK7 = "dd-MM-yyyy HH:mm:ss:SSSS";
	public String DELIMITER = ";";

	public PTUInputCSV(String filePath, String headerFormat)
	{
		super(filePath, headerFormat);
		setCAT_DATE_STRING_FORMAT_JDK7(CAT_DATE_STRING_FORMAT_JDK7);
		setDELIMITER(DELIMITER);
	}
	
	public static void main(String[] args)
	{
		Logger._verboseLogs_DEBUG();
		PTUInputCSV csv = new PTUInputCSV(CSV_PTU_PATH_1, HEADER);
		csv.exec();
		csv.writeMeas();
		csv.showCalendarMap();
	}
	
	public void exec()
	{
		Logger.log(LogEnum.INFO,"filePath: "+ filePath);
		// check .csv extension
		File f = new File(filePath);
		if (!f.getPath().endsWith(".csv"))
		{
			Logger.log(LogEnum.ERROR,"WRONG .csv file: "+ filePath);
			return;
		}


		try (BufferedReader reader = new BufferedReader(new StringReader(this.parser.getFileString())))
		{
			// line 1 and 2
			String firstLine = reader.readLine();
			String secondLine = reader.readLine();
			composeCATLogHeader (firstLine, secondLine);

			// checks first row
			if (true/*checkHeader(firstLine)*/)
			{
				String line =reader.readLine();
				String ts = "";
				String lastTs = "";
				while (null != line)
				{
					String[] fields = line.split(DELIMITER);
					List<String> row = new ArrayList<>();

					if (true && fields.length>2/*fields.length==headerSize *//*fieldAssessment (String s)*/)
					{
						// Date and Time
						ts = fields[1].trim();
						if (!lastTs.equals(ts))
						{
							lastTs=ts;
							row.add(ts);
							AttrsAndValue aav = null;
							for (int x=1; x<fields.length /*&& x<MAX_ATTR_SIZE*/; x++)
							{
								//Logger.log(LogEnum.DEBUG,"*"+fields[x].trim());
								
								String field = fields[x].trim();
								String lettersField = StringUtils.onlyLetters(field);
								int type = 0;
								
								if (lettersField.length()>0)
								{
									// Raw Value case (Hex case)
									if (("x").equalsIgnoreCase(String.valueOf(lettersField.charAt(0))))
									{
										field = Integer.toString(Integer.decode(field));
										type = typeMap.get(RawValueChart.NAME);
										//Logger.log(LogEnum.DEBUG, "*"+field);
									}
									// Enum/Value case
									else
									{
										type = typeMap.get(EnumChart.NAME);
									}
									
								}
								else
								{
									// Calc Value
									type = typeMap.get(CalcValueChart.NAME);
								}
								
								//TODO: magic happens here!
								if (this.measValMap.containsKey(x))
								{
									aav = measValMap.get(x);
								}
								if (null!=aav && field.length()>0)
								{
									aav.newAttr(ts, type, field);
								}
								
							}
							//this.rows.add(row);
							//fosList.add(new VarPojo(/*row.get(0), Integer.parseInt(row.get(1)), row.get(2))*/));
							//Logger.log(LogEnum.DEBUG,row.toString());

							if (fields.length>MAX_ATTR_SIZE)
							{
								//Logger.log(LogEnum.WARNING, "MAX_ATTR_SIZE="+(rowValues.size()+1)+"(it should be="+MAX_ATTR_SIZE+")");
								Logger.log(LogEnum.WARNING, "["+fields.length + "]"+line);
								//System.exit(0);
							}
						}
					}
					else
					{
						Logger.log(LogEnum.WARNING,"\trow does not have header size: "+Arrays.asList(fields));
					}
					if (null != line)
					{
						line = reader.readLine();
					}

				}
				reader.close();
			}
		}
		catch (IOException e)
		{
			Logger.log(LogEnum.ERROR," breaking down rows in:" + filePath);
			e.printStackTrace();
		}

		this.calMap = resolveTimestampCollection ();

	}
	
	protected void composeCATLogHeader (String rawLine1, String rawLine2)
	{
		// shows headers
		Logger.log(LogEnum.INFO,rawLine1);
		Logger.log(LogEnum.INFO,rawLine2);

		// init maps
		this.measNameMap =  new TreeMap<>();
		this.measValMap = new TreeMap<>();
		

		varAndAttrChecker (rawLine1, rawLine2);
				showMeasNameMap ();
//				System.exit(0);

		// naked headerChecker
		// split headers
		String[] vars1 = rawLine1.split(DELIMITER);
		String[] vars2 = rawLine2.split(DELIMITER);
		List<String> varList = new ArrayList<>();
		List<String> varNakedList = new ArrayList<>();
		for (int x=0; x<vars2.length; x++)
		{
			// all vars
			String var = vars2[x].trim();
			//Logger.log(LogEnum.DEBUG,var);
			varList.add(var);

			// just the naked ones
			if (vars2[x].length()>1)
			{
				varNakedList.add(vars2[x]);
			}
		}


		//		showMeasNameMap ();
		MAX_ATTR_SIZE= vars2.length;
		Logger.log(LogEnum.INFO,"Line 1 size: " + varList.size() + " vs. "+ vars1.length);
		Logger.log(LogEnum.INFO,"Naked Line 2 size:" + varNakedList.size() + " vs. "+ ((varNakedList.size()-1)*1));
		Logger.log(LogEnum.INFO,"Line 2 size: " + varList.size() + " vs. "+ vars2.length);
//		System.exit(0);



	}
	
	protected boolean varAndAttrChecker (String rawLine1, String rawLine2)
	{
		//String[] vars1 = rawLine1.split(DELIMITER);
		String[] vars2 = rawLine2.split(DELIMITER);

		// first header
		for (int x=0; x<vars2.length; x++)
		{
			// all vars
			String varName = vars2[x].trim();
			if (varName.length()!=0)
			{
				// adds attr
				AttrsAndValue newAttr = new AttrsAndValue(x, varName, getCAT_DATE_STRING_FORMAT_JDK7());
				this.measNameMap.put(x, varName);

				newAttr.addFoTypeAndPos(typeMap.get(RawValueChart.NAME), RawValueChart.NAME);
				newAttr.addFoTypeAndPos(typeMap.get(CalcValueChart.NAME),CalcValueChart.NAME);
				newAttr.addFoTypeAndPos(typeMap.get(EnumChart.NAME), EnumChart.NAME);
				Logger.log(LogEnum.DEBUG,"--"+varName);
				Logger.log(LogEnum.DEBUG,newAttr.showTypeAndPos());
				this.measValMap.put(x, newAttr);

			}
			else
			{
				Logger.log(LogEnum.ERROR,"Wrong var(is empty), pos=" +x);
			}
		}


		return false;
	}

}
