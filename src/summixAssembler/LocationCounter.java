package summixAssembler;

/**
 * 
 * @author Dan Stottlemire
 *
 */

public class LocationCounter {
	
	static int curAddr;
	
	public static boolean relative;
	
	
	/**
	 * Incrementer for the Location Counter
	 * 
	 * @param amount - The value to increment the Location Counter
	 */
	
	public static void incrementAmt(int amount) {
			short startPage = (short) (curAddr >>> 7);
			curAddr = (short)(curAddr + amount);
			short curPage = (short) (curAddr >>> 7);
			if (relative && (!(curPage==startPage))) {
				System.out.println("ERROR: Page rollover with relocatable program.");
			}
		}
	
	public static void incrementAfterLiteral(int amount) {
		short startPage = (short) (curAddr >>> 7);
		curAddr = (short)(curAddr + amount);
		short curPage = (short) (curAddr >>> 7);
		if (!(curPage==startPage)) {
			System.out.println("ERROR: Page rollover when assigning memory to literal.");
		}
		
	}
		

	/**
	 * Setter for the Location Counter
	 * 
	 * @param address - The address which to set the Location Counter.
	 * @param isRelative - A boolean which states if the Address is relative or not.
	 */
	
	public static void set(int address, boolean isRelative){
	
		relative = isRelative;
		
		if (!relative){
		curAddr = address;
		}
		else{
		curAddr = 0;
		}
	}
	
	/**
	 * Getter for the Location Counter
	 * 
	 * @return the current address of the Location Counter.
	 */
	
	public static short getAddress(){
		
		return (short) curAddr;
	}
}

