package summixAssembler;

import java.util.StringTokenizer;

public class Pass2 {
	
	int counter = 2;
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
	
	private void getTokens()
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
	}
	
	public TextFile processFile()
	{
		boolean foundEndLine = false;

		//get header record from first line, place into the body file
		p2File.input(body.getLine());
		//while(not end record or end of file) {output text records}
		while(!body.isEndOfFile() && !foundEndLine)
		{
			foundEndLine = processAnyLine();
			if (!foundEndLine)
				{
					counter ++;
				}
		}
		
		if (foundEndLine)
		{
			processEndLine();
			counter++;
		}
		else
		{
			System.out.println("ERROR: No end of line record present in sourecode. Expected at line " + counter);
		}
		
		body.reset();
		return body;
	}
	
	private void processEndLine()
	{
		getTokens();
		if(true)
		{
			
		}
	}
	
	private boolean processAnyLine()
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
			processTextLine();
		}
		
		return foundEnd;
	}
	
	private void processTextLine()
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
	
	private boolean isValReg(String register)
	{
		boolean flag = false;
		if (register != null && register.matches("^R[0-7]$")) //Is a regular register
		{
			flag = true;
		}
		//Is an absolute symbol that exists in the table and is in the correct range for a register
		else if(register != null && SymbolTable.isDefined(register) && !SymbolTable.isRelative(register)
				&& (SymbolTable.getValue(register) <= 7 & SymbolTable.getValue(register) >= 0)) 
		{
			flag = true;
		}
		return flag;
	}

	private boolean isValAddr(String address)
	{
		boolean flag = false;
		if ()
		{
			
		}
		return flag;
	}
	
	private boolean isValIndex(String address)
	{
		boolean flag = false;
		if ()
		{
			
		}
		return flag;
	}
	
	private boolean isValTrapVect(String address)
	{
		boolean flag = false;
		if ()
		{
			
		}
		return flag;
	}
	
	private boolean isValImm(String immediate)
	{
		boolean flag = false;

		if (immediate != null)
		{
			if (immediate.startsWith("#"))
			{
				try{
					//Is a decimal value in the proper range for an immediate value
					if (Integer.parseInt(immediate.substring(1)) >= -16 && Integer.parseInt(immediate.substring(1)) <= 15 )
					{
						flag = true;
					}
				}
				catch(NumberFormatException e){}
			}
			else if (immediate.startsWith("x"))
			{
				try{
					//Is a hex value in the proper range for an immediate value
					if (Integer.parseInt(immediate.substring(1), 16) >= -16 && Integer.parseInt(immediate.substring(1), 16) <= 15)
					{
						flag = true;
					}	
				}
				catch(NumberFormatException e){}
			}
			//Is an absolute symbol in the table that is in the proper range for an immediate value
			else if (SymbolTable.isDefined(immediate) && !SymbolTable.isRelative(immediate)
					&& SymbolTable.getValue(immediate) >= -16 && SymbolTable.getValue(immediate) <= 15)
			{
				flag = true;
			}
		}

		return flag;
	}
	
	//can be symbol, number, or Rx
	private short regVal(String reg) {
		short returnVal = 0;
		
		if (reg.charAt(0)=='R' || reg.charAt(0)=='x') {
			returnVal = Short.parseShort(reg.substring(1));
		} else if (SymbolTable.isDefined(reg)) { //symbol
			returnVal = SymbolTable.getValue(reg);
		} else { // number
			returnVal = Short.parseShort(reg.substring(0));
		}
		return returnVal;
	}

	private void processWrite(String op, String arg) //Write op with arguments to p2File
	{
		short DR, SR1, SR2, imm5, pgoffset9, index6, BaseR, n, z, p, L, SR, trapvect8;
		boolean badArg = false;
		StringTokenizer st = new StringTokenizer(arg, ",");
		String input = new String("T");
		short finalOp;
		
		
		//Figure out which op you have
		if (op.equals("ADD"))
		{
			finalOp = MachineOpTable.getOp("ADD");
			getArgTokens(3, st);
			//Valid regs; DR,SR1,SR2 and the final SR2 is NOT a symbol, last SR2, if it is a symbol, is always an IMM5
			if (!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1])
					&& isValReg(argTokArray[2]) && !SymbolTable.isDefined(argTokArray[2]))
			{
				//construct instruction in finalOp, you have a good layout for the args token 0 is DR, token 1 is SR1, token 2 is SR2
				//Don't forget to set the link bit to 0
				DR = regVal(argTokArray[0]);
				SR1 = regVal(argTokArray[1]);
			}
			//Valid reg + immediate value; DR,SR1,IMM5
			else if (!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValImm(argTokArray[2])) 
			{
				//construct instruction in finalOp, you have a good layout for the args token 0 is DR, token 1 is SR1, token 2 is IMM5
				//Don't forget to set the link bit to 1
			}
			else //regs are invalid, something's screwed up with the tokens, maybe you have too many, maybe the IMM5 value is a # followed by letters
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
		else//Op is a branch op
		{
			
		}
		
		if (badArg)
		{
			System.out.print("ERROR: Improperly formed arguements at line" + counter);
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
