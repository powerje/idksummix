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
 * @author Mike
 * @author Jim
 *
 */
public class Integrate {

	/** this container holds our assembled TextFile objects */
	static ArrayList<TextFile> objectFiles = new ArrayList<TextFile>();
	/** this container holds our source code TextFile objects */
	static ArrayList<TextFile> sourceFiles = new ArrayList<TextFile>();

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

		//different modes to execute


		//input IPLA
		//loop through to get valid user input, take it in hex
		System.out.print("Please input an IPLA (Initial Program Load Address: ");
		try {
			ipla = br.readLine();
		} catch (IOException e1) {
			System.out.print("ERROR: Integrator is unable to take user input, bailing out.");
			System.exit(0);
		}
		memoryStart = summixSimulator.SummiX_Utilities.hexStringToInt(ipla);


		//use many of the same checks / information from assembler
		/*
		 Valid input from user: link -f add1.txt add2.txt -o add3.o add4.o
		 So we need a method to assemble the arguments for the -f switch
		 */
		while (i < args.length)
		{
			//f switch
			if(args[i].equalsIgnoreCase("-f")) //Check file switch, if it's there, increment and make sure that the filename is next
			{
				fileSwitch = true;
				//while the user chose -f and we're not past the end of arguments entered loop through
				while(fileSwitch && (i < args.length))
				{
					i++;

					if ((i < args.length) && fileSwitch)
					{
						//verify that the next arg isn't the -o identifier.
						if(args[i].equalsIgnoreCase("-o"))
						{
							fileSwitch = false;
							objectSwitch = true;
							i--;
						} else {			
							//clear symbol table, location counter, etc..
							sourceFileName = args[i];
							//create p1file
							TextFile sFile = new TextFile();
							try {
								sFile = new TextFile(sourceFileName);
							} catch (IOException e) {
								System.out.println("Could not open file: " + args[i]);
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
							objectFiles.add(p2File);
						}
					}
					else if(!(args.length > i) && fileSwitch)
					{
						System.out.println("ERROR: Missing [fileName] after the -f switch on command line.");
						fileSwitch = false;
					}
				}
			}
			//o switch
			else if (args[i].equalsIgnoreCase("-o")) {
				objectSwitch = true;
				//in here we need to save these in whatever container we keep our assembled files in to give to the loader
				while ((i < args.length) && objectSwitch) {

					i++;
					if ((i < args.length) && objectSwitch) {
						try {
							if (!args[i].equalsIgnoreCase("-f")) {
								TextFile objectFile = new TextFile(args[i]);
								objectFiles.add(objectFile);	
							} else {
								//fileSwitch
								objectSwitch = false;
								fileSwitch = true;
								i--;
							}
						} catch (IOException e) {
							if (args[i].equalsIgnoreCase("-o")) {
								System.out.println("ERROR: Missing [fileName] after the -o switch on command line.");
								objectSwitch = false;								
							} else {
								System.out.println("Could not open file: " + args[i]);
								System.exit(-1);
							}
						}
					}  
				}
			}
			// bad switch
			else
			{
				System.out.println("ERROR: " + args[i] + " is a malformed switch.");
			}
			i++;
		}

		TextFile finalObj = new TextFile();
		finalObj = Linker.processObjects(objectFiles, memoryStart);
		//do not do this, LinkerPass1.processObjects(objectFiles, memoryStart);

		try {
			finalObj.write("finalObject.o");
		} catch (IOException e) {
			System.out.println("Could not write finalObject.o.");
			e.printStackTrace();
		}

		//pass final TextFile to summixSimulator
		String[] argArray = {"finalObject.o"};
		try {
			summixSimulator.Simulator.main(argArray);
		} catch (IOException e) {
			System.out.println("Could not load finalObject.o.");
			e.printStackTrace();
		}
	}

}
