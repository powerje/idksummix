package summixAssembler;

/**
 * 
 * @author Mike Irwin
 *
 */
public class ListFile {
	
	//The below three variables have been DECLARED
	String p2MainLine = new String();
	boolean comment = false;
	private TextFile source = null; //This one also has global scope.
	private TextFile p2 = null; //These variables have global scope inside of the ListFile class. ANY method can use them.
	
	public ListFile (TextFile source_orig, TextFile p2_orig)
	{
		//System.out.println("here");
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
		String listSource = source.getLine();
		/*source.display();
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
		}*/
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
		//System.out.println("address");
		String listP2Address = "";
		
		if(!p2.isEndOfFile())
		{
			p2MainLine = p2.getLine();
			
			if(p2MainLine.indexOf(';') == 0)
			{
				listP2Address = p2MainLine;
				comment = true;
			}
			else
			{
			//	System.out.println(p2MainLine);
				listP2Address = p2MainLine.substring(1,5);
				System.out.println("balla " + p2MainLine);
				comment = false;
			//	System.out.println(listP2Address);
			}	
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
		//System.out.println("op");
		//System.out.println(p2MainLine);
		String listP2Op = p2MainLine;
		if(!p2.isEndOfFile())
				{
				if(p2MainLine.indexOf(';') != 0)
				{
					listP2Op = p2MainLine.substring(5,9);  //should be 5-8?
					comment = false;
				}
				else
				{
					listP2Op = p2MainLine;
					comment = true;
				}
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
	
	private static int hexstringToInt(String input) {
		//System.out.println("hex to int?");
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
	

	public static String OutputBinaryP2(String op)
	{
		//System.out.println("outputbinary");
		String binaryString = new String();
		String tempString = null;
		int opInt = hexstringToInt(op);
		
		tempString = Integer.toBinaryString(opInt);
		int len = tempString.length();
		while(len < 16)
		{
			binaryString = binaryString.concat("0");
			len++;
		}
		binaryString = binaryString.concat(tempString);
		
		return binaryString;
	}
	/**
	 * Checks to see whether a machineOp or pseudoOp is found within the string (which lets us know if it is a legal call)
	 * 
	 * @param sourceLine - the string line that the method will be checking (from the source file)
	 * @return good - boolean
	 */
	private boolean isGood(String sourceLine)
	{
		boolean good = false;
		
		if ((sourceLine.indexOf(".ORIG")!= -1) || (sourceLine.indexOf(".EQU")!= -1) || (sourceLine.indexOf(".FILL")!= -1)
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
	
	public String shortToHexString(short data) {
		String returnVal = Integer.toHexString((int) data);
		if (returnVal.length() > 4) 
		{
			returnVal = returnVal.substring(returnVal.length() - 4, returnVal.length());
		}
		while (returnVal.length() < 4) 
		{
			returnVal = "0" + returnVal;
		}
		return returnVal.toUpperCase();
	}

	/**
	 * The main method used to create the ListFile.  This method will be called from the assembler and then run its helper methods
	 * in order to add 1 line from the source and p2 files.
	 * 
	 * @param source - original source file
	 * @param p2 - file created after pass2
	 * @return listFile - TextFile
	 */
	
	public TextFile CreateListFile()
	{
		TextFile listFile = new TextFile();
		String sourceLine = new String();
		String oHeader = new String();
		String p2Line = new String();
		String completeRow = new String();
		int progCount = 1;
		source.reset();
		p2.reset();
		
		
		while(!source.isEndOfFile() && !p2.isEndOfFile())
		{
			//get one source line to analyze
			sourceLine = ProcessLineSource(source);
			
			if(sourceLine == "") //empty line
			{
				//System.out.println("empty line");
				//do nothing?  There is nothing from the text record that corresponds
				progCount++;
				
			}
			else if(sourceLine.indexOf(".ORIG") != -1) //line up header records
			{
				//System.out.println(".orig");
				completeRow = p2.getLine();
				completeRow = completeRow.concat("\t\t\t\t\t\t( ");
				completeRow = completeRow.concat(Integer.toString(progCount));
				completeRow = completeRow.concat(" ) ");
				completeRow = completeRow.concat(sourceLine);
				listFile.input(completeRow);
				progCount++;
				
			}
			else if(sourceLine.indexOf(".END") != -1) //deal with end
			{
				//progCount
				completeRow = ("\t\t\t\t\t\t\t\t\t( ");
				completeRow = completeRow.concat(Integer.toString(progCount));
				completeRow = completeRow.concat(" ) ");
				
				//sFile
				completeRow = completeRow.concat(sourceLine);
				listFile.input(completeRow);
				progCount++;
				
			}
			else if(isGood(sourceLine)) //deal with .EQU, .BLKW, .STRZ or regular op code
			{
				if(sourceLine.indexOf(".EQU") != -1) // no address in p2
				{
					//System.out.println(".equ");
					//progCount
					completeRow = completeRow.concat("( ");
					completeRow = completeRow.concat(Integer.toString(progCount));
					completeRow = completeRow.concat(" ) ");
					
					//sFile
					completeRow = completeRow.concat(sourceLine);
					
					listFile.input(completeRow);
					progCount++;
				}
				else if(sourceLine.indexOf(".BLKW") != -1) //increment progCount more
				{
					//System.out.println(".blkw");
					String p2add = new String();
					
					if(sourceLine.indexOf('#') == -1)
					{
						p2add = ProcessLineP2Address(p2);
					}
					if(p2add.indexOf(';') != 0)
					{
						System.out.println("in if");
						completeRow = p2add;
						//progCount
						completeRow = completeRow.concat("\t\t\t\t\t\t\t\t\t( ");
						completeRow = completeRow.concat(Integer.toString(progCount));
						completeRow = completeRow.concat(" ) ");
						
						//sFile
						completeRow = completeRow.concat(sourceLine);
						listFile.input(completeRow);
						progCount++;
					}
					else
					{
						System.out.println("in else");
						completeRow = p2add;
						//progCount
						completeRow = completeRow.concat("\t\t\t\t( ");
						completeRow = completeRow.concat(Integer.toString(progCount));
						completeRow = completeRow.concat(" ) ");
						
						//sFile
						completeRow = completeRow.concat(sourceLine);
						listFile.input(completeRow);
						progCount++;
					}
					
					
				}
				
				else if(sourceLine.indexOf(".STRZ") != -1) //multiple oRecords for 1 line in source
				{
					//System.out.println(".strz");
					//print 1 line from oRecord and source (must do this)
					completeRow = "( ";
					completeRow = completeRow.concat(ProcessLineP2Address(p2));
					completeRow = completeRow.concat(" ) ");
					completeRow = completeRow.concat(ProcessLineP2Op(p2));
					completeRow = completeRow.concat(" ");
					completeRow = completeRow.concat(OutputBinaryP2(ProcessLineP2Op(p2)));
					completeRow = completeRow.concat(" ");
					
					//progCount
					completeRow = completeRow.concat("\t\t( ");
					completeRow = completeRow.concat(Integer.toString(progCount));
					completeRow = completeRow.concat(" ) ");
					
					//sFile
					completeRow = completeRow.concat(sourceLine);
					listFile.input(completeRow);
					
					int index = sourceLine.indexOf('"');
					String sub = sourceLine.substring(index);
					int len = sub.length();  //normally -1 however we need a null added on too
					int i = 1;
					while(i < (len - 1))  // -1 for the already displayed text record
					{
						completeRow = "( ";
						completeRow = completeRow.concat(ProcessLineP2Address(p2));
						completeRow = completeRow.concat(" ) ");
						completeRow = completeRow.concat(ProcessLineP2Op(p2));
						completeRow = completeRow.concat(" ");
						completeRow = completeRow.concat(OutputBinaryP2(ProcessLineP2Op(p2)));
						completeRow = completeRow.concat(" ");
						
						//progCount
						completeRow = completeRow.concat("\t\t( ");
						completeRow = completeRow.concat(Integer.toString(progCount));
						completeRow = completeRow.concat(" ) ");
						listFile.input(completeRow);
						
						i++;
					}
					
					progCount++;
				}
				else //normal display
				{
					//System.out.println("normal");
					String p2address = ProcessLineP2Address(p2);
					//System.out.println(comment);
					//if(!LiteralTable.isLiteralAddress(p2address))
					//{
						if(p2address.length() == 4)
						{
							//object file
							completeRow = "( ";
							completeRow = completeRow.concat(p2address);
							completeRow = completeRow.concat(" ) ");
							completeRow = completeRow.concat(ProcessLineP2Op(p2));
							completeRow = completeRow.concat(" ");
							completeRow = completeRow.concat(OutputBinaryP2(ProcessLineP2Op(p2)));
							completeRow = completeRow.concat(" ");
							
							//progCount
							completeRow = completeRow.concat("\t\t( ");
							completeRow = completeRow.concat(Integer.toString(progCount));
							completeRow = completeRow.concat(" ) ");
							
							//sFile
							completeRow = completeRow.concat(sourceLine);
						}
						else
						{
							completeRow = p2address;
							completeRow = completeRow.concat("\t\t\t( ");
							completeRow = completeRow.concat(Integer.toString(progCount));
							completeRow = completeRow.concat(" ) ");
							completeRow = completeRow.concat(sourceLine);
						}
						listFile.input(completeRow);
						progCount++;
					}
				//}
			}
			else if(!isGood(sourceLine) && sourceLine.indexOf('E') == -1) //deal with error line
			{
				completeRow = p2.getLine();
				completeRow = completeRow.concat("\t\t\t( ");
				completeRow = completeRow.concat(Integer.toString(progCount));
				completeRow = completeRow.concat(" ) ");
				completeRow = completeRow.concat(sourceLine);
				completeRow = completeRow.concat(" ");
				listFile.input(completeRow);
				progCount++;
				
			}
			
		}
		listFile = LiteralTable.printTable(listFile);
		return listFile;
	}
	
}
