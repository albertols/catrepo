package application.plot;

public class ChartFactory
{
	public AbstractChart makeChart (String type)
	{
		switch (type) {
		case RawValueChart.NAME:
			return new RawValueChart(RawValueChart.NAME);

		case EnumChart.NAME:
			return new EnumChart(EnumChart.NAME);

		default:
			return null;//TODO: standalone LineChart with number Axis
		}
	}

}
