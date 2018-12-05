package application;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

<<<<<<< HEAD
import javafx.scene.control.ListView;

/**
 * 
 * Resources
 *   Observable list implementation learned from https://rterp.wordpress.com/2015/09/21/binding-a-list-of-strings-to-a-javafx-listview
 *   Styling from combination of various sources including
 *   - https://docs.oracle.com/javafx/2/layout/size_align.htm
 *   - http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
 *   - https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html
 *   - https://stackoverflow.com/questions/43508511/hover-and-pressed-in-javafx
 *   - 
 * @author whickman
=======
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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
>>>>>>> branch 'master' of https://github.com/uwcswhickman/uwcs400
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
		this.rng = new Random();
		this.idIndex = new HashSet<String>();
		// food options list initial setup
		this.foodOptionsLV = new ListView<FoodItem>();
		this.foodOptionsProperty = new SimpleListProperty<FoodItem>(FXCollections.observableArrayList());
		this.foodOptionsLV.itemsProperty().bind(foodOptionsProperty);
		for (FoodItem nxt: this.sessionData.getAllFoodItems())
		{
			this.foodOptionsProperty.add(nxt);
			this.idIndex.add(nxt.getID());
		}
		

		// cells will show only the item's name
		this.foodOptionsLV.setCellFactory(lv -> new ListCell<FoodItem>() {
		    @Override
		    protected void updateItem(FoodItem item, boolean empty) {
		        super.updateItem(item, empty);
		        setText(item == null ? null : item.getName() );
		    }
		});
		
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
		
		System.out.println(GetUniqueID());
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
<<<<<<< HEAD
	public ListView<String> GetMeal()
=======
	public ListView<FoodItem> GetMeal()
>>>>>>> branch 'master' of https://github.com/uwcswhickman/uwcs400
	{
<<<<<<< HEAD
		ListView<String> rtnLV = new ListView<String>();
		rtnLV.getItems().addAll("Giant_CheddarCheeseTwiceBakedPotatoes", "Spartan_ShreddedMozzarellaCheese", "Detour_EnergyBarChocolatePeanutButter");
		return rtnLV;
=======
		return this.mealLV;
>>>>>>> branch 'master' of https://github.com/uwcswhickman/uwcs400
	}
	
	public void AddToMeal(FoodItem toAdd)
	{
		if (!this.mealListProperty.contains(toAdd))
		{
			this.mealListProperty.add(toAdd);
		}
	}
	
	public void RemoveFromMeal(FoodItem toRemove)
	{
		this.mealListProperty.remove(toRemove);
	}
	
	public TreeMap<Constants.Nutrient, Double> GetMealAnalysis()
	{
		// add everything up from this.meal and return map
		return new TreeMap<Constants.Nutrient, Double>();
	}

	// filter methods
	public List<String> GetAllNutrients()
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
	
<<<<<<< HEAD
=======
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
	
>>>>>>> branch 'master' of https://github.com/uwcswhickman/uwcs400
	// Options methods
<<<<<<< HEAD
	public ListView<String> GetFoodOptions()
	{
		ListView<String> rtnLV = new ListView<String>();
		rtnLV.getItems().addAll(this.dummyOptions());
		return rtnLV;
//		GetFoodOptions("", null); // for real implementation, we should run regular method with no inputs, which will give the full list 
	}
	
	public ListView<String> GetFoodOptions(String nameContains, List<String> rules)
=======
	public ListView<FoodItem> GetFoodOptionsListView()
>>>>>>> branch 'master' of https://github.com/uwcswhickman/uwcs400
	{
<<<<<<< HEAD
		// get filtered by name and get filtered by nutrient, then probably take the smaller one and use use retainAll to find intersection to return
		return new ListView<String>();
=======
		return foodOptionsLV; 
>>>>>>> branch 'master' of https://github.com/uwcswhickman/uwcs400
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
	public boolean AddFoodItem(String name, String calories, String fat, String carbohydrate, String fiber, String protein)
	{
		// parse all attributes to make sure they're doubles, get a unique ID, and then create FoodItem and add to list
		
		return false;
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
		char[] ID = new char[Constants.IDLENGTH];
		for (int i = 0; i < Constants.IDLENGTH; i++)
		{
			ID[i] = Constants.HexDigits[rng.nextInt(Constants.HexDigits.length)];
		}
		return new String(ID);
	}
}