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
	
	private int getVarAmount(Token arg)
	{
		
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
			literal = Short.parseShort(strLiteral.substring(index+1));  //error checking regarding the parsing?
		}
		else { //hex value
			literal = hexstringToShort(strLiteral.subSequence(index + 1, strLiteral.length()));
		}
		
		return literal;
		
	}
	
	private String processHeader()
	{
		String progName;
		boolean isRelative;
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
		//LocationCounter.set(token, isRelative);
		int token_array_size = token_array.length;
		int i = 0;
		while(i < token_array_size)
		{
			if((token_array[i].getType() != TokenType.EOL))
			{
				headerRecord += token_array[i];
				headerRecord += " ";
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
				SymbolTable.input(token_array[0].getText(), LocationCounter.getAddress(), LocationCounter.relative);
				
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
				SymbolTable.input(token_array[0].getText(), LocationCounter.getAddress(), LocationCounter.relative);
				
				if (isLiteral(token_array[2]))
				{
					literals.add(getLiteral(token_array[2]));
				}
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
				processHeader();
			}
			if((token_array[0].getText() == ".END") || (token_array[1].getText() == ".END"))
			{
				processEnd();
			}
			else if((PseudoOpTable.isPseudoOp(token_array[0].getText())) || (MachineOpTable.isOp(token_array[0].getText())) ||
					(PseudoOpTable.isPseudoOp(token_array[1].getText())) || (MachineOpTable.isOp(token_array[1].getText())))
			{
				processText();
			}
			

		
		
		return p1file;
	}
}

