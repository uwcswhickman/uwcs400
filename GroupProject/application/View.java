package application;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View extends Application {
	
	// to make grid height and width all relative to a single number input so we can easily update it with one number
	private static final double smallSectionRatio = .5; // for columns, the middle is 1/2 the size of left and right; for rows, the top and bottom are 1/2 the size of the middle row
	private static final double heightToWidthRatio = .4;  // height is 40% of the width
	
	private static final double baseWidth = 500;  // this is the only number we need to update to change the overall scale of the grid
	private static final double baseHeight = baseWidth * heightToWidthRatio;  // height is 40% of the width
	
	private static final double topHeight = baseHeight * smallSectionRatio;	// top height is relative to middle height
	private static final double middleHeight = baseHeight;		// middle is the default
	private static final double bottomHeight = baseHeight * smallSectionRatio; // bottom height is relative to middle height
	private static final double rightWidth = baseWidth;			// right width is default
	private static final double centerWidth = baseWidth * smallSectionRatio;  // center width is relative to right and left width
	private static final double leftWidth = baseWidth;			// left width is default
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		try
		{
			GridPane parent = new GridPane();
			VBox foodOptionsCol = GetFoodOptionsColumn();
			VBox addRemItemsCol = GetAddRemoveItemsColumn();
			VBox mealCol = GetMealColumn();
			
			parent.addColumn(0, foodOptionsCol);
			parent.addColumn(1, addRemItemsCol);
			parent.addColumn(2, mealCol);
			
			Scene scene = new Scene(parent);
			
			scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
			
			primaryStage.setTitle("Meal Analysis App");
			primaryStage.setScene(scene);;	
			primaryStage.show();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	// Left column
	private VBox GetFoodOptionsColumn()
	{
		VBox rtnBox = new VBox();
		HBox top = GetOptionsLoadSaveBox();
		HBox middle = GetOptionsListBox();
		HBox bottom = GetOptionsListButtons();
		rtnBox.getChildren().addAll(top, middle, bottom);
		return rtnBox;
	}
	
	// Top Left
	private HBox GetOptionsLoadSaveBox()
	{
		HBox rtnBox = new HBox();
		Label lblOptions = new Label("Food Options");
		Button btnLoadList = new Button("Load List");
		Button btnSaveList = new Button("Save List");
		rtnBox.setMinHeight(topHeight);
		rtnBox.setMinWidth(rightWidth);
		rtnBox.getChildren().addAll(lblOptions, btnLoadList, btnSaveList);
		return rtnBox;
	}
	
	// Middle left
	private HBox GetOptionsListBox()
	{
		HBox rtnBox = new HBox();
		// add scrollbox
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(rightWidth);
		return rtnBox;
	}
	
	// Bottom left
	private HBox GetOptionsListButtons()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(bottomHeight);
		rtnBox.setMinWidth(rightWidth);
		Button btnNewItem = new Button("Add New Item");
		Button btnFilters = new Button("Filters");
		Button btnClearFilters = new Button("Clear Filters");
		rtnBox.getChildren().addAll(btnNewItem, btnFilters, btnClearFilters);
		return rtnBox;
	}
	
	private VBox GetAddRemoveItemsColumn()
	{
		VBox rtnBox = new VBox();
		Label top = new Label("Top");
		top.setMinHeight(topHeight);
		top.setMinWidth(centerWidth);
		rtnBox.getChildren().add(top);
		Label middle = new Label("Middle");
		middle.setMinHeight(middleHeight);
		middle.setMinWidth(centerWidth);
		rtnBox.getChildren().add(middle);
		Label bottom = new Label("Bottom");
		bottom.setMinHeight(bottomHeight);
		bottom.setMinWidth(centerWidth);
		rtnBox.getChildren().add(bottom);
		return rtnBox;
	}
	
	private VBox GetMealColumn()
	{
		VBox rtnBox = new VBox();
		Label top = new Label("Top");
		top.setMinHeight(topHeight);
		top.setMinWidth(leftWidth);
		rtnBox.getChildren().add(top);
		Label middle = new Label("Middle");
		middle.setMinHeight(middleHeight);
		middle.setMinWidth(leftWidth);
		rtnBox.getChildren().add(middle);
		Label bottom = new Label("Bottom");
		bottom.setMinHeight(bottomHeight);
		bottom.setMinWidth(leftWidth);
		rtnBox.getChildren().add(bottom);
		return rtnBox;
	}
}
