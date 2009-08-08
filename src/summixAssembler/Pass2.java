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

	private void getTokens()
	{
		numberOfTokens = 0;
		Token temp = new Token(";", TokenType.COMMENT);
		
		while(temp.getType() != TokenType.EOL)
		{ 
			//System.out.println("Pulled out this token:" + temp.getText());
			temp = body.getToken();
			if (!(numberOfTokens > 4))
			{
				token_array[numberOfTokens] = temp;
			}
			numberOfTokens++;
		}
	}
	
	public TextFile processFile()
	{
		boolean foundEndLine = false;

		//get header record from first line, place into the body file
		//Are header records relocatable?
		p2File.input(body.getLine()); //Get header
		body.getLine(); //Flush old header
		
		//while(not end record or end of file) {output text records}
		while(!body.isEndOfFile() && !foundEndLine)
		{
			foundEndLine = processAnyLine();
		}
		
		if (foundEndLine)
		{
			processEndLine();
		}
		else
		{
			System.out.println("ERROR: No end of line record present in sourecode. Expected at line " + body.getReport());
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
		//System.out.println("Process any line");
		boolean foundEnd = false;
		
		if (numberOfTokens > 4) //You got too many tokens
		{
			System.out.println("ERROR: Oversized sourecode at line " + body.getReport());
		}
		else if (token_array[0].getText() == ".END" || token_array[1].getText() == ".END") //If the line is an end line, stop processing and return true
		{
			foundEnd = true;
		}
		else if (numberOfTokens == 1) //Must be an EoL token by itself
		{
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
			System.out.print("ERROR: Malformed sourcecode at line " + body.getReport());
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
		if (address != null)
		{
			if (address.startsWith("#"))
			{
				try{
					//Is a decimal value in the proper range for an address value
					if (Integer.parseInt(address.substring(1)) >= 0 && Integer.parseInt(address.substring(1)) <= 0xFFFF )
					{
						flag = true;
					}
				}
				catch(NumberFormatException e){}
			}
			else if (address.startsWith("x"))
			{
				try{
					//Is a hex value in the proper range for an immediate value
					if (Integer.parseInt(address.substring(1), 16) >= 0 && Integer.parseInt(address.substring(1), 16) <= 0xFFFF)
					{
						flag = true;
					}	
				}
				catch(NumberFormatException e){}
			}
			//Is an absolute symbol in the table that is in the proper range for an immediate value
			else if (SymbolTable.isDefined(address) && SymbolTable.getValue(address) >= 0 && SymbolTable.getValue(address) <= 0xFFFF)
			{
				flag = true;
			}
		}
		
		return flag;
	}
	
	private boolean isValIndex(String index)
	{
		boolean flag = false;
		
		if (index != null)
		{
			if (index.startsWith("#"))
			{
				try{
					//Is a decimal value in the proper range for an index6 value
					if (Integer.parseInt(index.substring(1)) >= 0 && Integer.parseInt(index.substring(1)) <= 63 )
					{
						flag = true;
					}
				}
				catch(NumberFormatException e){}
			}
			else if (index.startsWith("x"))
			{
				try{
					//Is a hex value in the proper range for an index6 value
					if (Integer.parseInt(index.substring(1), 16) >= 0 && Integer.parseInt(index.substring(1), 16) <= 63)
					{
						flag = true;
					}	
				}
				catch(NumberFormatException e){}
			}
			//Is an absolute symbol in the table that is in the proper range for an index6 value
			else if (SymbolTable.isDefined(index) && !SymbolTable.isRelative(index)
					&& SymbolTable.getValue(index) >= 0 && SymbolTable.getValue(index) <= 63)
				{
				flag = true;
				}
		}
		return flag;
	}
		
	private boolean isValTrapVect(String trap)
	{
		boolean flag = false;
		if (trap != null)
		{
			if (trap.startsWith("#"))
			{
				try{
					//Is a decimal value in the proper range for an trapvector value
					if (Integer.parseInt(trap.substring(1)) >= 0 && Integer.parseInt(trap.substring(1)) <= 0xFF )
					{
						flag = true;
					}
				}
				catch(NumberFormatException e){}
			}
			else if (trap.startsWith("x"))
			{
				try{
					//Is a hex value in the proper range for an trapvector value
					if (Integer.parseInt(trap.substring(1), 16) >= 0 && Integer.parseInt(trap.substring(1), 16) <= 0xFF)
					{
						flag = true;
					}	
				}
				catch(NumberFormatException e){}
			}
			//Is an absolute symbol in the table that is in the proper range for an index6 value
			else if (SymbolTable.isDefined(trap) && !SymbolTable.isRelative(trap)
					&& SymbolTable.getValue(trap) >= 0 && SymbolTable.getValue(trap) <= 63)
				{
				flag = true;
				}
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
	
	//an immut5 is either a string in the form #(decimal value) x(hex value) or a symbol
	private short immVal(String key) {
		short returnVal = 0;
		if(SymbolTable.isDefined(key)) //Symbol
		{
			returnVal = SymbolTable.getValue(key);
		}
		else if(key.charAt(0) == '#') //Decimal number
		{
			returnVal = Short.parseShort(key.substring(1));
		}
		else //Must be hex
		{
			returnVal = Short.parseShort(key.substring(1), 16);
		}
		return returnVal;
	}
	
	//can be symbol, number, or Rx
	private short regVal(String key) {

		short returnVal = 0;
		
		if (key.charAt(0)=='R' || key.charAt(0)=='x') {
			returnVal = Short.parseShort(key.substring(1));
		} else if (SymbolTable.isDefined(key)) { //symbol
			returnVal = SymbolTable.getValue(key);
		} else { // number
			returnVal = Short.parseShort(key.substring(0));
		}
		return returnVal;
	}

	private int addrVal(String key)
	{
		int returnVal = 0;
		if(SymbolTable.isDefined(key)) //Symbol
		{
			returnVal = SymbolTable.getValue(key);
		}
		else if(key.charAt(0) == '#') //Decimal number
		{
			returnVal = Integer.parseInt(key.substring(1));
		}
		else //Must be hex
		{
			returnVal = Integer.parseInt(key.substring(1), 16);
		}
		
		returnVal &= 0x1FF; //keep the lower right 9 bits
		
		return returnVal;
	}
	
	private short indexVal(String key)
	{
		short returnVal = 0;
		
		if (key.charAt(0)=='x')
		{
			returnVal = Short.parseShort(key.substring(1));
		}
		else if (SymbolTable.isDefined(key))
		{ //symbol
			returnVal = SymbolTable.getValue(key);
		}
		else
		{ // number
			returnVal = Short.parseShort(key.substring(0));
		}
		return returnVal;
	}
	
	private short trapVal(String key){
		short returnVal = 0;
		
		if (key.charAt(0)=='x') {
			returnVal = Short.parseShort(key.substring(1));
		} else if (SymbolTable.isDefined(key)) { //symbol
			returnVal = SymbolTable.getValue(key);
		} else { // number
			returnVal = Short.parseShort(key.substring(0));
		}
		return returnVal;
	}
	
	private void getArgTokens(int amount, StringTokenizer st)
	{

		int counter = 0;
		
		while (counter < 3)
		{
			argTokArray[counter] = null;
			counter++;
		}
		
		counter = 0;
		while (counter < amount && st.hasMoreTokens()) //Get the tokens you want
		{
			argTokArray[counter] = st.nextToken();
			counter++;
		}
	}
	
	private void processWrite(String op, String arg) //Write op with arguments to p2File
	{
		short DR, SR1, SR2, imm5, pgoffset9, index6, BaseR, n, z, p, SR, trapvect8;
		int addr;
		boolean M0 = false;
		boolean M1 = false;
		boolean badArg = false;
		boolean doNothing = false;
		boolean isFill = false;
		StringTokenizer st = new StringTokenizer(arg, ",");
		String input = new String("T");
		short finalOp = 0;
		
		System.out.println("Inside process write with this op:" + op);
		
		if (MachineOpTable.isOp(op))
		{
			finalOp = MachineOpTable.getOp(op);			
		}

		
		//Figure out which op you have
		if (op.equals("ADD"))
		{
			getArgTokens(3, st);
			//Valid regs; DR,SR1,SR2 and the final SR2 is NOT a symbol, last SR2, if it is a symbol, is always an IMM5
			if (!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1])
					&& isValReg(argTokArray[2]) && !SymbolTable.isDefined(argTokArray[2]))
			{
				//construct instruction in finalOp, you have a good layout for the args token 0 is DR, token 1 is SR1, token 2 is SR2
				//Don't forget to set the link bit to 0
				DR = regVal(argTokArray[0]);
				SR1 = regVal(argTokArray[1]);
				SR2 = regVal(argTokArray[2]);
				
				finalOp |= SR2;
				finalOp |= (SR1 << 6);
				finalOp |= (DR << 9);

			}
			//Valid reg + immediate value; DR,SR1,IMM5
			else if (!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValImm(argTokArray[2])) 
			{
				//construct instruction in finalOp, you have a good layout for the args token 0 is DR, token 1 is SR1, token 2 is IMM5
				//Don't forget to set the link bit to 1
				DR = regVal(argTokArray[0]);
				SR1 = regVal(argTokArray[1]);
				imm5 = immVal(argTokArray[2]);
				finalOp |= (SR1 << 6);
				finalOp |= (DR << 9);
				finalOp |= imm5;
				finalOp |= (1 << 5);
			}
			else //regs are invalid, something's screwed up with the tokens, maybe you have too many, maybe the IMM5 value is a # followed by letters
			{
				badArg = true;
			}
		}
		else if (op.equals("AND"))
		{
			getArgTokens(3, st);
			
			//Valid regs; DR,SR1,SR2 and the final SR2 is NOT a symbol, last SR2, if it is a symbol, is always an IMM5
			if (!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1])
					&& isValReg(argTokArray[2]) && !SymbolTable.isDefined(argTokArray[2]))
			{
				//construct instruction in finalOp, you have a good layout for the args token 0 is DR, token 1 is SR1, token 2 is SR2
				//Don't forget to set the link bit to 0
				DR = regVal(argTokArray[0]);
				SR1 = regVal(argTokArray[1]);
				SR2 = regVal(argTokArray[2]);
				
				finalOp |= SR2;
				finalOp |= (SR1 << 6);
				finalOp |= (DR << 9);

			}
			//Valid reg + immediate value; DR,SR1,IMM5
			else if (!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValImm(argTokArray[2])) 
			{
				//construct instruction in finalOp, you have a good layout for the args token 0 is DR, token 1 is SR1, token 2 is IMM5
				//Don't forget to set the link bit to 1
				DR = regVal(argTokArray[0]);
				SR1 = regVal(argTokArray[1]);
				imm5 = immVal(argTokArray[2]);
				finalOp |= (SR1 << 6);
				finalOp |= (DR << 9);
				finalOp |= imm5;
				finalOp |= (1 << 5);
			}
			else //regs are invalid, something's screwed up with the tokens, maybe you have too many, maybe the IMM5 value is a # followed by letters
			{
				badArg = true;
			}
		}
		else if (op.equals("JSR"))
		{
			getArgTokens(1, st);
			if(!st.hasMoreTokens() && isValAddr(argTokArray[0]))
			{
				addr = addrVal(argTokArray[0]);
				finalOp |= addr;
				finalOp |= 1 << 11; //Link bit set
				//No link bit set
			}
			else
			{
				badArg = true;
			}
			
		}		
		else if(op.equals("JMP"))
		{
			getArgTokens(1, st);
			if(!st.hasMoreTokens() && isValAddr(argTokArray[0]))
			{
				addr = addrVal(argTokArray[0]);
				finalOp |= addr;

			}
			else
			{
				badArg = true;
			}	
		}
		else if (op.equals("JSRR"))
		{
			getArgTokens(2, st);
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValIndex(argTokArray[1]))
			{
				BaseR = regVal(argTokArray[0]);
				index6 = indexVal(argTokArray[1]);
				finalOp |= BaseR << 6;
				finalOp |= index6;
				finalOp |= 1 << 11; //Link bit set
				//No link bit set
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("JMPR"))
		{
			getArgTokens(2, st);
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValIndex(argTokArray[1]))
			{
				BaseR = regVal(argTokArray[0]);
				index6 = indexVal(argTokArray[1]);
				finalOp |= BaseR << 6;
				finalOp |= index6;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("LD"))
		{
			getArgTokens(2, st);
			//CAN HAVE LITERAL.  DO SOME SPECIAL SHIT
		}
		else if (op.equals("LDI"))
		{
			getArgTokens(2, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
			{
				DR = regVal(argTokArray[0]);
				addr = addrVal(argTokArray[1]);
				finalOp |= DR << 9;
				finalOp |= addr;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("LDR"))
		{
			getArgTokens(3, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValIndex(argTokArray[2]))
			{
				DR = regVal(argTokArray[0]);
				BaseR = regVal(argTokArray[1]);
				index6 = indexVal(argTokArray[2]);
				
				finalOp |= DR << 9;
				finalOp |= BaseR << 6;
				finalOp |= index6;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("LEA"))
		{
			getArgTokens(2, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
			{
				DR = regVal(argTokArray[0]);
				addr = regVal(argTokArray[1]);

				finalOp |= DR << 9;
				finalOp |= addr;
			}
			else
			{
				badArg = true;
			}			
		}
		else if (op.equals("NOT"))
		{
			getArgTokens(2, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]))
			{
				DR = regVal(argTokArray[0]);
				SR = regVal(argTokArray[1]);
				
				finalOp |= DR << 9;
				finalOp |= SR << 6;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("ST"))
		{
			getArgTokens(2, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
			{
				SR = regVal(argTokArray[0]);
				pgoffset9 = (short) addrVal(argTokArray[1]);
				
				finalOp |= SR << 9;
				finalOp |= pgoffset9;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("STI"))
		{
			getArgTokens(2, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
			{
				SR = regVal(argTokArray[0]);
				pgoffset9 = (short) addrVal(argTokArray[1]);
				
				finalOp |= SR << 9;
				finalOp |= pgoffset9;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("STR"))
		{
			getArgTokens(3, st);
			
			if(!st.hasMoreTokens() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValIndex(argTokArray[2]))
			{
				SR = regVal(argTokArray[0]);
				BaseR = regVal(argTokArray[1]);
				index6 =  indexVal(argTokArray[2]);
				finalOp |= SR << 9;
				finalOp |= BaseR << 6;
				finalOp |= index6;
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals("TRAP"))
		{
			getArgTokens(1, st);
			
			if(!st.hasMoreTokens() && isValTrapVect(argTokArray[0]))
			{
				trapvect8 = trapVal(argTokArray[0]);
				finalOp |= trapvect8;
			}
			else
			{
				badArg = true;
			}			
		}
		else if (op.matches("^BRN?Z?P?$"))
		{
			getArgTokens(1, st);
			if (!st.hasMoreTokens() && isValAddr(argTokArray[0]))
			{
				finalOp |= addrVal(argTokArray[0]);
			}
			else
			{
				badArg = true;
			}
		}
		else if (op.equals(".END"))
		{

		}
		else if (op.equals(".EQU"))
		{
			doNothing = true;
		}
		else if (op.equals(".FILL"))
		{
			isFill = true;
		}
		else if (op.equals(".STRZ"))
		{
			
		}
		else if (op.equals(".BLKW"))
		{
			
		}
		
		
		if (badArg)
		{
			System.out.println("ERROR: Improperly formed argument at line" + body.getReport());
			p2File.input("ERRORLINE");
		}
		else
		{
			boolean needsRelocation = false;
			if (argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]) && SymbolTable.isRelative(argTokArray[0]))
			{
				needsRelocation = true;
			}
			else if (argTokArray[1] != null && SymbolTable.isDefined(argTokArray[1]) && SymbolTable.isRelative(argTokArray[1]))
			{
				needsRelocation = true;
			}
			else if (argTokArray[2] != null && SymbolTable.isDefined(argTokArray[2]) && SymbolTable.isRelative(argTokArray[2]))
			{
				needsRelocation = true;
			}
			if (needsRelocation)
			{
				p2File.input(input.concat(shortToHexString(finalOp)) + "M0"); //Get finished op, turn it into a string, append it to the output string, and the write it to the file
			}
			else if(isFill)
			{
				p2File.input(input.concat(shortToHexString(finalOp)) + "M1"); //Get finished op, turn it into a string, append it to the output string, and the write it to the file
			}
			else if (!doNothing){
				p2File.input(input.concat(shortToHexString(finalOp))); //Get finished op, turn it into a string, append it to the output string, and the write it to the file				
			}
			
			p2File.display();
		}
	}

	private void processWrite(String op) //Write op with no arguments to p2File
	{
		String input = new String("T");
		p2File.input(input.concat(shortToHexString(MachineOpTable.getOp(op)))); //Get op from machineop table, turn it into a string, append it to the output string, and the write it to the file
	}
	
	public static String shortToHexString(short data) {
		String returnVal = Integer.toHexString((int) data);
		if (returnVal.length() > 4) 
		{
			returnVal = returnVal.substring(returnVal.length() - 4, returnVal.length());
		}
		while (returnVal.length() < 4) 
		{
			returnVal = "0" + returnVal;
		}
		return returnVal.toUpperCase();
		}
}