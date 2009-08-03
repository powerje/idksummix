package summixAssembler;

public class Pass1 {

	TextFile body, line, p1file;
	String headerRecord;
	String strLine;
	Token token;
	
	public Pass1(TextFile incomingSource)
	{
		body = incomingSource;
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
		
		if((token.getType() != EOL) || (token.getType() != COMMENT))
		{
			isRelative = false;
			headerRecord += token.getText();
		}
		else
		{
			isRelative = true;
		}
		
		int addr = Integer.parseInt(token.getText());
		LocationCounter.set(addr, isRelative);
		
	
		
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
