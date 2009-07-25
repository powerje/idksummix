package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class PseudoOpTable {

	public class PseudoOp {
		public short size;			//size of the op code
		public boolean isVariable;	//is the size dependant on the instruction code?
		
		PseudoOp(int size, boolean isVariable) {
			this.size = (short) size;
			this.isVariable = isVariable;
		}
		
		public String toString() {
			return "(" + this.size + ", " + this.isVariable + ")";
		}
	}
	
	private static Map<String, PseudoOp> pseudoOps = new HashMap<String, PseudoOp>();
	
	public void initialize() {
		//format:	Mnemonic, size, relativity
		pseudoOps.put(".ORIG", new PseudoOp(0, false));
		pseudoOps.put(".END",  new PseudoOp(0, false));
		pseudoOps.put(".EQU",  new PseudoOp(0, false));
		pseudoOps.put(".FILL", new PseudoOp(1, false));
		pseudoOps.put(".STRZ", new PseudoOp(0, true));
		pseudoOps.put(".BLKW", new PseudoOp(0, true));
	}
	
	public boolean isVariable(String name) {
		boolean returnVal = false;
		
		if (pseudoOps.containsKey(name)) {
			returnVal = pseudoOps.get(name).isVariable;
		} else {
			//error
		}
		return returnVal;
	}
	
	public short getSize(String name){
		short returnVal = 0;
		if (pseudoOps.containsKey(name)) {
			returnVal = pseudoOps.get(name).size;
		}
		return returnVal;
		
	}

}
