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
	
	public static final List<String> Comparators = Collections.unmodifiableList(
		    new ArrayList<String>() {{
		        add("<=");
		        add("==");
		        add(">=");
		    }});
	
	public enum Nutrient
	{
		calories,
		fat,
		carbohydrate,
		fiber,
		protein;
	}
}
