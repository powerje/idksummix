package summixAssembler;

public class Pass1 {

	Token[] token_array = new Token[5]; 
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
			count++;
			num_params++;
		}
		return num_params;
	}
	
	public String processHeader(){
		
		String progName;
		
		Boolean isRelative = false;
		strLine = body.getLine();
		line.input(strLine);
		token = line.getToken();
		
		// Process ProgName
		headerRecord += token.getText();
		headerRecord += " ";
		
		// Process .ORIG
		token = line.getToken();
		headerRecord += token.getText();
		headerRecord += " ";
		
		// Process isRelative
		token = line.getToken();
		
		if((token.getType() != TokenType.EOL) || (token.getType() != TokenType.COMMENT))
		{
			isRelative = false;
			headerRecord += token.getText();
		}
		else
		{
			isRelative = true;
		}
		/*!  NEED TO PARSE HEX TO INT USE SUMMIX_UTILITIES 
		int addr = Integer.parseInt(token.getText());
		LocationCounter.set(addr, isRelative);
		!*/
	
		
		return headerRecord;
	}
	
	public TextFile processFile()
	{
		body.processHeader();
		p1file.input(headerRecord);
		
		
		// set Location Counter
		// store initial LC for later calculation of segment size
		
		
		return p1file;
	}
}
