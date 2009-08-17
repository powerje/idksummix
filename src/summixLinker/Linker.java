package summixLinker;

import summixAssembler.Assembler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import summixAssembler.TextFile;
import summixSimulator.*;

public class Linker {

	/**
	 * @param args
	 */
	public Linker(TextFile main, TextFile addon)
	{
		//pass 2 files to be linked at a time..
		
		//start with main, and then add to the end of it all of the lines of addon
		
		/*
		String[] args = {fileName};
		
		try {
			summixSimulator.Simulator.main(args);
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		*/
		
	}
	
	public void assemble(TextFile file)
	{
		//pass the textfile to the assembler.
	}
	
	public TextFile link()
	{
		TextFile fFile = new TextFile();
		//assemble source code or check for object file?\
		
		/*
		if(main is source)
		 * {
		 * 	assemble(main); //pass to assembler if necessary
		 * }
		 * else if (addon is source)
		 * {
		 * 	assemble(addon); //pass to assembler if necessary
		 * }
		 * 
		 * 
		 * for object files start with main, and go line by line through addon, adding to the end of main until EndofFile.
		 * 
		 * this part is where I am a little confused.. do we connect files by means of .ENT / .EXT signals or are we just adding line by line to join them?
		 * 
		 * we will need to concat the files so I imagine it would be a matter of going line by line until you reach an end file where you then would consider that
		 * grouping of 2 files complete.
		 * 
		 * 
		 */
		return fFile;
	}

			
	}
