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
	
	private enum Simulator_State {
		QUIET,
		TRACE,
		STEP
	}
	
	private Simulator_State simState;

	
	public static void main(String[] args) throws IOException {
		/**
		 * Main procedure of the simulator
		 * 
		 * @param args filename mode timeout
		 */
		int timeOutCounter = 1000;
		String fileName = "input.txt";
		
		SummiX_Machine machine = new SummiX_Machine();
		
		//Figure out which args are present
		
		//If (they've entered the filename arg)
			//new fileName = filename arg
		//else prompt for file name
		new Loader(fileName, machine);	//for testing purposes using hard coded input
		
		//If (they've entered the running mode arg)
			//Use the mode arg to set the simState
		//else prompt for running mode
			
		//If (they've entered the timeout arg)
			//Use the timeout arg to set the timeOutCounter
		//else prompt for running mode (default = 1000)
		
		//case select for mode type
		
		//QUIET MODE
		//while (opCode is not Halt and timeout is not exceeded)
			//run interpreter
		
		//TRACE MODE
		//output ("memory page" and register)
		//while (opCode is not Halt and timeout is not exceeded)
			//run interpreter
			//output each executed instruction including the emmory locations and registers affected or used
			//output ("memory page" and registers)
		
		//STEP MODE
		//output ("memory page" and register)
		//while (opCode is not Halt and timeout is not exceeded)
			//prompt user to continue
			//run interpreter
			//output each executed instruction including the emmory locations and registers affected or used
			//output ("memory page" and registers)
		System.out.println((short) (0x7FFF + 0x7FFF));
	}
}
