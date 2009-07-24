package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class PseudoOpTable {
	
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
