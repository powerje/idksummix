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
	
	public static void incrementAfterLiteral(int amount) {
		short startPage = (short) (curAddr >>> 7);
		curAddr = (short)(curAddr + amount);
		short curPage = (short) (curAddr >>> 7);
		if (!(curPage==startPage)) {
			System.out.println("ERROR: Page rollover when assigning memory to literal.");
		}
		
	}
	
	public static void incrementAfterVarOp(Token varOp, Token args){
	
		if (varOp.getText().equals(".BLKW")) // Enter if the variable op is block of words.
		{
			int index1 = args.getText().indexOf('x');
			if (index1 != -1) //arg is a hex value
			{
				short argAmt = 0;
				
			try{
				argAmt = hexstringToShort(args.getText().subSequence(index1 + 1, args.getText().length()));
			}
			catch(NumberFormatException e)	{}
			
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
				if(!(SymbolTable.isDefined(args.getText())))
				{
					System.out.println("ERROR: Symbol used for .BLKW operand was not previously defined.");
				}
				else
				{
					short argAmt = SymbolTable.getValue(args.getText());
					LocationCounter.incrementAmt((int) argAmt);
				}
			}
			else
			{
				System.out.println("ERROR: Arguments for .BLKW are INVALID or MALFORMED!");

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
	
	public static boolean isRelative() {
		return relative;
	}
}

