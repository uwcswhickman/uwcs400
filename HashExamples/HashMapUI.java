
public class HashMapUI {

	public static void main(String[] args) {
		String cat = "CAT";
		String longString = "Will Hickman's string for testing big hash values";
		
		System.out.println(HashFunctions.stringHasher(cat));
		
		System.out.println(HashFunctions.stringHasherWithBitShift(cat));
		
		System.out.println(cat.hashCode());
		
		System.out.println(longString.hashCode());
		System.out.println(HashFunctions.stringHasherWithBitShift(longString));
	}
}
