// Hello X-Team Blue
public class HashFunctions {
	
	/**
	 * Recreate Java's string hashing function but using just basic Math library. This is the slow version
	 * @param key - the string to hash
	 * @return hash value for given string, using same logic as Java's built in string hasher
	 */
	public static int stringHasher(String key)
	{
		int rtn = 0;
		int length = key.length();
		for (int i = 0; i < key.length(); i++)
		{
			rtn += key.charAt(i) * Math.pow(31, length - i - 1);
		}
		return rtn;
	}
	
	/******
	 * Recreate Java's string hashing function using bit shifting
	 * @param key - the string to hash
	 * @return hash value for given string, using same logic as Java's built in string hasher
	 */
	public static int stringHasherWithBitShift(String key)
	{
		int rtn = 0;
		int length = key.length();
		
		// hash = C(0) * 31^(n - 1) + C(1) * 31^(n - 2) + ... + C(n - 2) * 31^1 + C(n - 1) * 31^0
		for (int i = 0; i < key.length(); i++)
		{
			// get ascii code for character
			int ascii = (int) key.charAt(i);
			
			// now do the polynomial  stuff with 31
			int nMinusOne = length - i - 1;
			
			// interesting thing to note here: The addition is with 32 bit addition (i.e. add them and get back an int, not a long), but I'm doing all of the 
			// polynomial stuff with long so that I try to keep stuff as exact as possible. And I'm getting the same results as the string.hashCode() method
			// so it seems like I did it the same way. At first, I was doing everything with long, and i was getting different (much larger) answers when 
			// working with long strings. But this fixed it. Saw on wikipedia that they were using "32 bit addition", so I tried it, and it worked
			rtn += ascii * powersOfThirtyOne(nMinusOne);
		}
		return rtn;
	}
	
	/**
	 * Demo of bit shifting. returns 2^x where x is the argument passed in
	 * @throws IllegalArgumentException - if argument is less than 0
	 */
	public static int powerOfTwo(int pow) throws IllegalArgumentException
	{
		if (pow < 0)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			return 1 << pow;
		}
	}
	
	/**
	 * Returns powers of 31. Uses bit-shifting, so this is fast
	 * @param power - the power to raise 31 to
	 * @return 31^power as long
	 */
	public static long powersOfThirtyOne(int power) throws IllegalArgumentException
	{
		if (power < 0)
		{
			throw new IllegalArgumentException();
		}
		else if (power == 0)
		{
			return 1;
		}
		else
		{
			return powersOfThirtyOneHelper(1, power);
		}
	}
	
	/**
	 * Recursion helper for getting a power of 31. Uses bit shifting to make it speedy
	 * @param current - the current product from the recursion
	 * @param powersLeft - the number times left that we need to multiply by 31
	 * @return current * 31^powersLeft
	 */
	private static long powersOfThirtyOneHelper(long current, int powersLeft)
	{
		if (powersLeft == 1)
		{
			return thirtyOneTimes(current);
		}
		else
		{
			long next = thirtyOneTimes(current);
			return powersOfThirtyOneHelper(next, powersLeft - 1);
		}
	}
	
	/**
	 * multiply a positive number by 31, using bit shifting
	 * @param multiplier - the number to multiply by 31
	 * @return multiplier * 31
	 */
	public static long thirtyOneTimes(long multiplier)
	{
		// Want to implement 31 * someNumber using bit shifts and using the fact that 31 = 32 - 1
		// 31 * multiplier == (32 - 1) * multiplier
		// == (2^5 - 1) * multiplier == 2^5 * multiplier - multiplier 
		// Ok, almost there:
		// == (2 * multiplier) * 2) * 2) * 2) * 2) - multiplier
		// == ((multiplier << 1) << 1) << 1) << 1) - multiplier == (multiplier << 5) - multiplier
		return (multiplier << 5) - multiplier;
	}
	
	public static boolean isPrime(int toCheck, int[] knownPrimes)
	{
		double half = toCheck / 2;
		
		for (int i = 0; i < knownPrimes.length; i++)
		{
			
		}
		return false;
	}
	
	private static int[] knownPrimes(int upTo, int[] knownPrimes)
	{
		int lastKnown = 2;
		double half = upTo / 2;
		if (knownPrimes.length > 0)
		{
			lastKnown = knownPrimes[knownPrimes.length - 1];
		}
		// only need to check numbers less than half and then the number itself
		if (lastKnown >= upTo)
		{
			return knownPrimes;
		}
		else if (lastKnown >= half)
		{
			if (isPrime(upTo, knownPrimes))
			{
				return knownPrimes.clone();
			}
		}
		
		return new int[1];
	}
}//GO TEAM BLUE!!
