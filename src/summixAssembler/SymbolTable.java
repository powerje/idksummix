package summixAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the symbols collected by the pass1 object. Dispenses information about them as well.
 * @author Jim
 *
 */
public class SymbolTable {	

	/**
	 *
	 * Takes an short input and returns a four character hex representation of it from 0000 to FFFF 
	 * @param data	input
	 * @return hex representation from 0000 to FFFF 
	 * @author Jim
	 *
	 */
	public static String shortToHexString(short data) {
		String returnVal = Integer.toHexString((int) data);
		if (returnVal.length() > 4) 
		{
			returnVal = returnVal.substring(returnVal.length() - 4, returnVal.length());
		}
		while (returnVal.length() < 4) 
		{
			returnVal = "0" + returnVal;
		}
		return "0x" + returnVal.toUpperCase();
	}


	/**
	 * Symbol class used by the symbol table internally to store representations of symbols
	 * @author Jim
	 *
	 */
	private static class Symbol {
		/**
		 * Constructs object of type Symbol 
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
			return "(" + shortToHexString(this.value) + ", " + this.isRelative + ") ";
		}

		public short value;
		public boolean isRelative;
	}

	private static Map<String, Symbol> symbols = new HashMap<String, Symbol>();

	/**
	 * Inputs a symbol into the symbol table and stores its value, name, and its relativity.
	 * 
	 * @param key the key for the symbol
	 * @param value the value of the symbol
	 * @param relative whether or not this symbols value is relative to its location in memory
	 */
	public static void input(String key, short value, boolean relative) {	
		if (symbols.containsKey(key)) {
			System.out.println("ERROR: Symbol " + key + " defined multiple times.");
		} else if (true) {		//can't start with R, x, #
			
		} else {
			symbols.put(key, new Symbol(value, relative));
		}
	}

	/**
	 * Gets the value of a symbol. Prints out an error if it's not defined in the table.
	 * 
	 * @param key the key for the symbol
	 * @return the value of the symbol denoted by key
	 */
	public static short getValue(String key) {
		short returnVal = 0;
		if (!symbols.containsKey(key)) {
			System.out.println("ERROR: The value of symbol " + key + " is undefined in symbol table. The value 0 has been used in its place.");
		} else {
			returnVal = symbols.get(key).value;
		}
		return returnVal;
	}

	/**
	 * checks to see if a symbol is relative
	 * @param key the key for this symbol
	 * @return True if the symbol is relative
	 */
	public static boolean isRelative(String key) {
		return symbols.get(key).isRelative;
	}

	/**
	 * Checks to see if a symbol exists in the symbol table
	 * @param key
	 * @return  True if a symbol is defined in the table
	 */
	public static boolean isDefined(String key) {
		return symbols.containsKey(key);
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
