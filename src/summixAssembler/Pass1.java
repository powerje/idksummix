package summixAssembler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Dan Stottlemire
 * @author Mike Irwin
 *
 */

public class Pass1 {

	Token[] token_array = new Token[4];
	String[] record_string = new String[100];
	TextFile body;
	TextFile line = new TextFile();
	TextFile p1file = new TextFile();
	String headerRecord="", endRecord="", textRecord="";
	String strLine;
	Token token;
	int num_tokens;
	short start;
	private static Set<Short> literals = new HashSet<Short>();
	
	/**
	 * hexstringToShort -
	 * Takes a CharSequence that is a hex number and converts it to a short.
	 * 
	 * @param input CharSequence to be converted into an int of its hex value
	 */
	
	private short hexstringToShort(CharSequence input) {
		int returnVal = 0; // needs initialized in the case an exception is caught
		try {
			returnVal = Integer.valueOf((String) input, 16).intValue();
		} catch (NumberFormatException e)	{
			System.out.println("");
			System.exit(-1); //error
		}
		return (short) returnVal;
	}
	
	public static String shortToHexString(short data) {
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

	
	public Pass1(TextFile incomingSource)
	{
		body = incomingSource;
		body.stripComments();
	}
	
	/**
	 * getTokens -
	 * The method takes no parameters and returns nothing. 
	 * It fills a token array with all the tokens in a line of a TextFile and 
	 * sets the global variable num_tokens to the number of tokens place into the array.
	 * 0 < num_tokens < 5
	 */
	
	private void getTokens()
	{
		int count = 0;
		num_tokens = 0;
		
		while(count < 4)
		{
			token_array[count] = body.getToken();
			if(token_array[count].getType() == TokenType.EOL)
			{
				count = 4;
			}
			count++;
			num_tokens++;
		}
	}
		
	private boolean isPseudoOp(Token op)
	{
		boolean opFlag = false;
		
		if(PseudoOpTable.isPseudoOp(op.getText()))
		{
			opFlag = true;
		}
		
		return opFlag;
	}
	
	private boolean isVarPseudoOp(Token op)
	{
		boolean opFlag = false;
		
		if(op.getText().equals(".BLKW") || op.getText().equals(".STRZ"))
		{
			opFlag = true;
		}
		
		return opFlag;
	}
	
	private boolean isOp(Token op)
	{
		boolean opFlag = false;
		
		if(PseudoOpTable.isPseudoOp(op.getText()) || (MachineOpTable.isOp(op.getText())))
		{
			opFlag = true;
		}
		
		return opFlag;
	}
	
	private boolean isLiteral(Token arg)
	{
		boolean literal = false;
		String strToken;
		strToken = arg.getText();
		int index = strToken.indexOf('=');
		
		if(index == -1)
		{
			literal = false;
		}
		else
		{
			literal = true;
		}
		return literal;
	}
	private short getLiteral(Token arg)
	{
		short literal = 0;
		String strToken = arg.getText();
		String strLiteral = arg.getText();

		int index = strToken.indexOf('x');
		if (index == -1) // not hex? must be decimal
		{
			index = strToken.indexOf('#');
			literal = Short.parseShort(strLiteral.substring(index+1));  
		}
		else { //hex value
			literal = hexstringToShort(strLiteral.subSequence(index + 1, strLiteral.length()));
		}
		
		return literal;
		
	}
	
	private String processHeader()
	{
	String progName = "ERROR", strStartAddr = "ERROR";
	boolean isRelative = false; 

	// process the Program Name
	if(token_array[0].getType() == TokenType.ALPHA)
	{
		progName = token_array[0].getText();

		//make sure that Program Name has 6 characters and ONLY 6 characters
		int extraNeeded = 6 - progName.length();
		while (extraNeeded > 0)
		{
			progName += "_";
			extraNeeded--;
		}
		
		if (progName.length() > 6)
		{
			progName = (String) progName.subSequence(0, 6);
		}
	}
	else
	{
		System.out.println("ERROR: Invalid or malformed program name exists!");
	}

	// Set the Location Counter

	if(token_array[2].getType() == TokenType.EOL)
	{
		isRelative = true; 
		strStartAddr = "0000";
	}
	else if(token_array[2].getType() == TokenType.ALPHA)
	{
		strStartAddr = token_array[2].getText();
		int index = strStartAddr.indexOf('x');
		if (index != -1)
		{
			start = hexstringToShort(strStartAddr.subSequence(index + 1, strStartAddr.length())); // should be error checking here?
			strStartAddr = strStartAddr.substring(index + 1);
			if(start > 65535)
			{
				System.out.println("ERROR: The origin address exceeds the max addressable memory location!");
			}
		}
		else
		{
			System.out.println("ERROR: Expected hex value for start of segment memory location!");
		}
	}
	else
	{
		System.out.println("ERROR: Expected hex value for start of segment memory location!");
	}

	LocationCounter.set((int)(start), isRelative);

	// Construct the header record string
	try {
	headerRecord = progName + strStartAddr; 
	}
	catch (NullPointerException e) {}


	return headerRecord;
	}

	private String processText()
	{
		if(isOp(token_array[1]))
		{
			if(token_array[0].getType() == TokenType.ALPHA && num_tokens == 4 )
			{
				if(token_array[1].getText().equals(".EQU"))
				{
					int index1 = token_array[2].getText().indexOf('x');
					if (index1 != -1) //hex
					{
						short arg = hexstringToShort(token_array[2].getText().subSequence(index1 + 1, token_array[2].getText().length()));
						SymbolTable.input(token_array[0].getText(), arg, LocationCounter.relative);
					}
					int index2 = token_array[2].getText().indexOf('#');
					if (index2 != -1) //decimal
					{
						short arg = Short.parseShort(token_array[2].getText().substring(index2+1));
						SymbolTable.input(token_array[0].getText(), arg, LocationCounter.relative);
					}
					if (index1 == -1 && index2 == -1) //symbol table
					{
						if(!(SymbolTable.isDefined(token_array[2].getText())))
						{
							System.out.println("ERROR: Symbol used for .EQU operand was not previously defined.");
						}
						else
						{
							short arg = SymbolTable.getValue(token_array[2].getText());
							SymbolTable.input(token_array[0].getText(), arg, LocationCounter.relative);
						}
					}
				}
				else if(isPseudoOp(token_array[1]) && (!(token_array[1].getText().equals("EQU"))))
				{
					if(token_array[1].getText().equals(".FILL"))
					{
						LocationCounter.incrementAmt(1);
					}
					else if(isVarPseudoOp(token_array[1]))
					{
						if (token_array[1].getText().equals(".BLKW"))
						{
							int index1 = token_array[2].getText().indexOf('x');
							if (index1 != -1) //hex
							{
								short arg = hexstringToShort(token_array[2].getText().subSequence(index1 + 1, token_array[2].getText().length()));
								LocationCounter.incrementAmt((int) arg);
							}
							int index2 = token_array[2].getText().indexOf('#');
							if (index2 != -1) //decimal
							{
								short arg = Short.parseShort(token_array[2].getText().substring(index2+1));
								LocationCounter.incrementAmt((int) arg);
							}
							if (index1 == -1 && index2 == -1) //symbol table
							{
								if(!(SymbolTable.isDefined(token_array[2].getText())))
								{
									System.out.println("ERROR: Symbol used for .BLKW operand was not previously defined.");
								}
								
								short arg = SymbolTable.getValue(token_array[2].getText());
								LocationCounter.incrementAmt((int) arg);
							}
						}
						else if (token_array[1].getText().equals(".STRZ"))
						{
							if(token_array[2].getType() == TokenType.QUOTE)
							{
								String arg = token_array[2].getText();
								int length = token_array[2].getText().length();
								length -= 2; //get rid of quotes
								length += 1; //add null
								
								LocationCounter.incrementAmt(length);
							}
							else if (token_array[2].getType() == TokenType.ERROR)
							{
								System.out.println("ERROR: Arguments and / or operand for .STRZ is malformed.");
							}
						}
					}
					else
					{
						SymbolTable.input(token_array[0].getText(), LocationCounter.getAddress(), LocationCounter.relative);
					}
					if (isLiteral(token_array[2]))
					{
						literals.add(getLiteral(token_array[2]));
					}
				}
			
			}
			else if(isOp(token_array[0]))
			{
				if(isPseudoOp(token_array[0]))
				{
					if(token_array[0].getText().equals(".FILL"))
					{
						LocationCounter.incrementAmt(1);
					}
					else if(isVarPseudoOp(token_array[0]))
					{
						if (token_array[0].getText().equals(".BLKW"))
						{
							int index1 = token_array[1].getText().indexOf('x');
							if (index1 != -1) //hex
							{
								short arg = hexstringToShort(token_array[1].getText().subSequence(index1 + 1, token_array[1].getText().length()));
								LocationCounter.incrementAmt((int) arg);
							}
							int index2 = token_array[1].getText().indexOf('#');
							if (index2 != -1) //decimal
							{
								short arg = Short.parseShort(token_array[1].getText().substring(index2+1));
								LocationCounter.incrementAmt((int) arg);
							}
							if (index1 == -1 && index2 == -1) //symbol table
							{
								if(!(SymbolTable.isDefined(token_array[1].getText())))
								{
									System.out.println("ERROR: Symbol used for .BLKW operand was not previously defined.");
								}
							
								short arg = SymbolTable.getValue(token_array[1].getText());
								LocationCounter.incrementAmt((int) arg);
							}
						}
						else if (token_array[0].getText().equals(".STRZ"))
						{
							if(token_array[1].getType() == TokenType.QUOTE)
							{
								String arg = token_array[1].getText();
								int length = token_array[1].getText().length();
								length -= 2; //get rid of quotes
								length += 1; //add null
							
								LocationCounter.incrementAmt(length);
							}
							else if (token_array[1].getType() == TokenType.ERROR)
							{
								System.out.println("ERROR: Arguments and / or operand for .STRZ is malformed.");
							}
						}
					}
					if(num_tokens == 3 )
					{	
						if (isLiteral(token_array[2]))
						{
							literals.add(getLiteral(token_array[2]));
						}
					}
				}
			}
		}

		
			
		int token_array_size = token_array.length;
		int i = 0;
		while(i < token_array_size)
		{
			if((token_array[i].getType() != TokenType.EOL))
			{
				textRecord += token_array[i];
				textRecord += " ";
			}
			i++;
		}
		
		return textRecord;
	}
	
	
	private String processEnd()
	{
		boolean relative = false;
		String strEndAddr = "No_End_Addr";
		short end;
		
		if(token_array[1].getText().equals(".END"))
		{
				if((num_tokens == 4) && (token_array[2].getType() == TokenType.ALPHA))
				{
					strEndAddr = token_array[2].getText();
					int index = strEndAddr.indexOf('x');
					if (index != -1)
					{
						end = hexstringToShort(strEndAddr.subSequence(index + 1, strEndAddr.length())); // should be error checking here?
						if(((int)(end)) > 65535)
						{
							System.out.println("ERROR: The end address exceeds the max addressable memory location!");
						}
					}
					else if(!(SymbolTable.isDefined(token_array[2].getText())))
					{
						System.out.println("ERROR: Symbol for start of execution was not previously defined.");
					}
				}
				if(token_array[0].getType() == TokenType.ALPHA)
				{
					if(!(SymbolTable.isDefined(token_array[0].getText())))
					{
						 SymbolTable.input(token_array[0].getText(), LocationCounter.getAddress(), relative);
					}
				}
				else
				{
					System.out.println("ERROR: Invalid or Malformed Label!");
				}
		}
		else if(token_array[0].getText().equals(".END"))
		{
			if((num_tokens == 3) && (token_array[1].getType() == TokenType.ALPHA))
			{
				strEndAddr = token_array[1].getText();
				int index = strEndAddr.indexOf('x');
				if (index != -1)
				{
					end = hexstringToShort(strEndAddr.subSequence(index + 1, strEndAddr.length())); // should be error checking here?
					if(((int)(end)) > 65535)
					{
						System.out.println("ERROR: The end address exceeds the max addressable memory location!");
					}
				}
				else if(!(SymbolTable.isDefined(token_array[1].getText())))
				{
					System.out.println("ERROR: Symbol for start of execution was not previously defined.");
				}
			}		
		}

		int token_array_size = token_array.length;
		int i = 0;
		while(i < token_array_size)
		{
			if((token_array[i].getType() != TokenType.EOL))
			{
				endRecord += token_array[i];
	
				endRecord += " ";
			}
			i++;
		}
		
		return endRecord;
	}
	
	public TextFile processFile()
	{
		boolean endFlag = false, origFlag = false, textFlag = false;
				
		while(!body.isEndOfFile())
		{
			getTokens();
			
			while((token_array[0].getType() == TokenType.EOL))
			{
				getTokens();
			}
			if(token_array[1].getText().equals(".ORIG"))
			{
				if (origFlag == true)
				{
					System.out.println("ERROR: The program contains MULTIPLE header recorders.");
				}
				headerRecord = processHeader();
				origFlag = true;
			}
			else if((PseudoOpTable.isPseudoOp(token_array[0].getText())) || (MachineOpTable.isOp(token_array[0].getText())) ||
					(PseudoOpTable.isPseudoOp(token_array[1].getText())) || (MachineOpTable.isOp(token_array[1].getText())))
			{
				textRecord = processText();
				textFlag = true;
				p1file.input(textRecord);
			}
			else if((token_array[0].getText().equals(".END")) || (token_array[1].getText().equals(".END")))
			{
				if (endFlag == true)
				{
					System.out.println("ERROR: The program contains MULTIPLE end records.");
				}
				endRecord = processEnd();
				endFlag = true;
				p1file.input(endRecord);
			}
			//increment location counter
			if (MachineOpTable.isOp(token_array[1].getText())) {
				LocationCounter.incrementAmt(MachineOpTable.getSize(token_array[1].getText()));
			}
		}
		
		// Print error messages if no Header or End record, also if not at least one text record present.
		
		// Check for a header recorder
		if (origFlag == false)
		{
			System.out.println("ERROR: The program contains NO header record.");
		}
		
		// Check for at least one text record
		if (textFlag == false)
		{
			System.out.println("ERROR: The program does not contains at least ONE text record.");
		}
		
		// Check for an end recorder
		if (endFlag == false)
		{
			System.out.println("ERROR: The program contains NO end recorder.");
		}

		// Add Lits to Literal Table and increment the Location Counter for all of the Literals
		Iterator<Short> literal = literals.iterator();
		
		while (literal.hasNext())
		{
			Short lit = (Short) literal.next();
			LiteralTable.input(lit.shortValue(), LocationCounter.getAddress());
			LocationCounter.incrementAfterLiteral(1);
		}
		
		// Construct and Insert the Header Recorder
		
		// Calculate the Program segment size
		short size = (short) (LocationCounter.getAddress() - start);
		String sizeStr = shortToHexString(size);
		System.out.println(size + " lc: " + LocationCounter.getAddress() + " start: " + start);
		// Add an "H" to the beginning of header record
		String headerFinal = "H";
		headerFinal += headerRecord;
		headerFinal += sizeStr;
		headerRecord = headerFinal;
		
		// Insert the header record at the top of the p1file
		p1file.insertLine(0, headerRecord);
		
		return p1file;
	}
}

