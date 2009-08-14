package summixLinker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import summixAssembler.TextFile;

public class Integrate {

	//is this going to be the actual "running class" for this package?
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//Display intro
		 System.out.println(" _____  _____  ___  ___  ___  _____ ______  _   __");
		 System.out.println("|_   _||  ___|/ _ \\ |  \\/  | |_   _||  _  \\| | / /");
		 System.out.println("  | |  | |__ / /_\\ \\| .  . |   | |  | | | || |/ /"); 
		 System.out.println("  | |  |  __||  _  || |\\/| |   | |  | | | ||    \\ ");
		 System.out.println("  | |  | |___| | | || |  | |  _| |_ | |/ / | |\\  \\ ");
		 System.out.println("  \\_/  \\____/\\_| |_/\\_|  |_/  \\___/ |___/  \\_| \\_/ ");
		 System.out.println("                                                 -TOO PRO");
		
		 int i = 0;
		 String sourceFileName = null;
		 String ipla = new String();
		 boolean fileSwitch=false, verboseSwitch=false, dumpSwitch=false;
		 BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		 
		 //different modes to execute
		 
		 
		 //input IPLA
		 
		 System.out.print("Please input an IPLA (Initial Program Load Address: ");
		 try {
			ipla = br.readLine();
		} catch (IOException e1) {
			System.out.print("ERROR: Integrator is unable to take user input, bailing out.");
			System.exit(0);
		}
		
		
		 //use many of the same checks / information from assembler
		
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

			
			boolean goodBuild = false;
			TextFile sFile = null;
			//Take filename from user, and try to make an object from it until you've successfully made one
			//enter multiple file names
			String ans = "y";
			
			//how can I input multiple files while having unique variable names so they don't overwrite each other??
			
			while(ans == "y")
			{
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
							System.out.print("ERROR: Integrator is unable to take user input, bailing out.");
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
				
				//do actual work in here...
				
				/*if we pull 1 file, and then send it to the linker, the linker will link the main file and then an empty file.
				the returned value is then the new "main file." At that point, we can ask for another file, and it goes through a similar process.
				This new file is the "addon" for the linker which will be joined with the original main.  That returned value is the new "main" and we 
				continue until the user is done inputting files.
				
				call Linker(main, addon);
				
				 */
				
				
				System.out.println("Input another file? (y/n)");
				try {
					ans = br.readLine();
				} catch (IOException e) {
					System.out.print("ERROR: Integrator is unable to take user input, bailing out.");
					System.exit(0);
					}
			}
			/*now that the user is done inputting files, we will send the final main TextFile to our simulator from lab1.  This file will be an object file that
			 * is acceptable by the original simulator. 
			 * 
			 * call summixSimulator/simulator
			 * 
			 */
	}
	
}
