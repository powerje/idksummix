package summixLinker;

import java.util.ArrayList;

import summixAssembler.TextFile;

public class Linker {

	/**
	 * @param args
	 */
	public TextFile Linker(ArrayList<TextFile> objects, int memoryStart)
	{

		LinkerPass1.processObjects(objects, memoryStart);
		TextFile finalObjectFile = LinkerPass2.processObjects(objects, memoryStart);


		return finalObjectFile;

	}

}