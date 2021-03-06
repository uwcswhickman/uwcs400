/**
 * Filename:   FoodData.java
 * Project:    Group Project
 * Authors:    sapan (sapan@cs.wisc.edu), Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 *
 * Semester:   Fall 2018
 * Course:     CS400 - Lecutre 46373
 * 
 * Due Date:   12/16/18
 * Version:    1.0
 * 
 * Credits:    N/A
 * 
 * Bugs:       No known bugs
 */

package application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems. 
 * Implements FoodDataADT<FoodItem> 
 * 
 * @author sapan (sapan@cs.wisc.edu), Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;
    
    // formatter for nutrient values - strips trailing 0s
    private DecimalFormat numFormatter = new DecimalFormat("0.#");
    
    // hashed set of all of the food items for quick lookup while applying filters
    private HashSet<FoodItem> foodItemLookup;
    
    // Map of nutrients and their corresponding indices
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    /**
     * Public constructor
     */
    public FoodData() 
    {
    	this.foodItemList = new LinkedList<FoodItem>();
        this.foodItemLookup = new HashSet<FoodItem>();
        this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
        for (Constants.Nutrient nxt: Constants.Nutrient.values())
        {
        	this.indexes.put(nxt.toString(), new BPTree<Double, FoodItem>(25));
        }
    }
    
    /**
     * Loads the data in the .csv file
     * 
     * @param filePath path of the food item data file 
     *        (e.g. folder1/subfolder1/.../foodItems.csv) 
     */
    @Override
    public void loadFoodItems(String filePath) {
    	// try to load data from file
    	List<String> rawData = null;
    	try
    	{
    		rawData = loadFromFile(filePath);
    	}
    	catch (FileNotFoundException e)	{ }
    	
    	if (rawData != null)
    	{
    		clearFoodItems();
    		// if data exists, parse and add it to our session's food list
        	parseData(rawData);
    	}
    }
    /**
     *  Reset food item list and hashed set
     */
    private void clearFoodItems()
    {
    	this.foodItemList = new LinkedList<FoodItem>();
        this.foodItemLookup = new HashSet<FoodItem>();
    }
    
    /**
     * Load all lines of data from a file
     * 
     * @param filePath
     * @throws SecurityException - if a security manager exists and its SecurityManager.checkRead(java.lang.String) method denies read access to the file or directory
     * @throws FileNotFoundException - if filePath is not found
     * @return list of data rows from file
     */
    private List<String> loadFromFile(String filePath) throws FileNotFoundException
    {
    	List<String> rtnList = new LinkedList<String>();
    	
    	File file = new File( filePath );

        if (file.exists())                          
        {
			Scanner inFile = new Scanner(file);
			
			while (inFile.hasNextLine())
            {
                rtnList.add(inFile.nextLine());
            }
			inFile.close();
        }
        return rtnList;
    }
    
    /**
     * Parse raw data in the format specified in loadFoodItems, and if data format matches, adds new FoodItem to the session's list
     * @param rawData - list of data lines from file
     */
    private void parseData(List<String> rawData)
    {
    	for (String nxtLine: rawData)
    	{
    		String[] pieces = nxtLine.split(",");
    		boolean valid = true;
    		if (pieces.length >= 12)
    		{
    			FoodItem nxtItm = new FoodItem(pieces[0], pieces[1]);
    			for (int i = 2; i < 12; i++)
    			{
    				try
    				{
    					String nutrient = pieces[i].toLowerCase();
    					Constants.Nutrient.valueOf(nutrient);  // throws IllegalArgumentException if not in the list
        				double value = Double.parseDouble(pieces[++i]);  // throws NumberFormatException if not parse-able. 
        				nxtItm.addNutrient(nutrient, value);
    				}
    				catch (Exception e)
    				{
    					valid = false;
    					break; // break out of for loop for this line, since this line isn't valid
    				}
    			}
    			if (valid)
    			{
        			this.addFoodItem(nxtItm);
    			}
    		}
    	}
    	Collections.sort(this.foodItemList, (left, right) -> 
    		{ 
    			return left.getName().toLowerCase().compareTo(right.getName().toLowerCase()); 
    		});
    }
    
    /**
     * Gets all the food items that have name containing the substring - case-insensitive
     * 
     * @param substring - substring to be searched
     * @return list of filtered food items; if no food item matched, return empty list
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
    	
    	substring = substring.toLowerCase();
    	
    	LinkedList<FoodItem> rtnList = new LinkedList<FoodItem>();
    	
    	// foodItemList is always sorted by name, so we don't need to sort again
    	for (FoodItem nxt: this.foodItemList)
		{
			if (nxt.getName().toLowerCase().contains(substring))
			{
				rtnList.add(nxt);
			}
		}
        return rtnList;
    }

    /**
     * Gets all the food items that fulfill ALL the provided rules
     * 
     * @param rules - list of rules
     * @return list of filtered food items; if no food item matched, return empty list
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
    	HashSet<FoodItem> current = this.foodItemLookup;
    	HashSet<FoodItem> filtered = null;
        for (String rule: rules)
        {
        	String[] pieces = rule.split(" ");
        	// use rule to filter current list of food items
        	filtered = filterByOneRule(current, pieces[0], pieces[1], pieces[2]);
        	current = filtered;
        }
        
        LinkedList<FoodItem> rtnList = new LinkedList<FoodItem>();
        for(FoodItem nxt: filtered)
        {
        	rtnList.add(nxt);
        }
        // return filtered list of food items
        return rtnList;
    }
    /**
     * Filter a hashed set of food items based on a rule
     * 
     * @param startingList - initial list of food items to be filtered
     * @param nutrient - string specifying nutrient to filter by
     * @param comparator - string specifying comparator
     * @param amt - string specifying amount 
     * @return filtered list of food items
     * 
     */
    private HashSet<FoodItem> filterByOneRule(HashSet<FoodItem> startingList, String nutrient, String comparator, String amt)
    {
    	HashSet<FoodItem> rtnList = new HashSet<FoodItem>();
    	BPTree<Double, FoodItem> idx = this.indexes.get(nutrient.toString());
    	Double amtAsDouble = Double.parseDouble(amt);
    	List<FoodItem> filtered = idx.rangeSearch(amtAsDouble, comparator);
    	for (FoodItem nxt: filtered)
    	{
    		if (startingList.contains(nxt))
    		{
    			rtnList.add(nxt);
    		}
    	}
    	return rtnList;
    }

    /**
     * Adds a food item to the loaded data.
     * @param foodItem - the food item instance to be added
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        this.foodItemList.add(foodItem);
        this.foodItemLookup.add(foodItem);
        // add to the nutrient index
        for (String nutrient: foodItem.getNutrients().keySet())
        {
        	double amt = foodItem.getNutrientValue(nutrient);
        	BPTree<Double, FoodItem> idx = this.indexes.get(nutrient);
        	idx.insert(amt, foodItem);
        }
    }

    /**
     * Gets the list of all food items.
     * @return list of FoodItem
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
    	return this.foodItemList;
    }
    
    /**
     * Save the list of food items in ascending order by name
     * 
     * @param filename name of the file where the data needs to be saved 
     */
    public void saveFoodItems(String filename) {
    	Collection<String> formattedData = formatData(this.foodItemList);
    	
    	File file = new File( filename );
    	
    	file.delete();
    	try 
		{
	        if (file.createNewFile())                          
	        {           
				FileWriter fw = new FileWriter(file);
				
				for (String nxt: formattedData)
				{
					fw.write(nxt);
					fw.write("\r\n");
				}
				fw.close();
	        }
		} 
		catch (IOException e) { }
    }
    /**
    * format food data to be saved to file
    * 
    * @param list - list of food data to be formatted
    * @return list of formatted food data
    */
    private Collection<String> formatData(List<FoodItem> list)
    {
    	List<String> formattedData = new LinkedList<String>();
    	for (FoodItem nxt: list)
    	{
    		formattedData.add(serializeFoodItem(nxt));
    	}
    	
    	return formattedData;
    }
    /**
     * translate data about a single food item to a formatted string
     * 
     * @param item - the food item to be formatted
     * @return string with formatted food item data
     */
    private String serializeFoodItem(FoodItem item)
    {
    	StringBuilder sb = new StringBuilder();
		sb.append(item.getID());
		sb.append(",");
		sb.append(item.getName());
		
		HashMap<String, Double> nutrients = item.getNutrients();
		Constants.Nutrient[] nutrientList = Constants.Nutrient.values();
		for (int i = 0; i < nutrientList.length; i++)
		{
			sb.append(",");
			// label
			sb.append(nutrientList[i]);
			sb.append(",");
			// amount - use formatter to drop trailing zeros
			sb.append(this.numFormatter.format(nutrients.get(nutrientList[i].toString())));
		}
		return sb.toString();
    }
}
