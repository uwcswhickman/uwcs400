import java.util.Scanner;

public class HashMapUI {

	public static void main(String[] args) {
		
		String prompt = "Enter a string:";
		
		String toHash = getString(prompt);
		
		System.out.println("Java's hashCode() output for " + toHash);
		System.out.println(toHash.hashCode());
		
		System.out.println("Output for recration of Java's string hash function using bit shift on " + toHash);
		System.out.println(HashFunctions.stringHasherWithBitShift(toHash));
		
	}
	
	private static String getString(String prompt)
	{
		Scanner scanner = new Scanner(System. in); 
		System.out.println(prompt);
		String input = scanner. nextLine();
		scanner.close();
		return input;
	}
}
