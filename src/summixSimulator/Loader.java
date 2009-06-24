package summixSimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The SummiX loader puts the input information into data 
 * structures that represent the memory and registers of the 
 * machine.
 * 
 * @author Mike/Mike/Don/Jim
 * 
 */
public class Loader {
	private BufferedReader br;
	private int init;
	private int length;
	
	private int hexstringToInt(CharSequence input) {
		//there is a lot of crappy looking casting going on here, is there a better way?
		return Integer.valueOf((String) input, 16).intValue();
	}
	
	private void getHeader(SummiX_Machine machine) throws IOException {	
		String input = this.br.readLine();
		//later add error checking to look for H and do something with segment name (positions 0-6)

		this.init 	= hexstringToInt(input.subSequence(7,  11)); //initial program load address?  not the pc though, that's specified in the End Record
		this.length	= hexstringToInt(input.subSequence(11, 15));//length of the segment		
	}
	
	private void fillMemory(SummiX_Machine machine) throws IOException {
		String input = this.br.readLine();
		while (input.charAt(0) == 'T')	//Text Record
		{
			int addr = hexstringToInt(input.subSequence(1, 5)); //need to convert addr to page and offset
			int data = hexstringToInt(input.subSequence(5, 9));			
			input = this.br.readLine();
		}
		//all that is left is the end record which sets the PC
		machine.setPC((short)hexstringToInt(input.subSequence(1,5)));
		System.out.println(machine.getPC());
	}
	
	public Loader(String filename, SummiX_Machine machine) throws IOException {
		this.br = new BufferedReader(new FileReader(filename));	
		getHeader(machine);
		fillMemory(machine);
		this.br.close();
	}
}
