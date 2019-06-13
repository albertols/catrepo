package application.plot;

import javafx.scene.chart.XYChart;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

public abstract class AbstractChart
{
	private XYChart chart;
	private String name;
	private RowConstraints rowc;
	private ColumnConstraints colc;
	
	public AbstractChart(String name)
	{
		setName(name);
	}
	
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
	
	
	
	
	

}
