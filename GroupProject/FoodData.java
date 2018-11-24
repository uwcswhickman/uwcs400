import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
	private enum Nutrient
	{
		calories,fat,carbohydrate,fiber,protein;
	}
    // List of all the food items.
    private List<FoodItem> foodItemList;
    
    // hashed set of all of food items for quick lookup
    private HashSet<FoodItem> foodItemLookup;
    
    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    // name lookup index with chunks of size 2 & 5
    private HashMap<String, HashSet<FoodItem>> nameIndex;
        
    /**
     * Public constructor
     */
    public FoodData() {
        this.foodItemList = new LinkedList<FoodItem>();
        this.foodItemLookup = new HashSet<FoodItem>();
        this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
        this.nameIndex = new HashMap<String, HashSet<FoodItem>>();
        for (Nutrient nxt: Nutrient.values())
        {
        	this.indexes.put(nxt.toString(), new BPTree<Double, FoodItem>(128));
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
        
    	List<String> rawData = getRawData(filePath);
    	
    	parseData(rawData);
    }
    
    private void parseData(List<String> rawData)
    {
    	for (String nxtLine: rawData)
    	{
    		String[] pieces = nxtLine.split(",");
    		
    		if (pieces.length >= 12)
    		{
    			FoodItem nxtItm = new FoodItem(pieces[0], pieces[1]);
    			for (int i = 2; i < 12; i++)
    			{
    				nxtItm.addNutrient(pieces[i], Double.parseDouble(pieces[++i]));
    			}
    			this.addFoodItem(nxtItm);
    		}
    	}
    }
    
    private List<String> getRawData(String filePath)
    {
    	List<String> rtnList = new LinkedList<String>();
    	
    	File file = new File( filePath );

        if (file.exists())                          
        {                                             
			try 
			{
				Scanner inFile = new Scanner(file);
				
	            while ( inFile.hasNextLine() )
	            {
	                rtnList.add(inFile.nextLine()); // save if needed 
	            }
	            
	            // Close the buffered reader input stream attached to the file
	            inFile.close();
			} 
			catch (FileNotFoundException e) { }
        }
        return rtnList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
    	substring = substring.toLowerCase();
    	
    	LinkedList<FoodItem> rtnList = new LinkedList<FoodItem>();
    	
    	Collection<FoodItem> oneByOneCheck;
    	
    	if (substring.length() < 3)
    	{
    		// we don't have any indices this small, so we'll just have to loop over the whole set
    		oneByOneCheck = this.foodItemList;
    	}
    	else
    	{
    		HashSet<FoodItem> currentList = new HashSet<FoodItem>(); 
    		for (FoodItem nxt: this.foodItemList)
    		{
    			currentList.add(nxt);
    		}
    		if (substring.length() >= 5)
        	{
    			for (int i = 0; i <= substring.length() - 5; i++)
        		{
        			String toSrch = substring.substring(i, i + 5);
        			filterBySubString(currentList, toSrch);
        			if (currentList.size() == 1)
        			{
        				break;
        			}
        		}
        	}
        	else
        	{
        		for (int i = 0; i < substring.length() - 3; i++)
        		{
        			String toSrch = substring.substring(i, i + 3);
        			filterBySubString(currentList, toSrch);
        			if (currentList.size() == 1)
        			{
        				break;
        			}
        		}
        	}
    		// now we should have a pretty filtered down list, depending on how long the original search string was
    		oneByOneCheck = currentList;
    	}
    	// hopefully this is a small list (probably just one element), but if search string is 2 or fewer characters, this could take a while
    	for (FoodItem nxt: oneByOneCheck)
		{
			if (nxt.getName().toLowerCase().contains(substring))
			{
				rtnList.add(nxt);
			}
		}
        return rtnList;
    }
    private void filterBySubString(HashSet<FoodItem> startingList, String substring)
    {
    	HashSet<FoodItem> bySubstring = this.nameIndex.get(substring);
    	
    	startingList.retainAll(bySubstring);
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
        // add to the nutrient index
        for (String nutrient: foodItem.getNutrients().keySet())
        {
        	double amt = foodItem.getNutrientValue(nutrient);
        	BPTree<Double, FoodItem> idx = this.indexes.get(nutrient);
        	idx.insert(amt, foodItem);
        }
        // add to the name index
        String name = foodItem.getName().toLowerCase();
        if (foodItem.getName().length() >= 3)
        {
        	for (int i = 0; i < name.length() - 3; i++)
        	{
        		String nxtChnk = name.substring(i, i + 3);
        		if (!this.nameIndex.containsKey(nxtChnk))
        		{
        			this.nameIndex.put(nxtChnk, new HashSet<FoodItem>());
        		}
        		this.nameIndex.get(nxtChnk).add(foodItem);
        	}
        	for (int i = 0; i < name.length() - 5; i++)
        	{
        		String nxtChnk = name.substring(i, i + 5);
        		if (!this.nameIndex.containsKey(nxtChnk))
        		{
        			this.nameIndex.put(nxtChnk, new HashSet<FoodItem>());
        		}
        		this.nameIndex.get(nxtChnk).add(foodItem);
        	}
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
    	List<String> formattedAndSorted = formatAndSortData();
    	
    	File file = new File( filename );
    	
    	file.delete();
    	try 
		{
	        if (file.createNewFile())                          
	        {           
	        	System.out.println("Saving " + formattedAndSorted.size() + " files to " + filename);
				FileWriter fw = new FileWriter(file);
				
				for (String nxt: formattedAndSorted)
				{
					fw.write(nxt);
					fw.write("\r\n");
				}
				fw.close();
				System.out.println("File save successful");
	        }
		} 
		catch (IOException e) { }
    }
    
    private List<String> formatAndSortData()
    {
    	List<String> rtnList = new LinkedList<String>();
    	
    	TreeMap<String, FoodItem> sortedNames = new TreeMap<String, FoodItem>();
    	
    	for (FoodItem nxt: this.foodItemList)
    	{
    		// combine name with ID so it's unique, since name isn't gauranteed to be unique
    		sortedNames.put(nxt.getName() + nxt.getID(), nxt);
    	}
    	
    	for (String name: sortedNames.keySet())
    	{
    		FoodItem nxt = sortedNames.get(name);
    		StringBuilder sb = new StringBuilder();
    		sb.append(nxt.getID());
    		sb.append(",");
    		sb.append(nxt.getName());
    		sb.append(",");
    		HashMap<String, Double> nutrients = nxt.getNutrients();
    		Nutrient[] nutrientList = Nutrient.values();
    		NumberFormat formatter = new DecimalFormat("#0");
    		for (int i = 0; i < nutrientList.length; i++)
    		{
    			sb.append(nutrientList[i]);
    			sb.append(",");
    			sb.append(formatter.format(nutrients.get(nutrientList[i].toString())));
    			sb.append(",");
    		}
    		rtnList.add(sb.toString());
    	}
    	return rtnList;
    }
    
    public static void main(String[] args) {
    	String filePath = "C:\\WillSource\\CS400\\uwcs400\\GroupProject\\foodItems.csv";
    	FoodData data = new FoodData();
    	data.loadFoodItems(filePath);
    	List<FoodItem> allItems = data.getAllFoodItems();
    	System.out.println(allItems.size() + " items loaded");
    	List<FoodItem> byName = data.filterByName("Vegetable");
    	for (FoodItem nxt: byName)
    	{
    		System.out.println(nxt.getName());
    	}
    	data.saveFoodItems("C:\\WillSource\\CS400\\uwcs400\\GroupProject\\foodItemsMySave.csv");
    }

}
