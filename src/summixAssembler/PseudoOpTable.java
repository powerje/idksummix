package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class PseudoOpTable {
	private class PseudoOp {
		public short op;
		public short size;
	}
	
	Map<String, PseudoOp> pseudoOps = new HashMap<String, PseudoOp>();
	

	public short getOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (pseudoOps.containsKey(name)) {
			returnVal = pseudoOps.get(name).op;
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
