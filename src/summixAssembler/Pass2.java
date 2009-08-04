package summixAssembler;

public class Pass2 {
	TextFile body;
	Token[] token_array = new Token[4];
	int numberOfTokens;
	TextFile p2File = new TextFile();
	
	public Pass2(TextFile incomingSource)
	{
		body = incomingSource;
		body.reset();
	}
	
	private int getTokens()
	{
		int count = 0;
		numberOfTokens = 0;
		while(count < 4)
		{
			token_array[count] = body.getToken();
			if(token_array[count].getType() == TokenType.EOL)
			{
				count = 4;
			}
			count++;
			numberOfTokens++;
		}
		return numberOfTokens;
	}
	
	public TextFile processFile()
	{
		boolean foundEndLine = false;
		int counter = 1;
		//get header record from first line, place into the body file
		p2File.input(body.getLine());
		//while(not end record or end of file) {output text records}
		while(!body.isEndOfFile() && !foundEndLine)
		{
			foundEndLine = processAnyLine(counter);
			if (!foundEndLine)
				{
					counter ++;
				}
		}
		
		if (foundEndLine)
		{
			processEndLine(counter);
			counter++;
		}
		
		//write end record to body
		
		
		return body;
	}
	
	private void processEndLine(int counter)
	{
		getTokens();
		if(true)
		{
			
		}
	}
	
	private boolean processAnyLine(int counter)
	{
		getTokens();
		
		boolean foundEnd = false;
		
		if (numberOfTokens > 4 || numberOfTokens < 1) //If you haven't gotten any tokens, or you got too many tokens
		{
			System.out.println("ERROR: Malformed line at line " + counter);
		}
		else if (token_array[0].getText() == ".END" || token_array[1].getText() == ".END") //If the line is an end line, stop processing and return true
		{
			foundEnd = true;
		}
		else if (numberOfTokens == 1) //Must be an EoL token by itself
		{
			counter++;
			p2File.input("");
		}
		else
		{
			processTextLine(counter);
		}
		
		return foundEnd;
	}
	
	private void processTextLine(int counter)
	{
		if (isAnOp(token_array[0].getText()) && (numberOfTokens == 3 || numberOfTokens == 2)) //<op><maybe arg>
		{
			//check to see if there are args
			if (numberOfTokens == 3) //Then it has an arg <op><arg>
			{
				
			}
			else //It has no arg <op>
			{
				
			}
		}
		else if(isAnOp(token_array[1].getText()) && (numberOfTokens == 4 || numberOfTokens == 3)) //<label><op><maybe arg>
		{
			//check to see if there are args
			if (numberOfTokens == 4) //There is an arg <label><op><arg>eolTok
			{
				
			}
			else //There is no arg <label><op>eolTok
			{

			}
		}
		else
		{
			System.out.print("ERROR: Malformed sourcecode at line " + counter);
		}
	}
	
	private boolean isAnOp(String incomingOp)
	{
		boolean flag = false;
		if (MachineOpTable.isOp(incomingOp))
		{
			flag = true;
		}
		else if (PseudoOpTable.isPseudoOp(incomingOp))
		{
			flag = true;
		}
		return flag;
	}
}
