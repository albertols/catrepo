package application;

import java.awt.ScrollPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;
import com.parser.utils.csv.AttrAndValue;
import com.parser.utils.csv.AttrsAndValue;
import com.parser.utils.csv.CSVConfig;
import com.parser.utils.csv.InputCSV;
import com.sun.xml.internal.ws.client.ClientSchemaValidationTube;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

public class MainController implements Initializable
{

	@FXML
	public TreeTableView<CATRow> varTree;

	@FXML
	public Button plotBtn;

	@FXML
	public Button clearBtn;

	@FXML
	public LineChart<?, ?> enumChart;

	@FXML
	public TextArea log_TextArea;

	@FXML
	public TextField startTextField;
	
	@FXML
	public TextField samplesTextField;

	@FXML
	public TextField endTextField;
	
	@FXML
	public TextField sampleTimeTextField;

	@FXML
	public Slider startSlider;

	@FXML
	public Slider endSlider;

	public TreeItem <CATRow> varsRoot;

	@FXML
	private AnchorPane bigChartPane;

	private LineChart<Number, Number> bigChart;

	@FXML
	private BorderPane borderPaneBigChart;

	private InputCSV csv;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initCSV ();

		// init UI
		initSliders ();
		initPlots ();
		initTreeItemView ();

	}
	
	Calendar startSimTime;
	Calendar endSimTime;

	private void initSliders()
	{
		if (initTimestampFields ())
		{
			// max and min
			Iterator<Integer> iter = csv.calMap.keySet().iterator();
			Integer min = iter.next();
			Integer max = min;
			while (iter.hasNext())
			{
				max = iter.next();
			}
			
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
			startSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number oldVal, Number newVal) {
					int val = newVal.intValue();
					startTextField.setText(StringUtils.calendarToString(csv.calMap.get(val), InputCSV.CAT_DATE_STRING_FORMAT_JDK7)
							+ "("+Integer.toString(val)+")");
					if (startSlider.getValue()>endSlider.getValue())
					{
						new GuiAlert(AlertType.ERROR, "Info CATLogViewer", "Error " + startSlider.getId(),
								startSlider.getId() + " value ("+startSlider.getValue()+") annot be greater than "+ endSlider.getId()
								+ " value ("+endSlider.getValue()+")");
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
					endTextField.setText(StringUtils.calendarToString(csv.calMap.get(val), InputCSV.CAT_DATE_STRING_FORMAT_JDK7)
							+ "("+Integer.toString(val)+")");
					if (endSlider.getValue()<startSlider.getValue())
					{
						new GuiAlert(AlertType.ERROR, "Info CATLogViewer", "Error " + endSlider.getId(),
								endSlider.getId() + " value ("+endSlider.getValue()+") cannot be less than "+ startSlider.getId()
								+ " value ("+startSlider.getValue()+")");
						endSlider.setValue(startSlider.getValue()+1);
						startSlider.setValue(endSlider.getValue()-1);
					}
					simTimeChecker ();
				}
			});
			
			// samples
			samplesTextField.setText(Integer.toString(max));
		}
	}
	
	private void initPlots ()
	{
	
		NumberAxis bigxAxis = new NumberAxis();
		bigxAxis.setLabel("Timestamp");
//		bigxAxis.setAutoRanging(false);
//		bigxAxis.setTickLabelsVisible(false);
//		bigxAxis.setTickMarkVisible(false);
//		bigxAxis.setMinorTickVisible(false);
	
		NumberAxis  bigyAxis = new NumberAxis();
		bigyAxis.setLabel("values");
//		bigyAxis.setAutoRanging(false);
//		bigyAxis.setTickLabelsVisible(false);
//		bigyAxis.setTickMarkVisible(false);
//		bigyAxis.setMinorTickVisible(false);
	
		bigChart =  new LineChart<>(bigxAxis, bigyAxis);
		bigChart.setTitle("RawValue Chart");
		bigChart.setLegendVisible(true);
		bigChart.getXAxis().setAutoRanging(true);
		bigChart.getYAxis().setAutoRanging(true);
		borderPaneBigChart.setCenter(bigChart);
	
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
		// gets .csv
		log_TextArea.appendText("Reading "+ InputCSV.CSV_PATH);
		Logger._verboseLogs_DEBUG();
		csv = new InputCSV(InputCSV.CSV_PATH, InputCSV.HEADER);
		csv.exec();
		csv.writeMeas();
		csv.showCalendarMap();
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
		//TreeTableColumn<CATRow, Boolean> colCheckBox =  new TreeTableColumn<CATRow, Boolean>("Plot");
		//colCheckBox.setCellValueFactory(new TreeItemPropertyValueFactory<CATRow, Boolean>("isPloted"));

		// OPTION 2
		//		colCheckBox.setCellFactory(new Callback<TreeTableColumn<CATRow,Boolean>, TreeTableCell<CATRow,Boolean>>() {
		//
		//            @Override
		//            public TreeTableCell<CATRow, Boolean> call(TreeTableColumn<CATRow, Boolean> e) {
		//                return new CheckboxCellFactory();
		//            }
		//        });



		//		// OPTION 1: works but does not editcheckboxes
		//		colCheckBox.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(colCheckBox));
		//		colCheckBox.setCellFactory(new Callback<TreeTableColumn<CATRow,Boolean>,TreeTableCell<CATRow,Boolean>>() {
		//            @Override
		//            public TreeTableCell<CATRow,Boolean> call( TreeTableColumn<CATRow,Boolean> p ) {
		//                CheckBoxTreeTableCell<CATRow,Boolean> cell = new CheckBoxTreeTableCell<CATRow,Boolean>();
		//                cell.setAlignment(Pos.CENTER);
		//                cell.setDisable(false);
		//                cell.setEditable(true);
		//                //cell.forTreeTableColumn(getSelectedProperty)
		//                //Logger.log(LogEnum.DEBUG,p.get);
		//                return cell;
		//            }
		//        });
		//
		//		colCheckBox.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CATRow,Boolean>, //
		//				ObservableValue<Boolean>>() {
		//			@Override
		//			public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<CATRow,Boolean> param) {
		//				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty();
		//				if (null!=param.getValue() && null!=param.getValue().getValue())
		//				{
		//					TreeItem<CATRow> treeItem = param.getValue();
		//					CATRow row = treeItem.getValue();
		//					booleanProp= new SimpleBooleanProperty(row.isPloted());
		//
		//					// Note: singleCol.setOnEditCommit(): Not work for
		//					// CheckBoxTreeTableCell.
		//					// When "Single?" column change.
		//					booleanProp.addListener(new ChangeListener<Boolean>() {
		//						@Override
		//						public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
		//								Boolean newValue) {
		//							row.setPloted(newValue);
		//							Logger.log(LogEnum.DEBUG,"\trefreshed checkbox for "+row.getVarName() +"="+newValue);
		//						}                     
		//					});
		//					//Logger.log(LogEnum.DEBUG,"\trefreshed checkbox for "+row.getVarName() +"="+booleanProp);
		//					return booleanProp;
		//				}
		//				return booleanProp;
		//			}
		//		});


		//		// GENDER (COMBO BOX).
		//		valTypeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AttrsAndValue, String>, //
		//        ObservableValue<String>>() {
		// 
		//            @Override
		//            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AttrsAndValue, String> param) {
		//                TreeItem<AttrsAndValue> treeItem = param.getValue();
		//                AttrsAndValue attr = treeItem.getValue();
		//                // F,M
		//                String genderCode = attr.getTypeAndPos().get(0);
		//                //String type = Gender.getByCode(genderCode);
		//                return new SimpleObjectProperty<String>(genderCode);
		//            }
		//        });
		//		
		//        ObservableList<String> genderList = FXCollections.observableArrayList(//
		//                "Raw Value","Calc Value","Enum/Bitmap");
		//        valTypeCol.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(genderList));
		// 
		//        // After user edit on cell, update to Model.
		//        valTypeCol.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<AttrsAndValue, String>>() {
		// 
		//            @Override
		//            public void handle(TreeTableColumn.CellEditEvent<AttrsAndValue, String> event) {
		//                TreeItem<AttrsAndValue> item = event.getRowValue();
		//               // Employee emp = item.getValue();
		//                //String newGender = event.getNewValue();
		//                //emp.setGender(newGender.getCode());
		//                //Logger.log(LogEnum.DEBUG,"Single column commit. new gender:" +newGender);
		//               // Logger.log(LogEnum.DEBUG,"EMP:"+emp.isSingle());
		//            }
		//        });
		//        
		//        valTypeCol.setCellFactory((TreeTableColumn<AttrsAndValue, Map<Integer, String>> param) -> {
		//			TreeTableCell<AttrsAndValue, Map<Integer, String>> cell = new TreeTableCell<AttrsAndValue, Map<Integer, String>>(){
		//				@Override
		//				// updates cells color
		//				protected void updateItem(Map<Integer, String> item, boolean empty)
		//				{
		//					super.updateItem(item, empty);
		//					
		//					if (!empty && null!=item)
		//					{
		//						//Logger.log(LogEnum.DEBUG,item);
		//					}
		//					//cell.
		//				}
		//			};
		//			return cell;
		//		});

		// adds columns and root
		varTree.getColumns().add(colCheckBox);
		varTree.getColumns().add(varsCol);
		varTree.getColumns().add(valTypeCol);
		varTree.getColumns().add(posCol);
		varTree.setRoot(varsRoot);
	}

	private boolean initTimestampFields()
	{
		endSimTime = null;
		startSimTime=null;
		if (null!=csv.endCal)
		{
			endSimTime = csv.endCal;
			endTextField.setText(StringUtils.calendarToString(endSimTime, InputCSV.CAT_DATE_STRING_FORMAT_JDK7));
		}
		if (null!=csv.startCal)
		{
			startSimTime = csv.startCal;
			startTextField.setText(StringUtils.calendarToString(startSimTime, InputCSV.CAT_DATE_STRING_FORMAT_JDK7));
		}
		
		if (null!=endSimTime && null !=startSimTime)
		{
			sampleTimeTextField.setText(StringUtils.friendlyTimeDiff(startSimTime, endSimTime));
			return true;
		}
		return false;
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
			//new GuiAlert(AlertType.INFORMATION, "Info CATLogViewer", "Warning " + component.getId(), "not implemented yet");
			bigChart.getData().clear();
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
		varsRoot.getChildren().forEach((catRow)->{
			CATRow row = catRow.getValue();
			if (row.getCheckBox().isSelected())
			{
				log_TextArea.appendText("\nPloting "+row.getVarName());
				plotVar(row.getAttrsAndValue());
			}
		});
	}


	private void plotVar(AttrsAndValue attrs)
	{	
		//defining a series
		String name = attrs.getVarName();
		Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.setName(name);

		List<AttrAndValue> attrsMapEntry = attrs.attrsMap
				.entrySet()
				.stream()
				.filter(e -> e.getKey().equals("Raw Value"))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(null);
//		int x=0;
//		if (null!=attrsMapEntry && attrsMapEntry.size()>0)
//		{
//			for (AttrAndValue attr:attrsMapEntry)
//			{
//				//populating the series with data
//				if(("Raw Value").equals(attr.type) && x%3==0)
//				{
//					Number y = Integer.parseInt(attr.v);
//					if (name.contains("Speed"))y=Integer.parseInt(attr.v)/1000;
//					series.getData().add(new XYChart.Data<Number, Number>(x, y));
//				}
//				x++;
//			}
//		}
		int x=0;
		if (null!=attrsMapEntry && attrsMapEntry.size()>0)
		{
			for (AttrAndValue attr:attrsMapEntry)
			{
				Number y = Integer.parseInt(attr.v);
				if (name.contains("Speed"))
				{
					y=Integer.parseInt(attr.v)/1000;
				}
				if (isAttrInDateRange(attr))
				{
					series.getData().add(new XYChart.Data<Number, Number>(x, y));
					x++;
				}
				
			}
		}

		bigChart.getData().add(series);
		bigChart.autosize();
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
			CATRow row = new CATRow (vars, new CheckBox(), new ComboBox<>());
			//row = new CATRow (vars);
			list.add(new TreeItem<CATRow>(row));
			//Logger.log(LogEnum.DEBUG,vars);
			System.out.print(vars.posName + " ");
		});
		return list;
	}

	/**
	 * 
	 * @author alopez
	 *
	 */
	public class CheckboxCellFactory extends TreeTableCell<CATRow, Boolean>
	{
		private CheckBox checkBox;

		public CheckboxCellFactory()
		{
			checkBox = new CheckBox();
			checkBox.setSelected(false);
			checkBox.setOnAction(new EventHandler<ActionEvent>()
			{
				//	            @Override
				//	            public void handle(ActionEvent event)
				//	            {
				//	                Logger.log(LogEnum.DEBUG,"clicked: "+checkBox.isSelected());
				//	                //---I called this here to save changes into the cell after clicking on the CheckBox
				//	                commitEdit(checkBox.isSelected());
				//	            }

				@Override
				public void handle(ActionEvent event) {
					boolean c = checkBox.isSelected();
					TreeTableRow<CATRow> tree = getTreeTableRow();
					CATRow row = tree.getItem();
					row.setCheckBox(checkBox);
					commitEdit(checkBox.isSelected());
					Logger.log(LogEnum.DEBUG,"Updated "+row.getVarName() + "="+row.getCheckBox().isSelected());
					Logger.log(LogEnum.DEBUG,row.getAttrsAndValue().toString());
				}
			});
		}

		@Override
		protected void updateItem(Boolean item, boolean empty) {
			if (empty) {
				setText(null);
				setGraphic(null);
			}else if (null!=item){
				checkBox.setSelected(item);
				setText(null);
				setGraphic(checkBox);
				//Logger.log(LogEnum.DEBUG,"Updated "+item);

			}

		}
	}

}
