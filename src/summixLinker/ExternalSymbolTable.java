package summixLinker;
import java.util.HashMap;
import java.util.Map;

public class ExternalSymbolTable {
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
	
	public static String shortToHexStringNoPrefix(short data) {
		String returnVal = Integer.toHexString((int) data);
		if (returnVal.length() > 4) 
		{
			returnVal = returnVal.substring(returnVal.length() - 4, returnVal.length());
		}
		while (returnVal.length() < 4) 
		{
			returnVal = "0" + returnVal;
		}
		return returnVal.toUpperCase();
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

	/**	concrete representation of symbols in the symbol table*/
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
			System.out.println("ERROR: Symbol defined multiple times: " + key);
		} else if (!key.matches("^\\w+$") || ((key.charAt(0)=='x') || (key.charAt(0)=='R'))) {		//can't start with R, x, or a number
			//check if its a number...
			System.out.println("ERROR: Invalid symbol name: " + key);
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
			System.out.println("ERROR: The value of this symbol is undefined in symbol table: " + key + ". The value 0 has been used in its place.");
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
