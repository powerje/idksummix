package summixSimulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;

/**
 * The SummiX loader puts the input information into data 
 * structures that represent the memory and registers of the 
 * machine.
 * 
 * @author Mike/Mike/Dan/Jim
 * 
 */
public class Loader {
	private BufferedReader br;
	private int init;
	private int length;
	
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
	
	private short getPage(int addr) {
		/**
		 * Gets the upper 7 bits (page) out of the address given by the input.
		 * 
		 * @param addr the complete address given
		 */
		//upper 7 bits (shift right 9)
		return (short) (addr >> 9);
	}
	
	private short getOffset(int addr) {
		/**
		 * Gets the lower 9 bits (offset) out of the address given by the input
		 * 
		 * @param addr the complete address given
		 */
		//lower 9 bits (bitmask out upper 7)
		return (short) (0x1FF & addr);
	}
	
	private void getHeader(SummiX_Machine machine) throws IOException {
		/**
		 * Gets the header information out of the input
		 * 
		 * @param machine the SummiX_Machine to potentially put the header info into
		 */
		String input = this.br.readLine();
		if (input.charAt(0) != 'H') {
			System.out.println("Expected: H");
			System.exit(-1);	//error
		}
		this.init 	= hexstringToInt(input.subSequence(7,  11)); //programs memory begins here
		this.length	= hexstringToInt(input.subSequence(11, 15)); //length of the segment of memory
	}
	
	private void fillMemory(SummiX_Machine machine) throws IOException {
		/**
		 * Reads values from the input and stores them into the machine's memory
		 * 
		 * @param machine the SummiX_Machine to store data in
		 */
		String input = this.br.readLine();
		if (input.charAt(0) != 'T') {
			System.out.println("Expected: T");
			System.exit(-1);	//error
		}		
		while (input.charAt(0) == 'T')	//Text Record
		{
			int addr = hexstringToInt(input.subSequence(1, 5));
			if (addr < this.init) {
				System.out.println("Address given (" + addr + " is less than start address (" + this.init + ")");
				System.exit(-1);
			}
			if (addr > (this.init + this.length)) {
				System.out.println("Address given (" + addr + " is greater than max address (" + (this.init + this.length) + ")");
				System.exit(-1);
			}
			
			int data = hexstringToInt(input.subSequence(5, 9));	
			//store data into machine memory
			machine.setMemory(getPage(addr), getOffset(addr), (short) data);
			input = this.br.readLine();
		}
		//all that is left is the end record which sets the PC
		if (input.charAt(0) != 'E') {
			System.out.println("Expected: E");
			System.exit(-1);	//error
		}
		machine.setPC((short)hexstringToInt(input.subSequence(1,5)));
	}
	
	public Loader(String filename, SummiX_Machine machine) throws IOException {
		this.br = new BufferedReader(new FileReader(filename));	
		getHeader(machine);
		fillMemory(machine);
		this.br.close();
	}
}
