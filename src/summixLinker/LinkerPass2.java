package summixLinker;

import java.util.ArrayList;
import java.util.Iterator;

import summixAssembler.SymbolTable;
import summixAssembler.TextFile;

public class LinkerPass2 {

	private static TextFile finalObjectFile = new TextFile();
	private static String programName;
	private static short programSize;
	private static short programStartingAddress;
	private static boolean processedFirstHeader;
	private static String finalObjectHeader;

	public static TextFile processObjects(ArrayList<TextFile> objects, int memoryStart)
	{
		programStartingAddress = (short) memoryStart;
		Iterator<TextFile> objectCycler = objects.iterator();

		while(objectCycler.hasNext())
		{
			processObjectFile(objectCycler.next());
		}

		return finalObjectFile;
	}

	private static void processObjectFile(TextFile unlinkedObject)
	{
		while(!unlinkedObject.isEndOfFile())
		{
			processAnyLine(unlinkedObject.getLine());
		}
	}

	private static void processAnyLine(String line)
	{

		if (line.startsWith("H"))
		{
			programName = line.substring(1, 7);
			programSize += Short.parseShort(line.substring(11, 15), 16);

			if(!processedFirstHeader)
			{
				processedFirstHeader = true;
				finalObjectHeader = "H" + programName + line.substring(1, 7); //Add in H[program name][start address] still need to add the size
			}

		}
		else if(line.startsWith("T"))
		{
			short front = (short) (ExternalSymbolTable.getValue(programName) + Short.parseShort(line.substring(1, 5), 16)); //First four after T
			short back = 0; //Last four after T
			short temp = 0;
			
			if (line.length() == 11) //Line has M0 or M1 record, but no X record
			{
				if (line.charAt(10) == 0) //M0
				{
					back = Short.parseShort(line.substring(5, 9), 16);
					temp = ExternalSymbolTable.getValue(programName);
					temp &= 0x1FF;
					back += temp;
				}
				else //M1
				{
					back = ExternalSymbolTable.getValue(programName);
					back += temp;
				}
			}
			else if (line.length() > 11) //Line has M0/M1 record, AND has an X record
			{
				if (line.charAt(10) == 0) //M0
				{
					
				}
				else //M1
				{
					
				}				
			}
			else //Line has neither M0/M1/X record
			{
				
			}
			
			finalObjectFile.input("T" + SymbolTable.shortToHexStringNoPrefix(front) + SymbolTable.shortToHexStringNoPrefix(back));
		}
		else if(line.startsWith("E"))
		{
			finalObjectHeader += ExternalSymbolTable.shortToHexStringNoPrefix(programSize);
			finalObjectFile.input("E");
		}
		else if(line.startsWith(";"))
		{
			System.out.println("ERROR: Object file for program " + programName + " has an error in it.");
		}


	}
}
