package application;
	
import java.io.File;
import java.io.IOException;

import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.StringUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class App extends Application
{
	public static String s_os = File.separator;
	public Parent root;
	public Scene scene;
	
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,400,400);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
			
			root = FXMLLoader.load(getClass().getResource("/res/layout/main.fxml"));
			root.setId("rootTab");
			scene = new Scene(root);
			primaryStage.setTitle("CATLogViewer");
			primaryStage.setScene(scene);
			scene.getStylesheets().addAll(this.getClass().getResource("/res/style.css").toExternalForm());
			scene.getStylesheets().addAll(this.getClass().getResource("/res/chart1.css").toExternalForm());
			//primaryStage.setMaxHeight(950);
			//primaryStage.setMaxWidth(2000);
			//primaryStage.setMaximized(true);
			primaryStage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// closes all the Threads
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent e)
			{
				Logger.log(LogEnum.INFO,"Closing " + this.getClass().getSimpleName()+ " and appending log");
				try
				{
					Logger.appendFileForLogList("output/CATLogViewer_"+StringUtils.currentTimeString()+".log");
				}
				catch (IOException e1)
				{
					Logger.log(LogEnum.ERROR,"Closing " + getClass().getSimpleName());
					e1.printStackTrace();
				}
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
