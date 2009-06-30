package summixSimulator;

public class SummiX_Utilities {
	public static short getBits(short data, int p, int n) {
		short bitmask = (short) ((short) ~0 << (16 - n));
		int i =  ((int)bitmask >>> n);
		System.out.println(Integer.toHexString((int)bitmask).substring(4,8));
		System.out.println(Integer.toHexString(i));
		return (short)1;
	}
}
