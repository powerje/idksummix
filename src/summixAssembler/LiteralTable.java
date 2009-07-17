package summixAssembler;

import java.util.HashSet;
import java.util.Set;

public class LiteralTable {
	private class Literal {
		public String name;
		public int address;
	}
	
	private static Set<Literal> literals = new HashSet<Literal>(5);

}

