/**
 * Filename:   FoodItem.java
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
import java.util.HashMap;

/**
 * This class represents a food item with all its properties.
 * 
 * @author sapan (sapan@cs.wisc.edu), Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 */
public class FoodItem {
    // The name of the food item.
    private String name;

    // The id of the food item.
    private String id;

    // Map of nutrients and value.
    private HashMap<String, Double> nutrients;
    
    /**
     * Constructor
     * @param name name of the food item
     * @param id unique id of the food item 
     */
    public FoodItem(String id, String name) {
    	this.id = id;
    	this.name = name;
    	this.nutrients = new HashMap<String, Double>();
    }
    
    /**
     * Gets the name of the food item
     * 
     * @return name of the food item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the unique id of the food item
     * 
     * @return id of the food item
     */
    public String getID() {
        return this.id;
    }
    
    /**
     * Gets the nutrients of the food item
     * 
     * @return nutrients of the food item
     */
    public HashMap<String, Double> getNutrients() {
        return this.nutrients;
    }

    /**
     * Adds a nutrient and its value to this food. 
     * If nutrient already exists, updates its value.
     */
    public void addNutrient(String name, double value) {
    	name = name.toLowerCase();
        this.nutrients.put(name, value);
    }

    /**
     * Returns the value of the given nutrient for this food item. 
     * If not present, then returns 0.
     */
    public double getNutrientValue(String name) {
        Double value = this.nutrients.get(name);
    	if (value != null)
        {
        	return value;
        }
    	else
    	{
    		return 0;
    	}
    }
    
}
