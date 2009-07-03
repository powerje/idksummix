package summixSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		STEP,
		ERROR
	}
	
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
				simState = getState(br.readLine().charAt(0));
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
			
		//for STEP or TRACE need to print initial values of the machine, maybe should make this a method call..
		if ((simState == Simulator_State.STEP) || (simState == Simulator_State.TRACE)) {
			System.out.println();
			for (int i=0;i < 8;i++) { //print general registers
				String registerOutput = Integer.toHexString((int)machine.loadRegister(i));
				if (registerOutput.length() > 4) {
					registerOutput = registerOutput.substring(registerOutput.length()-4,registerOutput.length());
				}
				System.out.print("| R" + i + ": 0x" + registerOutput + "\t");
			}
			System.out.print("|\n| PC: 0x" + Integer.toHexString((int)machine.getPC()) + "\t|");
			System.out.println(" CCR: N, " + machine.getN() + "\t| Z, " + machine.getZ() + "\t| P, " + machine.getP() + "\t|");	
		}
		
		while ((!Interpreter.getInstruction(machine, machine.loadMemory(SummiX_Utilities.getBits(machine.getPC(), 0, 7), SummiX_Utilities.getBits(machine.getPC(),7,9))))
				&& (counter < timeOutCounter)) {
			//case select for mode type
			switch (simState) {
			case STEP:
				br.readLine();
			case TRACE:
				//TRACE MODE
				//output ("memory page" and registers)
				System.out.println();
				for (int i=0;i < 8;i++) { //print general registers
					String registerOutput = Integer.toHexString((int)machine.loadRegister(i));
					if (registerOutput.length() > 4) {
						registerOutput = registerOutput.substring(registerOutput.length()-4,registerOutput.length());
					}
					System.out.print("| R" + i + ": 0x" + registerOutput + "\t");
				}
				System.out.print("|\n| PC: 0x" + Integer.toHexString((int)machine.getPC()) + "\t|");
				System.out.println(" CCR: N, " + machine.getN() + "\t| Z, " + machine.getZ() + "\t| P, " + machine.getP() + "\t|");
				//quiet mode gets executed with the boolean in the while
				break;
			}
		counter++;
		}
		if (counter==timeOutCounter) {
			System.out.println("System error: instruction limit exceeded!");
			System.exit(-1);
		}
		/*TODO:
		  output each executed instruction including the memory locations and registers affected or used
		  output ("memory page" and registers)
		*/
	}
}
