package summixAssembler;

public class Symbol {
	Symbol(short value, boolean relative) {
		this.value = value;
		this.isRelative = relative;
	}
	
	public String toString() {
		return "(" + this.value + ", " + this.isRelative + ")";
	}
	
	public short value;
	public boolean isRelative;
}
