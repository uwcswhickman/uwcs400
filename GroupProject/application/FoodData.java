package application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu), Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;
    
    // hashed set of all of the food items for quick lookup while applying filters
    private HashSet<FoodItem> foodItemLookup;
    
    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    private int toLoad;
    
    // ToDo: Remove before submitting!
    public void SetNumToLoad(int toLoad)
    {
    	this.toLoad = toLoad;
    }
    
    public String printNutrientIdx(String nutrient)
    {
    	return this.indexes.get(nutrient).toString();
    }
        
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
        	this.indexes.put(nxt.toString(), new BPTree<Double, FoodItem>(3));
        }
        //this.loadFoodItems(Constants.InitialDataPath);
    }
    
    /**
     * ToDo: Delete before submitting
     * @param branchingFactor
     */
    public FoodData(int branchingFactor) 
    {
    	this.foodItemList = new LinkedList<FoodItem>();
        this.foodItemLookup = new HashSet<FoodItem>();
        this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
        for (Constants.Nutrient nxt: Constants.Nutrient.values())
        {
        	this.indexes.put(nxt.toString(), new BPTree<Double, FoodItem>(branchingFactor));
        }
        //this.loadFoodItems(Constants.InitialDataPath);
    }
    
    
    /**
     * Loads the data in the .csv file
     * 
     * file format:
     * <id1>,<name>,<nutrient1>,<value1>,<nutrient2>,<value2>,...
     * <id2>,<name>,<nutrient1>,<value1>,<nutrient2>,<value2>,...
     * 
     * Example:
     * 556540ff5d613c9d5f5935a9,Stewarts_PremiumDarkChocolatewithMintCookieCrunch,calories,280,fat,18,carbohydrate,34,fiber,3,protein,3
     * 
     * Note:
     *     1. All the rows are in valid format.
     *  2. All IDs are unique.
     *  3. Names can be duplicate.
     *  4. All columns are strictly alphanumeric (a-zA-Z0-9_).
     *  5. All food items will strictly contain 5 nutrients in the given order:    
     *     calories,fat,carbohydrate,fiber,protein
     *  6. Nutrients are CASE-INSENSITIVE. 
     * 
     * @param filePath path of the food item data file 
     *        (e.g. folder1/subfolder1/.../foodItems.csv) 
     */
    @Override
    public void loadFoodItems(String filePath) {
        
    	List<String> rawData = null;
    	try
    	{
    		rawData = loadFromFile(filePath);
    	}
    	catch (FileNotFoundException e)
    	{
    		// I don't love this, but we can't pass errors back with the given method signature, so do nothing
    	}
    	if (rawData != null)
    	{
    		// parse data and add it to our session's food list
        	parseData(rawData);
    	}
    }
    
    /**
     * Load all lines of data from a file
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
			
			// ToDo: Remove toLoad check before submitting
            while (inFile.hasNextLine() && rtnList.size() <= this.toLoad)
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
    			return left.getName().compareTo(right.getName()); 
    		});
    }
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
    	
    	substring = substring.toLowerCase();
    	
    	LinkedList<FoodItem> rtnList = new LinkedList<FoodItem>();
    	
    	// hopefully this is a small list (probably just one element), but if search string is 2 or fewer characters, this could take a while
    	for (FoodItem nxt: this.foodItemList)
		{
			if (nxt.getName().toLowerCase().contains(substring))
			{
				rtnList.add(nxt);
			}
		}
        return rtnList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
    	HashSet<FoodItem> current = this.foodItemLookup;
    	HashSet<FoodItem> filtered = null;
        for (String rule: rules)
        {
        	String[] pieces = rule.split(" ");
        	
        	filtered = filterByOneRule(current, pieces[0], pieces[1], pieces[2]);
        	current = filtered;
        }
        
        LinkedList<FoodItem> rtnList = new LinkedList<FoodItem>();
        for(FoodItem nxt: filtered)
        {
        	rtnList.add(nxt);
        }
        
        return rtnList;
    }
    
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

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        this.foodItemList.add(foodItem);
        this.foodItemLookup.add(foodItem);
        if (this.foodItemList.size() % 10000 == 0)
        {
        	System.out.println("Item " + this.foodItemList.size());
        }
        // add to the nutrient index
        for (String nutrient: foodItem.getNutrients().keySet())
        {
        	double amt = foodItem.getNutrientValue(nutrient);
        	BPTree<Double, FoodItem> idx = this.indexes.get(nutrient);
        	idx.insert(amt, foodItem);
        }
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
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
    	Collection<String> formattedAndSorted = formatAndSortData(this.foodItemList);
    	
    	File file = new File( filename );
    	
    	file.delete();
    	try 
		{
	        if (file.createNewFile())                          
	        {           
				FileWriter fw = new FileWriter(file);
				
				for (String nxt: formattedAndSorted)
				{
					fw.write(nxt);
					fw.write("\r\n");
				}
				fw.close();
	        }
		} 
		catch (IOException e) { }
    }
    
    private Collection<String> formatAndSortData(List<FoodItem> list)
    {
    	
    	TreeMap<String, String> sortedByName = new TreeMap<String, String>();
    	
    	for (FoodItem nxt: list)
    	{
    		String key = nxt.getName().toLowerCase() + nxt.getID();
    		sortedByName.put(key, serializeFoodItem(nxt));
    	}
    	
    	return sortedByName.values();
    }
    
    private static String serializeFoodItem(FoodItem item)
    {
    	StringBuilder sb = new StringBuilder();
		sb.append(item.getID());
		sb.append(",");
		sb.append(item.getName());
		sb.append(",");
		HashMap<String, Double> nutrients = item.getNutrients();
		Constants.Nutrient[] nutrientList = Constants.Nutrient.values();
		NumberFormat formatter = new DecimalFormat("#0");
		for (int i = 0; i < nutrientList.length; i++)
		{
			sb.append(nutrientList[i]);
			sb.append(",");
			sb.append(formatter.format(nutrients.get(nutrientList[i].toString())));
			sb.append(",");
		}
		return sb.toString();
    }
    
    public static void main(String[] args) {
    	FoodData data = new FoodData();
    	data.loadFoodItems(Constants.InitialDataPath);
    	List<FoodItem> allItems = data.getAllFoodItems();
    	System.out.println(allItems.size() + " items loaded");
    	testNameSearch(data, "aigR");
    	testRules(data);
    	testSaveData(data);
    }
    
    private static void testSaveData(FoodData data)
    {
    	data.saveFoodItems("C:\\WillSource\\CS400\\uwcs400\\GroupProject\\foodItemsMySave.csv");
    }
    
    private static void testNameSearch(FoodData data, String toFind)
    {
    	System.out.println("Items containing string \"" + toFind + "\"");
    	List<FoodItem> byName = data.filterByName(toFind);
    	for (FoodItem nxt: byName)
    	{
    		System.out.println(nxt.getName());
    	}
    	System.out.println();
    }
    
    private static void testRules(FoodData data)
    {
    	System.out.println("Items passing the following rules:");
    	List<String> rules = new LinkedList<String>();
    	rules.add("calories <= 50");
    	rules.add("calories >= 50");
    	
    	rules.add("calories == 50");
    	
    	for (String nxt: rules)
    	{
    		System.out.println(nxt);
    	}
    	List<FoodItem> filteredData = data.filterByNutrients(rules);
    	System.out.println("Found " + filteredData.size() + " items");
    	for (FoodItem nxt: filteredData)
    	{
    		System.out.println(printHelper(nxt, "calories"));
    	}
    	System.out.println(data.indexes.get("calories").toString());
    	System.out.println();
    }
    
    private static String printHelper(FoodItem item, String toShow)
    {
    	return item.getName() + ": " + item.getNutrientValue(toShow);
    }

}
