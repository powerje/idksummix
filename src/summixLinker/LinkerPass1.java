package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;
import summixAssembler.TextFile;
import summixSimulator.SummiX_Utilities;

public class LinkerPass1 {
	static int PC;
	static int IPLA;
	
	public static void processObjects(ArrayList<TextFile> objects, int memoryStart)
	{
		IPLA = memoryStart;
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
		
	}

	private static void processAbsoluteExternalSymbol(String line) {
		
	}

	private static void processHeaderRecord(String line) {
		String programName = line.substring(1,6);
		int programAddr = IPLA + summixSimulator.SummiX_Utilities.hexStringToInt(line.substring(7,10));
	}
	
}
