package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;

import summixAssembler.TextFile;

/**
 * Processes the object files with pass1 and pass2, then returns the final program file,  loaded into memory  at the the spot decided by the user.
 * @author Michael Pinnegar
 *
 */
public class Linker {

/**
 * Takes the object files in, as well as the start of memory, and processes the object files into a program file.
 * @param objects Object files to be processed into a single program file.  First object file is main.
 * @param memoryStart Start of memory as decided by user.
 * @return Program file
 */
	public static TextFile processObjects(ArrayList<TextFile> objects, int memoryStart)
	{
		LinkerPass1.processObjects(objects, memoryStart);
		TextFile finalObjectFile = LinkerPass2.processObjects(objects);
		return finalObjectFile;
	}

}