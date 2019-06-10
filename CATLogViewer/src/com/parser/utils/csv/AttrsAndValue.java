package com.parser.utils.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Saves timestamp and value
 * @author alopez
 *
 */
public class AttrsAndValue
{
	public int posName;
	public String varName;
	public Map<Integer, String> typeAndPos = new HashMap<Integer, String>();
	public ArrayList<String> typeList = new ArrayList<>();
	public List<AttrAndValue> attrs = new ArrayList<AttrAndValue>();
	public Map<String, List<AttrAndValue>> attrsMap = new HashMap<String, List<AttrAndValue>>();
	
	public AttrsAndValue(int pos, String varName)
	{
		this.posName = pos;
		setVarName(varName);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder ();
		sb.append(posName);
		int x=0;
		for (AttrAndValue a:attrs)
		{
			if (x==3)
			{
				x=0;
				sb.append("\n");
			}
			x++;
			sb.append("\t"+a);
		}
		
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
		addAtrr (a);
		//System.out.println("\t"+a);
	}
	
	public void addAtrr (AttrAndValue a)
	{
		attrs.add(a);
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
