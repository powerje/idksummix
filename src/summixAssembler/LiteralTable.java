package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class LiteralTable {
	private static Map<Short, Short> literals = new HashMap<Short, Short>();
	
	public static void input(short key, short addr) {
		if (!literals.containsKey(key)) {
			literals.put(key, addr);
		} else {
			//error
		}
	}
	
	public static void input (String key, String addr) {
		input(Short.parseShort(key),Short.parseShort(addr));
	}
	
	public static void input (short key, String addr) {
		input(key,Short.parseShort(addr));
	}
	
	public static void input (String key, short addr) {
		input(Short.parseShort(key),addr);
	}
	
	public static short getAddress(short key) {
		short returnVal = 0;
		if (literals.containsKey(key)) {
			returnVal = literals.get(key).shortValue();
		} else {
			//error
		}
		return returnVal;
	}
	
	public static void display() {
		System.out.println( "Literal Table:\n" +
						 	"Format: key (address)");
		System.out.println(literals.toString());
	}
	
}

