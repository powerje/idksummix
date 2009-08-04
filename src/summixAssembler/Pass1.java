package summixAssembler;

public class Pass1 {

	Token[] token_array = new Token[5];
	String[] record_string = new String[100];
	TextFile body, line, p1file;
	String headerRecord;
	String strLine;
	Token token;
	
	public Pass1(TextFile incomingSource)
	{
		body = incomingSource;
	}
	public int getTokens()
	{
		int count = 0;
		int num_params = 0;
		while(count < 5)
		{
			token_array[count] = body.getToken();
			if(token_array[count].getType() == TokenType.EOL)
			{
				count = 5;
			}
			count++;
			num_params++;
		}
		return num_params;
	}	
	
	public String processHeader()
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
			headerRecord += token_array[i];
			headerRecord += " ";
			i++;
		}
		return headerRecord;
	}
	
	public String processText()
	{
		

		
		return headerRecord;
	}
	
	public String processEnd()
	{
		if(token_array[1].getText() == ".END")
		{
		
		}
		else if(token_array[0].getText() == ".END")
		{
			
		
		}

		
		return headerRecord;
	}
	
	public TextFile processFile()
	{
		while(!body.isEndOfFile())
		{
			int num_params = getTokens();
			
			while((token_array[0].getType() == TokenType.COMMENT) || (token_array[0].getType() == TokenType.EOL))
			{
				num_params = getTokens();
			}
			if(token_array[1].getText() == ".ORIG")
			{
				processHeader();
			}
			else if((PseudoOpTable.isPseudoOp(token_array[0].getText())) || (MachineOpTable.isOp(token_array[0].getText())) ||
					(PseudoOpTable.isPseudoOp(token_array[1].getText())) || (MachineOpTable.isOp(token_array[1].getText())))
			{
				processText();
			}
			else if((token_array[0].getText() == ".END") || (token_array[1].getText() == ".END"))
			{
				processEnd();
			}
		
		}
	
		
		// set Location Counter
		// store initial LC for later calculation of segment size
		
		
		return p1file;
	}
}
