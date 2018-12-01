package application;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorInput;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;


public class Test2 extends Application{
	final int SIZE = 60;
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
		VBox vBox1 = new VBox(10);
		HBox hBox1 = new HBox(10);
		HBox hBox2 = new HBox(10);
		Label label1 = new Label("Food Options");
		Label label2 = new Label("Meal List");
		
		
      
		
		HBox hBox3 = new HBox(10);
		
		
		//trying scroll pane
//		ScrollPane pane1 = new ScrollPane();
//		//pane1.setPrefSize(100, 200);
//		pane1.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//		pane1.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//		
//		ScrollPane pane2 = new ScrollPane();
//		
//		
//		root.setLeft(pane1);
//		root.setRight(pane2);
//		pane1.setPrefSize(100, 100);
	
		//all buttons on ui
		Button button1 = new Button("Add to Meal"); // center buttons
		Button button2 = new Button("Remove from Meal");// center buttons
		Button button3 = new Button("Load List");
		Button button4 = new Button("Save List");
		Button button5 = new Button("Add New Item");
		Button button6 = new Button("Filters");
		Button button7 = new Button("Clear Filters");
		Button button8 = new Button("Analyze");
		
		//setting buttons inside hboxes and vboxes in corresponding areas of ui
		vBox1.getChildren().addAll(button1, button2);
		hBox1.getChildren().addAll(label1, button3, button4, label2);
		
		//setting offsets of labels 
		HBox.setMargin(label1, new Insets(0, 50, 0, 0));
		HBox.setMargin(label2, new Insets(0, 0, 0, 300));
		
		//adding space between buttons
		HBox.setMargin(button5, new Insets(0, 0, 0, 100));
		HBox.setMargin(button8, new Insets(0, 0, 0, 300));
		hBox2.getChildren().addAll(button5, button6, button7, button8);
		
		
		root.setTop(hBox1);
		root.setCenter(vBox1);
		root.setBottom(hBox2);
		
		//scrollpane attempt
		// BorderPane main = new BorderPane();

	        ScrollPane scroll = new ScrollPane();
	        VBox scrollBox = new VBox();

	        // Demo purposes; Wouldn't normally do this - just let the box automatically fit the content
	        scrollBox.setPrefSize(100, 50);
	        scrollBox.setEffect(new ColorInput(0,0,1000,500,Color.LIME));

	        scroll.setContent(scrollBox);

	       // root.setLeft(new Label("Left Content"));
	       root.setLeft(scroll);
	       
	       ScrollPane scroll2 = new ScrollPane();
	       VBox scrollBox2 = new VBox();
	       
	       scrollBox2.setPrefSize(100, 50);
	       scrollBox2.setEffect(new ColorInput(0,0,100,500,Color.LIME));
	       scroll2.setContent(scrollBox2);
	       root.setRight(scroll2);

	      
		
		
		
		
		
		vBox1.prefHeightProperty().bind(root.heightProperty().subtract(SIZE*2));
		vBox1.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(root, 800, 500);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Meal Analysis");
		primaryStage.show();
	}
	
	public static void main(String [] args) {
		launch(args);
	}


}
