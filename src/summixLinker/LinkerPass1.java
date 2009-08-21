package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;
import summixAssembler.TextFile;
/**
 * 
 * @author Jim
 * @author Dan
 */
public class LinkerPass1 {
	/** The program load address to keep track of */
	static int PLA;
	
	/**
	 * 
	 * @param objects
	 * @param memoryStart
	 */
	public static void processObjects(ArrayList<TextFile> objects, int memoryStart)
	{
		PLA = memoryStart;
		
		Iterator<TextFile> i = objects.iterator();

		//iterate through each object file and pull their info into the external symbol table
		while (i.hasNext()) {
			TextFile current = i.next();
			processObjectFile(current);
		}
		
		//check final PLA against memoryStart (IPLA) and compare pages 
		checkPage((short)PLA, (short)memoryStart);
		
	}
	
	/**
	 * 
	 * @param code
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
	}

	/**
	 * 
	 * @param line
	 */
	private static void processRelocatableExternalSymbol(String line) {
		String symbolName = line.substring(1, line.indexOf('=')); //everything from the R til the =
		int symbolValue = PLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(line.indexOf('=')+1));	//everything after the =
		ExternalSymbolTable.input(symbolName, (short)symbolValue, false);
	}

	/**
	 * 
	 * @param line
	 */
	private static void processAbsoluteExternalSymbol(String line) {
		String symbolName = line.substring(1, line.indexOf('=')); //everything from the A til the =
		int symbolValue = summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(line.indexOf('=')+1));	//everything after the =
		ExternalSymbolTable.input(symbolName, (short)symbolValue, false);
	}

	/**
	 * 
	 * @param line
	 */
	private static void processHeaderRecord(String line) {
		String programName = line.substring(1,6);
		int programAddr = PLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(7,11));
		int programLength = summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(11,15));
		if (!(line.charAt(15)=='R'))  {	
			//non-relocatable program
			System.out.println("ERROR: Non-relocatable program " + programName + " cannot be linked properly.");
		}
		ExternalSymbolTable.input(programName, (short) programAddr, false);
		PLA += programLength;	//get ready for the next program
	}
	
	/**
	 * 
	 * @param page1
	 * @param page2
	 */
	private static void checkPage(short page1, short page2) {
		page1 = (short) (page1 >>> 7);
		page2 = (short) (page2 >>> 7);
		if (!(page1==page2)) {
			System.out.println("ERROR: Page rollover within program.");
		}
	}
}
