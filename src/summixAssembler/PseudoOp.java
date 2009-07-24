package summixAssembler;

public class PseudoOp {
	public short op;			//4 bit op code
	public short size;			//size of the op code
	public boolean isVariable;	//is the size dependant on the instruction code?
	
	PseudoOp(short op, short size, boolean isVariable) {
		this.op = op;
		this.size = size;
		this.isVariable = isVariable;
	}
	
	public String toString() {
		return "(" + this.op + ", " + this.size + ", " + this.isVariable + ")";
	}
}