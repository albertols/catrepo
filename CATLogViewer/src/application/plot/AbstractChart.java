package application.plot;

import java.util.Calendar;
import java.util.List;

import com.parser.utils.csv.AttrAndValue;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

public abstract class AbstractChart implements Comparable<AbstractChart>
{
	private XYChart chart;
	private String name;
	private RowConstraints rowc;
	private ColumnConstraints colc;
	private Calendar startSimTime;
	private Calendar endSimTime;
	private int proirity = 0;
	
	public AbstractChart(String name)
	{
		setName(name);
	}
	
	public AbstractChart(Calendar start, Calendar end)
	{
		setStartSimTime(start);
		setEndSimTime(end);
	}

	public abstract Series getSeries (String varName, List<AttrAndValue> attrsMapEntry);
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public XYChart getChart() {
		return chart;
	}
	public void setChart(XYChart chart) {
		this.chart = chart;
	}
	public RowConstraints getRowc() {
		return rowc;
	}
	public void setRowc(RowConstraints rowc) {
		this.rowc = rowc;
	}
	public ColumnConstraints getColc() {
		return colc;
	}
	public void setColc(ColumnConstraints colc) {
		this.colc = colc;
	}
	
	public Calendar getStartSimTime() {
		return startSimTime;
	}

	public void setStartSimTime(Calendar startSimTime) {
		this.startSimTime = startSimTime;
	}

	public Calendar getEndSimTime() {
		return endSimTime;
	}

	public void setEndSimTime(Calendar endSimTime) {
		this.endSimTime = endSimTime;
	}
	
	public int getProirity() {
		return proirity;
	}

	public void setProirity(int proirity) {
		this.proirity = proirity;
	}
	
	@Override
	public int compareTo(AbstractChart priorityTocompare) {
		return Integer.compare(getProirity(), priorityTocompare.getProirity());
//		if(getProirity() > priorityTocompare.getProirity())
//		{
//			System.out.println("\t > "+getName()+"="+getProirity() + " against. "+
//					priorityTocompare.getName()+"="+priorityTocompare.getProirity());
//			return 1;
//		}
//		else if(getProirity() < priorityTocompare.getProirity()) 
//		{
//			System.out.println("\t < "+getName()+"="+getProirity() + " against. "+
//					priorityTocompare.getName()+"="+priorityTocompare.getProirity());
//			return -1;
//		}
//		else
//		{
//			System.out.println("\t ="+getName()+"="+getProirity() + " against. "+
//					priorityTocompare.getName()+"="+priorityTocompare.getProirity());
//			return 0;
//		}
	}

	protected boolean isAttrInDateRange (AttrAndValue attr)
	{
		if (startSimTime!=null && endSimTime!=null)
		{
			if(attr.getTime().after(startSimTime.getTime())
					&& attr.getTime().before(endSimTime.getTime()))
			{
				return true;

			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		
	}

}
