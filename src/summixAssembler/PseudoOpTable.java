package summixAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jim
 *
 */
public class PseudoOpTable {

	/**
	 * 
	 * @author Jim
	 *
	 */
	private static class PseudoOp {
		public short size;			//size of the op code
		public boolean isVariable;	//is the size dependent on the instruction code?
		
		/**
		 * 
		 * @param size the size of the pseudo op to be created
		 * @param isVariable whether or not the size of the pseudo op to be created is variable
		 */
		PseudoOp(int size, boolean isVariable) {
			this.size = (short) size;
			this.isVariable = isVariable;
		}
		
		/**
		 * Returns a String object representing the specified pseudo op. 
		 */
		public String toString() {
			return "(" + this.size + ", " + this.isVariable + ")\n";
		}
	}
	
	private static Map<String, PseudoOp> pseudoOps = new HashMap<String, PseudoOp>();

	/**
	 * Initializes the pseudo op table.
	 */
	public static void initialize() {
		//format:	Mnemonic, size, variability of puesdop size
		pseudoOps.put(".ORIG", new PseudoOp(0, false));
		pseudoOps.put(".END",  new PseudoOp(0, false));
		pseudoOps.put(".EQU",  new PseudoOp(0, false));
		pseudoOps.put(".FILL", new PseudoOp(1, false));
		pseudoOps.put(".STRZ", new PseudoOp(-1, true));
		pseudoOps.put(".BLKW", new PseudoOp(-1, true));
	}
	
	/**
	 * 
	 * @param name the key to search for the pseudo op
	 * @return whether or not the pseudo op is of variable size
	 */
	public static boolean isVariable(String name) {
		return pseudoOps.get(name).isVariable;
	}
	
	/**
	 * 
	 * @param name the key to search for the pseudo op
	 * @return whether or not the given string is a pseudoOp
	 */
	public static boolean isPseudoOp(String name) {
		return pseudoOps.containsKey(name);
	}
	
	/**
	 * 
	 * @param name the key to search for the pseudo op
	 * @return the size of the pseudo op
	 */
	public static short getSize(String name){
		return pseudoOps.get(name).size;
	}

	/**
	 * Display the pseudo op table to the console. Useless method.
	 */
	public static void display() {
		System.out.println( "Pseudo Op Table:\n" +
						 	"Format: Mnemonic (size, variable size)");
		System.out.println(pseudoOps.toString());
	}
}
