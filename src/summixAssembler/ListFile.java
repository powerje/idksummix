package summixAssembler;

/**
 * 
 * @author Mike Irwin
 *
 */
public class ListFile {
	
	//The below three variables have been DECLARED
	private String p2MainLine = new String();
	private TextFile source = null; //This one also has global scope.
	private TextFile p2 = null; //These variables have global scope inside of the ListFile class. ANY method can use them.
	
	public ListFile (TextFile source_orig, TextFile p2_orig)
	{
		source = source_orig; //These variables have been ASSIGNED
		p2 = p2_orig;
		
		source.reset();
		p2.reset();
	}
	
	public boolean commentSource, commentP2;
	/**
	 * This method processes 1 line from the source file.
	 * 
	 * @param source - original source that is given to us with the problem.
	 * @return listSource - String
	 */
	private String ProcessLineSource(TextFile source)
	{
		source.display();
		String listSource = "", line = "";
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
			//listSource.concat("\t");
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
		p2.display();
		String listP2Address = "";
		if(p2.isEndOfFile())
		{
			String p2MainLine = p2.getLine();
		
			listP2Address = p2MainLine.substring(1,4);
		}
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
		p2.display();
		String listP2Op = "";
		if(p2.isEndOfFile())
		{
			listP2Op = p2MainLine.substring(5,8);  //should be 5-8?
		}
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
	
	private boolean isGood(String sourceLine)
	{
		boolean good = false;
		
		if ((sourceLine.indexOf(".ORIG")!= -1) || (sourceLine.indexOf(".END")!= -1) || (sourceLine.indexOf(".EQU")!= -1) || (sourceLine.indexOf(".FILL")!= -1)
				|| (sourceLine.indexOf(".STRZ")!= -1) || (sourceLine.indexOf(".BLKW")!= -1) || (sourceLine.indexOf("BR")!= -1) || (sourceLine.indexOf("BRN")!= -1)
				|| (sourceLine.indexOf("BRZ")!= -1) || (sourceLine.indexOf("BRP")!= -1) || (sourceLine.indexOf("BRNZ")!= -1) || (sourceLine.indexOf("BRNP")!= -1)
				|| (sourceLine.indexOf("BRZP")!= -1) || (sourceLine.indexOf("BRNZP")!= -1) || (sourceLine.indexOf("ADD")!= -1) || (sourceLine.indexOf("LD")!= -1)
				 || (sourceLine.indexOf("ST")!= -1) || (sourceLine.indexOf("JSR")!= -1) || (sourceLine.indexOf("JMP")!= -1) || (sourceLine.indexOf("AND")!= -1)
				 || (sourceLine.indexOf("LDR")!= -1) || (sourceLine.indexOf("STR")!= -1) || (sourceLine.indexOf("DBUG")!= -1) || (sourceLine.indexOf("NOT")!= -1)
				 || (sourceLine.indexOf("LDI")!= -1) || (sourceLine.indexOf("STI")!= -1) || (sourceLine.indexOf("JSRR")!= -1) || (sourceLine.indexOf("JMPR")!= -1)
				 || (sourceLine.indexOf("RET")!= -1) || (sourceLine.indexOf("LEA")!= -1) || (sourceLine.indexOf("TRAP")!= -1))
		{
			good = true;
		}
		else
		{
			good = false;
		}
		return good;
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
		String oHeader = new String();
		String p2Line = new String();
		String completeRow = new String();
		int progCount = 1;
		source.reset();
		p2.reset();
		
		//remove object file header
		oHeader = p2.getLine();
		
		while(!source.isEndOfFile() && !p2.isEndOfFile())
		{
			//get one source line to analyze
			sourceLine = ProcessLineSource(source);
			
			if(sourceLine == "") //empty line
			{
				//do nothing?  There is nothing from the text record that corresponds
				completeRow.concat("( " + progCount + " ) ");
				listFile.input(completeRow);
				progCount++;
			}
			else if(sourceLine.indexOf(".ORIG") != -1) //line up header records
			{
				completeRow = oHeader;
				completeRow.concat("( " + progCount + " ) ");
				completeRow.concat(sourceLine);
				
			}
			else if(isGood(sourceLine)) //deal with .EQU, .BLKW, .STRZ or regular op code
			{
				if(sourceLine.indexOf(".EQU") != -1) // no address in p2
				{
					//progCount
					completeRow.concat("( " + progCount + " ) ");
					
					//sFile
					completeRow.concat(sourceLine);
					
					listFile.input(completeRow);
					progCount++;
				}
				else if(sourceLine.indexOf(".BLKW") != -1) //increment progCount more
				{
					completeRow = "( " + ProcessLineP2Address(p2) + " ) \t\t\t";
					
					//progCount
					completeRow.concat("( " + progCount + " ) ");
					
					//sFile
					completeRow.concat(sourceLine);
					listFile.input(completeRow);
					progCount++;
					
				}
				else if(sourceLine.indexOf(".STRZ") != -1) //multiple oRecords for 1 line in source
				{
					//print 1 line from oRecord and source (must do this)
					completeRow = "( " + ProcessLineP2Address(p2) + " ) ";
					completeRow.concat(ProcessLineP2Op(p2) + " ");
					completeRow.concat(OutputBinaryP2(ProcessLineP2Op(p2)) + " ");
					
					//progCount
					completeRow.concat("( " + progCount + " ) ");
					
					//sFile
					completeRow.concat(sourceLine);
					listFile.input(completeRow);
					
					int index = sourceLine.indexOf('"');
					String sub = sourceLine.substring(index);
					int len = sub.length();  //normally -1 however we need a null added on too
					int i = 0;
					while(i < (len - 1))  // -1 for the already displayed text record
					{
						completeRow = "( " + ProcessLineP2Address(p2) + " ) ";
						completeRow.concat(ProcessLineP2Op(p2) + " ");
						completeRow.concat(OutputBinaryP2(ProcessLineP2Op(p2)) + " ");
						
						//progCount
						completeRow.concat("( " + progCount + " ) ");
						listFile.input(completeRow);
						
						i++;
					}
					progCount++;
				}
				else //normal display
				{
					//object file
					completeRow = "( " + ProcessLineP2Address(p2) + " ) ";
					completeRow.concat(ProcessLineP2Op(p2) + " ");
					completeRow.concat(OutputBinaryP2(ProcessLineP2Op(p2)) + " ");
					
					//progCount
					completeRow.concat("( " + progCount + " ) ");
					
					//sFile
					completeRow.concat(sourceLine);
					
					listFile.input(completeRow);
					progCount++;
					
				}
			}
			else if(!isGood(sourceLine)) //deal with error line
			{
				completeRow = p2.getLine();
				completeRow.concat("( " + progCount + " ) ");
				completeRow.concat(sourceLine);
				
				listFile.input(completeRow);
				progCount++;
				
			}
			
		}
		return listFile;
	}
	
}
