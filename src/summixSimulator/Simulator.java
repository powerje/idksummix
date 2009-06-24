package summixSimulator;

import java.io.FileNotFoundException;

/**
 * The SummiX simulator is the user interface that displays the
 * state of the machine as appropriate and controls execution.
 * 
 * @author Mike/Mike/Don/Jim
 * 
 */

public class Simulator {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		SummiX_Machine machine = new SummiX_Machine();
		//Loader loader = new Loader(args[0], machine);
		Loader loader = new Loader("example_input.txt", machine);
		
	}

}
