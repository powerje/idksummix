package summixAssembler;

import java.util.StringTokenizer;

public class Pass2 {
	
	TextFile body;
	Token[] token_array = new Token[4];
	int numberOfTokens;
	TextFile p2File = new TextFile();
	String[] argTokArray = new String[3]; 
	
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
		else
		{
			System.out.println("ERROR: No end of line record present in sourecode. Expected at line " + counter);
		}
		
		body.reset();
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
			System.out.println("ERROR: Oversized sourecode at line " + counter);
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
			if (numberOfTokens == 3) //Then it has an arg; <op><arg>
			{
				processWrite(token_array[0].getText(), token_array[1].getText());
			}
			else //It has no arg; <op>
			{
				processWrite(token_array[0].getText());
			}
		}
		else if(isAnOp(token_array[1].getText()) && (numberOfTokens == 4 || numberOfTokens == 3)) //<label><op><maybe arg>
		{
			//check to see if there are args
			if (numberOfTokens == 4) //There is an arg; <label><op><arg>eolTok
			{
				processWrite(token_array[1].getText(), token_array[2].getText());
			}
			else //There is no arg; <label><op>eolTok
			{
				processWrite(token_array[1].getText());
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
	
	private void getArgTokens(int amount, StringTokenizer st)
	{
		int counter = 0;
		while (counter < amount && st.hasMoreTokens())
		{
			argTokArray[counter] = st.nextToken();
			counter++;
		}
	}
	
	private boolean isValReg(String register, Boolean canBeRelative)
	{
		boolean flag = false;
		if (register != null && register.matches("^R[0-7]$"))
		{
			flag = true;
		}
		else if(register != null && )
		return flag;
	}

	private boolean isValImm(String immediate)
	{
		boolean flag = false;
		if (immediate != null && immediate.matches("^R[0-7]$"))
		{
			flag = true;
		}
		return flag;
	}
	
	private void processWrite(String op, String arg) //Write op with arguments to p2File
	{
		short DR, SR1, SR2, imm5, pgoffset9, index6, BaseR, n, z, p, L, SR, trapvect8;
		boolean badArg = false;
		StringTokenizer st = new StringTokenizer(arg, ",");
		String input = new String("T");

		
		
		//Figure out which op you have
		if (op.equals("ADD"))
		{
			getArgTokens(3, st);
			if (isValReg(argTokArray[0], false) && isValReg(argTokArray[1], false) && isValReg(argTokArray[2], false)) //Valid regs 
			{
				
			}
			else if (isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValImm(argTokArray[2])) //Valid reg + immediate value
			{
				
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("AND"))
		{
			
		}
		else if (op.equals("JSR"))
		{
			
		}		
		else if (op.equals("JSRR"))
		{
			
		}
		else if (op.equals("LD"))
		{
			
		}
		else if (op.equals("LDI"))
		{
			
		}
		else if (op.equals("LDR"))
		{
			
		}
		else if (op.equals("LEA"))
		{
			
		}
		else if (op.equals("NOT"))
		{
			
		}
		else if (op.equals("ST"))
		{
			
		}
		else if (op.equals("STI"))
		{
			
		}
		else if (op.equals("STR"))
		{
			
		}
		else if (op.equals("TRAP"))
		{
			
		}
		else //Op is a branch op
		{
			
		}
		
	}
	
	private void processWrite(String op) //Write op with no arguments to p2File
	{
		String input = new String("T");
		p2File.input(input.concat(shortToHexString(MachineOpTable.getOp(op)))); //Get op from machineop table, turn it into a string, append it to the output string, and the write it to the file
	}
	
	private String shortToHexString(short data) {
		String returnVal = Integer.toHexString((int) data);
		if (returnVal.length() > 4) {
			returnVal = returnVal.substring(returnVal.length() - 4, returnVal.length());
		}
		return returnVal.toUpperCase();
	}
}
