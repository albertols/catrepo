package application;

import com.parser.utils.csv.AttrsAndValue;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class CATRow
{
	private AttrsAndValue attrsAndValue;
	private int pos;
	private String varName;
	private CheckBox checkBox;
	private ComboBox<String> comboBox;
	private boolean isPloted=true;
	
	public CATRow(AttrsAndValue attrsAndValue)
	{
		this.attrsAndValue = attrsAndValue;
		this.pos = attrsAndValue.posName;
		this.varName = attrsAndValue.varName;
		
	}
	
	public CATRow(AttrsAndValue attrsAndValue, CheckBox checkBox, ComboBox<String> cb)
	{
		this.attrsAndValue = attrsAndValue;
		this.pos = attrsAndValue.posName;
		this.varName = attrsAndValue.varName;
		this.checkBox = checkBox;
		this.comboBox = cb;
		initCheckBox ();
	}

	private void initCheckBox()
	{
		if (this.comboBox.getItems().size()>0 && null!=this.comboBox.getItems().get(0))
		{
			this.comboBox.setValue(this.comboBox.getItems().get(0));
		}
//		getCheckBox().selectedProperty().addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//				getCheckBox().setSelected(!newValue);
//				System.out.println("hello" + varName + "=" + getCheckBox().isSelected());
//			}
//		});
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
	
	
	
	
	
	
	

}
