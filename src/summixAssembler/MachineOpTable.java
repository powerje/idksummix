package summixAssembler;

import java.util.HashMap;
import java.util.Map;

public class MachineOpTable {
	public class MachineOp {
		public short op;			//4 bit op code
		public short size;			//size of the op code
		
		MachineOp(int op, int size) {
			this.op = (short) op;
			this.size = (short) size;
		}
		
		public String toString() {
			return "(" + this.op + ", " + this.size + ")";
		}
		
	}

	private static Map<String, MachineOp> machineOps = new HashMap<String, MachineOp>();
	
	public void initialize() {
		machineOps.put("ADD", new MachineOp(0x0001, 1));
	}
	
	public short getOp(String name){
		//we want this bitshifted or not?
		short returnVal = 0;
		if (machineOps.containsKey(name)) {
			returnVal = (short) (machineOps.get(name).op << 12);
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
