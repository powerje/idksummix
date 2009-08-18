package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;
import summixAssembler.TextFile;
import summixSimulator.SummiX_Utilities;

public class LinkerPass1 {
	static int programLength;
	static int IPLA;
	static int PLA;
	
	public static void processObjects(ArrayList<TextFile> objects, int memoryStart)
	{
		IPLA = memoryStart;
		PLA = IPLA;
		
		Iterator<TextFile> i = objects.iterator();

		//iterate through each object file and pull their info into the external symbol table
		while (i.hasNext()) {
			TextFile current = i.next();
			processObjectFile(current);
		}
	}
	
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

	private static void processRelocatableExternalSymbol(String line) {
		String symbolName = line.substring(1, line.indexOf('=') - 1); //everything from the R til the =
		int symbolValue = PLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(line.indexOf('=')+1));	//everything after the =
		ExternalSymbolTable.input(symbolName, (short)symbolValue, false);
	}

	private static void processAbsoluteExternalSymbol(String line) {
		String symbolName = line.substring(1, line.indexOf('=') - 1); //everything from the A til the =
		int symbolValue = summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(line.indexOf('=')+1));	//everything after the =
		ExternalSymbolTable.input(symbolName, (short)symbolValue, false);
	}

	private static void processHeaderRecord(String line) {
		String programName = line.substring(1,6);
		int programAddr = PLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(7,10));
		programLength = summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(11,14));
		//maybe check for relocatability here also?
		ExternalSymbolTable.input(programName, (short) programAddr, false);
		PLA += programLength;	//get ready for the next program
	}
	
}
