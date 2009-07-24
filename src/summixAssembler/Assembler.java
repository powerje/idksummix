package summixAssembler;

import java.io.*;


public class Assembler {
	public static void main(String[] args) throws IOException {
		
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
		String sourceFileName = "";
		TextFile sFile = null;
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		
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
				sourceFileName = br.readLine();				
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
		
		//If -d switch is set, dump p1File, symbol table, and literal table to the display with the Dump() helper function

		//Give Pass2 the p1File and then take the return value as p2File

		//Create lFile from sFile (or maybe p1File?), p2File, symbol table, and literal table
		//Use the overloaded constructor of the TextFile to do this
		//Code should look something like this TextFile lFile = new TextFile(sFile, p2File);

		//If -v switch is set, display p2File and lFile

		//Write lFile and p2File to files with some sort of extension. Probably .l and .o respectively. .l is for list and .o is for object

		//Display success message for names of files written, and maybe some info about them like size
	}
}
