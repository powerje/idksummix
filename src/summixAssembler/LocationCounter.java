package summixAssembler;

public class LocationCounter {
	
	private static short curAddr = 0;
	
	public static void incrementAmt(short amount) {
		
		short offset = (short) ((curAddr << 7) >>> 9);
		//short curPage = (short) ((curAddr >> 9) << 7);
		//short newPage = (short) (((curAddr + amount) >> 9) << 7);

		if (((offset + amount)) >= 511){
			//System.out.print("Error:Location Counter Page Rollover! Previous Page: "+ curPage + "New Page: "+ newPage);
			//curAddr = (short) (curAddr + amount);
		}
		else{
			curAddr = (short) (curAddr + amount);
		}
	}
	
	public static void set(short address){
	
		curAddr = address;
	}
	
	public static short getAddress(){
		
		return curAddr;
	}
}

