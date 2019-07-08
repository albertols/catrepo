package application.chart;

import java.util.Calendar;

public class ChartFactory
{
	public AbstractChart makeChart (String type)
	{
		switch (type) {
		case RawValueChart.NAME:
			return new RawValueChart(RawValueChart.NAME);
		case EnumChart.NAME:
			return new EnumChart(EnumChart.NAME);
		case CalcValueChart.NAME:
			return new CalcValueChart(CalcValueChart.NAME);

		default:
			//return new CalcValueChart(RawValueChart.NAME);
			return null;//TODO: standalone LineChart with number Axis
		}
	}
	
	public AbstractChart makeSeries (String type, Calendar start, Calendar end)
	{
		AbstractChart ac = null;
		switch (type)
		{
		case RawValueChart.NAME:
			ac =  new RawValueChart(start, end);
			break;
			
		case EnumChart.NAME:
			ac = new EnumChart(start, end);
			break;
			
		case CalcValueChart.NAME:
			ac =  new CalcValueChart(start, end);
			break;

		default:
			return ac;//TODO: standalone LineChart with number Axis
		}
		ac.setName(type);
		return ac;
		
	}

}
