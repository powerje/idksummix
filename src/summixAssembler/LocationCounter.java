package summixAssembler;

/**
 * 
 * @author Dan Stottlemire
 *
 */

public class LocationCounter {
	
	/** The location counters current address*/
	static private int curAddr;
	
	/** The boolean that states if the program is relocatable or not*/
	private static boolean relative;
	
	
	/**
	 * incrementAmt takes an int (amount) and increments the Location Counter by that given integer amount.
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
	/**
	 * hexstringToShort takes a CharSequence (input), which is a string representing a hex value 
	 * an returns the short value of the corresponding hex value.
	 * 
	 * @param input - the hex string corresponding to the short value needed.
	 */
	
	private static short hexstringToShort(CharSequence input) {
		int returnVal = 0; // needs initialized in the case an exception is caught
		try {
			returnVal = Integer.valueOf((String) input, 16).intValue();
		} catch (NumberFormatException e)	{
			System.out.println("");
			System.exit(-1); //error
		}
		return (short) returnVal;
	}
	
	/**
	 * incrementAfterLiteral takes an int (amount) and increments the Location Counter by that amount. 
	 * The method is used to increment the LC for each literal present in the Literal Table.
	 * The method protects against page roll over for absolute literals.
	 * 
	 * @param amount - the amount to increment the Location Counter.
	 */
	
	public static void incrementAfterLiteral(int amount) {
		short startPage = (short) (curAddr >>> 7);
		curAddr = (short)(curAddr + amount);
		short curPage = (short) (curAddr >>> 7);
		if (!(curPage==startPage)) {
			System.out.println("ERROR: Page rollover when assigning memory to literal.");
		}
		
	}
	
	/**
	 * incrementAfterVarOp takes a Token varOp, which is the pseudo operation, and a Token args, which are the arguments for the 
	 * given psuedo operation, and increments the Location Counter depending on the variable length of the arguments.
	 * 
	 * @param varOp - The variable length pseudo operation.
	 * @param args - The args of the pseudo operation.
	 */
	
	public static void incrementAfterVarOp(Token varOp, Token args){
	
		if (varOp.getText().equals(".BLKW")) // Enter if the variable op is block of words.
		{
			int index1 = args.getText().indexOf('x');
			if (index1 != -1) //arg is a hex value
			{
				short argAmt = hexstringToShort(args.getText().subSequence(index1 + 1, args.getText().length()));			
				LocationCounter.incrementAmt((int) argAmt);
			}
		
			int index2 = args.getText().indexOf('#');
			if (index2 != -1) //arg is a decimal value
			{
				short arg = 0;
			try{
				arg = Short.parseShort(args.getText().substring(index2+1));
			}
			catch(NumberFormatException e) {}
				
				LocationCounter.incrementAmt((int) arg);
			}
		
			if (index1 == -1 && index2 == -1 && args.getType() == TokenType.ALPHA) //arg is a symbol
			{
					short argAmt = SymbolTable.getValue(args.getText());
					LocationCounter.incrementAmt((int) argAmt);
			}
		}
	
		else if (varOp.getText().equals(".STRZ")) // Enter if variable op is .STRZ
		{
			if(args.getType() == TokenType.QUOTE) // arg is not malformed
			{
				String argStr = args.getText();
				int argLength = argStr.length();
				argLength -= 2; //get rid of quotes
				argLength += 1; //add null
			
				LocationCounter.incrementAmt(argLength);
			}
		
			else if (args.getType() == TokenType.ERROR)
			{
				System.out.println("ERROR: Arguments and / or operand for .STRZ is malformed.");
			}
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
	
	/**
	 * returns if the program is relocatable or not
	 * 
	 * @return True if the program is relocatable
	 */
	
	public static boolean isRelative() {
		return relative;
	}
}

