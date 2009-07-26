package summixAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jim
 *
 */
public class SymbolTable {	
	/**
	 * 
	 * @author Jim
	 *
	 */
	private static class Symbol {
		/**
		 * 
		 * @param value the value of the symbol to be created
		 * @param relative whether or not this symbols value is relative to its location in memory
		 */
		Symbol(short value, boolean relative) {
			this.value = value;
			this.isRelative = relative;
		}
		/**
		 * Returns a String object representing the specified symbol. 
		 */
		public String toString() {
			return "(" + this.value + ", " + this.isRelative + ")";
		}
		
		public short value;
		public boolean isRelative;
	}

	private static Map<String, Symbol> symbols = new HashMap<String, Symbol>();

	/**
	 * 
	 * @param key the key for the symbol
	 * @param value the value of the symbol
	 * @param relative whether or not this symbols value is relative to its location in memory
	 */
	public static void input(String key, short value, boolean relative) {
		if (symbols.containsKey(key)) {
			//error
		} else {
			symbols.put(key, new Symbol(value, relative));
		}
	}

	/**
	 * 
	 * @param key the key for the symbol
	 */
	public static void input(String key) {
		if (symbols.containsKey(key)) {
			//error
		} else {
			symbols.put(key, null);
		}
	}
	
	/**
	 * 
	 * @param key the key for the symbol
	 * @param value the value of the symbol
	 * @param relative whether or not this symbols value is relative to its location in memory
	 */
	public static void update(String key, short value, boolean relative) {
		if (!symbols.containsKey(key)) {
			//error
		} else {
			symbols.put(key, new Symbol(value, relative));
		}
	}
	
	/**
	 * 
	 * @param key the key for the symbol
	 * @return the value of the symbol denoted by key
	 */
	public static short getValue(String key) {
		short returnVal = 0;
		if (!symbols.containsKey(key)) {
			//error
		} else {
			returnVal = symbols.get(key).value;
		}
		return returnVal;
	}
	
	/**
	 * 
	 * @param key the key for this symbol
	 * @return the relativity of the symbol denoted by key
	 */
	public static boolean isRelative(String key) {
		boolean returnVal = false;
		if (!symbols.containsKey(key)) {
			//error
		} else {
			returnVal = symbols.get(key).isRelative;
		}
		return returnVal;
	}
	
	/**
	 * Display the symbol table to the console
	 */
	public static void display() {
		System.out.println( "Symbol Table:\n" +
						 	"Format: Mnemonic (value, isRelative)");
		System.out.println(symbols.toString());
	}
}
