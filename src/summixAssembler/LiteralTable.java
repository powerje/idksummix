package summixAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jim
 *
 */
public class LiteralTable {
	private static Map<Short, Short> literals = new HashMap<Short, Short>();

	/**
	 * 
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */
	public static void input(short key, short addr) {
		if (!literals.containsKey(key)) {
			literals.put(key, addr);
		} else {
			//error
		}
	}
	
	/**
	 * 
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */
	public static void input (String key, String addr) {
		input(Short.parseShort(key),Short.parseShort(addr));
	}

	/**
	 * 
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */
	public static void input (short key, String addr) {
		input(key,Short.parseShort(addr));
	}

	/**
	 * 
	 * @param key the key to add
	 * @param addr the address to give the new literal
	 */	
	public static void input (String key, short addr) {
		input(Short.parseShort(key),addr);
	}
	
	/**
	 * 
	 * @param key the key to search for
	 * @return the address of the literal given by key
	 */
	public static short getAddress(short key) {
		short returnVal = 0;
		if (literals.containsKey(key)) {
			returnVal = literals.get(key).shortValue();
		} else {
			//error
		}
		return returnVal;
	}
	
	/**
	 * Display the contents of the literal table to the console.
	 */
	public static void display() {
		System.out.println( "Literal Table:\n" +
						 	"Format: key (address)");
		System.out.println(literals.toString());
	}
	
}

