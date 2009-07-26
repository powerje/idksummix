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
			return "(" + this.size + ", " + this.isVariable + ")";
		}
	}
	
	private static Map<String, PseudoOp> pseudoOps = new HashMap<String, PseudoOp>();

	/**
	 * Initializes the pseudo op table.
	 */
	public static void initialize() {
		//format:	Mnemonic, size, relativity
		pseudoOps.put(".ORIG", new PseudoOp(0, false));
		pseudoOps.put(".END",  new PseudoOp(0, false));
		pseudoOps.put(".EQU",  new PseudoOp(0, false));
		pseudoOps.put(".FILL", new PseudoOp(1, false));
		pseudoOps.put(".STRZ", new PseudoOp(0, true));
		pseudoOps.put(".BLKW", new PseudoOp(0, true));
	}
	
	/**
	 * 
	 * @param name the key to search for the pseudo op
	 * @return whether or not the pseudo op is of variable size
	 */
	public static boolean isVariable(String name) {
		boolean returnVal = false;
		
		if (pseudoOps.containsKey(name)) {
			returnVal = pseudoOps.get(name).isVariable;
		} else {
			//error
		}
		return returnVal;
	}
	
	/**
	 * 
	 * @param name the key to search for the pseudo op
	 * @return the size of the pseudo op
	 */
	public static short getSize(String name){
		short returnVal = 0;
		if (pseudoOps.containsKey(name)) {
			returnVal = pseudoOps.get(name).size;
		} else {
			//error
		}
		return returnVal;
		
	}

	/**
	 * Display the pseudo op table to the console.
	 */
	public static void display() {
		System.out.println( "Pseudo Op Table:\n" +
						 	"Format: Mnemonic (size, variable size)");
		System.out.println(pseudoOps.toString());
	}
}
