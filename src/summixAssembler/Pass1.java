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
	TextFile body, orig;
	TextFile line = new TextFile();
	TextFile p1file = new TextFile();
	String headerRecord="", endRecord="", textRecord="";
	String strLine,progName = "ERROR ", strStartAddr = "FFFF";
	Token token;
	int num_tokens;
	short start;
	private static Set<Short> literals = new HashSet<Short>();
	boolean errorLineFlag = false;
	
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
	
	private void constructRecord(String recordType){
		
		if(recordType.equals("end")){
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
		}
		else if(recordType.equals("text")){
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
		}
		else if(recordType.equals("header")){
			// Construct the header record string
			try {
			headerRecord = progName + strStartAddr; 
			}
			catch (NullPointerException e) {}
		}
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
		orig = body;
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
		
		if (num_tokens == 4 && token_array[num_tokens - 1].getType() != TokenType.EOL)
		{
			errorLineFlag = true;
			System.out.println("ERROR: Too many tokens.");
		}
	}
		
	private boolean isPseudoOp(Token op)
	{
		boolean opFlag = false;
		
		if(PseudoOpTable.isPseudoOp(op.getText()) && !op.getText().equals(".END") && !op.getText().equals(".ORIG"))
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
	
	private void processEQU()
	{
		Token label = token_array[0], op = token_array[1], arg = token_array[2];
		
		if(op.getText().equals(".EQU"))
		{
			int index1 = op.getText().indexOf('x');
			if (index1 != -1) //hex
			{
				short argVal = hexstringToShort(arg.getText().subSequence(index1 + 1, arg.getText().length()));
				SymbolTable.input(label.getText(), argVal, false);
			}
			int index2 = arg.getText().indexOf('#');
			if (index2 != -1) //decimal
			{
				short argVal = Short.parseShort(arg.getText().substring(1));
				SymbolTable.input(label.getText(), argVal, false);
			}
			if (index1 == -1 && index2 == -1) //symbol table
			{
					boolean argRel = SymbolTable.isRelative(arg.getText());
					short argVal = SymbolTable.getValue(arg.getText());
					SymbolTable.input(label.getText(), argVal, argRel);
			}
		}
	}
 	private String processHeader()
	{
	boolean isRelative = false;

	// process the Program Name
	if(token_array[0].getType() == TokenType.ALPHA)
	{
		progName = token_array[0].getText();

		//make sure that Program Name has 6 characters and ONLY 6 characters
		int extraNeeded = 6 - progName.length();
		while (extraNeeded > 0)
		{
			progName += " ";
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

	constructRecord("header");

	return headerRecord;
	}

	private String processText()
	{
		if(isOp(token_array[1]))
		{
			if(token_array[0].getType() == TokenType.ALPHA && num_tokens == 4 ) //this means the line has a label for sure right?
			{
				//because im working under the above assumption i am adding the label to the symbol table
				//oops assumption wrong for .EQU, so added if statement
				if (!token_array[1].getText().equals(".EQU")) {
					SymbolTable.input(token_array[0].getText(), (short)LocationCounter.curAddr, LocationCounter.isRelative());
				}

				if(token_array[1].getText().equals(".EQU"))
				{
						processEQU();
				}
				else if(isPseudoOp(token_array[1])) 
				{
					if(token_array[1].getText().equals(".FILL"))
					{
						LocationCounter.incrementAmt(1);
					}
					
					else if(isVarPseudoOp(token_array[1]))
					{
						LocationCounter.incrementAfterVarOp(token_array[1], token_array[2]);	
					}
					
					else
					{
						SymbolTable.input(token_array[0].getText(), LocationCounter.getAddress(), LocationCounter.relative);
					}			
				}
				//I think this place means machineOp? also can take care of literals used with pseudoOps here
				if (isLiteral(token_array[2]))
				{
					literals.add(getLiteral(token_array[2]));
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
						LocationCounter.incrementAfterVarOp(token_array[0], token_array[1]);

					}			
				}
			}
		}
		//added this to get literals 

		if (num_tokens > 1) {
			if (isLiteral(token_array[1]))
			{
				literals.add(getLiteral(token_array[1]));
			}
			if (isLiteral(token_array[2]))
			{
				literals.add(getLiteral(token_array[2]));
			}
			if (MachineOpTable.isOp(token_array[1].getText())) {
				LocationCounter.incrementAmt(MachineOpTable.getSize(token_array[1].getText()));
			}	
		}
		if (MachineOpTable.isOp(token_array[0].getText())) {
			LocationCounter.incrementAmt(MachineOpTable.getSize(token_array[0].getText()));
		}	
	
		constructRecord("text");
		
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
		
		constructRecord("end");

		return endRecord;
	}

	public TextFile processFile()
	{
		boolean endFlag = false, origFlag = false, textFlag = false;
		
		while(!body.isEndOfFile())
		{
			getTokens();
			
			if (num_tokens > 4)
			{
				errorLineFlag = true;
			} 
		
			//While (You don't have an empty AND you aren't at the end of file, get more tokens)
			while((token_array[0].getType() == TokenType.EOL) && !body.isEndOfFile())
			{
				getTokens();
			}
			
			//MUST at least have <token>eolTok
			if(token_array[1].getText().equals(".ORIG"))
			{
				if (origFlag == true)
				{
					System.out.println("ERROR: The program contains MULTIPLE header recorders.");
				}
				else
				{
					headerRecord = processHeader();
					origFlag = true;
				}
				
			} //At least <token>eolTok and 2nd token isn't .ORIG
			else if(!(token_array[0].getText().equals(".ORIG") || token_array[0].getText().equals(".END") || token_array[1].getText().equals(".END"))
					&& (isOp(token_array[0]) || isOp(token_array[1])))
					
			{//<op><token> or <token><op> but <op> is not .end or .orig 
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
			System.out.println("ERROR: The program contains NO end record.");
		}

		// Add Lits to Literal Table and increment the Location Counter for all of the Literals
		Iterator<Short> literal = literals.iterator();
		
		while (literal.hasNext())
		{
			Short lit = literal.next();
			LiteralTable.input(lit.shortValue(), LocationCounter.getAddress());
			LocationCounter.incrementAfterLiteral(1);
		}
		// Construct and Insert the Header Recorder
		
		// Calculate the Program segment size
		short size = (short) ((LocationCounter.getAddress() + 1) - start);
		String sizeStr = shortToHexString(size);
		// Add an "H" to the beginning of header record
		String headerFinal = "H";
		headerFinal += headerRecord;
		headerFinal += sizeStr;
		if (LocationCounter.relative) {
			headerFinal += "R";
		}
		headerRecord = headerFinal;
		
		// for some reason p1File was completely messed up so I hacked this on, not sure what the heck was going on with p1file, but didn't want to try to figure it out
//		orig.insertLine(0, headerRecord); I changed it so that there's a special field to hold the header
		orig.insertHeader(headerRecord);
		
		return orig;
	}
}

