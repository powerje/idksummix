package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class MachineOpTable {
	
	private static Map<String, MachineOp> machineOps = new HashMap<String, MachineOp>();
	
	public short getOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (machineOps.containsKey(name)) {
			returnVal = machineOps.get(name).op;
		} else {
			//error
		}
		return returnVal;
	}
	
	public short getShiftedOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (machineOps.containsKey(name)) {
			returnVal =  (short) (machineOps.get(name).op << 12);
		} else {
			//error
		}
		return returnVal;
	}

	public boolean isVariable(String name) {
		boolean returnVal = false;
		
		if (machineOps.containsKey(name)) {
			returnVal = machineOps.get(name).isVariable;
		} else {
			//error
		}
		return returnVal;
	}
	
	public short getSize(String name){
		short returnVal = 0;
		if (machineOps.containsKey(name)) {
			returnVal = machineOps.get(name).size;
		}
		return returnVal;		
	}

}
