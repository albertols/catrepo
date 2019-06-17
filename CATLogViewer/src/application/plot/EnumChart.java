package application.plot;

import java.util.Calendar;
import java.util.List;

import com.parser.utils.csv.AttrAndValue;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class EnumChart extends AbstractChart
{
	public final static String NAME = "Enum/Bitmap";
	
	public EnumChart(Calendar start, Calendar end)
	{
		super (start, end);
	}
	
	public EnumChart(String name)
	{
		super (name);
		setProirity(2);
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
		
		
		setRowc(new RowConstraints(350));
		getRowc().setVgrow(Priority.ALWAYS);
		//chartsGrid.getRowConstraints().add(x, row1);
		
		setColc(new ColumnConstraints(1041.0));
		getColc().setHgrow(Priority.ALWAYS);
	}
	
	
	@Override
	public Series getSeries(String varName, List<AttrAndValue> attrsMapEntry)
	{
		int x=0;
		Series<Number, String> enumSeries = new XYChart.Series<Number, String>();
		enumSeries.setName(varName);
		if (null!=attrsMapEntry && attrsMapEntry.size()>0)
		{
			for (AttrAndValue attr:attrsMapEntry)
			{
				String y = attr.v;
				
				if (isAttrInDateRange(attr) && y.length()>0)
				{
					enumSeries.getData().add(new XYChart.Data<Number, String>(x, y));
					x++;
				}
			}
		}
		return enumSeries;
	}

}
