package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;
import summixAssembler.TextFile;
/**
 * LinkerPass1 contains the first pass of the summix linker/loader.  Its main function is to populate the
 * external symbol table and check for page overflow within the program.
 * @author Jim
 * @author Dan
 */
public class LinkerPass1 {
	/** The program load address to keep track of */
	static int PLA;
	/** The length of the current program being processed */
	static int programLength;
	
	/**
	 * Process all object files sent in an ArrayList and fills the external symbol table based on them
	 * @param objects ArrayList of object files
	 * @param memoryStart the first location in memory for the final program to run
	 */
	public static void processObjects(ArrayList<TextFile> objects, int memoryStart)
	{
		PLA = memoryStart;
		
		Iterator<TextFile> i = objects.iterator();

		//iterate through each object file and pull their info into the external symbol table
		while (i.hasNext()) {
			TextFile current = i.next();
			current.reset();
			processObjectFile(current);
		}
		
		//check final PLA against memoryStart (IPLA) and compare pages 
		checkPage((short)PLA, (short)memoryStart);
		
	}
	
	/**
	 * Process individual object file
	 * @param code TextFile that contains the data in the object file
	 */
	private static void processObjectFile(TextFile code) {
		while (!code.isEndOfFile()) {
			String line = code.getLine();
			if (line.charAt(0)=='H') {
				processHeaderRecord(line);
			} else if (line.charAt(0)=='A') {
				processAbsoluteExternalSymbol(line);
			} else if (line.charAt(0)=='R') {
				processRelocatableExternalSymbol(line);
			}
		}
		PLA += programLength;	//get ready for the next program
	}

	/**
	 * Processes all relocatable external symbols
	 * @param line string containing data on the relocatable symbol
	 */
	private static void processRelocatableExternalSymbol(String line) {
		String symbolName = line.substring(1, line.indexOf('=')); //everything from the R til the =
		int symbolValue = PLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(line.indexOf('=')+1));	//everything after the =
		ExternalSymbolTable.input(symbolName, (short)symbolValue, false);
	}

	/**
	 * Processes all absolute external symbols
	 * @param line string containing data on the absolute symbol
	 */
	private static void processAbsoluteExternalSymbol(String line) {
		String symbolName = line.substring(1, line.indexOf('=')); //everything from the A til the =
		int symbolValue = summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(line.indexOf('=')+1));	//everything after the =
		ExternalSymbolTable.input(symbolName, (short)symbolValue, false);
	}

	/**
	 * Processes header records for each object file
	 * @param line string containing header record
	 */
	private static void processHeaderRecord(String line) {
		String programName = line.substring(1,7);
		int programAddr = PLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(7,11));
		programLength = summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(11,15));
		if (!(line.charAt(15)=='R'))  {	
			//non-relocatable program
			System.out.println("ERROR: Non-relocatable program " + programName + " cannot be linked properly.");
		}
		ExternalSymbolTable.input(programName, (short) programAddr, false);
	}
	
	/**
	 * Checks two addresses to ensure they are on the same page
	 * @param page1 the first address
	 * @param page2 the second address
	 */
	private static void checkPage(short page1, short page2) {
		page1 = (short) (page1 >>> 7);
		page2 = (short) (page2 >>> 7);
		if (!(page1==page2)) {
			System.out.println("ERROR: Page rollover within program.");
		}
	}
}
