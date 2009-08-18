package summixLinker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import summixAssembler.*;

import summixAssembler.TextFile;

public class Integrate {
	
	/** this container holds our assembled TextFile objects */
	static ArrayList<TextFile> objectFiles = new ArrayList<TextFile>();
	/** this container holds our source code TextFile objects */
	static ArrayList<TextFile> sourceFiles = new ArrayList<TextFile>();
	
	
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
		 boolean fileSwitch=false, objectSwitch=false;
		 
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
				if(args[i].equalsIgnoreCase("-f")) //Check file switch, if it's there, increment and make sure that the filename is next
				{
					fileSwitch = true;
					while(fileSwitch)
					{
						i++;
						//verify that the next arg isn't the -o identifier.
						if(args[i].equalsIgnoreCase("-o"))
						{
							fileSwitch = false;
						}
						
						if ((args.length > i) && fileSwitch)
						{
							//clear symbol table, location counter, etc..
							
							sourceFileName = args[i];
							//create p1file
							TextFile sFile = new TextFile();
							try {
								sFile = new TextFile(sourceFileName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							TextFile p1File = new TextFile();
							Pass1 pass1 = new Pass1(sFile);
							p1File = pass1.processFile();
							
							//create p2file
							TextFile p2File = new TextFile();
							Pass2 pass2 = new Pass2(p1File);
							p2File = pass2.processFile();
							
							//add assembled object to object files
							objectFiles.add(p2File);  //DOES THIS ADD AT THE BEGINNING, END, WHAT?
						}
						else if(!(args.length > i) && fileSwitch)
						{
							System.out.println("ERROR: Missing [fileName] after the -f switch on command line.");
							fileSwitch = false;
						}
					}
				}
				else if (args[i].equalsIgnoreCase("-o")) {
					//in here we need to save these in whatever container we keep our assembled files in to give to the loader
					boolean foundOne = false;
					objectSwitch = true;
					i++;
				
					//verify that the next arg isn't the -f identifier.
					if(args[i].equalsIgnoreCase("-f"))
					{
						objectSwitch = false;
					}
					
					while (args.length > i) {
						foundOne = true;
						try {
							TextFile objectFile = new TextFile(args[i]);
							objectFiles.add(objectFile);
							i++;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(args[i].equalsIgnoreCase("-s")) 
				{
					//sim mode
				}				
				else
				{
					System.out.println("ERROR: " + args[i] + "is a malformed switch.");
				}
				i++;
			}
	
			LinkerPass1.processObjects(objectFiles, memoryStart);
			
			//pass final TextFile to summixSimulator
			
			
	}
	
}
