package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.parser.utils.csv.AttrAndValue;
import com.parser.utils.csv.AttrsAndValue;

import application.chart.AbstractChart;
import application.chart.ChartFactory;
import application.chart.EnumChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CATRow
{
	private AttrsAndValue attrsAndValue;
	private int pos;
	private String varName;
	private CheckBox checkBox;
	private ComboBox<String> comboBox;
	private TextField yCor;
	private boolean isPloted=true;
	
//	public CATRow(AttrsAndValue attrsAndValue)
//	{
//		this.attrsAndValue = attrsAndValue;
//		this.pos = attrsAndValue.posName;
//		this.varName = attrsAndValue.varName;
//		
//	}
	
	public CATRow(AttrsAndValue attrsAndValue, CheckBox checkBox, ComboBox<String> cb)
	{
		this.attrsAndValue = attrsAndValue;
		this.pos = attrsAndValue.posName;
		this.varName = attrsAndValue.varName;
		this.checkBox = checkBox;
		this.comboBox = cb;
		this.yCor =  new TextField();
		this.yCor.setDisable(false);
		this.yCor.setVisible(true);
		this.yCor.setEditable(true);
		this.yCor.setId("scale-text-field-y");
		//this.yCor.setStyle("@res/style.css/scale-text-field-0");
		initCheckBox ();
	}

	private void initCheckBox()
	{
		if (this.comboBox.getItems().size()>0 && null!=this.comboBox.getItems().get(0))
		{
			this.comboBox.setValue(this.comboBox.getItems().get(0));
			//Logger.log(LogEnum.DEBUG,"Init CheckBox="+varName);
			automaticSelection ();
		}		
	}

	/**
	 * 
	 */
	private void automaticSelection()
	{
		// 1) Gets all CheckBox types: Raw Value, Calc Value, Enum
		List<AbstractChart> selectionPriorityList =  new ArrayList<AbstractChart>();
		for (String typeItem:this.comboBox.getItems())
		{
			AbstractChart ac = new ChartFactory().makeChart(typeItem);

			if (null!=ac)
			{
				selectionPriorityList.add(ac);
			}
		}
		
		// 2) insertion by priority
		Collections.sort(selectionPriorityList, new EnumChart().new PriorityPlot());
		
		// 3) Selects ComboBox with type
		// Loop 1: Enum, Calc Value, Raw Value
		int x=0;
		for (AbstractChart ac: selectionPriorityList)
		{
			String type = ac.getName();
			List<AttrAndValue> attrs = attrsAndValue.attrsMap.get(ac.getName());
			if (null!=attrs && attrs.size()>0)
			{
				// Loop 2: if value in attribute exists, set the type in ComboBox and CheckBox selected
				for (AttrAndValue attr: attrs)
				{
					if (attr.v.length()>0 && !("0").equals(attr.v) && !(" ").equals(attr.v))
					{
						this.comboBox.setValue(type);
						if (x==0)
						{
							this.checkBox.setSelected(true);
						}
						return;
					}
				}
			}
			x++;
			
		}
	}

	public AttrsAndValue getAttrsAndValue() {
		return attrsAndValue;
	}

	public void setAttrsAndValue(AttrsAndValue attrsAndValue) {
		this.attrsAndValue = attrsAndValue;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public ComboBox<String> getComboBox() {
		return comboBox;
	}

	public void setComboBox(ComboBox<String> comboBox) {
		this.comboBox = comboBox;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public boolean isPloted() {
		return isPloted;
	}

	public void setPloted(boolean isPloted) {
		this.isPloted = isPloted;
	}

	public TextField getYCor() {
		return yCor;
	}

	public void setYCor(TextField yCor) {
		this.yCor = yCor;
	}
	
	


}
