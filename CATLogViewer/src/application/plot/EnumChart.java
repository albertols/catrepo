package application.plot;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class EnumChart extends AbstractChart
{
	public final static String NAME = "Enum/Bitmap";
	public EnumChart(String name)
	{
		super (name);
		NumberAxis bigxAxis = new NumberAxis();
		bigxAxis.setLabel("Timestamp");
//		bigxAxis.setAutoRanging(false);
//		bigxAxis.setTickLabelsVisible(false);
//		bigxAxis.setTickMarkVisible(false);
//		bigxAxis.setMinorTickVisible(false);
	
		CategoryAxis  bigyAxis = new CategoryAxis();
		bigyAxis.setLabel("Enums");

		setChart(new LineChart<>(bigxAxis, bigyAxis));
		getChart().setTitle(getName()+" Chart");
		
		getChart().setLegendVisible(true);
		getChart().getXAxis().setAutoRanging(true);
		getChart().getYAxis().setAutoRanging(true);
		
		
		setRowc(new RowConstraints(300));
		getRowc().setVgrow(Priority.ALWAYS);
		//chartsGrid.getRowConstraints().add(x, row1);
		
		setColc(new ColumnConstraints(1041.0));
		getColc().setHgrow(Priority.ALWAYS);
	}

}
