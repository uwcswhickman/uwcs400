package application;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Resources
 * Pop-up implementation from here: https://stackoverflow.com/questions/22166610/how-to-create-a-popup-windows-in-javafx
 * numeric text field from here: https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
 * focus and selection clearing in listviews - https://stackoverflow.com/questions/51520325/clear-selection-when-tableview-loses-focus
 * focus and selection - https://stackoverflow.com/questions/17522686/javafx-tabpane-how-to-listen-to-selection-changes
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
	private ListView<FoodItem> optionsList;
	private ListView<FoodItem> meal;
	
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
			// top right - Meal list lable
			parent.add(GetMealLabel(), 2, 0);
			// middle right - scrollable meal list
			parent.add(GetMealList(), 2, 1);
			// bottom right - analyze button
			parent.add(GetMealAnalyzeButton(), 2, 2);
			// middle center - Add/remove items from meal list - must initialize after GetOptionsListBox and GetMealList so that those two ListViews are already initialized
			parent.add(GetAddRemoveButtons(this.optionsList, this.meal), 1, 1);
			
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
		Button btnLoadList = newButton("Load List", "btnLoadList", true);
		btnLoadList.setTooltip(new Tooltip("Load new options list from a file"));
		btnLoadList.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						Stage dialog = GetLoadPopUp();
						dialog.show();
					}
				});
		Button btnSaveList = newButton("Save List", "btnSaveList", true);
		btnSaveList.setTooltip(new Tooltip("Save current options list to a file in alphabetical order"));
		btnSaveList.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						Stage dialog = GetSavePopUp();
						dialog.show();
					}
				});
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
		foodList.getStyleClass().add("selectable-list");
		this.optionsList = foodList;

		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(leftWidth);
		rtnBox.getChildren().addAll(foodList);
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
		
		Button btnNewItem = newButton("Add New Item", "btnNewItem", true);
		btnNewItem.setTooltip(new Tooltip("Add a custom food item to the options list"));
		btnNewItem.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						Stage dialog = GetNewItemPopUp();
						dialog.show();
					}
				});
		
		Button btnFilters = newButton("Filters", "btnFilters", true);
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
		Button btnClearFilters = newButton("Clear Filters", "btnClearFilters", true);
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
	private VBox GetAddRemoveButtons(ListView<FoodItem> optionsList, ListView<FoodItem> meal)
	{
		VBox rtnBox = new VBox();
		rtnBox.setMinHeight(middleHeight);
		rtnBox.setMinWidth(centerWidth);
		rtnBox.setAlignment(Pos.CENTER);
		
		Button btnAddItem = newButton("Add to Meal", "btnAddItem", true);
		btnAddItem.setTooltip(new Tooltip("Add selected item to meal list"));
		btnAddItem.setDisable(true);
		btnAddItem.setMaxWidth(Double.MAX_VALUE);
		
		optionsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FoodItem>() {
		    @Override
		    public void changed(ObservableValue<? extends FoodItem> observable, FoodItem oldVal, FoodItem newVal)
		    {
		        if (optionsList.isFocused() && (newVal != null))
		        {
		        	// if options list has focus, enable the Add button
		        	btnAddItem.setDisable(false);
		        }
		        else if (!btnAddItem.isFocused())
		        {
		        	// if it doesn't have focus and the user clicked anything OTHER than the Add button, then disable the Add button
		        	btnAddItem.setDisable(true);
		        }
		    }
		});
		optionsList.focusedProperty().addListener(new ChangeListener<Boolean>() {
			
			public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) 
			{
			    if (newVal && (optionsList.selectionModelProperty().getValue().getSelectedItem() != null)) {
			    	btnAddItem.setDisable(false);
			    }
			    else if (!btnAddItem.isFocused())
			    {
			    	btnAddItem.setDisable(true);
			    }
			}
		});
		btnAddItem.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						FoodItem selected = optionsList.getSelectionModel().selectedItemProperty().getValue();
						controller.AddToMeal(selected);
						btnAddItem.setDisable(true); // disable the add button after clicked. it will re-enable when user clicks in the Options section again
					}
				});
		
		Button btnRemoveItem = newButton("Remove from Meal", "btnRemoveItem", true);
		btnRemoveItem.setTooltip(new Tooltip("Remove selected item from meal list"));
		btnRemoveItem.setDisable(true);
		btnRemoveItem.setMaxWidth(Double.MAX_VALUE);
		meal.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FoodItem>() {
		    @Override
		    public void changed(ObservableValue<? extends FoodItem> observable, FoodItem oldVal, FoodItem newVal)
		    {
		        if (meal.isFocused() && (newVal != null))
		        {
		        	// if options list has focus, enable the Add button
		        	btnRemoveItem.setDisable(false);
		        }
		        else if (!btnRemoveItem.isFocused())
		        {
		        	// if it doesn't have focus and the user clicked anything OTHER than the Add button, then disable the Add button
		        	btnRemoveItem.setDisable(true);
		        }
		    }
		});
		meal.focusedProperty().addListener(new ChangeListener<Boolean>() {
			
			public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) 
			{
			    if (newVal && (meal.selectionModelProperty().getValue().getSelectedItem() != null)) {
			    	btnRemoveItem.setDisable(false);
			    }
			    else if (!btnRemoveItem.isFocused())
			    {
			    	btnRemoveItem.setDisable(true);
			    }
			}
		});
		btnRemoveItem.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						FoodItem selected = meal.getSelectionModel().selectedItemProperty().getValue();
						controller.RemoveFromMeal(selected);
						btnRemoveItem.setDisable(true); // disable the add button after clicked. it will re-enable when user clicks in the Options section again
					}
				});
		
		
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

		ListView<FoodItem> mealList = controller.GetMeal();
		mealList.getStyleClass().add("selectable-list");
		this.meal = mealList;
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
		Button btnAnalyze = newButton("Analyze", "btnAnalyze", true);
		btnAnalyze.setTooltip(new Tooltip("Analyze nutrient totals from the current meal"));
		btnAnalyze.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						Stage dialog = GetAnalysisPopUp();
						dialog.show();
					}
				});
		Pane spacer = new Pane();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
		rtnBox.getChildren().addAll(spacer, btnAnalyze);
		return rtnBox;
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
		Button btnAddAttRule = newButton("Add filter", "btnAddAttRule", false);
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
		Button btnAddNameFilter = newButton("Add filter", "btnAddNameFilter", false);
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
		Button btnApply = newButton("Apply filters", "btnApply", false);
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
		Button btnClear = newButton("Clear filters", "btnClear", false);
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
	
	private Stage GetLoadPopUp()
	{
		Stage loadList = new Stage();
		
		VBox root = new VBox();
		Scene loadScene = new Scene(root);
		loadScene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		loadList.setScene(loadScene);
		
		// header text
		HBox row1 = new HBox();
		Label fileName = new Label("Load data from file:");
		row1.getChildren().add(fileName);
		root.getChildren().add(row1);
				
		// file entry
		HBox row2 = new HBox();
		TextField fileField = new TextField();
		fileField.setMaxHeight(45);
		Button btnLoadFile = newButton("Load", "btnLoadFile", false);
		btnLoadFile.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						try {
							controller.TryLoad(fileField.getText());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		
		row2.getChildren().addAll(fileField, btnLoadFile);
		root.getChildren().add(row2);
		return loadList;
	}
	
	private Stage GetSavePopUp()
	{
		Stage saveList = new Stage();
		
		VBox root = new VBox();
		Scene saveScene = new Scene(root);
		saveScene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		saveList.setScene(saveScene);
		
		// header text
		HBox row1 = new HBox();
		Label fileName = new Label("Filepath:");
		row1.getChildren().add(fileName);
		root.getChildren().add(row1);
				
		// file entry
		HBox row2 = new HBox();
		TextField fileField = new TextField();
		fileField.setMaxHeight(45);
		Button btnSaveFile = newButton("Save", "btnSaveFile", false);
		btnSaveFile.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						try {
							controller.TrySave(fileField.getText());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		
		row2.getChildren().addAll(fileField, btnSaveFile);
		root.getChildren().add(row2);
		return saveList;
	}
	
	private Stage GetNewItemPopUp()
	{
		Stage newItem = new Stage();
		
		VBox root = new VBox();
		Scene newItemScene = new Scene(root);
		newItemScene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		newItem.setScene(newItemScene);
		
		// header text
		HBox row1 = new HBox();
		Label fileName = new Label("Add new item:");
		row1.getChildren().add(fileName);
		root.getChildren().add(row1);
				
		// item name field
		HBox row2 = new HBox();
		Label itemName = new Label("Name: ");
		TextField itemField = new TextField();
		itemField.setMaxHeight(45);
		row2.getChildren().addAll(itemName,itemField);
		root.getChildren().add(row2);
		
		// item calories, fat, carbs
		HBox row3 = new HBox();
		Label itemCals = new Label("Calories: ");
		TextField itemCalsField = new TextField();
		itemCalsField.setMaxHeight(45);
		Label itemFat = new Label("Fat: ");
		TextField itemFatField = new TextField();
		itemFatField.setMaxHeight(45);
		Label itemCarbs = new Label("Carbs: ");
		TextField itemCarbsField = new TextField();
		itemCarbsField.setMaxHeight(45);
		row3.getChildren().addAll(itemCals,itemCalsField,itemFat,itemFatField,itemCarbs,itemCarbsField);
		root.getChildren().add(row3);
		
		// item protein, fiber, Add button
		HBox row4 = new HBox();
		Label itemProtein = new Label("Protein: ");
		TextField itemProteinField = new TextField();
		itemProteinField.setMaxHeight(45);
		Label itemFiber = new Label("Fiber: ");
		TextField itemFiberField = new TextField();
		itemFiberField.setMaxHeight(45);
		Button btnAdd = newButton("Add", "btnAdd", false);
		btnAdd.setOnAction(
				new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						// TODO: add event
					}
				});
		
		row4.getChildren().addAll(itemProtein,itemProteinField,itemFiber,itemFiberField,btnAdd);
		root.getChildren().add(row4);
		
		return newItem;
	}
	
	private Stage GetAnalysisPopUp()
	{
		Stage analysis = new Stage();
		
		VBox root = new VBox();
		Scene analysisScene = new Scene(root);
		analysisScene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		analysis.setScene(analysisScene);
		
		// header text
		HBox row1 = new HBox();
		Label fileName = new Label("Meal Analysis");
		row1.getChildren().add(fileName);
		root.getChildren().add(row1);
				
		// nutrient fields
		HBox row2 = new HBox();
		Label cals = new Label("Calories: ");
		TextField calsField = new TextField();
		calsField.setMaxHeight(45);
		row2.getChildren().addAll(cals,calsField);
		root.getChildren().add(row2);
		
		HBox row3 = new HBox();
		Label fat = new Label("Fat: ");
		TextField fatField = new TextField();
		calsField.setMaxHeight(45);
		row3.getChildren().addAll(fat,fatField);
		root.getChildren().add(row3);
		
		HBox row4 = new HBox();
		Label carbs = new Label("Carbs: ");
		TextField carbsField = new TextField();
		carbsField.setMaxHeight(45);
		row4.getChildren().addAll(carbs,carbsField);
		root.getChildren().add(row4);
		
		HBox row5 = new HBox();
		Label protein = new Label("Protein: ");
		TextField proteinField = new TextField();
		proteinField.setMaxHeight(45);
		row5.getChildren().addAll(protein,proteinField);
		root.getChildren().add(row5);
		
		HBox row6 = new HBox();
		Label fiber = new Label("Fiber: ");
		TextField fiberField = new TextField();
		fiberField.setMaxHeight(45);
		row6.getChildren().addAll(fiber,fiberField);
		root.getChildren().add(row6);
		
		return analysis;
		
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
	
	private Button newButton(String btnCaption, String ID, boolean enforceMinWidth)
	{
		Button rtnButton = new Button(btnCaption);
		rtnButton.setId(ID);
		if (enforceMinWidth)
		{
			rtnButton.setMinWidth(minButtonSize);
		}
		
		return rtnButton;
	}
}