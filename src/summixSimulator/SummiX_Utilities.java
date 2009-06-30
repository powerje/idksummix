package summixSimulator;

public class SummiX_Utilities {
	public static short getBits(short data, int p, int n) {
		return (short) ((data >>> (16-p-n)) & ((1 << n)-1));
	}
	
	public enum InstructionCode{
		ADD,
		ADD2,
		AND,
		AND2,
		BRN,
		BRZ,
		BRP,
		BRGE,
		BRLE,
		BRA,
		NOP,
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
		RND
	}
}
