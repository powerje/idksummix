package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;

import summixAssembler.SymbolTable;
import summixAssembler.TextFile;
/**
 * Pass two used to link together the object files that make up a program.
 * Pass two uses the symbol table readied and validated by pass 1 to process pass 2.
 * It adjusts the memory addresses for each instruction in the object files before they're loaded in to the final program file to account for linkage to other objects.
 * Will print out an error of any error lines are detected in the object files.
 * @author Michael Pinnegar
 *
 */
public class LinkerPass2 {

	/** Final linked program file. */
	private static TextFile finalObjectFile = new TextFile();
	/** Program name of the current source code file being worked on.*/
	private static String programName;
	/** Start of execution for the final program file.*/
	private static String startAddress;
	/** Size of the final program file.*/
	private static short programSize;
	/** Whether or not the first header entry for the main source code has been processed.*/
	private static boolean processedFirstHeader;
	/** Whether or not the first end entry for the main source code has been processed.*/
	private static boolean processedFirstEnd;
	/** Finalized header record for the final program file.*/
	private static String finalObjectHeader;

	/**
	 * Takes any arrayList of compiled object files. Translates them into a single program file that is then returned.
	 * @param objects Objects to be linked together into a single program file.
	 * @return Linked program file ready to be run.
	 */
	public static TextFile processObjects(ArrayList<TextFile> objects)
	{
		Iterator<TextFile> objectCycler = objects.iterator();

		while(objectCycler.hasNext())
		{
			TextFile temp = objectCycler.next();
			temp.reset();
			processObjectFile(temp);
		}

		finalObjectHeader += ExternalSymbolTable.shortToHexStringNoPrefix(programSize);
		finalObjectFile.insertLine(0, finalObjectHeader);
		finalObjectFile.input("E" + startAddress);
		finalObjectFile.display();
		return finalObjectFile;
	}

	/**
	 * Processes every line of an object file one by one.
	 * @param unlinkedObject Object file to be linked into the program file.
	 */
	private static void processObjectFile(TextFile unlinkedObject)
	{
		boolean foundEnd = false;

		while(!unlinkedObject.isEndOfFile() && !foundEnd)
		{
			foundEnd = processAnyLine(unlinkedObject.getLine());
		}
	}

	/**
	 * Processes a header record, error record, end record, or text record, adding appropriate entries to the final program file.
	 * @param line Record to be processed and added to the linked program file.
	 * @return Returns true if the end record has been processed.
	 */
	private static boolean processAnyLine(String line)
	{
		boolean foundEnd = false;

		if (line.startsWith("H"))
		{
			programName = line.substring(1, 7);
			programSize += Short.parseShort(line.substring(11, 15), 16);

			if(!processedFirstHeader)
			{
				processedFirstHeader = true;
				finalObjectHeader = "H" + programName + ExternalSymbolTable.shortToHexStringNoPrefix((ExternalSymbolTable.getValue(line.substring(1, 7)))); //Add in H[program name][start address] still need to add the size
			}

		}
		else if(line.startsWith("T"))
		{

			short front = (short) (ExternalSymbolTable.getValue(programName) + Short.parseShort(line.substring(1, 5), 16)); //First four after T
			short back = (short)Integer.parseInt(line.substring(5, 9), 16); //Last four after T
			short temp = 0;

			if (line.length() == 11) //Line has M0 or M1 record, but no X record
			{
				if (line.charAt(10) == '0') //M0
				{
					temp = ExternalSymbolTable.getValue(programName);
					temp &= 0x1FF;
					back += temp;
				}
				else //M1
				{
					temp = ExternalSymbolTable.getValue(programName);
					back += temp;
				}
			}
			else if (line.length() > 11) //Line has M0/M1 record, AND has an X record
			{
				if (line.charAt(10) == '0') //M0
				{
					temp = ExternalSymbolTable.getValue(line.substring(12));
					temp &= 0x1FF;
					back += temp;
				}
				else //M1
				{
					temp = ExternalSymbolTable.getValue(line.substring(12));
					back += temp;
				}				
			}
			//else Line has neither M0/M1/X record

			finalObjectFile.input("T" + SymbolTable.shortToHexStringNoPrefix(front) + SymbolTable.shortToHexStringNoPrefix(back));
		}
		else if(line.startsWith("E"))
		{
			if (!processedFirstEnd)
			{
				processedFirstEnd = true;
				startAddress =  ExternalSymbolTable.shortToHexStringNoPrefix((short)((int)ExternalSymbolTable.getValue(programName) + Integer.parseInt(line.substring(1), 16)));
			}
			foundEnd = true;
		}
		else if(line.startsWith(";"))
		{
			System.out.println("ERROR: Object file for program " + programName + " has an error in it.");
		}
		return foundEnd;

	}
}
