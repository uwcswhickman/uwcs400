package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author whickman
 * Resources
 * - constant list implementation from here - https://stackoverflow.com/questions/9285011/declaring-an-arraylist-object-as-final-for-use-in-a-constants-file
 */
public class Constants {
	
	public static final int IDLENGTH = 24;
	
	public static final String InitialDataPath = System.getProperty("user.dir") + "\\foodItems.csv";
	
	public static final String[] Comparators = new String[] { "<=", "==", ">=" };
	
	public static final char[] HexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };	
	
	public enum Nutrient
	{
		calories,
		fat,
		carbohydrate,
		fiber,
		protein;
	}
}
