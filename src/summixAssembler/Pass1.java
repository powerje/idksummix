package summixAssembler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	TextFile body, line, p1file;
	String headerRecord, endRecord, textRecord;
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
	
	public Pass1(TextFile incomingSource)
	{
		body = incomingSource;
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
	
	private boolean isMachineOp(Token op)
	{
		boolean opFlag = false;
		
		if(MachineOpTable.isOp(op.getText()))
		{
			opFlag = true;
		}
		
		return opFlag;
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
		
		if(op.getText() == ".BLKW" || op.getText() == ".STRZ")
		{
			opFlag = true;
		}
		
		return opFlag;
	}
	
	private boolean isNotVarPseudoOp(Token op)
	{
		boolean opFlag = false;
		
		if(op.getText() == ".FILL")
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
		String progName = null, strStartAddr = null;
		boolean isRelative = false;	
		
		// process the Program Name
		if(token_array[0].getType() == TokenType.ALPHA)
		{
			//make sure that Program Name has 6 characters
			int extraNeeded = 6 - (token_array[0].getText().length());
			if (extraNeeded > 0)
			{
				for(int i=0; i < extraNeeded; i++)
				{
					progName = token_array[0].getText().concat(" ");
				}
			}
			else
			{
				progName = token_array[0].getText();
			}
		}
		else
		{
			System.out.println("ERROR: Invalid or Malformed Program Name exists!");
		}
		
		// Set the Location Counter
		
		if(token_array[2].getType() == TokenType.EOL)
		{
			isRelative = true;	
		}
		else if(token_array[2].getType() == TokenType.ALPHA)
		{
			strStartAddr = token_array[2].getText();
			int index = strStartAddr.indexOf('x');
			if (index != -1)
			{
	
				start = hexstringToShort(strStartAddr.subSequence(index + 1, strStartAddr.length())); // should be error checking here?
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
		
		headerRecord += progName;
		
		int token_array_size = token_array.length;
		int i = 1;
		while(i < token_array_size)
		{
			if(token_array[i].getType() != TokenType.EOL)
			{
				headerRecord += token_array[i];
			}
			i++;
		}
		return headerRecord;
	}

	private String processText()
	{
		if((PseudoOpTable.isPseudoOp(token_array[1].getText())) || (MachineOpTable.isOp(token_array[1].getText())))
		{
			if(token_array[0].getType() == TokenType.ALPHA && num_tokens == 4 )
			{
				if(token_array[1].getText() == ".EQU")
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
						short arg = SymbolTable.getValue(token_array[2].getText());
						SymbolTable.input(token_array[0].getText(), arg, LocationCounter.relative);
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
		else if((PseudoOpTable.isPseudoOp(token_array[0].getText())) || (MachineOpTable.isOp(token_array[0].getText())))
		{
			if(num_tokens == 3 )
			{	
				if (isLiteral(token_array[2]))
				{
					literals.add(getLiteral(token_array[2]));
				}
			}
			
		}
		
		if(isPseudoOp(token_array[1]))
		{
			if(token_array[1].getText() == ".FILL")
			{
				LocationCounter.incrementAmt(1);
			}
			else if(isVarPseudoOp(token_array[1]))
			{
				if (token_array[1].getText() == ".BLKW")
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
						short arg = SymbolTable.getValue(token_array[2].getText());
						LocationCounter.incrementAmt((int) arg);
					}
				}
				else if (token_array[1].getText() == ".STRZ")
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
						//error
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
		if(token_array[1].getText() == ".END")
		{
				if((num_tokens == 4) && (token_array[2].getType() == TokenType.ALPHA))
				{
					if(!(SymbolTable.isDefined(token_array[2].getText())))
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
		else if(token_array[0].getText() == ".END")
		{
			if((num_tokens == 3) && (token_array[1].getType() == TokenType.ALPHA))
			{
				if(!(SymbolTable.isDefined(token_array[1].getText())))
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
		while(!body.isEndOfFile())
		{
			getTokens();
			
			while((token_array[0].getType() == TokenType.EOL))
			{
				getTokens();
			}
			if(token_array[1].getText() == ".ORIG")
			{
				String headerRecord = processHeader();
				
			}
			if((token_array[0].getText() == ".END") || (token_array[1].getText() == ".END"))
			{
				String endRecord = processEnd();
				p1file.input(endRecord);
			}
			else if((PseudoOpTable.isPseudoOp(token_array[0].getText())) || (MachineOpTable.isOp(token_array[0].getText())) ||
					(PseudoOpTable.isPseudoOp(token_array[1].getText())) || (MachineOpTable.isOp(token_array[1].getText())))
			{
				String textRecord = processText();
				p1file.input(textRecord);
			}
			
		}

		LocationCounter.incrementAfterLiteral(LiteralTable.size()); 
		
		short size = (short) (LocationCounter.getAddress() - start); // needs to be a hex string 
		String sizeStr = null;
		String headerFinal = "H";
		headerFinal.concat(headerRecord);
		headerRecord = headerFinal.concat(sizeStr);	
		
		p1file.insertLine(0, headerRecord);
		return p1file;
	}
}

