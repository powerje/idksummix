package summixSimulator;

import java.io.IOException;

/**
 * The SummiX simulator is the user interface that displays the
 * state of the machine as appropriate and controls execution.
 * 
 * @author Mike/Mike/Dan/Jim
 * 
 */

public class Simulator {
	/**
	 * @param args command line arguments args[0] - filename, arg[1] - mode of simulator (quiet, trace, or step)
	 * @throws IOException 
	 */
	
	private enum Simulator_States {
		QUIET,
		TRACE,
		STEP
	}
	
	private int simState;
	
	public static void main(String[] args) throws IOException {
		/**
		 * Main procedure of the simulator
		 * 
		 * @param args filename mode timeout
		 */
		SummiX_Machine machine = new SummiX_Machine();
		//new Loader(args[0], machine);
		new Loader("input.txt", machine);	//for testing purposes using hard coded input
		short a = SummiX_Utilities.getBits((short)0xd000,0, 4);
		System.out.println(Integer.toHexString(a));
	}
}
