package summixAssembler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SymbolTable {
	/*
	private class Symbol {
		public String name;
		public int value;
	}
	
	private static Set<Symbol> symbols = new HashSet<Symbol>(5);
	*/
	Map<String, Integer> symbols = new HashMap<String, Integer>();
}
