package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class PseudoOpTable {
	private class PseudoOp {
		public short op;			//4 bit op code
		public short size;			//size of the op code
		public boolean isVariable;	//is the size dependant on the instruction code?
	}
	
	private static Map<String, PseudoOp> pseudoOps = new HashMap<String, PseudoOp>();
	
	public short getOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (pseudoOps.containsKey(name)) {
			returnVal = pseudoOps.get(name).op;
		} else {
			//error
		}
		return returnVal;
	}
	
	public short getShiftedOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (pseudoOps.containsKey(name)) {
			returnVal =  (short) (pseudoOps.get(name).op << 12);
		} else {
			//error
		}
		return returnVal;
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
