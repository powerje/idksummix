package summixAssembler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LiteralTable {
	Map<Short, Short> literals = new HashMap<Short, Short>();
	
	public void input(short key, short addr) {
		if (!literals.containsKey(key)) {
			literals.put(key, addr);
		} else {
			//error
		}
	}
	
	public short getAddress(short key) {
		short returnVal = 0;
		if (literals.containsKey(key)) {
			returnVal = literals.get(key).shortValue();
		} else {
			//error
		}
		return returnVal;
	}
	
	public void display() {
		System.out.println(literals.toString());
	}
	
}

