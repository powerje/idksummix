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
	private int getTokens()
	{
		int count = 0;
		int num_params = 0;
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
		return num_params;
	}
	private Boolean isLiteral(Token arg)
	{
		Boolean literal = false;
		int i = 0;
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
		String strLiteral = null;
		
		int index = strToken.indexOf('=');
		strLiteral.substring(index+2);
		
		literal = Short.parseShort(strLiteral);  //error checking regarding the parsing?
		
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
			if(token_array[0].getType() == TokenType.ALPHA)
			{
				Boolean isLiteral;
				SymbolTable.input(token_array[0].getText(), LocationCounter.getAddress(), LocationCounter.relative);
				if(isLiteral = isLiteral(token_array[2]))
				{
					short literal = getLiteral(token_array[2]);
					literals.add(literal);
				}
			}
			else
			{
				//error
			}
		}
		else if((PseudoOpTable.isPseudoOp(token_array[0].getText())) || (MachineOpTable.isOp(token_array[0].getText())))
		{
			
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
			int num_params = getTokens();
			
			while((token_array[0].getType() == TokenType.EOL))
			{
				num_params = getTokens();
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
			
		
		}
	
		
		// set Location Counter
		// store initial LC for later calculation of segment size
		
		
		return p1file;
	}
}
