package application.plot;

import java.util.Calendar;
import java.util.List;

import com.parser.utils.csv.AttrAndValue;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class CalcValueChart extends AbstractChart
{
	public final static String NAME = "Calc Value";
	
	public CalcValueChart(Calendar start, Calendar end)
	{
		super (start, end);
	}
	
	public CalcValueChart(String name)
	{
		super (name);
		setProirity(1);
		NumberAxis bigxAxis = new NumberAxis();
		bigxAxis.setLabel("Timestamp");
	
		NumberAxis  bigyAxis = new NumberAxis();
		bigyAxis.setLabel("values");

		setChart(new LineChart<>(bigxAxis, bigyAxis));
		getChart().setTitle(getName()+" Chart");
		
		getChart().setLegendVisible(true);
		getChart().getXAxis().setAutoRanging(true);
		getChart().getYAxis().setAutoRanging(true);
		
		// constraints
		setRowc(new RowConstraints(532));
		getRowc().setVgrow(Priority.ALWAYS);
		//chartsGrid.getRowConstraints().add(x, row1);
		
		setColc(new ColumnConstraints(1041.0));
		getColc().setHgrow(Priority.ALWAYS);
	}

	@Override
	public Series getSeries(String varName, List<AttrAndValue> attrsMapEntry)
	{
		
		return null;
	}

}
