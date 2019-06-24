package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application
{
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
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
