package summixAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jim
 *
 */
public class MachineOpTable {
	/**
	 * 
	 * @author Jim
	 * 
	 */
	private static class MachineOp {
		public short op;			//4 bit op code
		public short size;			//size of the op code
		
		/**
		 * 
		 * @param op the op code of the machine op
		 * @param size the size (in words of memory) of the machine op
		 */
		MachineOp(int op, int size) {
			this.op = (short) op;
			this.size = (short) size;
		}

		/**
		 * Returns a String object representing the specified machine op.  
		 */
		public String toString() {
			return "(" + this.op + ", " + this.size + ")";
		}
	}

	private static Map<String, MachineOp> machineOps = new HashMap<String, MachineOp>();

	/**
	 * Initialize the MachineOpTable
	 */
	public static void initialize() {
		machineOps.put("BR", 	new MachineOp(0, 1));
		machineOps.put("BRA", 	new MachineOp(0, 1));
		machineOps.put("BRN", 	new MachineOp(0, 1));
		machineOps.put("BRZ", 	new MachineOp(0, 1));
		machineOps.put("BRP", 	new MachineOp(0, 1));
		machineOps.put("BRNZ", 	new MachineOp(0, 1));
		machineOps.put("BRNP", 	new MachineOp(0, 1));
		machineOps.put("BRZP", 	new MachineOp(0, 1));
		machineOps.put("BRNZP", new MachineOp(0, 1));
		machineOps.put("ADD", 	new MachineOp(1, 1));
		machineOps.put("LD",	new MachineOp(2, 1));
		machineOps.put("ST", 	new MachineOp(3, 1));
		machineOps.put("JSR",  	new MachineOp(4, 1));
		machineOps.put("JMP",  	new MachineOp(4, 1));
		machineOps.put("AND", 	new MachineOp(5, 1));
		machineOps.put("LDR",	new MachineOp(6, 1));
		machineOps.put("STR", 	new MachineOp(7, 1));
		machineOps.put("DBUG", 	new MachineOp(8, 1));
		machineOps.put("NOT", 	new MachineOp(9, 1));
		machineOps.put("LDI",	new MachineOp(10, 1));
		machineOps.put("STI", 	new MachineOp(11, 1));
		machineOps.put("JSRR", 	new MachineOp(12, 1));
		machineOps.put("JMPR", 	new MachineOp(12, 1));
		machineOps.put("RET", 	new MachineOp(13, 1));
		machineOps.put("LEA", 	new MachineOp(14, 1));
		machineOps.put("TRAP", 	new MachineOp(15, 1));
	}

	/**
	 * 
	 * @param name the key to be found
	 * @return the op code of the machine op given by key of name shifted into its position in an instruction code
	 */
	public static short getOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (machineOps.containsKey(name)) {
			returnVal = (short) (machineOps.get(name).op << 12);
		} else {
			//error
		}
		return returnVal;
	}

	/**
	 * 
	 * @param name the key to be found
	 * @return the size of the MachineOp with the given key of name
	 */
	public static int getSize(String name){
		int returnVal = 0;
		if (machineOps.containsKey(name)) {
			returnVal = machineOps.get(name).size;
		}
		return returnVal;		
	}

	/**
	 * Prints the machine op table to the display
	 */
	public static void display() {
		System.out.println( "Machine Op Table:\n" +
						 	"Format: Mnemonic (op, size)");
		System.out.println(machineOps.toString());
	}

}
