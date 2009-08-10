package summixAssembler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * LiteralTable is a static class  that contains exactly one
 * of each literal in a program and an address to store that literal in.
 * 
 * @author Jim
 *
 */
public class LiteralTable {
	/**  Hash map to store name -> address relationship in.*/
	private static Map<Short, Short> literals = new HashMap<Short, Short>();
	/** size of the hashmap */
	private static int size = 0;
	
	private static Collection<Short> c = null;
	private static Iterator<Short> iterator = null;
	/**
	 * Adds a literal to the literal table
	 * 
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */
	public static void input(short key, short addr) {
		if (!literals.containsKey(key)) {
			literals.put(key, addr);
			size++;
		}
	}
	
	/**
	 * Adds a literal to the literal table
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */
	public static void input (String key, String addr) {
		input(Short.parseShort(key),Short.parseShort(addr));
	}

	/**
	 * Checks to see if a literal is in the table
	 * @param key the key to look for
	 * @return whether or not the key exist in the table
	 */
	public static boolean isLitereal(String key)
	{
		return literals.containsKey(key);
	}
	
	/**
	 * Adds a literal to the literal table
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */
	public static void input (short key, String addr) {
		input(key,Short.parseShort(addr));
	}

	/**
	 * Adds a literal to the literal table
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */	
	public static void input (String key, short addr) {
		input(Short.parseShort(key),addr);
	}
	
	/**
	 * Returns the address where a literal is stored
	 * @param key the key to search for
	 * @return the address of the literal given by key
	 */
	public static short getAddress(short key) {
		short returnVal = 0;
		if (literals.containsKey(key)) {
			returnVal = literals.get(key).shortValue();
		} else {
			System.out.println("ERROR: Literal is undefined in the literal table: " + key + " The value 0 has been used in its place.");
		}
		return returnVal;
	}
	
	/**
	 * Returns the address where a literal is stored
	 * @param key the key to search for
	 * @return the address of the literal given by key
	 */
	public static short getAddress(String key)
	{
		return getAddress(Short.parseShort(key));
	}
	
	/**
	 * Returns the address where a literal is stored
	 * @param key the key to search for
	 * @return the address of the literal given by key
	 */
	public static short getAddress(int key)
	{
		return getAddress((short)key);
	}
	
	/**
	 * Display the contents of the literal table to the console.
	 */
	public static void display() {
		System.out.println( "Literal Table:\n" +
						 	"Format: key (address)");
		System.out.println(literals.toString());
	}
	
	/**
	 * Preps literal table to have values extracted. Should only be called once, and before getNextLiteral() is called
	 * @see getNextLiteral
	 */
	public static void intializeIterator()
	{
		c = literals.values();
		iterator = c.iterator();
	}
	
	/**
	 * Method to pull out the literals one by one from the literal table. MUST call intializeIterator() before calling this method.
	 * @return The value of the literal. If there are no more left, it returns null.
	 * @see inializeIterator
	 */
	public static Short getNextLiteral()
	{
			return iterator.next(); // necessary because iterator.next is of type Short
	}
	
	/**
	 * Checks to see if there are anymore literals left to pull out
	 * @return	True if there are anymore literals left to iterate through
	 */
	public static boolean areMoreLiterals()
	{
		if(iterator.hasNext())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

