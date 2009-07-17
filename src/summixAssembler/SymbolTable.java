package summixAssembler;

import java.util.HashSet;
import java.util.Set;

public class SymbolTable {
	public class Symbol {
		private String name;
		private int value;
		
		public String getName() {
			return name;
		}
		
		public int getValue() {
			return value;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setValue(int value) {
			this.value = value;
		}
	}
	
	private static Set<Symbol> symbols = new HashSet<Symbol>(5);
	
}
