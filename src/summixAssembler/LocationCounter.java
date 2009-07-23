package summixAssembler;

public class LocationCounter {
	
	private static short addr = 0;
	
	public void incrementAmt(short amount) {
		
		addr = (short) (addr + amount);
	}
	
	public void set(short address){
	
		addr = address;
	}
	
	public short getAddress(){
		
		return addr;
	}

}