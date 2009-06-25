package summixSimulator;

import java.io.IOException;

/**
 * The SummiX simulator is the user interface that displays the
 * state of the machine as appropriate and controls execution.
 * 
 * @author Mike/Mike/Don/Jim
 * 
 */

public class Simulator {
	/**
	 * @param args command line arguments args[0] - filename, arg[1] - mode of simulator (quiet, trace, or step)
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SummiX_Machine machine = new SummiX_Machine();
		//new Loader(args[0], machine);
		new Loader("input.txt", machine);	//for testing purposes using hard coded input

	}
}
