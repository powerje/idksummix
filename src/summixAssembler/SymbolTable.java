package summixAssembler;

public class SymbolTable {
	public class Symbol {
		private String name;
		private int value;
		
		public String getName() {
			return name;
		}
		
		public int getValue() {
			return value;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setValue(int value) {
			this.value = value;
		}
	}
}
