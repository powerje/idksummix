package summixAssembler;

import java.util.HashSet;
import java.util.Set;

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
		
		public void setAddress(int address) {
			this.address = address;
		}
	}
	
	private static Set<Literal> literals = new HashSet<Literal>(5);

}

