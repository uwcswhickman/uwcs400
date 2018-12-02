package application;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

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
 *
 */
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
	
	public List<String> dummyOptions()
	{
		List<String> rtnList = new LinkedList<String>();
		rtnList.add("Tinkyada_BrownRice14OzPasta");
		rtnList.add("AlFresco_ChickenSausageBurgersHotItalianStyle");
		rtnList.add("EccePanis_EuropeanBaguette");
		rtnList.add("Tostitos_TortillaChipsThickHeartyRounds");
		rtnList.add("Keebler_SimplyMadeCookiesButter");
		rtnList.add("Swanson_ChickenBreastWhitePremiumChunkinWater");
		rtnList.add("Arnold_BreadPremiumWhiteEnriched");
		rtnList.add("SamsChoice_AllNaturalChocolateIceCream");
		rtnList.add("GreatValue_FudgeDoubleFilledTwistShoutSandwichCookies");
		rtnList.add("AndrewEverett_ShreddedCheeseMozzarella");
		rtnList.add("Zoglos_HotDogsSavoryMeatlessFranks");
		rtnList.add("Kraft_CheeseItalianThreeCheese");
		rtnList.add("MarketDay_MiniOmeletswithReducedFatCheese");
		rtnList.add("MedicalWeightLossClinic_FruitDrinkMixedBerry");
		rtnList.add("LowesFoods_ShreddedCheeseMildCheddar");
		rtnList.add("Herrs_PotatoChipsFiremansBBQChicken");
		rtnList.add("Reese_TroutGoldenSmokedFillets375Oz");
		rtnList.add("RaisinBran_RaisinBran");
		rtnList.add("MorningFreshFarms_FancyShreddedNaturalCheeseSharpCheddar");
		rtnList.add("AmericasChoice_RipePittedLargeOlives");
		rtnList.add("SafewayKitchens_ChickenKiev");
		rtnList.add("Goya_EmpanadasBeef");
		rtnList.add("HurricaneBay_SouthwesternMarinade");
		rtnList.add("PopcornIndiana_AgedWhiteCheddarPopcorn");
		rtnList.add("MidwestCountryFare_TomatoesWholePeeled");
		rtnList.add("HeritageFarm_Bologna");
		rtnList.add("HillCountryFare_HoneySweetCornBread");
		rtnList.add("Giant_CheddarCheeseTwiceBakedPotatoes");
		rtnList.add("Spartan_ShreddedMozzarellaCheese");
		rtnList.add("Detour_EnergyBarChocolatePeanutButter");
		rtnList.add("JuicyJuice_100JuiceApple");
		rtnList.add("Welchs_FrozenSmoothieKitBlueberryCherryBlend");
		rtnList.add("FairOaksFarms_MilkFatFree");
		rtnList.add("GhirardelliChocolate_DarkMeltingWafers");
		rtnList.add("BumbleBee_RedSalmonWildAlaskaSockeye");
		rtnList.add("GoldEmblem_LuxuryWafers");
		rtnList.add("Borden_PunchPineappleBanana");
		rtnList.add("Hormel_FranksWranglersCoarseGroundCheeseSmoked");
		Collections.sort(rtnList);
		return rtnList;
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
	public ListView<String> GetMeal()
	{
		ListView<String> rtnLV = new ListView<String>();
		
		return rtnLV;
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
	public ListView<String> GetFoodOptions()
	{
		ListView<String> rtnLV = new ListView<String>();
		rtnLV.getItems().addAll(this.dummyOptions());
		return rtnLV;
//		GetFoodOptions("", null); // for real implementation, we should run regular method with no inputs, which will give the full list 
	}
	
	public ListView<String> GetFoodOptions(String nameContains, List<String> rules)
	{
		// get filtered by name and get filtered by nutrient, then probably take the smaller one and use use retainAll to find intersection to return
		return new ListView<String>();
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
