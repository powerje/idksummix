package summixAssembler;

/**
 * 
 * @author Dan Stottlemire
 *
 */

public class LocationCounter {
	
	static int curAddr;
	
	/**
	 * Incrementer for the Location Counter
	 * 
	 * @param amount - The value to increment the Location Counter
	 */
	
	public static void incrementAmt(int amount) {
			curAddr = (short)(curAddr + amount);
		}
	
	// public static void incrementAfterLiteral(int amount){
		
	//} I do not think we need this.

	/**
	 * Setter for the Location Counter
	 * 
	 * @param address - The address which to set the Location Counter.
	 * @param isRelative - A boolean which states if the Address is relative or not.
	 */
	
	public static void set(int address, boolean isRelative){
	
		if (!isRelative){
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

