package summixSimulator;

public class SummiX_Utilities {
	public static short getBits(short data, int p, int n) {
		return (short) ((data >>> (16-p-n)) & ((1 << n)-1));
	}
}
