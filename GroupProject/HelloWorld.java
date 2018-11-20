import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloWorld extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
	
		
	
		Parent root = FXMLLoader.load(getClass().getResource("MealAnalysis.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Meal Analysis App");
		primaryStage.setScene(scene);;	
		primaryStage.show();
		
	}
}
