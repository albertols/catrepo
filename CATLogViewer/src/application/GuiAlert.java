package application;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class GuiAlert extends Alert
{
	public String titleText = null;
	public String headerText = null;
	public String contentText = null;
	public AlertType alertType = null;
	public Optional<ButtonType> result = null;
	

	public GuiAlert(AlertType alertType, String titleText, String headerText, String contentText)
	{
		super(alertType);
		this.alertType=alertType;
		this.titleText = titleText;
		this.headerText = headerText;
		this.contentText = contentText;
		this.setTitle(titleText);
		this.setHeaderText(headerText);
		this.setContentText(contentText);
		result = this.showAndWait();
		
	}
	
	public boolean action ()
	{
		boolean res = true;
		switch (alertType) {
		case INFORMATION:

			break;

		case WARNING:

			break;

		case ERROR:

			break;

		case CONFIRMATION:
			if (result.get() != ButtonType.OK)
			{
				res = false;
			}
			
		default:
			break;
		}
		
		return res;
	}
	
	
	
	
	
	

}
