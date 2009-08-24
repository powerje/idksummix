package summixLinker;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import summixAssembler.*;

import summixAssembler.TextFile;
/**
 * The integrate class combines the summixAssembler and summixSimulator with the summixLinker to
 * create one program from one or more source files and run them in the simulator.
 * @author Mike Irwin
 * @author Jim Power
 *
 */
public class Integrate {

	/** this container holds our assembled TextFile objects */
	private static ArrayList<TextFile> objectFiles = new ArrayList<TextFile>();

	/**
	 * Takes command line arguments from the user, calls the assembler to assemble
	 * source code files, creates an arraylist of object files to send to the linker,
	 * and calls the simulator to run the final program.
	 * @param args string array of the command line arguments given by the user
	 */
	public static void main(String[] args) {

		//Display intro
		System.out.println(" _____  _____  ___  ___  ___  _____ ______  _   __");
		System.out.println("|_   _||  ___|/ _ \\ |  \\/  | |_   _||  _  \\| | / /");
		System.out.println("  | |  | |__ / /_\\ \\| .  . |   | |  | | | || |/ /"); 
		System.out.println("  | |  |  __||  _  || |\\/| |   | |  | | | ||    \\ ");
		System.out.println("  | |  | |___| | | || |  | |  _| |_ | |/ / | |\\  \\ ");
		System.out.println("  \\_/  \\____/\\_| |_/\\_|  |_/  \\___/ |___/  \\_| \\_/ ");
		System.out.println("                                                 -TOO PRO");

		int i = 0, memoryStart;
		String sourceFileName = null;
		String ipla = new String();
		boolean fileSwitch=false,objectSwitch=false;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		//check to see if user entered any arguments
		if (args.length==0) {
			System.out.println("ERROR: Must input arguments in the form: -f [sourceFile] -o [objectFile]");
			System.exit(-1);
		}

		/*
		 Valid input from user: link -f add1.txt add2.txt -o add3.o add4.o
		 So we need a method to assemble the arguments for the -f switch
		 */
		while (i < args.length)
		{
			if (fileSwitch && (i < args.length)) {
				//verify that the next arg isn't the -o identifier.
				if(args[i].equalsIgnoreCase("-o")) {
					fileSwitch = false;
					objectSwitch = true;
				} else {			
					//clear symbol table, location counter, etc..
					sourceFileName = args[i];
					//create p1file
					TextFile sFile = new TextFile();
					try {
						sFile = new TextFile(sourceFileName);
					} catch (IOException e) {
						System.out.println("ERROR: Could not open file: " + args[i]);
						System.exit(-1);
					}

					//init tables
					summixAssembler.MachineOpTable.initialize();
					summixAssembler.PseudoOpTable.initialize();

					//create p1File
					Pass1 pass1 = new Pass1(sFile);
					TextFile p1File = pass1.processFile();

					//create p2File
					Pass2 pass2 = new Pass2(p1File);
					TextFile p2File = pass2.processFile();

					//add to the end of the arrayList
					try {
						p2File.write(args[i] + ".o");
					} catch (IOException e) {
						System.out.println("ERROR: Could not write file: " + args[i] + ".o");
						System.exit(-1);
					}
					objectFiles.add(p2File);
				}
			} else if (objectSwitch && (i < args.length)) {
				if (args[i].equalsIgnoreCase("-f")) {
					objectSwitch = false;
					fileSwitch = true;		
				} else {
					TextFile objectFile;
					try {
						objectFile = new TextFile(args[i]);
						objectFiles.add(objectFile);
					} catch (IOException e) {
						System.out.println("ERROR: File not found: " + args[i]);
						System.exit(-1);
					}	
				}
			} 

			if (args[i].equalsIgnoreCase("-o")) {
				objectSwitch = true;
				fileSwitch = false;
				//if no arguments after -o
				if (i==(args.length-1)) {
					System.out.println("ERROR: Missing [Filename] in -o switch.");
					System.exit(-1);
				}
			} else if (args[i].equals("-f")) {
				objectSwitch = false;
				fileSwitch = true;
				if (i==(args.length-1)) {
					System.out.println("ERROR: Missing [Filename] in -f switch.");
					System.exit(-1);
				}
			} 
			i++;
		}

		
		//get start of memory from user
	    //loop through to get valid user input, take it in hex
		System.out.print("Please input an IPLA (Initial Program Load Address): ");
		try {
			ipla = br.readLine();
		} catch (IOException e1) {
			System.out.print("ERROR: Integrator is unable to take user input, bailing out.");
			System.exit(0);
		}
		memoryStart = summixSimulator.SummiX_Utilities.hexStringToInt(ipla);
		
		
		//create finalObject.o
		TextFile finalObj = new TextFile();
		finalObj = Linker.processObjects(objectFiles, memoryStart);
	
		try {
			finalObj.write("finalObject.o");
		} catch (IOException e) {
			System.out.println("ERROR: Could not write finalObject.o.");
			e.printStackTrace();
		}

		//pass final TextFile to summixSimulator
		String[] argArray = {"finalObject.o"};
		try {
			summixSimulator.Simulator.main(argArray);
		} catch (IOException e) {
			System.out.println("ERROR: Could not load finalObject.o.");
			e.printStackTrace();
		}
	}
}

