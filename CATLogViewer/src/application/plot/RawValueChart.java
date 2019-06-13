package application.plot;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class RawValueChart extends AbstractChart
{
	public final static String NAME = "Raw Value";
	
	public RawValueChart(String name)
	{
		super (name);
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

}
