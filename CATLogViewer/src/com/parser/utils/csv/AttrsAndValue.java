package com.parser.utils.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Saves timestamp and value
 * @author alopez
 *
 */
public class AttrsAndValue
{
	public static String CAT_DATE_STRING_FORMAT_JDK8 = null;
	public static String CAT_DATE_STRING_FORMAT_JDK7 = null;
	public int posName;
	public String varName;
	public Map<Integer, String> typeAndPos = new TreeMap<Integer, String>();
	public ArrayList<String> typeList = new ArrayList<>();
	
	/**
	 * Stores @AttrAndValue in a List. It is accessed by key type.
	 */
	public Map<String, List<AttrAndValue>> attrsMap = new HashMap<String, List<AttrAndValue>>();
	
	public AttrsAndValue(int pos, String varName, String format7, String format8)
	{
		this.posName = pos;
		setVarName(varName);
		AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK8 = format8;
		AttrsAndValue.CAT_DATE_STRING_FORMAT_JDK7 = format7;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder ();
		sb.append(posName);
		int x=0;
//		for (AttrAndValue a:attrs)
//		{
//			if (x==3)
//			{
//				x=0;
//				sb.append("\n");
//			}
//			x++;
//			sb.append("\t"+a);
//		}
		
		attrsMap.forEach((type, attrList)->{
			attrList.forEach((attr)->{
				sb.append("\t"+attr+"\n");
			});
		});
		
		sb.append("\n");
		
		return sb.toString();
	}



	public void addFoTypeAndPos (int pos, String type)
	{
		typeAndPos.put(pos, type);
		typeList.add(type);
		attrsMap.put(type, new ArrayList<>());
	}
	
	public void newAttr (String ts, int typePos, String v)
	{
		String type = typeAndPos.get(typePos);
		AttrAndValue a = new AttrAndValue(ts, type, v);
		attrsMap.get(type).add(a);
		//System.out.println("\t"+a);
	}
	
	public String showTypeAndPos ()
	{
		StringBuilder sb = new StringBuilder();
		typeAndPos.forEach((pos,type)->{
			String s = "\t"+pos +"-"+type;
			sb.append(s+"\n");
			//System.out.println(s);
		});
		return sb.toString();
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public int getPos() {
		return posName;
	}

	public void setPos(int pos) {
		this.posName = pos;
	}
	
	public Map<Integer, String> getTypeAndPos() {
		return typeAndPos;
	}

	public void setTypeAndPos(Map<Integer, String> typeAndPos) {
		this.typeAndPos = typeAndPos;
	}

	public ArrayList<String> getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList<String> typeList) {
		this.typeList = typeList;
	}
	
	
	
	
	
	

}
