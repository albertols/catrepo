package application.plot;

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
		setProirity(2);
		setPriorityToPlot(2);
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
		int x=0;
		Series<Number, Number> rawSeries = new XYChart.Series<Number, Number>();
		rawSeries.setName(varName);
		if (null!=attrsMapEntry && attrsMapEntry.size()>0)
		{
			for (AttrAndValue attr:attrsMapEntry)
			{
				try
				{
					Double i = Double.valueOf(attr.v);
					if (isAttrInDateRange(attr))
					{
						if (("Tractive Effort Request").equals(varName))
						{
							i=i/2000;
						}
						//Logger.log(LogEnum.DEBUG,"\tdouble "+i);
						rawSeries.getData().add(new XYChart.Data<Number, Number>(x, i));
						x++;
					}
				}
				catch (Exception e)
				{
					Logger.log(LogEnum.ERROR,"Parsing into double "+attr.v);
				}
				
			}
		}
		return rawSeries;
	}

}
