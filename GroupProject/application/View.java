package application;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class View extends Application {
	
	// to make grid height and width all relative to a single number input so we can easily update it with one number
	private static final double smallSectionRatio = .3; // for columns, the middle is 1/2 the size of left and right; for rows, the top and bottom are 1/2 the size of the middle row
	private static final double heightToWidthRatio = .4;  // height is 40% of the width
	
	private static final double baseWidth = 500;  // this is the only number we need to update to change the overall scale of the grid
	private static final double baseHeight = baseWidth * heightToWidthRatio;  // height is 40% of the width
	
	private static final double topHeight = baseHeight * smallSectionRatio;	// top height is relative to middle height
	private static final double middleHeight = baseHeight;		// middle is the default
	private static final double bottomHeight = baseHeight * smallSectionRatio; // bottom height is relative to middle height
	private static final double rightWidth = baseWidth;			// right width is default
	private static final double centerWidth = baseWidth * smallSectionRatio;  // center width is relative to right and left width
	private static final double leftWidth = baseWidth;			// left width is default
	
	private static final double minButtonSize = 100;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		try
		{
			GridPane parent = new GridPane();
			
			parent.add(GetOptionsLoadSaveBox(), 0, 0);
			parent.add(GetOptionsListBox(), 0, 1);
			parent.add(GetOptionsListButtons(), 0, 2);
			parent.add(new VBox(), 1, 0); // dummy just to make it make sense for now. can remove later
			parent.add(GetAddRemoveButtons(), 1, 1);
			parent.add(new VBox(), 1, 2); // dummy just to make it make sense for now. can remove later
			parent.add(GetMealLabel(), 2, 0);
			parent.add(GetMealList(), 2, 1);
			parent.add(GetMealAnalyzeButton(), 2, 2);
			
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
	
	// Top Left
	private HBox GetOptionsLoadSaveBox()
	{
		HBox rtnBox = new HBox();
		rtnBox.setAlignment(Pos.CENTER);
		rtnBox.setPadding(new Insets(5, 5, 5, 5));
		Label lblOptions = new Label("Food Options");
		lblOptions.setFont(Font.font("Ariel", 18));
		Button btnLoadList = newButton("Load List");
		Button btnSaveList = newButton("Save List");
		rtnBox.setMinHeight(topHeight);
		rtnBox.setMinWidth(leftWidth);
		final Pane spacer = new Pane();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
		rtnBox.getChildren().addAll(lblOptions, spacer, btnLoadList, btnSaveList);
		return rtnBox;
	}
	
	// Middle left
	private HBox GetOptionsListBox()
	{
		HBox rtnBox = new HBox();
		// add scrollbox
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(leftWidth);
		return rtnBox;
	}
	
	// Bottom left
	private HBox GetOptionsListButtons()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(bottomHeight);
		rtnBox.setMinWidth(leftWidth);
		rtnBox.setAlignment(Pos.TOP_CENTER);
		Button btnNewItem = newButton("Add New Item");
		Button btnFilters = newButton("Filters");
		Button btnClearFilters = newButton("Clear Filters");
		final Pane spacer = new Pane();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
		rtnBox.getChildren().addAll(spacer, btnNewItem, btnFilters, btnClearFilters);
		return rtnBox;
	}
	
	// Middle Center
	private VBox GetAddRemoveButtons()
	{
		VBox rtnBox = new VBox();
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(centerWidth);
		rtnBox.setAlignment(Pos.CENTER);
		double preferredWidth = centerWidth * .8;
		Button btnAddItem = newButton("Add to Meal");
		Button btnRemoveItem = newButton("Remove from Meal");
		btnAddItem.setMinWidth(preferredWidth);
		btnRemoveItem.setMinWidth(preferredWidth);
		rtnBox.getChildren().addAll(btnAddItem, btnRemoveItem);
		return rtnBox;
	}
	
	private HBox GetMealLabel()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(topHeight);
		rtnBox.setMinWidth(rightWidth);
		rtnBox.setAlignment(Pos.CENTER_LEFT);
		Label lblMeal = new Label("Meal List");
		lblMeal.setFont(Font.font("Ariel", 18));
		rtnBox.getChildren().add(lblMeal);
		return rtnBox;
	}
	
	private HBox GetMealList()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(rightWidth);
		return rtnBox;
	}
	
	private HBox GetMealAnalyzeButton()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(bottomHeight);
		rtnBox.setMinWidth(rightWidth);
		Button btnAnalyze = newButton("Analyze");
		final Pane spacer = new Pane();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
		rtnBox.getChildren().addAll(spacer, btnAnalyze);
		return rtnBox;
	}
	
	private Button newButton(String btnCaption)
	{
		Button rtnButton = new Button(btnCaption);
		rtnButton.setMinWidth(minButtonSize);
		return rtnButton;
	}
}
