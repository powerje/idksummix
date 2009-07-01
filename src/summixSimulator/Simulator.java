package summixSimulator;

import java.io.IOException;
import java.util.Scanner;

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
		Scanner in = new Scanner(System.in);
		
		SummiX_Machine machine = new SummiX_Machine();
		
		//Length of args array indicates args entered (filename == 1, running mode == 2, and timeout == 3) 
		
		//ADD ERROR CHECKING ON ARGS LENGTH HERE
		
		//If (they've entered the filename arg)
		if (args.length > 0)
		{
			fileName = args[0];
			//new fileName = filename arg
		}
		else		//else prompt for file name
		{
			System.out.print("Please enter the input file's name: ");
			fileName = in.next();
		}

		//new Loader(fileName, machine);	//for testing purposes using hard coded input
		
		//If (they've entered the running mode arg)
		if (args.length > 1)
		{
			
		}
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
	}
}
