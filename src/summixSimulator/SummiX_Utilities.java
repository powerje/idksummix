package summixSimulator;

public class SummiX_Utilities {
	public static short getBits(short data, int p, int n) {
		return (short) ((data >>> (16-p-n)) & ((1 << n)-1));
	}
	
	public static short getAbsoluteBits(short data, int p, int n) {
		short s = (short) ((data >>> (16-p-n)) & ((1 << n)-1));
		return (short) (s << (16-p-n));
	}
	
	public static String shortToHexString(short data) {
		String returnVal = Integer.toHexString((int) data);
		if (returnVal.length() > 4) {
			returnVal = returnVal.substring(returnVal.length() - 4, returnVal.length());
		}
		return "0x" + returnVal.toUpperCase();
	}
	
	private int hexstringToInt(CharSequence input) {
		int returnVal = 0; // needs initialized in the case an exception is caught
		/**
		 * Takes a CharSequence that is a hex number and converts it to an integer.
		 * 
		 * @param input CharSequence to be converted into an int of its hex value
		 */
		//there is a lot of crappy looking casting going on here, is there a better way?
		//should probably check for anything other than hex digits in these CharSequence
		try {
			returnVal = Integer.valueOf((String) input, 16).intValue();
		} catch (NumberFormatException e)	{
			System.out.println("Expected: hex value");
			System.exit(-1); //error
		}
		return returnVal;
	}
	
	
	public enum Simulator_State {
		QUIET,
		TRACE,
		STEP,
		ERROR
	}
	
	public enum InstructionCode{
		ADD,
		ADD2,
		AND,
		AND2,
		BRX,
		DBUG,
		JSR,
		JSRR,
		LD,
		LDI,
		LDR,
		LEA,
		NOT,
		RET,
		ST,
		STI,
		STR,
		OUT,	//TRAP ENUMS THIS LINE AND LOWER
		PUTS,
		IN,
		HALT,
		OUTN,
		INN,
		RND,
		ERR
	}
}
