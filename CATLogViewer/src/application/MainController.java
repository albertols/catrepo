package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;
import com.parser.utils.csv.AttrAndValue;
import com.parser.utils.csv.AttrsAndValue;
import com.parser.utils.csv.InputCSV;

import application.plot.AbstractChart;
import application.plot.ChartFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;

public class MainController implements Initializable
{

	@FXML
	public TreeTableView<CATRow> varTree;

	@FXML
	public Button plotBtn;

	@FXML
	public Button clearBtn;

	@FXML
	public TextArea log_TextArea;
	
	@FXML
	public TextField samplesTextField;
	
	@FXML
	public TextField startTextField;
	
	@FXML
	public TextField endTextField;
	
	@FXML
	public TextField startCountField;
	
	@FXML
	public TextField endCountField;
	
	@FXML
	public TextField sampleTimeTextField;

	@FXML
	public Slider startSlider;

	@FXML
	public Slider endSlider;

	@FXML
	public GridPane chartsGrid;
	
	private TreeItem <CATRow> varsRoot;
	
	private InputCSV csv;

	private Calendar startSimTime;
	private Calendar endSimTime;

	/**
	 * Saves Charts by type
	 */
	private Map<String, LineChart> chartMap = new TreeMap<String, LineChart>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initCSV ();
	
		// init UI
		initSliders ();
		initPlots ();
		initTreeItemView ();
	
	}

	private boolean simTimeChecker ()
	{
		int start = (int)startSlider.getValue();
		int end = (int)endSlider.getValue();
		updateSimTime (csv.calMap.get(start), csv.calMap.get(end));
		if (start<end)
		{
			//log_TextArea.appendText("Sim Time OK:"+start+"<"+end);
			return true;
		}
		else if (start==0)
		{
			log_TextArea.appendText("WRONG Sim Time :"+start+"<"+end+"\n");
			Logger.log(LogEnum.WARNING,"WRONG Sim Time :"+start+"<"+end);
		}
		return false;
	}

	private void initCSV()
	{
		Logger._verboseLogs_DEBUG();
		// gets .csv
		log_TextArea.appendText("Reading "+ InputCSV.CSV_PATH_11);
		csv = new InputCSV(InputCSV.CSV_PATH_12, InputCSV.HEADER);
		csv.exec();
		//csv.writeMeas();
		//csv.showCalendarMap();
	}

	private void initSliders()
	{
		if (initTimestampFields ())
		{
			// max and min
			Integer min = 0;
			Integer max = csv.calMap.size()-1;
			
			// start
			startSlider.setMin(min);
			startSlider.setMax(max);
			startSlider.setValue(min);
			startSlider.setShowTickLabels(true);
			startSlider.setShowTickMarks(true);
			startSlider.setMajorTickUnit(500);
			startSlider.setMinorTickCount(5);
			startSlider.setBlockIncrement(10);
			
			// end
			endSlider.setMin(min);
			endSlider.setMax(max);
			endSlider.setValue(max);
			endSlider.setShowTickLabels(true);
			endSlider.setShowTickMarks(true);
			endSlider.setMajorTickUnit(500);
			endSlider.setMinorTickCount(5);
			endSlider.setBlockIncrement(10);
			
			// listeners
			// sliders
			startSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number oldVal, Number newVal) {
					int val = newVal.intValue();
					startTextField.setText(StringUtils.calendarToString(csv.calMap.get(val), InputCSV.CAT_DATE_STRING_FORMAT_JDK7));
					startCountField.setText(Integer.toString(val));
					if (startSlider.getValue()>endSlider.getValue())
					{
						new GuiAlert(AlertType.ERROR, "Info CATLogViewer", "Error " + startSlider.getId(),
								startSlider.getId() + " value="+(int)startSlider.getValue()+" cannot be greater than "+ endSlider.getId()
								+ " value="+(int)endSlider.getValue()+"");
						endSlider.setValue(startSlider.getValue()+1);
						startSlider.setValue(endSlider.getValue()-1);
					}
					
					simTimeChecker ();
				}
			});
			endSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number oldVal, Number newVal) {
					int val = newVal.intValue();
					endTextField.setText(StringUtils.calendarToString(csv.calMap.get(val), InputCSV.CAT_DATE_STRING_FORMAT_JDK7));
					endCountField.setText(Integer.toString(val));
					if (endSlider.getValue()<startSlider.getValue())
					{
						new GuiAlert(AlertType.ERROR, "Info CATLogViewer", "Error " + endSlider.getId(),
								endSlider.getId() + " value="+(int)endSlider.getValue()+" cannot be less than "+ startSlider.getId()
								+ " value="+(int)startSlider.getValue()+"");
						endSlider.setValue(startSlider.getValue()+1);
						startSlider.setValue(endSlider.getValue()-1);
					}
					simTimeChecker ();
				}
			});
					
			// TextField counters
			startCountField.textProperty().addListener((observable, oldValue, newValue)->{
				try
				{
					int i = Integer.parseInt(StringUtils.onlyNum(newValue.trim()));
					if (csv.calMap.containsKey(i) && i<Integer.parseInt(StringUtils.onlyNum(endCountField.getText().trim())))
					{
						startSlider.setValue(i);
					}
				}
				catch (Exception e)
				{
					// nothing to do
				}
			});
			endCountField.textProperty().addListener((observable, oldValue, newValue)->{
				try
				{
					int i = Integer.parseInt(StringUtils.onlyNum(newValue.trim()));
					if (csv.calMap.containsKey(i) && i>Integer.parseInt(StringUtils.onlyNum(startCountField.getText().trim())))
					{
						endSlider.setValue(i);
					}
				}
				catch (Exception e)
				{
					// nothing to do
				}
			});
			
			// samples
			samplesTextField.setText(Integer.toString(max+1));
		}
	}
	
	private boolean initTimestampFields()
	{
		endSimTime = null;
		startSimTime=null;
		if (null!=csv.endCal)
		{
			endSimTime = csv.endCal;
			endTextField.setText(StringUtils.calendarToString(endSimTime, InputCSV.CAT_DATE_STRING_FORMAT_JDK7));
			endCountField.setText(Integer.toString(csv.calMap.size()-1));
			endCountField.setMinHeight(endTextField.getMaxHeight());
		}
		if (null!=csv.startCal)
		{
			startSimTime = csv.startCal;
			startTextField.setText(StringUtils.calendarToString(startSimTime, InputCSV.CAT_DATE_STRING_FORMAT_JDK7));
			startCountField.setText("0");
			startCountField.setMinHeight(startTextField.getMaxHeight());
		}
		
		if (null!=endSimTime && null !=startSimTime)
		{
			sampleTimeTextField.setText(StringUtils.friendlyTimeDiff(startSimTime, endSimTime));
			return true;
		}
		return false;
	}

	private void initPlots ()
	{
		int x = 0;
		for (Entry<Integer, String> e: csv.measValMap.get(1).typeAndPos.entrySet())
		{
			String type = e.getValue();
			ChartFactory cf = new ChartFactory();
			AbstractChart ac = cf.makeChart(type);
			
			if (null!=ac)
			{
				chartsGrid.getRowConstraints().add(x, ac.getRowc());
				chartsGrid.getColumnConstraints().add(0, ac.getColc());
				chartsGrid.add(ac.getChart(), 0, x);
				chartMap.put(type, (LineChart) ac.getChart());
				x++;
			}
		}
	}

	private void initTreeItemView()
	{
		varsRoot =  new TreeItem<CATRow>();

		List<TreeItem <CATRow>> list = newRows();
		varsRoot.getChildren().addAll(list);
		varsRoot.setExpanded(true);

		// column for pos
		TreeTableColumn<CATRow, Integer> posCol =  new TreeTableColumn<CATRow, Integer>("Pos");
		posCol.setEditable(false);
		posCol.setPrefWidth(58.0);
		posCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("pos"));

		// column for variable
		TreeTableColumn<CATRow, String> varsCol =  new TreeTableColumn<>("VarName");
		varsCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("varName"));

		// column for valType
		TreeTableColumn<CATRow, Map<Integer, String>> valTypeCol =  new TreeTableColumn<>("ValueType");
		valTypeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("comboBox"));

		// column for variable
		TreeTableColumn<CATRow, CheckBox> colCheckBox =  new TreeTableColumn<CATRow, CheckBox>("Plot");
		colCheckBox.setPrefWidth(58);
		colCheckBox.setCellValueFactory(new TreeItemPropertyValueFactory<CATRow, CheckBox>("checkBox"));

		// adds columns and root
		varTree.getColumns().add(colCheckBox);
		varTree.getColumns().add(varsCol);
		varTree.getColumns().add(valTypeCol);
		varTree.getColumns().add(posCol);
		varTree.setRoot(varsRoot);
	}

	private void updateSimTime (Calendar start, Calendar end)
	{
		this.startSimTime = start;
		this.endSimTime = end;
	}

	@FXML
	private void handleAction(ActionEvent event)
	{
		ButtonBase component = (ButtonBase) event.getSource();
		switch (component.getId())
		{

		// plotBtn
		case "plotBtn":
//			TODO:clear chart map
			chartMap.forEach((type, lineChart)->{
				lineChart.getData().clear();
			});
			plotVars ();
			break;

			// plotBtn
		case "clearBtn":
			clearBoxes ();
			break;
		}
	}
	

	private void plotVars()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nPloting vars: ");
		varsRoot.getChildren().forEach((catRow)->{
			CATRow row = catRow.getValue();
			
			if (row.getCheckBox().isSelected())
			{
				sb.append(" "+row.getVarName()+",");
				plotVar(row);
			}
			
		});
		log_TextArea.appendText(sb.toString());
	}


	private void plotVar(CATRow row)
	{	
		AttrsAndValue attrs = row.getAttrsAndValue();
		//defining a series
		String name = attrs.getVarName();	
		String type = row.getComboBox().getValue();

		List<AttrAndValue> attrsMapEntry = attrs.attrsMap
				.entrySet()
				.stream()
				.filter(e -> e.getKey().equals(type))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(null);

		// getSeries
		chartMap.get(type).getData().add(getSeries(type, name, attrsMapEntry));
		chartMap.get(type).autosize();
	}
	
	private Series getSeries (String type, String name, List<AttrAndValue> attrsMapEntry)
	{
		Series<Number, Number> series = new XYChart.Series<Number, Number>();
		switch (type) {
		case "Raw Value":
			int x=0;
			Series<Number, Number> rawSeries = new XYChart.Series<Number, Number>();
			rawSeries.setName(name);
			if (null!=attrsMapEntry && attrsMapEntry.size()>0)
			{
				for (AttrAndValue attr:attrsMapEntry)
				{
					Number y = Integer.parseInt(attr.v);
					if (name.contains("Speed")) //TODO: input .xml
					{
						y=Integer.parseInt(attr.v)/1000;
					}
					if (isAttrInDateRange(attr))
					{
						rawSeries.getData().add(new XYChart.Data<Number, Number>(x, y));
						x++;
					}
				}
			}
			return rawSeries;
			// TODO
		case "Calc Value":
			break;

		case "Enum/Bitmap":
			
			x=0;
			Series<Number, String> enumSeries = new XYChart.Series<Number, String>();
			enumSeries.setName(name);
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

		default:
			return null;
		}
		return series;
	}
	
	private boolean isAttrInDateRange (AttrAndValue attr)
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

	private void clearBoxes()
	{
		log_TextArea.appendText("\nClearing Boxes");
		varsRoot.getChildren().forEach((catRow)->{
			CATRow row = catRow.getValue();
			row.getCheckBox().setSelected(false);
			//Logger.log(LogEnum.DEBUG,);
		});

	}

	private List<TreeItem<CATRow>> newRows()
	{
		List<TreeItem <CATRow>> list =  new ArrayList<>();

		csv.measValMap.forEach((pos,vars)->{
			CATRow row = new CATRow (vars,new CheckBox(),new ComboBox<>(FXCollections.observableArrayList(vars.getTypeList())));
			//row = new CATRow (vars);
			list.add(new TreeItem<CATRow>(row));
			//Logger.log(LogEnum.DEBUG,vars);
			System.out.print(vars.posName + " ");
		});
		return list;
	}

}
