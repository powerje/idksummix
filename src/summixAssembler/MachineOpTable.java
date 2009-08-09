package summixAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * MachineOpTable is a static class that contains the operations and size of
 * the SummiX machine ops.
 * 
 * @author Jim
 *
 */
public class MachineOpTable {
	/**
	 * The MachineOp class stores the bits for an op code and the size of the instruction. 
	 * @author Jim
	 * 
	 */
	private static class MachineOp {
		public short op;			//4 bit op code
		public short size;			//size of the op code
		
		/**
		 * Class used internally by the MachineOpTable class to store the machineops.
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
			return "(" + this.op + ", " + this.size + ")\n";
		}
	}
	
	/**
	 * The machineOps Map is the container that holds the MachineOpTable's concrete
	 * representation.
	 */
	private static Map<String, MachineOp> machineOps = new HashMap<String, MachineOp>();

	/**
	 * Initialize the MachineOpTable
	 */
	public static void initialize() {
		machineOps.put("BR", 	new MachineOp(0x0000, 1));
		machineOps.put("BRN", 	new MachineOp(0x0800, 1));
		machineOps.put("BRZ", 	new MachineOp(0x0400, 1));
		machineOps.put("BRP", 	new MachineOp(0x0200, 1));
		machineOps.put("BRNZ", 	new MachineOp(0x0C00, 1));
		machineOps.put("BRNP", 	new MachineOp(0x0A00, 1));
		machineOps.put("BRZP", 	new MachineOp(0x0600, 1));
		machineOps.put("BRNZP", new MachineOp(0x0E00, 1));
		machineOps.put("ADD", 	new MachineOp(0x1000, 1));
		machineOps.put("LD",	new MachineOp(0x2000, 1));
		machineOps.put("ST", 	new MachineOp(0x3000, 1));
		machineOps.put("JSR",  	new MachineOp(0x4800, 1));
		machineOps.put("JMP",  	new MachineOp(0x4000, 1));
		machineOps.put("AND", 	new MachineOp(0x5000, 1));
		machineOps.put("LDR",	new MachineOp(0x6000, 1));
		machineOps.put("STR", 	new MachineOp(0x7000, 1));
		machineOps.put("DBUG", 	new MachineOp(0x8000, 1));
		machineOps.put("NOT", 	new MachineOp(0x9000, 1));
		machineOps.put("LDI",	new MachineOp(0xA000, 1));
		machineOps.put("STI", 	new MachineOp(0xB000, 1));
		machineOps.put("JSRR", 	new MachineOp(0xC800, 1));
		machineOps.put("JMPR", 	new MachineOp(0xC000, 1));
		machineOps.put("RET", 	new MachineOp(0xD000, 1));
		machineOps.put("LEA", 	new MachineOp(0xE000, 1));
		machineOps.put("TRAP", 	new MachineOp(0xF000, 1));
	}

	/**
	 * Returns the op field of an operation's machine code shifted into position.
	 * 
	 * @param name the key to be found
	 * @return the op code of the machine op given by key of name shifted into its position in an instruction code
	 */
	public static short getOp(String name){
		return machineOps.get(name).op;
	}

	/**
	 * Returns the size of an operation listed in the machine op table
	 * 
	 * @param name the key to be found
	 * @return the size of the MachineOp with the given key of name
	 */
	public static int getSize(String name)
	{
		return machineOps.get(name).size;		
	}
	
	/**
	 * Checks if a string is one of the operations from the machine table
	 * 
	 * @param name String being compared to list of machineOps
	 * @return Returns true if string passed in is the name of a machineOp
	 */
	public static boolean isOp(String name)
	{
		return machineOps.containsKey(name);
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
