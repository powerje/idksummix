package summixAssembler;
/**
 * 
 * @author Mike Irwin
 *
 */
public class ListFile {
	
	public boolean commentSource, commentP2;
	/**
	 * This method processes 1 line from the source file.
	 * 
	 * @param source - original source that is given to us with the problem.
	 * @return listSource - String
	 */
	private String ProcessLineSource(TextFile source)
	{
		String listSource = null, line;
		Token token;
		
		token = source.getToken();
		if(token.getType() == TokenType.COMMENT)
		{
			listSource = token.getText();
			commentSource=true;
		}
		else
		{
			line = source.getLine();
			// add original token to the line, following by rest of the line.
			listSource = token.getText();
			listSource.concat("\t");
			listSource.concat(line);
		}
		return listSource;
	}
	/**
	 *This method processes the address from 1 line of the file that is returned from pass2.
	 * 
	 * @param p2 - TextFile returned from pass2
	 * @return listP2Address - String
	 */
	private String ProcessLineP2Address(TextFile p2)
	{
		String listP2Address = null;
		String tempP2Address = p2.getLine();
		
		listP2Address = tempP2Address.substring(1,4);
		
		return listP2Address;
	}
	/**
	 * This method processes the hex OP code from 1 line of the file that is returned from pass2.
	 * 
	 * @param p2 - TextFile returned from pass2
	 * @return listP2Op - String
	 */
	
	private String ProcessLineP2Op(TextFile p2)
	{
		String listP2Op = null;
		String tempP2Op = p2.getLine();
		
		listP2Op = tempP2Op.substring(1,4);
		
		return listP2Op;
	}
	/**
	 * converts a hex string to an integer (used later on for the binary conversion)
	 * 
	 * @author Jim Power
	 * @param input - string hex value to be converted
	 * @return returnVal - integer
	 */
	
	private int hexstringToInt(String input) {
		int returnVal = 0; // needs initialized in the case an exception is caught
		try {
			returnVal = Integer.valueOf(input, 16).intValue();
		} catch (NumberFormatException e)	{
			System.out.println("");
			System.exit(-1); //error
		}
		return returnVal;
	}
	/**
	 * Creates the 16-bit binary output of the original hex OP code
	 * 
	 * @param op - string hex opcode
	 * @return binaryString - string
	 */
	

	private String OutputBinaryP2(String op)
	{
		String binaryString = new String();
		String tempString = null;
		int opInt = hexstringToInt(op);
		
		tempString = Integer.toBinaryString(opInt);
		int len = tempString.length();
		while(len < 16)
		{
			binaryString.concat("0");
			len++;
		}
		binaryString.concat(tempString);
		
		return binaryString;
	}
	/**
	 * The main method used to create the ListFile.  This method will be called from the assembler and then run its helper methods
	 * in order to add 1 line from the source and p2 files.
	 * 
	 * @param source - original source file
	 * @param p2 - file created after pass2
	 * @return listFile - TextFile
	 */
	
	public TextFile CreateListFile(TextFile source, TextFile p2)
	{
		TextFile listFile = new TextFile();
		String sourceLine = new String();
		String p2Line = new String();
		String completeRow = new String();
		int progCount = 1;
		source.reset();
		p2.reset();
		
		
		System.out.println("DID WE GET HERE?!");
		while(!source.isEndOfFile() && !p2.isEndOfFile())
		{
			sourceLine = ProcessLineSource(source);  //get first line
			
			while(commentSource)
			{
				completeRow = "( ";
				completeRow.concat(Integer.toString(progCount));
				completeRow.concat(" )\t");
				completeRow.concat(sourceLine);
				progCount++;
				listFile.input(completeRow);
				sourceLine = ProcessLineSource(source);
			}
			
			//print header record
			completeRow.concat("( " + Integer.toString(progCount) + " )\t" + sourceLine);
			progCount++;
			listFile.input(completeRow);
			
			//dump header record of p2
			p2Line = p2.getLine();
			
			//print 1 line p2 file
			String lineOp = ProcessLineP2Op(p2);
			completeRow.concat("( " + ProcessLineP2Address(p2) + " ) " + " ");
			completeRow.concat(lineOp + " " + OutputBinaryP2(lineOp) + " ");
			
			//print count
			completeRow.concat("( " + Integer.toString(progCount) + " )\t");
			progCount++;
			
			//print 1 line source file
			sourceLine = ProcessLineSource(source);
			completeRow.concat(sourceLine);
			listFile.input(completeRow);
		}
		return listFile;
	}
	
}
