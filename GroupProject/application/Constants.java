/**
 * Filename:   Constants.java
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
 * 
 * Bugs:       No known bugs
 */
package application;

/**
 * Set of constants used throughout the program to centralize and standardize data. 
 */
public class Constants {
	
	// hex ID length used for food items
	public static final int IdLength = 24;
	
	// current working directory of the program
	public static final String CurrentDirectory = System.getProperty("user.dir");
	
	// initial path to a food items list - used at startup
	public static final String InitialDataPath = CurrentDirectory + "\\foodItems.csv";
	
	// default path to save food options list to
	public static final String DefaultSavePath = CurrentDirectory + "\\mySave.csv";
	
	// list of comparators used for filtering food based on nutrient values
	public static final String[] Comparators = new String[] { "<=", "==", ">=" };
	
	// hex digits - used for generating hex IDs for user-added food items
	public static final char[] HexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };	
	
	// list of possible io message types used for standardizing load and save responses
	public enum IOMessage
	{
		Success,
		IOEx,
		SecurityEx,
		FileNotFoundEx,
		UnexpectedEx
	}
	
	// possible nutrient labels from serialized data on file
	public enum Nutrient
	{
		calories,
		fat,
		carbohydrate,
		fiber,
		protein;
	}
}
