package application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import application.Constants.Nutrient;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * 
 * Resources
 *   Observable list implementation learned from https://rterp.wordpress.com/2015/09/21/binding-a-list-of-strings-to-a-javafx-listview
 *   Cell factory from https://stackoverflow.com/questions/44597921/how-to-specify-what-property-a-javafx-listview-should-display-when-using-a-custo
 *   Styling from combination of various sources including
 *   - https://docs.oracle.com/javafx/2/layout/size_align.htm
 *   - http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
 *   - https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html
 *   - https://stackoverflow.com/questions/43508511/hover-and-pressed-in-javafx
 * @author Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 *
 */
public class ViewController {
	
	// model
	private FoodData sessionData;
	private String nameContains;
	private List<String> attributeRules;
	private HashSet<String> idIndex; // for double-checking that new IDs we generate aren't already taken
	private Random rng; // for generating IDs. Should have just one instance so that we're not ending up with the same ID if we run it in quick succession
	private SimpleListProperty<String> allFiltersProperty; // for binding to Filters ListView
	private SimpleListProperty<FoodItem> foodOptionsProperty; // for binding to Filters ListView
	private SimpleListProperty<FoodItem> mealListProperty; // for binding to Filters ListView
	
	// ListViews
	private ListView<String> allFiltersLV;
	private ListView<FoodItem> foodOptionsLV;
	private ListView<FoodItem> mealLV;
	
	public ViewController()
	{
		// instantiate sessionData object using foodItems.csv in current directory
		// initialize idIndex from our data
		// instantiate other fields with empty objects
		this.sessionData = new FoodData();
		this.sessionData.loadFoodItems(Constants.InitialDataPath);
		this.rng = new Random();
		this.idIndex = new HashSet<String>();
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
		
		// filters list that we'll show in a pop-up
		this.allFiltersLV = new ListView<String>();
		this.allFiltersProperty =  new SimpleListProperty<String>(FXCollections.observableArrayList());
		this.allFiltersLV.itemsProperty().bind(allFiltersProperty);
		this.nameContains = "";
		this.attributeRules = new LinkedList<String>();
		
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
	
	private void resetFoodOptionsList()
	{
		this.foodOptionsProperty.clear();
		for (FoodItem nxt: this.sessionData.getAllFoodItems())
		{
			this.foodOptionsProperty.add(nxt);
			this.idIndex.add(nxt.getID());
		}
	}
	
	private void resetMealList()
	{
		this.mealListProperty.clear();
	}
	
	// File I/O
	public String TrySave(String filename)
	{
		// try to check for path existence
		File file = new File(filename);
    	
    	file.delete();
    	try 
		{
	        if (file.createNewFile())                          
	        {           
				FileWriter fw = new FileWriter(file);
				
				fw.close();
	        }
	        file.delete();
		}
		catch (IOException e) 
    	{ 
			return "Could not edit file";
    	}
    	catch (SecurityException e)
    	{
    		return "You do not have the security to access the file specified";
    	}
    	catch (Exception e)
    	{
    		return "Unexpected exception encountered. Data was not saved.";
    	}
		// then actually save
    	this.sessionData.saveFoodItems(filename);
		if (file.exists())
		{
			return "Data saved successfully";
		}
		else
		{
			return "Unexpected error encountered. Data was not saved.";
		}
	}
	
	public String TryLoad(String filePath)
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
	        	return "Could not find file specified";
	        }
		}
		catch (FileNotFoundException e)
		{
			return "Could not find file specified";
		}
		catch (SecurityException e)
		{
			return "You do not have the security to access the file specified";
		}
		catch (Exception e)
		{
			return "Unexpected exception caught. File was not loaded";
		}
		this.sessionData.loadFoodItems(filePath);
		if (this.sessionData.getAllFoodItems().size() > 0)
		{
			resetFoodOptionsList();
			resetMealList();
			return "Items loaded";
		}
		else
		{
			return "No items loaded. Please check the contents of the file specified";
		}
	}
	
	public String GetNumItemsLabelMsg()
	{
		String rtnStr = this.foodOptionsProperty.size() + " items";
		
		if (this.allFiltersProperty.size()>0)
		{
			rtnStr += " (filtered)";
		}
		
		return rtnStr;
	}
	
	// Meal methods
	public ListView<FoodItem> GetMeal()
	{
		return this.mealLV;
	}
	
	public void AddToMeal(FoodItem toAdd)
	{
		if (!this.mealListProperty.contains(toAdd))
		{
			this.mealListProperty.add(toAdd);
		}
		Collections.sort(this.mealListProperty, (left, right) -> 
			{ 
				return left.getName().toLowerCase().compareTo(right.getName().toLowerCase()); 
			});
	}
	
	public void RemoveFromMeal(FoodItem toRemove)
	{
		this.mealListProperty.remove(toRemove);
	}
	
	public TreeMap<Constants.Nutrient, Double> GetMealAnalysis()
	{
		// add everything up from this.meal and return map
		TreeMap<Constants.Nutrient, Double> rtnMap = new TreeMap<Constants.Nutrient, Double>();
		
		for (Constants.Nutrient nxtNutrient: Constants.Nutrient.values())
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
	public List<String> GetNutrientsAsStringList()
	{
		LinkedList<String> rtnList = new LinkedList<String>();
		for (Constants.Nutrient nxt: Constants.Nutrient.values())
		{
			rtnList.add(nxt.toString());
		}
		return rtnList;
	}
	
	public String GetDefaultNutrient()
	{
		return Constants.Nutrient.values()[2].toString(); // carbohydrate, because it's the longest one and makes the window initialize to the largest size it needs to be
	}
	
	public String[] GetAllComparators()
	{
		return Constants.Comparators;
	}
	
	public String GetDefaultComparator()
	{
		return Constants.Comparators[0];
	}
	
	public ListView<String> GetFiltersListView()
	{
		return allFiltersLV;
	}
	
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
	
	public void AddRule(String attribute, String compareSymbol, String val)
	{
		if (!isNullOrEmpty(attribute) && !isNullOrEmpty(compareSymbol) && !isNullOrEmpty(val))
		{
			String newRule = attribute + " " + compareSymbol + " " + val;
			this.attributeRules.add(newRule);
			this.allFiltersProperty.add(newRule);
		}
	}
	
	public void ClearRules()
	{
		this.allFiltersProperty.clear();
		this.nameContains = "";
		this.attributeRules.clear();
	}
	
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
	public ListView<FoodItem> GetFoodOptionsListView()
	{
		return foodOptionsLV; 
	}
	
	//https://www.programiz.com/java-programming/examples/string-empty-null
	public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
        {
        	return false;
        }
        return true;
    }
	
	// Add food item
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
	
	private void parseNutrient(Nutrient nutrient, String value, FoodItem item) throws NumberFormatException
	{
		double parsed = Double.parseDouble(value);
		item.addNutrient(nutrient.toString(), parsed);
	}
	
	private void SortFoodList()
	{
		Collections.sort(this.sessionData.getAllFoodItems(), (left, right) -> 
		{ 
			return left.getName().toLowerCase().compareTo(right.getName().toLowerCase()); 
		});
	}
	
	public boolean ValidID(String id)
	{
		return !this.idIndex.contains(id);
	}
	
	public String GetUniqueID()
	{
		String ID = nxtRandomID();
		
		while (this.idIndex.contains(ID))
		{
			ID = nxtRandomID();
		}
		
		return ID;
	}
	
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
