package summixAssembler;

public class Pass1 {

	TextFile body;
	
	public Pass1(TextFile incomingSource)
	{
		body = incomingSource;
	}
	
	public TextFile processFile()
	{
		// processHeaderRecord
		
			// while (!incomingSource.isEndOfFile()){
		
			// processProgName
		
				// incomingSource.getToken(token)	
		
				// while (token.getType != ALPHA or QUOTE){
		
				// if token.getType == ERROR{
					// error message
				// }
		
				// else if (token.getType == COMMENT or EOL) and
				// (!incomingSource.isEndOfFile()){
					// incomingSource.getToken(token)
					// }
			// }
		
			// progName = ((token.getText()) + (n)spaces) 
			// where n = 6 - (num_of_spaces(tokenText))
			// }

			// processOrig
		
			// Boolean isRelative
		
			// incomingSource.getToken(token)
		
			// if (token.getText() = .ORIG){
				// incomingSource.getToken(token)
				// if (token.getType() == HEX){
					// LocationCounter.set(token.getText(), isRelative)
				// }
				// else if (token.getType() == COMMENT){
					// 	
			// }
		
		
		// set Location Counter
		// store initial LC for later calculation of segment size
		
		
		return body;
	}
}
