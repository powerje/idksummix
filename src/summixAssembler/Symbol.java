package summixAssembler;

public class Symbol {
	Symbol(short value, boolean relative) {
		this.value = value;
		this.isRelative = relative;
	}
	public short value;
	public boolean isRelative;
}
