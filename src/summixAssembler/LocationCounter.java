package summixAssembler;

public class LocationCounter {
	
	private static int curAddr = 0;
	
	public static void incrementAmt(short amount) {
			curAddr = ((short)curAddr + amount);
		}

	public static void set(short address){
	
		curAddr = address;
	}
	
	public static short getAddress(){
		
		return (short) curAddr;
	}
}

