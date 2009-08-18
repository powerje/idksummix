package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;

import summixAssembler.TextFile;

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
		
	}
}
