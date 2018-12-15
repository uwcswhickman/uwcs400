/**
 * Filename:   ViewController.java
 * Project:    Group Project
 * Authors:    Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 *
 * Semester:   Fall 2018
 * Course:     CS400 - Lecutre 46373
 * 
 * Due Date:   12/16/18
 * Version:    1.0
 * 
 * Credits:    N/A
 *   Observable list implementation learned from https://rterp.wordpress.com/2015/09/21/binding-a-list-of-strings-to-a-javafx-listview
 *   Cell factory from https://stackoverflow.com/questions/44597921/how-to-specify-what-property-a-javafx-listview-should-display-when-using-a-custo
 *   String is null or empty: https://www.programiz.com/java-programming/examples/string-empty-null
 *   Styling from combination of various sources including
 *   - https://docs.oracle.com/javafx/2/layout/size_align.htm
 *   - http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
 *   - https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html
 *   - https://stackoverflow.com/questions/43508511/hover-and-pressed-in-javafx
 * Bugs:       No known bugs
 */
package application;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import application.Constants.IOMessage;
import application.Constants.Nutrient;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * Controller class used by the Main view class to mostly separate UI from actions
 * Creates an instance of the foodData class and wraps some of the data structures 
 * in UI-friendly formats like observable lists. Also does file I/O checking and reporting
 * that were not possible with the foodData class method signatures 
 */
public class ViewController {
	
	// model
	private FoodData sessionData; // FoodData instance for loading, organizing, and saving data
	private String nameContains;  // name filter - instance variable for easy application of rule filters + name filters that are persistent
	private List<String> attributeRules; // attribute rules list for use with filtering. instance variable so that it's persistent and can be appended to by the user in separate actions
	private HashSet<String> idIndex; // for double-checking that new IDs we generate aren't already taken
	private Random rng; // for generating IDs. Should have just one instance so that we're not ending up with the same ID if we run it in quick succession
	private SimpleListProperty<String> allFiltersProperty; // for binding to Filters ListView
	private SimpleListProperty<FoodItem> foodOptionsProperty; // for binding to Filters ListView
	private SimpleListProperty<FoodItem> mealListProperty; // for binding to Filters ListView
	
	// ListViews
	private ListView<String> allFiltersLV; // filters list view instance for use in the UI
	private ListView<FoodItem> foodOptionsLV; // food options list view instance for use in the UI
	private ListView<FoodItem> mealLV; // meal list view instance for use in the UI
	
	public ViewController()
	{
		this.sessionData = new FoodData();
		this.rng = new Random();
		this.idIndex = new HashSet<String>();
		
		// filters list that we'll show in a pop-up
		this.allFiltersLV = new ListView<String>();
		this.allFiltersProperty =  new SimpleListProperty<String>(FXCollections.observableArrayList());
		this.allFiltersLV.itemsProperty().bind(allFiltersProperty);
		this.nameContains = "";
		this.attributeRules = new LinkedList<String>();
		
		// food options list initial setup
		this.foodOptionsLV = new ListView<FoodItem>();
		this.foodOptionsProperty = new SimpleListProperty<FoodItem>(FXCollections.observableArrayList());
		this.foodOptionsLV.itemsProperty().bind(foodOptionsProperty);
		
		// cells will show only the item's name
		this.foodOptionsLV.setCellFactory(lv -> new ListCell<FoodItem>() {
		    @Override
		    protected void updateItem(FoodItem item, boolean empty) {
		        super.updateItem(item, empty);
		        setText(item == null ? null : item.getName() );
		    }
		});
		
		resetFoodOptionsList();
		
		// meal initial setup
		this.mealLV = new ListView<FoodItem>();
		this.mealListProperty = new SimpleListProperty<FoodItem>(FXCollections.observableArrayList());
		this.mealLV.itemsProperty().bind(mealListProperty);
		
		// cells will show only the item's name
		this.mealLV.setCellFactory(lv -> new ListCell<FoodItem>() {
		    @Override
		    protected void updateItem(FoodItem item, boolean empty) {
		        super.updateItem(item, empty);
		        setText(item == null ? null : item.getName() );
		    }
		});
	}
	
	/**
	 * Clear food options observable list and reload from our fooddata instance
	 */
	private void resetFoodOptionsList()
	{
		this.foodOptionsProperty.clear();
		for (FoodItem nxt: this.sessionData.getAllFoodItems())
		{
			this.foodOptionsProperty.add(nxt);
			this.idIndex.add(nxt.getID());
		}
	}
	
	/**
	 * Clear the meal observable list
	 */
	private void resetMealList()
	{
		this.mealListProperty.clear();
	}
	
	/** 
	 * Try to save to the specified file. Return IOMessage indicating result
	 * @param filename - file to save to
	 * @return IOMessage indicating result
	 */
	public IOMessage TrySave(String filename)
	{
		// we're going to do the basic checks that the FoodData save method does, 
		// but then we can send an error to the user, if we run into trouble
		File file = new File(filename);
    	
    	file.delete();
    	try 
		{
	        if (file.createNewFile())                          
	        {
				FileWriter fw = new FileWriter(file);
				
				fw.close();
				file.delete();
	        }
		}
		catch (IOException e) 
    	{ 
			return IOMessage.IOEx;
    	}
    	catch (SecurityException e)
    	{
    		return IOMessage.SecurityEx;
    	}
    	catch (Exception e)
    	{
    		return IOMessage.UnexpectedEx;
    	}
		// then actually save, now that we're confident it's possible
    	this.sessionData.saveFoodItems(filename);
    	// double-check that the file exists, just to be sure
		if (file.exists())
		{
			return IOMessage.Success;
		}
		else
		{
			return IOMessage.UnexpectedEx;
		}
	}
	
	public IOMessage TryLoad(String filePath)
	{
		// we're going to do the basic checks that the FoodData load method does, 
		// but then we can send an error to the user, if we run into trouble
		try {
			File file = new File( filePath );

	        if (file.exists())                          
	        {
				Scanner scanner = new Scanner(file);
				
	            scanner.close();
	        }
	        else
	        {
	        	return IOMessage.FileNotFoundEx;
	        }
		}
		catch (SecurityException e)
		{
			return IOMessage.SecurityEx;
		}
		catch (Exception e)
		{
			return IOMessage.UnexpectedEx;
		}
		
		// now actually load, since we're confident it will work
		this.sessionData.loadFoodItems(filePath);
		// if we successfully loaded data, then reset our observable list and meal and return success
		if (this.sessionData.getAllFoodItems().size() > 0)
		{
			resetFoodOptionsList();
			resetMealList();
			return IOMessage.Success;
		}
		else
		{
			return IOMessage.UnexpectedEx;
		}
	}
	/**
	 * Turn IOMessage enum value into a message for the user
	 * @param msg - IOMessage value to interpret
	 * @return user-friendly message representation of the input value 
	 */
	public String GetLongIOMessage(IOMessage msg)
	{
		switch(msg)
		{
			case Success:
				return "Success";
			case SecurityEx:
				return "You do not have access to this location";
			case IOEx:
				return "A file I/O exception occurred";
			case FileNotFoundEx:
				return "Could not find the file specified";
			default:
				return "Unexpected error occurred";
			
		}
	}
	
	/**
	 * Get the value to set in the number of items label in the UI
	 * @return "### items", if no filters active. "### items (filtered)", if filters are applied. 
	 */
	public String GetNumItemsLabelMsg()
	{
		String rtnStr = this.foodOptionsProperty.size() + " items";
		
		if (this.allFiltersProperty.size()>0)
		{
			rtnStr += " (filtered)";
		}
		
		return rtnStr;
	}
	
	/**
	 * Get the size of our session's food options list
	 * @return size of current data set
	 */
	public int GetNumItemsLoaded()
	{
		return this.sessionData.getAllFoodItems().size();
	}
	
	// Meal methods
	
	/**
	 * Get the ListView instance for our meal
	 * @return
	 */
	public ListView<FoodItem> GetMeal()
	{
		return this.mealLV;
	}
	
	/**
	 * Add a food item to the meal and then resort the meal list
	 * @param toAdd - FoodItem to add
	 */
	public void AddToMeal(FoodItem toAdd)
	{
		this.mealListProperty.add(toAdd);
		Collections.sort(this.mealListProperty, (left, right) -> 
			{ 
				return left.getName().toLowerCase().compareTo(right.getName().toLowerCase()); 
			});
	}
	
	/**
	 * Remove a FoodItem from our meal list
	 * @param toRemove - item to remove
	 */
	public void RemoveFromMeal(FoodItem toRemove)
	{
		this.mealListProperty.remove(toRemove);
	}
	
	/**
	 * Sum up the nutrients from all items in our meal list and return
	 * as a map with Nutrients as keys and totals as values
	 * @return map with Nutrients as keys and totals as values
	 */
	public TreeMap<Nutrient, Double> GetMealAnalysis()
	{
		// add everything up from this.meal and return map
		TreeMap<Nutrient, Double> rtnMap = new TreeMap<Nutrient, Double>();
		
		for (Nutrient nxtNutrient: Nutrient.values())
		{
			double sum = 0;
			for (FoodItem nxtItm: this.mealListProperty)
			{
				sum += nxtItm.getNutrientValue(nxtNutrient.name());
			}
			rtnMap.put(nxtNutrient, sum);
		}
		
		return rtnMap;
	}

	// filter methods
	
	/**
	 * Get all possible nutrients as a list of strings
	 * Used for things like a Combo box
	 * @return list of nutrients as strings
	 */
	public List<String> GetNutrientsAsStringList()
	{
		LinkedList<String> rtnList = new LinkedList<String>();
		for (Nutrient nxt: Nutrient.values())
		{
			rtnList.add(nxt.toString());
		}
		return rtnList;
	}
	
	/**
	 * Get the default nutrient to select, for instance
	 * in a combo box. Choose the longest (carbohydrate) so that
	 * window initializes with width that will handle the largest
	 * possible combination
	 * @return carbohydrate as a string
	 */
	public String GetDefaultNutrient()
	{
		return Nutrient.values()[2].toString(); // carbohydrate, because it's the longest one and makes the window initialize to the largest size it needs to be
	}
	
	/**
	 * Get all possible comparators as an array of strings
	 * @return array with "<=", "==", ">="
	 */
	public String[] GetAllComparators()
	{
		return Constants.Comparators;
	}
	
	/**
	 * Get default comparator "<=" for defaulting
	 * into a combo box
	 * @return "<="
	 */
	public String GetDefaultComparator()
	{
		return Constants.Comparators[0];
	}
	
	/**
	 * Get the filters ListView for use in the UI
	 * Will have all currently applied filters listed
	 * @return filters ListView
	 */
	public ListView<String> GetFiltersListView()
	{
		return allFiltersLV;
	}
	
	/**
	 * Add a name filter or replace the existing one, if one was already present
	 * @param nameContains - string to use for filtering
	 */
	public void AddNameFilter(String nameContains)
	{
		if (!isNullOrEmpty(nameContains))
		{
			if (!isNullOrEmpty(this.nameContains))
			{
				this.allFiltersProperty.remove("Name contains " + this.nameContains);
			}
			this.allFiltersProperty.add("Name contains " + nameContains);
			this.nameContains = nameContains;
		}
	}
	
	/**
	 * Add a nutrient rule to our list
	 * @param attribute - attribute to filter on
	 * @param compareSymbol - <=, ==, or >=
	 * @param val - value to compare to
	 */
	public void AddRule(String attribute, String compareSymbol, String val)
	{
		if (!isNullOrEmpty(attribute) && !isNullOrEmpty(compareSymbol) && !isNullOrEmpty(val))
		{
			String newRule = attribute + " " + compareSymbol + " " + val;
			this.attributeRules.add(newRule);
			this.allFiltersProperty.add(newRule);
		}
	}
	
	/**
	 * Clear all rules/filters
	 */
	public void ClearRules()
	{
		this.allFiltersProperty.clear();
		this.nameContains = "";
		this.attributeRules.clear();
	}
	
	/**
	 * Apply filters to our foodoptions ListView
	 */
	public void ApplyFilters()
	{
		this.foodOptionsProperty.clear();
		
		List<FoodItem> filtered = null;
		
		if (!isNullOrEmpty(this.nameContains))
		{
			filtered = this.sessionData.filterByName(this.nameContains);
		}
		if (!this.attributeRules.isEmpty())
		{
			if (filtered == null)
			{
				filtered = this.sessionData.filterByNutrients(this.attributeRules);
			}
			else
			{
				List<FoodItem> byRules = this.sessionData.filterByNutrients(this.attributeRules);
				if (byRules != null)
				{
					filtered.retainAll(byRules);
				}
			}
		}
		if (filtered == null)
		{
			this.foodOptionsProperty.addAll(this.sessionData.getAllFoodItems());
		}
		else
		{
			TreeMap<String, FoodItem> sortedByName = new TreeMap<String, FoodItem>();
			for (FoodItem nxt: filtered)
			{
				sortedByName.put(nxt.getName(), nxt);
			}
			this.foodOptionsProperty.addAll(sortedByName.values());
		}
	}
	
	// Options methods
	
	/**
	 * Get the food options ListView instance for use in the UI
	 * @return
	 */
	public ListView<FoodItem> GetFoodOptionsListView()
	{
		return foodOptionsLV; 
	}
	
	/**
	 * Quick check to make sure string has something in it
	 * https://www.programiz.com/java-programming/examples/string-empty-null
	 * @param str - string to check
	 * @return true if null or empty, false otherwise
	 */
	public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
        {
        	return false;
        }
        return true;
    }
	
	/**
	 * Add a food item to our session data, sort the list again, and then re-apply filters, if any are active
	 * @param ID - ID of the new item
	 * @param name - name of the item
	 * @param calories - number of calories
	 * @param fat - amount of fat
	 * @param carbohydrate - amount of carbs
	 * @param fiber - amount of fiber
	 * @param protein - amount of protein
	 * @return - true if successfully added. false if any parameter is empty or if any of the amount parameters can't be parsed as a Double
	 */
	public boolean AddFoodItem(String ID, String name, String calories, String fat, String carbohydrate, String fiber, String protein)
	{
		if (!isNullOrEmpty(ID) && !isNullOrEmpty(name))
		{
			try
			{
				FoodItem newItem = new FoodItem(ID, name);
				parseNutrient(Nutrient.calories, calories, newItem);
				parseNutrient(Nutrient.fat, fat, newItem);
				parseNutrient(Nutrient.carbohydrate, carbohydrate, newItem);
				parseNutrient(Nutrient.fiber, fiber, newItem);
				parseNutrient(Nutrient.protein, protein, newItem);
				this.sessionData.addFoodItem(newItem);
				this.idIndex.add(ID);
				SortFoodList();
				ApplyFilters();
			}
			catch (Exception e)
			{
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Parse a nutrient value to make sure it represents a double and add it to the FoodItem
	 * @param nutrient - nutrient to add
	 * @param value - value of the nutrient
	 * @param item - FoodItem instance to add this nutrient/val to
	 * @throws NumberFormatException - if value can't be parsed as a double
	 */
	private void parseNutrient(Nutrient nutrient, String value, FoodItem item) throws NumberFormatException
	{
		double parsed = Double.parseDouble(value);
		item.addNutrient(nutrient.toString(), parsed);
	}
	
	/**
	 * Sort food options list from our FoodData instance
	 */
	private void SortFoodList()
	{
		Collections.sort(this.sessionData.getAllFoodItems(), (left, right) -> 
		{
			return left.getName().toLowerCase().compareTo(right.getName().toLowerCase()); 
		});
	}
	
	/**
	 * Checks if passed in ID is already in our working set
	 * @param id - ID to check
	 * @return true, if id isn't already in our data set; false otherwise
	 */
	public boolean ValidID(String id)
	{
		return !this.idIndex.contains(id);
	}
	
	/**
	 * Get a new 24-digit hex ID that's not already in our session's FoodData instance
	 * @return
	 */
	public String GetUniqueID()
	{
		String ID = nxtRandomID();
		
		while (this.idIndex.contains(ID))
		{
			ID = nxtRandomID();
		}
		
		return ID;
	}
	
	/**
	 * Generate a random 24-digit hex ID
	 * @return random 24-digit hex ID
	 */
	private String nxtRandomID()
	{
		char[] ID = new char[Constants.IdLength];
		for (int i = 0; i < Constants.IdLength; i++)
		{
			ID[i] = Constants.HexDigits[rng.nextInt(Constants.HexDigits.length)];
		}
		return new String(ID);
	}
}
