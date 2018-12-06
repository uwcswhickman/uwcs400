package application;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BPTreeTest {
	
	private static FoodData data;
	private static FoodData largeDataSmallBranch;
	private static FoodData largeDataLargeBranch;
	private static List<FoodItem> foodItems;
	private static int numToLoad = 100000;
	private static String largeItemListPath = "C:\\WillSource\\CS400\\uwcs400\\GroupProject\\foodItemsLarge.csv";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		data = new FoodData();
		data.SetNumToLoad(numToLoad);
		data.loadFoodItems(Constants.InitialDataPath);
		largeDataSmallBranch = new FoodData();
		largeDataSmallBranch.SetNumToLoad(numToLoad);
		largeDataSmallBranch.loadFoodItems(largeItemListPath);
		largeDataLargeBranch = new FoodData(203);
		largeDataLargeBranch.SetNumToLoad(numToLoad);
		largeDataLargeBranch.loadFoodItems(largeItemListPath);
		foodItems = data.getAllFoodItems();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test00InsertOneAndRangeSearchLessThan() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		testTree.insert(10d, foodItems.get(0));
		FoodItem expected = foodItems.get(0);
		FoodItem actual = testTree.rangeSearch(12d, "<=").get(0);
		
		assertEquals(expected, actual);
	}
	@Test
	public void test01InsertOneAndRangeSearchGreaterThan() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		testTree.insert(10d, foodItems.get(0));
		FoodItem expected = foodItems.get(0);
		FoodItem actual = testTree.rangeSearch(10d, ">=").get(0);
		assertEquals(expected, actual);
	}
	@Test
	public void test02InsertOneAndRangeSearchEqualTo() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		testTree.insert(10d, foodItems.get(0));
		FoodItem expected = foodItems.get(0);
		FoodItem actual = testTree.rangeSearch(10d, "==").get(0);
		assertEquals(expected, actual);
	}
	
	@Test
	public void test03InsertSeveralAndRangeSearchLessThan() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double cutoff = 4;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			if (i <= cutoff)
			{
				expected.add(nxt);
			}
			
		}
		List<FoodItem> actual = testTree.rangeSearch(cutoff, "<=");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	@Test
	public void test04InsertSeveralAndRangeSearchGreaterThan() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double cutoff = 0;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			
			if (i >= cutoff)
			{
				expected.add(nxt);
			}
			
		}
		List<FoodItem> actual = testTree.rangeSearch(cutoff, ">=");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test05InsertSeveralAndRangeSearchEqualTo() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double keeper = 4;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			if (i == keeper)
			{
				expected.add(nxt);
			}
		}
		List<FoodItem> actual = testTree.rangeSearch(keeper, "==");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test06InsertSeveralAndRangeSearchGreaterOutsideRange() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double cutoff = 10;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			if (i >= cutoff)
			{
				expected.add(nxt);
			}
		}
		List<FoodItem> actual = testTree.rangeSearch(cutoff, ">=");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test07InsertSeveralAndRangeSearchLessOutsideRange() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(3);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double cutoff = -10;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			if (i <= cutoff)
			{
				expected.add(nxt);
			}
			
		}
		List<FoodItem> actual = testTree.rangeSearch(cutoff, "<=");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test08InsertSeveralLargeBranchingLessThan() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(101);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double cutoff = 4;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			if (i <= cutoff)
			{
				expected.add(nxt);
			}
			
		}
		List<FoodItem> actual = testTree.rangeSearch(cutoff, "<=");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	@Test
	public void test09InsertSeveralLargeBranchingGreaterThan() {
		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(101);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double cutoff = 0;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			
			if (i >= cutoff)
			{
				expected.add(nxt);
			}
			
		}
		List<FoodItem> actual = testTree.rangeSearch(cutoff, ">=");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test10InsertSeveralLargeBranchingEqualTo() {

		BPTreeADT<Double, FoodItem> testTree = new BPTree<Double, FoodItem>(101);
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double keeper = 4;
		for (int i = 0; i < 10; i++)
		{
			FoodItem nxt = foodItems.get(i);
			testTree.insert(i + 0.0, nxt);
			if (i == keeper)
			{
				expected.add(nxt);
			}
			
		}
		List<FoodItem> actual = testTree.rangeSearch(4d, "==");
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void test11InsertManyTimedTestSmallBranchingLessThan() {
		
		List<FoodItem> items = largeDataSmallBranch.getAllFoodItems();
		TreeMap<Double, List<FoodItem>> expectedMap = new TreeMap<Double, List<FoodItem>>();
		double cutoff = 50;
		for (FoodItem nxt: items)
		{
			double carbs = nxt.getNutrientValue("carbohydrate");
			if (!expectedMap.containsKey(carbs))
			{
				expectedMap.put(carbs, new LinkedList<FoodItem>());
			}
			expectedMap.get(carbs).add(nxt);
		}
		
		List<FoodItem> expected = new LinkedList<FoodItem>();
		
		for (double nxtVal: expectedMap.keySet())
		{
			if (nxtVal <= cutoff)
			{
				expected.addAll(expectedMap.get(nxtVal));
			}
			else
			{
				break;
			}
		}
		
		String rule = "carbohydrate <= 50";
		List<String> rules = new LinkedList<String>();
		rules.add(rule);
		List<FoodItem> actual = largeDataSmallBranch.filterByNutrients(rules);
		
		assertEquals(expected.size(), actual.size());
	}
	
	@Test
	public void test12InsertManyTimedTestLargeBranchingLessThan() {
		
		List<FoodItem> items = largeDataLargeBranch.getAllFoodItems();
		TreeMap<Double, List<FoodItem>> expectedMap = new TreeMap<Double, List<FoodItem>>();
		double cutoff = 50;
		for (FoodItem nxt: items)
		{
			double carbs = nxt.getNutrientValue("carbohydrate");
			if (!expectedMap.containsKey(carbs))
			{
				expectedMap.put(carbs, new LinkedList<FoodItem>());
			}
			expectedMap.get(carbs).add(nxt);
		}
		
		List<FoodItem> expected = new LinkedList<FoodItem>();
		
		for (double nxtVal: expectedMap.keySet())
		{
			if (nxtVal <= cutoff)
			{
				expected.addAll(expectedMap.get(nxtVal));
			}
			else
			{
				break;
			}
		}
		String rule = "carbohydrate <= 50";
		List<String> rules = new LinkedList<String>();
		rules.add(rule);
		List<FoodItem> actual = largeDataLargeBranch.filterByNutrients(rules);
		
		assertEquals(expected.size(), actual.size());
	}
	
	@Test
	public void test13InsertManyTimedTestSmallBranchingGreaterThan() {
		
		List<FoodItem> items = largeDataSmallBranch.getAllFoodItems();
		TreeMap<Double, List<FoodItem>> expectedMap = new TreeMap<Double, List<FoodItem>>();
		double cutoff = 50;
		for (FoodItem nxt: items)
		{
			double carbs = nxt.getNutrientValue("carbohydrate");
			if (!expectedMap.containsKey(carbs))
			{
				expectedMap.put(carbs, new LinkedList<FoodItem>());
			}
			expectedMap.get(carbs).add(nxt);
		}
		
		List<FoodItem> expected = new LinkedList<FoodItem>();
		
		for (double nxtVal: expectedMap.keySet())
		{
			if (nxtVal >= cutoff)
			{
				expected.addAll(expectedMap.get(nxtVal));
			}
		}
		
		String rule = "carbohydrate >= 50";
		List<String> rules = new LinkedList<String>();
		rules.add(rule);
		List<FoodItem> actual = largeDataSmallBranch.filterByNutrients(rules);
		
		assertEquals(expected.size(), actual.size());
	}
	
	@Test
	public void test14InsertManyTimedTestLargeBranchingGreaterThan() {
		
		List<FoodItem> items = largeDataLargeBranch.getAllFoodItems();
		TreeMap<Double, List<FoodItem>> expectedMap = new TreeMap<Double, List<FoodItem>>();
		double cutoff = 50;
		for (FoodItem nxt: items)
		{
			double carbs = nxt.getNutrientValue("carbohydrate");
			if (!expectedMap.containsKey(carbs))
			{
				expectedMap.put(carbs, new LinkedList<FoodItem>());
			}
			expectedMap.get(carbs).add(nxt);
		}
		
		List<FoodItem> expected = new LinkedList<FoodItem>();
		
		for (double nxtVal: expectedMap.keySet())
		{
			if (nxtVal >= cutoff)
			{
				expected.addAll(expectedMap.get(nxtVal));
			}
		}
		String rule = "carbohydrate >= 50";
		List<String> rules = new LinkedList<String>();
		rules.add(rule);
		List<FoodItem> actual = largeDataLargeBranch.filterByNutrients(rules);
		
		assertEquals(expected.size(), actual.size());
	}
	
	@Test
	public void test15InsertManyTimedTestSmallBranchingEqualTo() {
		
		List<FoodItem> items = largeDataSmallBranch.getAllFoodItems();
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double keeper = 50;
		for (FoodItem nxt: items)
		{
			double carbs = nxt.getNutrientValue("carbohydrate");
			if (keeper == carbs)
			{
				expected.add(nxt);
			}
		}
		
		String rule = "carbohydrate == 50";
		List<String> rules = new LinkedList<String>();
		rules.add(rule);
		List<FoodItem> actual = largeDataSmallBranch.filterByNutrients(rules);
		
		assertEquals(expected.size(), actual.size());
	}
	
	@Test
	public void test16InsertManyTimedTestLargeBranchingEqualTo() {
		List<FoodItem> items = largeDataLargeBranch.getAllFoodItems();
		List<FoodItem> expected = new LinkedList<FoodItem>();
		double keeper = 50;
		for (FoodItem nxt: items)
		{
			double carbs = nxt.getNutrientValue("carbohydrate");
			if (keeper == carbs)
			{
				expected.add(nxt);
			}
		}
		
		String rule = "carbohydrate == 50";
		List<String> rules = new LinkedList<String>();
		rules.add(rule);
		List<FoodItem> actual = largeDataLargeBranch.filterByNutrients(rules);
		
		assertEquals(expected.size(), actual.size());
	}
}
