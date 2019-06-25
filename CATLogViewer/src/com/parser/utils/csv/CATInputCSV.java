package com.parser.utils.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;
import com.parser.utils.csv.CSVConfig;

import application.chart.CalcValueChart;
import application.chart.EnumChart;
import application.chart.RawValueChart;

/**
 * Parser for an input .csv exported with CAT format (from .log).
 * @author alopez
 *
 */
public class CATInputCSV extends CSVConfig
{
	// static and final
	public final static String HEADER = "Date Time, VATO Actual_Location_Confirmed, , , VATO Actual_Location-Region_ID, , , VATO Actual_Location-Segment_ID, , , VATO Actual_Location-Offset, , , VATO Actual_Speed, , , VATO Actual_Speed_KPH, , , VATO Controlling_ATC, , , VATO CTM_Debug[0], , , VATO CTM_Debug[1], , , VATO CTM_Debug[2], , , VATO CTM_Debug[3], , , VATO CTM_Debug[4], , , VATO CTM_Debug[5], , , VATO CTM_Debug[6], , , VATO CTM_Debug[7], , , VATO Hold_Train_Result_From_Alarm, , , VATO Hold_Train_From_RATO, , , VATO Hold_In_Station, , , VATO Hold_Doors_Open, , , VATO Loss_Of_Comm_With_RATO, , , VATO Next_Operating_Mode, , , VATO NP_Read_Counter, , , VATO Occupied_Berths, , , VATO Operating_Mode, , , VATO Startup_Request, , , VATO VATC_Cradle_Index, , , VATO Initialization_Complete, , , Event Name, ";
	public final static String CSV_PATH = "input/csv/VATO_49917_CatRecAutoHolding_20190319_102330.log.csv";
	public final static String CSV_PATH_1 = "input/csv/VATO_61418_CatRecInitialization_20190520_122631.log.csv";
	public final static String CSV_PATH_2 = "input/csv/VATO_61418_CatRecInitialization_20190520_122730.log.csv";
	public final static String CSV_PATH_3 = "input/csv/VATP_11072_CatRecODOMET_20181016_033422.log.csv";
	public final static String CSV_PATH_4 = "input/csv/12_13Jun/VATO_38159_CatRecInitialization_20190612_103729.log.csv";
	public final static String CSV_PATH_5 = "input/csv/12_13Jun/VATO_38159_CatRecNP_20190612_113919.log.csv";
	public final static String CSV_PATH_6 = "input/csv/12_13Jun/VATO_38159_CatRecVATPRATP_20190612_112821.log.csv";
	public final static String CSV_PATH_7 = "input/csv/12_13Jun/VATP_32257_CatRecVATPRATP_20190612_112822.log.csv";
	public final static String CSV_PATH_8 = "input/csv/12_13Jun/VATO_38159_CatRecNP_20190612_035209.log.csv";
	public final static String CSV_PATH_9 = "input/csv/12_13Jun/VATO_38159_CatRecNP_20190613_115437.log.csv";
	public final static String CSV_PATH_10 = "input/csv/12_13Jun/VATO_38159_CatRecNP_20190613_120336.log.csv";
	public final static String CSV_PATH_11 = "input/csv/14Jun/VATO_38159_CatRecNP_20190614_105424.log.csv";
	public final static String CSV_PATH_12 = "input/csv/14Jun/VATP_32257_CatRecNP_20190614_105425.log.csv";
	
	// static
	public String CAT_DATE_STRING_FORMAT_JDK8 = "dd/MM/uuuu HH:mm:ss";
	public String CAT_DATE_STRING_FORMAT_JDK7 = "dd/MM/yyyy HH:mm:ss";
	public String DELIMITER = ", ";
	

	public Map<Integer, String> measNameMap =  null;
	public Map<Integer, AttrsAndValue> measValMap =  null;
	public int MAX_ATTR_SIZE=0;

	public Calendar startCal;
	public Calendar endCal;
	public Map<Integer ,Calendar> calMap = null;
	
	public Map<String, Integer> typeMap = new HashMap<String, Integer>() {
		{
			put(RawValueChart.NAME, 0);
			put(CalcValueChart.NAME, 1);
			put(EnumChart.NAME, 2);
		}
	};


	public CATInputCSV(String filePath, String headerFormat)
	{
		super(filePath, headerFormat);
		setCAT_DATE_STRING_FORMAT_JDK7(CAT_DATE_STRING_FORMAT_JDK7);
		setCAT_DATE_STRING_FORMAT_JDK8(CAT_DATE_STRING_FORMAT_JDK8);
		setDELIMITER(DELIMITER);
	}

	public static void main(String[] args)
	{
		Logger._verboseLogs_DEBUG();
		CATInputCSV csv = new CATInputCSV(CSV_PATH_10, HEADER);
		csv.exec();
		csv.writeMeas();
	}

	public void showCalendarMap ()
	{
		calMap.forEach((pos,date)->{
			Logger.log(LogEnum.DEBUG,pos+"="+StringUtils.calendarToString(date, CAT_DATE_STRING_FORMAT_JDK7));
		});
	}

	public void showMeasNameMap ()
	{
		measNameMap.forEach((k,v)->{
			Logger.log(LogEnum.DEBUG,k+"="+v);
		});
	}

	public void writeMeas ()
	{
		StringBuilder sb =  new StringBuilder();
		this.measValMap.forEach((meas, values)->{
			String name = measNameMap.get(meas);
			if (name.length()>1)
			{
				name = meas+"-"+name;
			}
			else
			{
				name = meas+"-[]";
			}
			Logger.log(LogEnum.DEBUG,name);
			sb.append(name+"\n");
			sb.append(values.toString()+"\n");
		});

		parser.flushFileHandlerWriter(parser.getFile().getName().trim()+".txt", sb.toString());
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
					List<AttrsAndValue> rowValues = new ArrayList<>();


					if (true && fields.length>1/*fields.length==headerSize *//*fieldAssessment (String s)*/)
					{
						// Date and Time
						ts = fields[0].trim();
						if (!lastTs.equals(ts))
						{
							lastTs=ts;
							row.add(ts);
							int count = 0;
							AttrsAndValue aav = null;
							for (int x=1; x<fields.length /*&& x<MAX_ATTR_SIZE*/; x++)
							{
								//Logger.log(LogEnum.DEBUG,"*"+fields[x].trim());
								if (count==typeMap.size())
								{
									count=0;
									//System.exit(0);
								}
								if (this.measValMap.containsKey(x))
								{
									aav = measValMap.get(x);
								}
								if (null!=aav)
								{
									//TODO: magic happens here!
									aav.newAttr(ts, count, fields[x].trim());
									count++;

									//									rowValues.add(valAndTs);
									//row.add(valAndTs.val);
									//									this.measValMap.get(x).add(valAndTs);
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

	protected boolean resolveTimestampBoundaries()
	{
		for (Entry<Integer, AttrsAndValue> entry :measValMap.entrySet())
		{
			AttrsAndValue attrs = entry.getValue();
			Entry<String, List<AttrAndValue>> attrsMapEntry = attrs.attrsMap.entrySet().stream().findFirst().orElse(null);
			if (null!=attrsMapEntry )
			{
				List<AttrAndValue> valList = attrsMapEntry.getValue();
				if (valList.size()>0)
				{
					AttrAndValue firstVal = valList.get(0);
					AttrAndValue lastVal = valList.get(valList.size()-1);
					if (firstVal.ts.equals(lastVal.ts))
					{
						return false;
					}
					else
					{
						startCal = firstVal.cal;
						endCal = lastVal.cal;
						return true;
					}
				}
			}
		}
		return false;
	}

	protected Map<Integer,Calendar> resolveTimestampCollection ()
	{
		Map<Integer, Calendar> calMap = new LinkedHashMap<>();
		int x=0;

		if (resolveTimestampBoundaries())
		{
			for (Entry<Integer, AttrsAndValue> entry :measValMap.entrySet())
			{
				AttrsAndValue attrs = entry.getValue();
				Entry<String, List<AttrAndValue>> attrsMapEntry = attrs.attrsMap.entrySet().stream().findFirst().orElse(null);
				if (null!= attrsMapEntry)
				{
					List<AttrAndValue> valList = attrsMapEntry.getValue();
					if (valList.size()>0)
					{
						for (AttrAndValue attr:valList)
						{
							calMap.put(x, attr.cal);
							x++;
						}
						return calMap;
					}
				}
			}
		}
		return calMap;
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
		//		showMeasNameMap ();
		//		System.exit(0);

		// naked headerChecker
		// split headers
		String[] vars1 = rawLine1.split(DELIMITER);
		String[] vars2 = rawLine2.split(DELIMITER);
		List<String> varList = new ArrayList<>();
		List<String> varNakedList = new ArrayList<>();
		for (int x=0; x<vars1.length; x++)
		{
			// all vars
			String var = vars1[x].trim();
			//Logger.log(LogEnum.DEBUG,var);
			varList.add(var);

			// just the naked ones
			if (vars1[x].length()>1)
			{
				varNakedList.add(vars1[x]);
			}
		}

		//		showMeasNameMap ();
		MAX_ATTR_SIZE= vars1.length;
		Logger.log(LogEnum.INFO,"Line 1 size: " + varList.size() + " vs. "+ vars1.length);
		Logger.log(LogEnum.INFO,"Naked Line 1 size:" + varNakedList.size() + " vs. "+ ((varNakedList.size()-1)*typeMap.size()));
		Logger.log(LogEnum.INFO,"Line 2 size: " + varList.size() + " vs. "+ vars2.length);
		//System.exit(0);

	}

	protected int blanksAhead (int offset, String[] vars1)
	{
		int blanksAhead = 0;
		boolean isBlank = true;
		for (int x=offset; x<vars1.length && isBlank; x++)
		{
			String var = vars1[x].trim();
			//Logger.log(LogEnum.DEBUG,var);
			if (var.length()==0)
			{
				blanksAhead++;
			}
			else
			{
				isBlank=false;
			}
		}
		return blanksAhead;
	}

	protected boolean varAndAttrChecker (String rawLine1, String rawLine2)
	{
		String[] vars1 = rawLine1.split(DELIMITER);
		String[] vars2 = rawLine2.split(DELIMITER);

		// first header
		for (int x=0; x<vars1.length; x++)
		{
			// all vars
			String varName = vars1[x].trim();
			if (varName.length()!=0)
			{
				// adds attr
				AttrsAndValue newAttr = new AttrsAndValue(x, varName, getCAT_DATE_STRING_FORMAT_JDK7(), getCAT_DATE_STRING_FORMAT_JDK8());
				this.measNameMap.put(x, varName);

				int blanksAhead= blanksAhead (x+1, vars1);
				Logger.log(LogEnum.DEBUG,"--"+varName + ": " + blanksAhead + " blanks ahead");
				// second header: gets Attributes using blanksAhead (from the first header)
				for (int y=x ; y<=x+blanksAhead && y<vars2.length; y++)
				{
					String attr = vars2[y].trim();
					//Logger.log(LogEnum.DEBUG,"\t"+attr);
					newAttr.addFoTypeAndPos(y-x, attr);
				}

				Logger.log(LogEnum.DEBUG,newAttr.showTypeAndPos());
				this.measValMap.put(x, newAttr);
				x=x+blanksAhead;
			}
			else
			{
				Logger.log(LogEnum.ERROR,"Wrong var(is empty), pos=" +x);
			}
		}


		return false;
	}

	@Override
	protected void readConfig() {

	}

	public String getCAT_DATE_STRING_FORMAT_JDK8() {
		return CAT_DATE_STRING_FORMAT_JDK8;
	}

	public void setCAT_DATE_STRING_FORMAT_JDK8(String cAT_DATE_STRING_FORMAT_JDK8) {
		CAT_DATE_STRING_FORMAT_JDK8 = cAT_DATE_STRING_FORMAT_JDK8;
	}

	public String getCAT_DATE_STRING_FORMAT_JDK7() {
		return CAT_DATE_STRING_FORMAT_JDK7;
	}

	public void setCAT_DATE_STRING_FORMAT_JDK7(String cAT_DATE_STRING_FORMAT_JDK7) {
		CAT_DATE_STRING_FORMAT_JDK7 = cAT_DATE_STRING_FORMAT_JDK7;
	}

	public String getDELIMITER() {
		return DELIMITER;
	}

	public void setDELIMITER(String dELIMITER) {
		DELIMITER = dELIMITER;
	}

}
