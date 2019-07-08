package application.chart;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import com.parser.utils.csv.AttrAndValue;

import javafx.geometry.Insets;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

/**
 * Abstract class to wrap a XTchart (e.g: LineChart), RowConstraints, ColumnConstraints and Insets (for a GridPane) and Calendar boundaries.
 * It also sets up priority to embed Charts in the GUI and priorities to be plot over other AbstractChart types.
 * 
 * @author alopez
 *
 */
public abstract class AbstractChart implements Comparable<AbstractChart>
{
	private XYChart chart;
	private String name;
	private RowConstraints rowc;
	private ColumnConstraints colc;
	private Calendar startSimTime;
	private Calendar endSimTime;
	private int proirity = 0;
	private int priorityToPlot=0;
	private Insets insets = new Insets(10, 1, 10, 1);
	
	public AbstractChart()
	{
		
	}
	
	public AbstractChart(String name)
	{
		setName(name);
	}
	
	public AbstractChart(Calendar start, Calendar end)
	{
		setStartSimTime(start);
		setEndSimTime(end);
	}

	public abstract Series getSeries (String varName, List<AttrAndValue> attrsMapEntry, double yCor);
	
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
	
	public Insets getInsets() {
		return insets;
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	public int getProirity() {
		return proirity;
	}

	public void setProirity(int proirity) {
		this.proirity = proirity;
	}
	
	public int getPriorityToPlot() {
		return priorityToPlot;
	}

	public void setPriorityToPlot(int priorityToPlot) {
		this.priorityToPlot = priorityToPlot;
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

	/**
	 * Assesses if an @AttrAndValue is within a Calendar range
	 * @param attr
	 * @return boolean
	 */
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
	
	/**
	 * Class to compare AbstractChart by priorityToPlot.
	 * 
	 * @author alopez
	 *
	 */
	public class PriorityPlot implements Comparator<AbstractChart>
	{
		@Override
		public int compare(AbstractChart ac0, AbstractChart ac1)
		{
			
			//return ac0.getPriorityToPlot()-ac1.getPriorityToPlot();
			return ac1.getPriorityToPlot()-ac0.getPriorityToPlot();
		}
		
	}

}
