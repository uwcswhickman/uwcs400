package application;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Resources
 * Pop-up implementation from here: https://stackoverflow.com/questions/22166610/how-to-create-a-popup-windows-in-javafx
 * numeric text field from here: https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
 * Styling tips from combination of various sources including
 *   - https://docs.oracle.com/javafx/2/layout/size_align.htm
 *   - http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
 *   - https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html
 *   - https://stackoverflow.com/questions/43508511/hover-and-pressed-in-javafx
 *   - https://stackoverflow.com/questions/39214586/how-to-align-a-button-right-in-javafx
 *   - https://stackoverflow.com/questions/25336796/tooltip-background-with-javafx-css
 *   
 * @author Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 *
 */
public class View extends Application {
	
	// to make grid height and width all relative to a single number input so we can easily update it with one number
	private static final double smallSectionRatio = .3; // for columns, the middle is 30% the size of left and right; for rows, the top and bottom are 30% the size of the middle row
	private static final double heightToWidthRatio = .4;  // height is 40% of the width
	
	private static final double baseWidth = 550;  // this is the only number we need to update to change the overall scale of the grid
	private static final double baseHeight = baseWidth * heightToWidthRatio;  // height is 40% of the width
	
	private static final double topHeight = baseHeight * smallSectionRatio;	// top height is relative to middle height
	private static final double middleHeight = baseHeight;		// middle is the default
	private static final double bottomHeight = baseHeight * smallSectionRatio; // bottom height is relative to middle height
	private static final double rightWidth = baseWidth;			// right width is default
	private static final double centerWidth = baseWidth * smallSectionRatio;  // center width is relative to right and left width
	private static final double leftWidth = baseWidth;			// left width is default
	
	private static final double minButtonSize = 100;	// basic minimum so that buttons don't look weirdly different all over the place
	
	private ViewController controller;
	private Stage primaryStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		try
		{
			this.controller = new ViewController();
			this.primaryStage = primaryStage;
			GridPane parent = new GridPane();
			
			// top left - options label + load/save list
			parent.add(GetOptionsLoadSaveBox(), 0, 0);
			// middle left - scrollable options list
			parent.add(GetOptionsListBox(), 0, 1);
			// bottom left - filters etc.
			parent.add(GetOptionsListButtons(), 0, 2);
			// middle center - Add/remove items from meal list
			parent.add(GetAddRemoveButtons(), 1, 1);
			// top right - Meal list lable
			parent.add(GetMealLabel(), 2, 0);
			// middle right - scrollable meal list
			parent.add(GetMealList(), 2, 1);
			// bottom right - analyze button
			parent.add(GetMealAnalyzeButton(), 2, 2);
			
			// make main scene
			Scene scene = new Scene(parent);
			
			// add common styles
			scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
			
			// set title and show
			primaryStage.setTitle("Meal Analysis App");
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			primaryStage.close();
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
		btnLoadList.setTooltip(new Tooltip("Load new options list from a file"));
		btnLoadList.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						Stage dialog = popupOutline();
						dialog.show();
					}
				});
		Button btnSaveList = newButton("Save List");
		btnSaveList.setTooltip(new Tooltip("Save current options list to a file in alphabetical order"));
		rtnBox.setMinHeight(topHeight);
		rtnBox.setMinWidth(leftWidth);
		Pane spacer = new Pane();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
		rtnBox.getChildren().addAll(lblOptions, spacer, btnLoadList, btnSaveList);
		return rtnBox;
	}
	
	/**
	 * Middle left - List of food options
	 * Use VBox because it supports width fill for children, which is what we need for a ListView
	 * @return VBox that contains a ListView with all food items
	 */
	private VBox GetOptionsListBox()
	{
		VBox rtnBox = new VBox();
		ListView<FoodItem> foodList = controller.GetFoodOptionsListView();	// unfiltered options - currently a dummy hard-coded list
		foodList.getStyleClass().add("options-list");
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(leftWidth);
		rtnBox.getChildren().add(foodList);
		rtnBox.setFillWidth(true);
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
		btnNewItem.setTooltip(new Tooltip("Add a custom food item to the options list"));
		
		Button btnFilters = newButton("Filters");
		btnFilters.setTooltip(new Tooltip("Apply filters to narrow down the options list"));
		btnFilters.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						Stage dialog = GetFilterPopUp();
						dialog.show();
					}
				});
		Button btnClearFilters = newButton("Clear Filters");
		btnClearFilters.setTooltip(new Tooltip("Remove any filters currently applied to the options list"));
		btnClearFilters.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						controller.ClearRules();
						controller.ApplyFilters();
					}
				});
		Pane spacer = new Pane();
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
		Button btnAddItem = newButton("Add to Meal");
		btnAddItem.setTooltip(new Tooltip("Add selected item to meal list"));
		//btnAddItem.getStyleClass().add("add-button"); // makes it green on hover
		Button btnRemoveItem = newButton("Remove from Meal");
		btnRemoveItem.setTooltip(new Tooltip("Remove selected item from meal list"));
		//btnRemoveItem.getStyleClass().add("remove-button"); // makes it dark red on hover
		btnAddItem.setMaxWidth(Double.MAX_VALUE);
		btnRemoveItem.setMaxWidth(Double.MAX_VALUE);
		rtnBox.getChildren().addAll(btnAddItem, btnRemoveItem);
		return rtnBox;
	}
	
	private HBox GetMealLabel()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(topHeight);
		rtnBox.setMinWidth(rightWidth);
		rtnBox.setAlignment(Pos.CENTER_LEFT);
		Label lblMeal = new Label("Meal");
		lblMeal.setFont(Font.font("Ariel", 18));
		rtnBox.getChildren().add(lblMeal);
		return rtnBox;
	}
	
	private VBox GetMealList()
	{
		VBox rtnBox = new VBox();
		ListView<String> mealList = controller.GetMeal();
		mealList.getStyleClass().add("no-op-list");
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(rightWidth);
		rtnBox.getChildren().add(mealList);
		rtnBox.setFillWidth(true);
		return rtnBox;
	}
	
	private HBox GetMealAnalyzeButton()
	{
		HBox rtnBox = new HBox();
		rtnBox.setMinHeight(bottomHeight);
		rtnBox.setMinWidth(rightWidth);
		Button btnAnalyze = newButton("Analyze");
		btnAnalyze.setTooltip(new Tooltip("Analyze nutrient totals from the current meal"));
		Pane spacer = new Pane();
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
	
	private Stage GetFilterPopUp()
	{
		VBox root = new VBox();
		// Create scene and stage
		Scene filterScene = new Scene(root);
		// add standard styling to make it look consistent
		filterScene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		Stage filters = new Stage();
		// make modal and set app's primary stage as the owner
		filters.initModality(Modality.APPLICATION_MODAL);
		filters.initOwner(this.primaryStage);
		filters.setTitle("Filters");
		filters.setScene(filterScene);
		filters.setOnHidden(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	              controller.ApplyFilters();
	          }
	      });
		
		// attributes label
		HBox firstRow = new HBox();
		Label attLabel = new Label("Attribute filter");
		firstRow.getChildren().add(attLabel);
		root.getChildren().add(firstRow);
		
		// attribute filter row
		HBox attFilterRow = new HBox();
		ComboBox<String> attSelector = new ComboBox<String>();
		// add all attributes from the Nutrient enum: calories, fat, carbohydrate, fiber, protein;
		attSelector.getItems().addAll(controller.GetAllNutrients());
		attSelector.setValue(controller.GetDefaultNutrient()); // first entry in the list
		
		ComboBox<String> comparatorSelector = new ComboBox<String>();
		// add all comparators from the Comparators const list: <=, ==, >=
		comparatorSelector.getItems().addAll(controller.GetAllComparators());
		comparatorSelector.setValue(controller.GetDefaultComparator()); // first entry in the list
		// value to compare to
		TextField val = new TextField();
		// make it numeric only
		val.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	val.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		val.getStyleClass().add("number-field");
		val.setMaxWidth(45);
		val.setMaxHeight(45);
		Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		Button btnAddAttRule = new Button("Add filter");
		btnAddAttRule.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						controller.AddRule(attSelector.getValue(), comparatorSelector.getValue(), val.getText());
					}
				});
		
		attFilterRow.getChildren().addAll(attSelector, comparatorSelector, val, spacer, btnAddAttRule);
		root.getChildren().add(attFilterRow);
		
		// name filter label row
		HBox nameFilterLabelRow = new HBox();
		Label nameContains = new Label("Name contains");
		nameFilterLabelRow.getChildren().add(nameContains);
		root.getChildren().add(nameFilterLabelRow);
		
		// name filter row
		HBox nameFilterRow = new HBox();
		TextField nameField = new TextField();
		nameField.setMaxHeight(45);
		Button btnAddNameFilter = new Button("Add filter");
		btnAddNameFilter.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						controller.AddNameFilter(nameField.getText());
					}
				});
		
		HBox.setHgrow(nameField, Priority.ALWAYS);
		nameFilterRow.getChildren().addAll(nameField, btnAddNameFilter);
		root.getChildren().add(nameFilterRow);
		
		// selected filter summary label
		HBox summaryLabelRow = new HBox();
		Label summaryLabel = new Label("Selected filters");
		summaryLabelRow.getChildren().add(summaryLabel);
		root.getChildren().add(summaryLabelRow);
		
		// selected filters summary list
		VBox summaryListContainer = new VBox();
		ListView<String> summary = controller.GetFiltersListView();
		summary.getStyleClass().add("no-op-list"); // don't allow row selection or highlighting, since we're not allowing manipulation on a row level
		summary.setPrefHeight(100);
		summaryListContainer.getChildren().add(summary);
		summaryListContainer.setFillWidth(true);
		root.getChildren().add(summaryListContainer);
		
		// apply filters and clear filters buttons
		HBox applyAndClearButtons = new HBox();
		Pane bottomSpacer = new Pane();
		HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
		Button btnApply = new Button("Apply filters");
		btnApply.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						// update list associated with options
						filters.close();
					}
				});
		Button btnClear = new Button("Clear filters");
		btnClear.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						controller.ClearRules();
					}
				});
		applyAndClearButtons.getChildren().addAll(bottomSpacer, btnApply, btnClear);
		root.getChildren().add(applyAndClearButtons);
		
		return filters;
	}
	
	/**
	 * Outline for a pop-up. See the btnFilters declaration in GetAddRemoveItems for 
	 * an example of how to use this tag to launch your pop-up
	 * @return
	 */
	private Stage popupOutline()
	{
		VBox root = new VBox();
		
		HBox firstRow = new HBox();
		Label firstRowLabel = new Label("First row");
		TextField firstRowField = new TextField();
		firstRowField.setMaxHeight(45); // this makes it the same height as buttons and labels, so it looks nice
		firstRow.getChildren().addAll(firstRowLabel, firstRowField);
		
		root.getChildren().add(firstRow);
		
		Scene scene = new Scene(root);
		// add standard styling to make it look consistent
		scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		Stage rtnStage = new Stage();
		rtnStage.initModality(Modality.APPLICATION_MODAL);
		rtnStage.initOwner(this.primaryStage);
		rtnStage.setTitle("Pop-up Title");
		rtnStage.setScene(scene);
		return rtnStage;
	}
	
	private class NumberTextField extends TextField
	{

	    @Override
	    public void replaceText(int start, int end, String text)
	    {
	        if (validate(text))
	        {
	            super.replaceText(start, end, text);
	        }
	    }

	    @Override
	    public void replaceSelection(String text)
	    {
	        if (validate(text))
	        {
	            super.replaceSelection(text);
	        }
	    }

	    private boolean validate(String text)
	    {
	        return text.matches("[0-9]*");
	    }
	}
}
