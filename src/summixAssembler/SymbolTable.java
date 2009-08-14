package summixAssembler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

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

	/** symbols that are ENT */
	private static Vector<String> entSymbols = new Vector<String>();
	
	/** symbols that are EXT */
	private static Vector<String> extSymbols = new Vector<String>();
	
	/**
	 * Adds an entry to symbol table
	 * @param ent the name of the entry
	 */
	public static void setEnt(String ent) {
		if (!entSymbols.contains(ent)) {
			entSymbols.add(ent);
		} else {
			System.out.println("ERROR: .ENT " + ent + " is already defined as an ENT.");
		}
	}
	
	/**
	 * Adds an external symbol to the symbol table
	 * @param ext the external symbol to be added
	 */
	public static void setExt(String ext) {
		if (!extSymbols.contains(ext)) {
			extSymbols.add(ext);
		} else {
			System.out.println("ERROR: .EXT " + ext + " is already defined as an EXT.");
		}
	}
	
	/**
	 * Checks whether or not the ext is valid
	 * @param ext the ext to check
	 * @return true if the ext is valid, false if not 
	 */
	public static boolean isExt(String ext) {
		return extSymbols.contains(ext);
	}
	
	/**
	 * Symbol table is expected to be complete, finalize gives any error messages associated with an incomplete table.
	 */
	public static void checkTable() {
		Iterator<String> i = entSymbols.iterator();
		while (i.hasNext()) {
			//look at each entSymbol
			String key = i.next();
			if (!(symbols.containsKey(key))) {
				//if that symbol is not defined in the symbol table we have a problem!
				System.out.println("ERROR: .ENT " + key + " is not defined within this scope.");
			}
			if (extSymbols.contains(key)) {
				System.out.println("ERROR: .ENT " + key + " is also defined as .EXT.");				
			}
		}
	}
	
	/**
	 * Adds all external symbols to p2File in the format I<symbol name>=<address of symbol>
	 * @param p2File the file to add external symbols to 
	 * @return
	 */
	public static TextFile printPass2Table(TextFile p2File) {
	
		Iterator<String> i = entSymbols.iterator();

		while (i.hasNext()) {
			String key = i.next();
 			String ent = "I" + key + "=" + shortToHexStringNoPrefix(symbols.get(key).value);
			p2File.input(ent);
		}
		
		return p2File;
	}
	
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
