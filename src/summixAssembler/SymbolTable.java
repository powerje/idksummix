package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {	
	
	private static class Symbol {
		Symbol(short value, boolean relative) {
			this.value = value;
			this.isRelative = relative;
		}
		
		public String toString() {
			return "(" + this.value + ", " + this.isRelative + ")";
		}
		
		public short value;
		public boolean isRelative;
	}

	private static Map<String, Symbol> symbols = new HashMap<String, Symbol>();

	public static void input(String key, short value, boolean relative) {
		if (symbols.containsKey(key)) {
			//error
		} else {
			symbols.put(key, new Symbol(value, relative));
		}
	}
	
	public static void input(String key) {
		if (symbols.containsKey(key)) {
			//error
		} else {
			symbols.put(key, null);
		}
	}
	
	public static void update(String key, short value, boolean relative) {
		if (!symbols.containsKey(key)) {
			//error
		} else {
			symbols.put(key, new Symbol(value, relative));
		}
	}
	
	public static short getValue(String key) {
		short returnVal = 0;
		if (!symbols.containsKey(key)) {
			//error
		} else {
			returnVal = symbols.get(key).value;
		}
		return returnVal;
	}
	
	public static boolean isRelative(String key) {
		boolean returnVal = false;
		if (!symbols.containsKey(key)) {
			//error
		} else {
			returnVal = symbols.get(key).isRelative;
		}
		return returnVal;
	}
	
	public static void display() {
		System.out.println( "Symbol Table:\n" +
						 	"Format: Mnemonic (value, isRelative)");
		System.out.println(symbols.toString());
	}
}
