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
	public Loader(String filename, SummiX_Machine machine) throws FileNotFoundException {
		this.br = new BufferedReader(new FileReader(filename));
	}
}
