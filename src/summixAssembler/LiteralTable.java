package summixAssembler;

import java.util.HashSet;
import java.util.Set;

import summixAssembler.SymbolTable.Symbol;

public class LiteralTable {
	public class Literal {
		private String name;
		private int address;
		
		public String getName() {
			return name;
		}
		
		public int getAddress() {
			return address;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setAddress(int value) {
			this.address = address;
		}
	}
	
	private static Set<Literal> literals = new HashSet<Literal>(5);

}

