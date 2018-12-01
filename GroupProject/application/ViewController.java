package application;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class ViewController {
	
	public enum Compare
	{
		LessThan,
		EqualTo,
		GreaterThan
	}
	
	private enum Nutrient
	{
		calories,
		fat,
		carbohydrate,
		fiber,
		protein;
	}
	
	private FoodData sessionData;
	private List<FoodItem> meal;
	private List<String> rules;
	private String nameContains;
	private HashMap<String, FoodItem> idIndex; // for double-checking that new IDs we generate aren't already taken
	private Random rng; // for generating IDs. Should have just one instance so that we're not ending up with the same ID if we run it in quick succession
	
	public ViewController()
	{
		// instantiate sessionData object using foodItems.csv in current directory
		// initialize idIndex from our data
		// instantiate other fields with empty objects
	}
	
	// File I/O
	public String TrySave(String savePath) throws FileNotFoundException
	{
		// try to check for path existence
		// then actually save
		// then i guess make sure that the file exists? 
		throw new FileNotFoundException("Not yet implemented.");
	}
	
	public String TryLoad(String savePath) throws FileNotFoundException
	{
		// try to check for path existence
		// then actually load
		throw new FileNotFoundException("Not yet implemented.");
	}
	
	// Meal methods
	public List<FoodItem> GetMeal()
	{
		return this.meal;
	}
	
	public void AddToMeal(FoodItem toAdd)
	{
		
	}
	
	public void RemoveFromMeal(FoodItem toRemove)
	{
		
	}
	
	public TreeMap<Nutrient, Double> GetMealAnalysis()
	{
		// add everything up from this.meal and return map
		return new TreeMap<Nutrient, Double>();
	}

	// filter methods
	public void AddNameFilter(String nameContains)
	{
		
	}
	
	public void AddRule(Nutrient attribute, Compare compareSymbol, String val)
	{
		// toString the enums, parse the value to make sure it's a double, and make a rule with it and add to this.rules
	}
	
	public void ClearRules()
	{
		// clear all rules
	}
	
	
	
	// Options methods
	public List<FoodItem> GetFoodOptions(String nameContains, List<String> rules)
	{
		// get filtered by name and get filtered by nutrient, then probably take the smaller one and use use retainAll to find intersection to return
		return new LinkedList<FoodItem>();
	}
	
	
	// Add food item
	public boolean AddFoodItem(String name, String calories, String fat, String carbohydrate, String fiber, String protein)
	{
		// parse all attributes to make sure they're doubles, get a unique ID, and then create FoodItem and add to list
		
		return false;
	}
	
	private String GetUniqueID()
	{
		// somehow generate an ID and make sure it's unique (probably need to double check that it doens't overlap with anything in our current list)
		return "";
	}
}
