package summixAssembler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pass1 {

	Token[] token_array = new Token[4];
	String[] record_string = new String[100];
	TextFile body, line, p1file;
	String headerRecord, endRecord, textRecord;
	String strLine;
	Token token;
	int num_params;
	short start;
	private static Set<Short> literals = new HashSet<Short>();
	
	private short hexstringToShort(CharSequence input) {
		int returnVal = 0; // needs initialized in the case an exception is caught
		/**
		 * Takes a CharSequence that is a hex number and converts it to a short.
		 * 
		 * @param input CharSequence to be converted into an int of its hex value
		 */
		//there is a lot of crappy looking casting going on here, is there a better way?
		//should probably check for anything other than hex digits in these CharSequence
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
	private void getTokens()
	{
		int count = 0;
		num_params = 0;
		
		while(count < 4)
		{
			token_array[count] = body.getToken();
			if(token_array[count].getType() == TokenType.EOL)
			{
				count = 4;
			}
			count++;
			num_params++;
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
		String progName, strStart = token_array[2].getText();
		boolean isRelative = false;	
		
		if(token_array[0].getType() == TokenType.ALPHA)
		{
			progName = token_array[0].getText();
		}
		else
		{
			//print error regarding program name
		}
		
		if(!(token_array[2].getType() == TokenType.ALPHA))
		{
			isRelative = true;	
		}
		
		int index = strStart.indexOf('x');
		start = hexstringToShort(strStart.subSequence(index + 1, strStart.length()));

		LocationCounter.set((int)(start), isRelative);
		//make sure that Prog name has 6 characters
		int extraNeeded = 6 - token_array[0].getText().length();
		String prog_name = null;
		if (token_array[0].getText().length() < 6)
		{
			for(int i=0; i<extraNeeded; i++)
			{
				prog_name = token_array[0].getText().concat(" ");
			}
		}
		else
		{
			prog_name = token_array[0].getText();
		}
		
		
		int token_array_size = token_array.length;
		int i = 1;
		headerRecord += prog_name;
		while(i < token_array_size)
		{
			if((token_array[i].getType() != TokenType.EOL))
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
			if(token_array[0].getType() == TokenType.ALPHA && num_params == 4 )
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
			if(num_params == 3 )
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
		if(token_array[1].getText() == ".END")
		{
			if(token_array[0].getType() == TokenType.ALPHA)
			{
				//add to symbol table
			}

		}
		else if(token_array[0].getText() == ".END")
		{
			//we do not need to do anything?
		
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

		LocationCounter.incrementAfterLiteral(LiteralTable.size());  // Jim can I have a size...Thanks
		
		short size = (short) (LocationCounter.getAddress() - start); // needs to be a hex string 
		String sizeStr;
		String headerFinal = "H";
		headerFinal.concat(headerRecord);
		headerRecord = headerFinal.concat(sizeStr);	
		
		p1file.insertLine(0, headerRecord);
		return p1file;
	}
}

