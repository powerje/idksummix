package summixAssembler;

import java.io.*;

/**
 * Assembler for SummiX source code.
 * 
 * This provides the main entry into the IDKSummiX assembler. Flow of control is determined by the Assembler class.
 * 
 * @author Michael Pinnegar
 *
 */
public class Assembler {
	
	/**
	 * 
	 * @param args	-d  DUMP MODE: Turns on a screen dump of the intermidate files between pass 1 and 2
	 * @param args  -f [fileName] FILENAME: Takes in the filename of the source code from the command line
	 * @param args  -v VERBOSE MODE: Displays the list and object files to the console before they are written to disk
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
		
		//Command line args
		//-d (turns on the dump between passes)
		//-f [fileName](lets the user input the fileName variable from the command line)
		//-v (verbose; means that the program outputs the [p2File] and [lFile] to the console before writing them to the files)
		boolean dumpSwitch = false;
		boolean fileSwitch = false;
		boolean verboseSwitch = false;
		boolean goodBuild = false;
		boolean objectWritten = false;
		boolean listWritten = false;
		String sourceFileName = "";
		TextFile sFile = null;
		TextFile p1File = null;
		TextFile p2File = null;
		TextFile lFile = new TextFile();
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		//init tables
		PseudoOpTable.initialize();
		MachineOpTable.initialize();
		
		//Figure out which arguments have been passed by the user, and store them as booleans Store the fileName as a string
		while (i < args.length)
		{
			if(args[i].equalsIgnoreCase("-f")) //Check file switch, if it's there, increment and make sure that the filename is next
			{
				fileSwitch = true;
				i++;
				
				if (args.length > i)
				{
					sourceFileName = args[i];
				}
				else
				{
					System.out.println("ERROR: Missing [fileName] after the -f switch on command line.");
					fileSwitch = false;
				}
			}
			else if(args[i].equalsIgnoreCase("-v")) 
			{
				verboseSwitch = true;
			}
			else if(args[i].equalsIgnoreCase("-d"))
			{
				dumpSwitch = true;
			}
			else
			{
				System.out.println("ERROR: " + args[i] + "is a malformed switch.");
			}
			i++;
		}

		
		//Take filename from user, and try to make an object from it until you've successfully made one
		while (!goodBuild)
		{
			if (fileSwitch)
			{
				fileSwitch = false;
			}
			else
			{
				System.out.print("Please input the file name of the source file:");
				try {
					sourceFileName = br.readLine();
				} catch (IOException e) {
					System.out.print("ERROR: Assembler is unable to take user input, bailing out.");
					System.exit(0);
				}				
			}

			//Create sFile from the user's fileName file
			try{
				sFile = new TextFile(sourceFileName);
				goodBuild = true;
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
		//Give Pass1 the sFile as a parameter, and then take the return value as p1File
		Pass1 pass1 = new Pass1(sFile);
		p1File = pass1.processFile();
		
		//If -d switch is set, dump p1File, symbol table, and literal table to the display with the Dump() helper function
		if (dumpSwitch)
		{
			System.out.println("Diagonistic dump mode has been enabled. Displaying intermediate file, symbol table, and literal table.");
			System.out.println("");
			p1File.display();
			System.out.println("");
			SymbolTable.display();
			System.out.println("");
			LiteralTable.display();
			System.out.println("");
		}

		//Give Pass2 the p1File and then take the return value as p2File
		Pass2 pass2 = new Pass2(p1File);
		p2File = pass2.processFile();
		
		p2File.display();
		//Create lFile from sFile, p2File, symbol table, and literal table
		lFile = makelFile(sFile, p2File);
		
		//If -v switch is set, display p2File and lFile before write
		if (verboseSwitch)
		{
			System.out.println("Verbose mode has been enabled. Displaying list file, and object file.");
			System.out.println("");
			lFile.display();
			System.out.println("");
			p2File.display();
			System.out.println("");
		}
		
		//Write lFile and p2File to files with extensions .l and .o respectively. .l is for list and .o is for object
		try {
			lFile.write(sourceFileName.concat(".l"));
			listWritten = true;
		}
		catch(IOException e)
		{
			System.out.println(e);
			
		}
	
		try {
			p2File.write(sourceFileName.concat(".o"));
			objectWritten = true;
		}
		catch(IOException e)
		{
			System.out.println(e);
		}

		
		//Display success message for names of files written, and maybe some info about them like size
		if (listWritten)
		{
			System.out.println(sourceFileName.concat(".l") + " was written successfully.");
		}
		else
		{
			System.out.println(sourceFileName.concat(".l") + " was not written successfully.");	
		}
		
		if (objectWritten)
		{
			System.out.println(sourceFileName.concat(".o") + " was written successfully.");
		}
		else
		{
			System.out.println(sourceFileName.concat(".o") + " was not written successfully.");	
		}
		try {br.close();} catch (IOException e) {};
	}

/**
 * 	Processes the list file and returns it 
 * @param sFile	Source code file 
 * @param p2File pass2 intermediate file
 * @return	listing file
 */
	private static TextFile makelFile(TextFile sFile, TextFile p2File)
	{		
		ListFile list = new ListFile();
		TextFile lFile = new TextFile();
		
		lFile = list.CreateListFile(sFile, p2File);
		
		return lFile;
	}
}
