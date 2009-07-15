package summixSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import summixSimulator.SummiX_Utilities.Simulator_State;

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
	
	private static Simulator_State getState(char c) {
	
		switch (c) {
		case 'q':
		case 'Q':
			return Simulator_State.QUIET;
		case 's':
		case 'S':
			return Simulator_State.STEP;
		case 't':
		case 'T':
			return Simulator_State.TRACE;
		default:
			return Simulator_State.ERROR;
		}
	}
	
	public static void main(String[] args) throws IOException {
		/**
		 * Main procedure of the simulator
		 * 
		 * @param args filename mode timeout
		 */
		Simulator_State simState = Simulator_State.ERROR;
		int timeOutCounter = 1000;
		int counter = 0;
		String fileName = "input.txt";
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
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
			fileName = br.readLine();
		}
		
		new Loader(fileName, machine);
	
		//If (they've entered the running mode arg)
		if (args.length > 1)
		{
			//Use the mode arg to set the simState
			simState = getState(args[1].charAt(0));
		}
		else 
		{
			while (simState==Simulator_State.ERROR) {
				//else prompt for running mode
				System.out.print("Please enter the simulator mode ([q]uiet, [s]tep, or [t]race: ");
				//maybe change this to produce an error if in.next().Length() > 1 ?
				try {
					simState = getState(br.readLine().charAt(0));
				} catch (StringIndexOutOfBoundsException e) {
					System.out.println("Default to quiet.");		//for testing purposes, we can just make it re-loop for real or set to something else
					simState = Simulator_State.QUIET;
				}
			}
		}
		
		//If (they've entered the timeout arg)
		if (args.length > 2)
		{
			//Use the timeout arg to set the timeOutCounter
			timeOutCounter = Integer.valueOf(args[2], 10).intValue();
		}
		else 
		{
			//else prompt for timeOutCounter (default = 1000)
			System.out.print("Please enter the timeout value [press 'ENTER' for default (1000)] : ");

			String temp = br.readLine();
			
			if (temp.length() > 0) {	
					timeOutCounter = Integer.valueOf(temp, 10).intValue();
			}
		}
	
		machine.setSimState(simState);
		
		//for STEP or TRACE need to print initial values of the machine registers and page of memory
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			machine.outputMemoryPage(machine.getPage());
			machine.outputMachineState();
			System.out.println("\n---");
		}
		
		while ((!Interpreter.getInstruction(machine, machine.loadMemory(SummiX_Utilities.getBits(machine.getPC(), 0, 7), SummiX_Utilities.getBits(machine.getPC(),7,9))))
				&& (counter < timeOutCounter)) {
			if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
				System.out.print("---\n");
			}
			if (simState==Simulator_State.STEP) {
				//require user input in step mode between instructions
				br.readLine();
			}
			counter++;
		}
		if (counter==timeOutCounter) {
			System.out.println("\nSystem error: instruction limit exceeded!");
			System.exit(-1);
		}
		
		if (simState == Simulator_State.STEP) {
			System.out.println("Press enter to continue.");
			br.readLine();
			machine.outputMemoryPage(machine.getPage());
			machine.outputMachineState();
		} else if (simState == Simulator_State.TRACE) {
			machine.outputMemoryPage(machine.getPage());
			machine.outputMachineState();
		}
	}
}
