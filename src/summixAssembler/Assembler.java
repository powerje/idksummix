package summixAssembler;

import java.io.IOException;

public class Assembler {
	public static void main(String[] args) throws IOException {
		SymbolTable.input("Test", (short) 10, false);
		SymbolTable.input("2Test", (short) 12, false);
		SymbolTable.input("3Test", (short) 13, true);
		SymbolTable.input("4Test", (short) 14, false);
		SymbolTable.display();
	}
}
