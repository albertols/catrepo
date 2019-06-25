package application.chart;

import java.util.Calendar;
import java.util.List;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.csv.AttrAndValue;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class RawValueChart extends AbstractChart
{
	public final static String NAME = "Raw Value";
	public static int PLACE_PRIORITY = 0;
	
	public RawValueChart(Calendar start, Calendar end)
	{
		super (start, end);
	}
	
	public RawValueChart(String name)
	{
		super (name);
		setProirity(PLACE_PRIORITY);
		setPriorityToPlot(1);
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
		
		setColc(new ColumnConstraints(1341.0));
		getColc().setHgrow(Priority.ALWAYS);
	}

	@Override
	public Series getSeries(String varName, List<AttrAndValue> attrsMapEntry, double yCor)
	{
		int x=0;
		Series<Number, Number> rawSeries = new XYChart.Series<Number, Number>();
		rawSeries.setName(varName);
		if (null!=attrsMapEntry && attrsMapEntry.size()>0)
		{
			for (AttrAndValue attr:attrsMapEntry)
			{
				try
				{
					Number y = Integer.parseInt(attr.v);
					y=Integer.parseInt(attr.v)/yCor;
					
					if (isAttrInDateRange(attr))
					{
						rawSeries.getData().add(new XYChart.Data<Number, Number>(x, y));
						x++;
					}
				}
				catch (Exception e)
				{
					Logger.log(LogEnum.ERROR,"Parsing into int "+this.getClass().getSimpleName()+" "+attr.v);
				}
			}
		}
		return rawSeries;
	}

}
