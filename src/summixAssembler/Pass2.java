package summixAssembler;

import java.util.StringTokenizer;

/**
 * Pass2 uses the SymbolTable, LiteralTable, MachineOpTable, and LocationCounter to process the p1File into machine code which it returns as the p2File.
 *	All of this functionality is carried out through the processFile() method.
 *
 * @author Michael Pinnegar
 * 
 *	
 */
public class Pass2 {
	/** User's source code. */
	private TextFile body;
	/** Array of tokens pulled from the last line of body during getTokens().*/
	private Token[] token_array = new Token[4];
	/** Number of tokens removed from the last line of body during getTokens().*/
	private int numberOfTokens;
	/** Object file to be returned.*/
	private TextFile p2File = new TextFile();
	/** Array of tokens pulled from the argument of an operation.*/
	private String[] argTokArray = new String[3];
	/** True if an end record has been extracted from the user's source code.*/
	private boolean foundEndLine = false;
	private short startOfExecution = 0; 

	/**
	 * Creates a Pass2 object and readies it to process p1File.
	 * 
	 * @param incomingSource	The p1File generated by pass1 	
	 */
	public Pass2(TextFile incomingSource)
	{

		body = incomingSource;
		body.reset();
	}

	/**
	 * Populates the array token_array with up to four tokens from the current line of body.
	 * The total number of tokens on that line is stored in numberOfTokens.
	 */
	private void getTokens()
	{
		numberOfTokens = 0;
		Token temp = new Token(";", TokenType.COMMENT);

		while(temp.getType() != TokenType.EOL)
		{ 
			temp = body.getToken();
			if (numberOfTokens < 4)
			{
				token_array[numberOfTokens] = temp;
			}
			numberOfTokens++;
		}
	}

	/**
	 * Processes the intermediate file, p1File, and returns p2File. Prints out error messages when they are encountered during the processing of the p2File.
	 * Also writes error messages into the p2File if the source code is incorrect.
	 * @return p2File	
	 */
	public TextFile processFile()
	{
		String header = new String(body.getHeader());//Get header
		String start = new String(header.substring(7, 11));//Get start location from the header
		LocationCounter.set(Integer.valueOf(start, 16), header.endsWith("R"));//Set LC to start location
		startOfExecution = Short.valueOf(start, 16);
		p2File.input(header);//Store header in new file

		//while(not end record or end of file) {output text records}
		while(!body.isEndOfFile() && !foundEndLine)
		{
			processAnyLine();
		}

		if (!foundEndLine)
		{
			System.out.println("ERROR: No end of line record present in sourcecode. Expected at line " + body.getReport());
		}

		return p2File;
	}

	/**
	 * Checks to see if a line of source code is well formed. Prints an error if it is not, and inputs the error into the object code.
	 * Calls processTextLine() to finish process well formed lines of source code. 
	 */
	private void processAnyLine()
	{
		getTokens();

		if (numberOfTokens > 4) //You got too many tokens
		{
			p2File.input(";ERROR MALFORMED SOURECODE ON THIS LINE");			
		}
		else if (numberOfTokens == 1 && token_array[0].getType() != TokenType.EOL) //Singleton that is not an EoL tok
		{
			if (token_array[0].getType() == TokenType.ALPHA && (token_array[0].getText().equals("DBUG") || token_array[0].getText().equals("RET")))
			{//Okay, you've got a good single command, process text
				processTextLine();
			}
			else
			{//You've got a bad single line command, spit out error
				System.out.println("ERROR: Malformed no arg op at line " + body.getReport());
				p2File.input(";ERROR MALFORMED SOURECODE ON THIS LINE");
			}
		}
		else if (numberOfTokens == 1)//must be EoL do nothing
		{}
		else if (!token_array[0].getText().equals(".ORIG") && token_array[1] != null && !token_array[1].getText().equals(".ORIG") )
		{//If there's an .ORIG in spot one or two, ignore the line, otherwise process it
			processTextLine();
		}

	}

	/**
	 * Checks to see if a well formed line of source code has an op in it. If it does not, an error code is printed to the screen and written to the object file.
	 * Otherwise processWrite() is called on that line of code. 
	 */
	private void processTextLine()
	{
		if (isAnOp(token_array[0].getText()) && (numberOfTokens == 3 || numberOfTokens == 2) && token_array[0].getText() != ".ORIG") //<op><maybe arg>
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
			System.out.println("ERROR: No op code found at line " + body.getReport());
			p2File.input(";ERROR NO OP CODE FOUND");
		}
	}

	/**
	 * Checks to see if a given candidate op is a valid pseudo op or machine op.
	 * 
	 * @param candidateOp	Candidate op
	 * @return	True if the candidate op matches an operation in the LiteralTable or the MachineOpTable
	 */
	private boolean isAnOp(String candidateOp)
	{
		boolean flag = false;
		if (MachineOpTable.isOp(candidateOp))
		{
			flag = true;
		}
		else if (PseudoOpTable.isPseudoOp(candidateOp))
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * Checks to see if a candidate register is a valid register 
	 * 
	 * @param register	candidate register
	 * @return	True if the candidate register is in the proper form and within the specified ranges
	 */
	private boolean isValReg(String register)
	{
		boolean flag = false;
		if (register != null && register.matches("^R[0-7]$")) //Is a regular register
		{
			//	System.out.println("reg val:" + )
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

	/**
	 * Checks to see if the candidate address is a valid address
	 * @param address	candidate address
	 * @return	True if the candidate address is in the proper form and within the specified ranges
	 */
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

	/**
	 * Checks to see if the candidate index is a valid index
	 * @param index	candidate index
	 * @return	True if the candidate index is in the proper form and within the specified ranges
	 */
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

	/**
	 * Checks to see if the candidate trap vector is a valid trap vector
	 * @param trap	candidate trap vector
	 * @return	True if candidate trap vector is a in the proper form and within the specified ranges
	 */
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

	/**
	 * Checks to see if the candidate immediate is a valid immediate
	 * @param immediate	candidate immediate
	 * @return	True if candidate immediate is a in the proper form and within the specified ranges
	 */
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
					if (Integer.parseInt(immediate.substring(1), 16) >= 0 && Integer.parseInt(immediate.substring(1), 16) <= 31)
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

	/**
	 * Strips formatting and returns the raw value of an incoming immediate. Must only be given well formed immediates. 
	 * @param key	incoming immediate
	 * @return	raw value of incoming immediate
	 */
	private short immVal(String key) {
		short returnVal = 0;
		if(SymbolTable.isDefined(key)) //Symbol
		{
			returnVal = SymbolTable.getValue(key);
		}
		else if(key.charAt(0) == '#') //Decimal number
		{
			returnVal =  (short) (Short.parseShort(key.substring(1)) & 31);
		}
		else //Must be hex
		{
			return Short.parseShort(key.substring(1), 16);
		}
		return returnVal;
	}

	/**
	 * Strips formatting and returns the raw value of an incoming register. Must only be given well formed registers. 
	 * @param key	incoming register
	 * @return	raw value of incoming register
	 */
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

	/**
	 * Strips formatting and returns the raw value of an incoming address. Must only be given well formed addresses. 
	 * @param key	incoming address
	 * @return	raw value of incoming address
	 */
	private short addrVal(String key)
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

		return (short) returnVal;
	}

	/**
	 * Strips formatting and returns the raw value of an incoming index. Must only be given well formed indexes.
	 * @param key	incoming index
	 * @return	raw value of incoming index
	 */
	private short indexVal(String key)
	{
		short returnVal = 0;

		if (key.charAt(0)=='x')
		{
			returnVal = Short.valueOf(key.substring(1), 16);
		}
		else if (SymbolTable.isDefined(key))
		{ //symbol
			returnVal = SymbolTable.getValue(key);
		}
		else
		{ // # number
			returnVal = Short.parseShort(key.substring(1));
		}
		return returnVal;
	}

	/**
	 * Returns the raw value of a literal
	 * @param key text representation of a literal
	 * @return	raw value of a literal
	 */
	private short literalVal(String key)
	{
		short returnVal = 0;

		if (key.startsWith("#"))
		{//decimal value
			returnVal = Short.valueOf(key.substring(1));
		}
		else if(key.startsWith("x"))
		{//Hex value
			returnVal = Short.valueOf(key.substring(1), 16);
		}
		else
		{//Must be symbol
			returnVal = SymbolTable.getValue(key);
		}

		return returnVal;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private boolean isValLiteral(String key)
	{
		boolean flag = false;
		try{


			if (key != null)
			{
				if(key.startsWith("=x") && Integer.valueOf(key.substring(2)) >= -32768 && Integer.valueOf(key.substring(2)) <= 32767)
				{
					flag = true;
				}
				else if (key.startsWith("=#") && Integer.valueOf(key.substring(2)) >= -32768 && Integer.valueOf(key.substring(2)) <= 32767)
				{
					flag = true;	
				}
				else if(key.startsWith("x") && Integer.valueOf(key.substring(1)) >= -32768 && Integer.valueOf(key.substring(1)) <= 32767)
				{
					flag = true;	
				}
				else if(key.startsWith("#")  && Integer.valueOf(key.substring(1)) >= -32768 && Integer.valueOf(key.substring(1)) <= 32767)
				{
					flag = true;
				}
				else if(SymbolTable.isDefined(key) && SymbolTable.getValue(key) >= -32768 && SymbolTable.getValue(key) <= 32767)
				{
					flag = true;
				}
			}
		}catch(NumberFormatException e)
		{}
		return flag;
	}

	/**
	 * Strips formatting and returns the raw value of an incoming trap vectors. Must only be given well formed trap vectors. 
	 * @param key	incoming trap vector
	 * @return	raw value of incoming trap vector
	 */
	private short trapVal(String key){
		short returnVal = 0;

		if (key.charAt(0)=='x') {
			returnVal = Short.parseShort(key.substring(1), 16);
		} else if (SymbolTable.isDefined(key)) { //symbol
			returnVal = SymbolTable.getValue(key);
		} else { // number
			returnVal = Short.parseShort(key.substring(0));
		}
		return returnVal;
	}


	/**
	 * Populates argTokArray with tokens from the incoming argument
	 * @param amount	indicates the number of tokens desired from the incoming argument
	 * @param st	incoming argument
	 */
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

	/**
	 * Validates an line of source code by processing the arguments of an operation. Writes the corresponding text record, or end record, to
	 * the p2File. Outputs an error to console, and to the p2File if the arguments are bad. Also increments the location counter.
	 * @param op	operation
	 * @param arg	arguments of an operation
	 */
	private void processWrite(String op, String arg) //Write op with arguments to p2File
	{
		short DR, SR1, SR2, imm5, pgoffset9, index6, BaseR, SR, trapvect8;
		int addr;
		boolean badArg = false;
		boolean doNothing = false;
		StringTokenizer st = new StringTokenizer(arg, ",");
		String input = new String("T");
		short finalOp = 0;

		if (MachineOpTable.isOp(op))
		{
			finalOp = MachineOpTable.getOp(op);
		}


		//Figure out which op you have
		if (op.equals("ADD"))
		{
			getArgTokens(3, st);
			//Valid regs; DR,SR1,SR2 and the final SR2 is NOT a symbol, last SR2, if it is a symbol, is always an IMM5
			if (!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1])
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
			else if (!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValImm(argTokArray[2])) 
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
			if (!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1])
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
			else if (!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValImm(argTokArray[2])) 
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
			if(!st.hasMoreElements() && isValAddr(argTokArray[0]))
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
			if(!st.hasMoreElements() && isValAddr(argTokArray[0]))
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
			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValIndex(argTokArray[1]))
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
			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValIndex(argTokArray[1]))
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
			//Load has valid R1 and then followed by a literal
			if (!st.hasMoreElements() && isValReg(argTokArray[0]) && isValLiteral(argTokArray[1]))
			{
				DR = regVal(argTokArray[0]);
				pgoffset9 = literalVal(argTokArray[1]);
				pgoffset9 &= 0x1FF;

				finalOp |= DR << 9;
				finalOp |= pgoffset9;

			}
			//Load has R1, an the next one is an address that isn't in the literal table
			else if (!st.hasMoreElements() && isValReg(argTokArray[0]) && (argTokArray[1] != null)
					&& !LiteralTable.isLitereal(argTokArray[1]) && isValAddr(argTokArray[1]))
			{
				DR = regVal(argTokArray[0]);
				pgoffset9 = addrVal(argTokArray[1]);
				finalOp |= DR << 9;
				finalOp |= pgoffset9;
			}
			else
			{
				badArg = true;
			}

		}
		else if (op.equals("LDI"))
		{
			getArgTokens(2, st);

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
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

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValIndex(argTokArray[2]))
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

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
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
		else if (op.equals("NOT"))
		{
			getArgTokens(2, st);

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]))
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

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
			{
				SR = regVal(argTokArray[0]);
				pgoffset9 = addrVal(argTokArray[1]);

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

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValAddr(argTokArray[1]))
			{
				SR = regVal(argTokArray[0]);
				pgoffset9 = addrVal(argTokArray[1]);

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

			if(!st.hasMoreElements() && isValReg(argTokArray[0]) && isValReg(argTokArray[1]) && isValIndex(argTokArray[2]))
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

			if(!st.hasMoreElements() && isValTrapVect(argTokArray[0]))
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
			if (!st.hasMoreElements() && isValAddr(argTokArray[0]))
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
			getArgTokens(1, st);
			if (!foundEndLine){
				foundEndLine = true;
				if(argTokArray[0] != null) //If there are args
				{
					if(isValAddr(argTokArray[0])) //If args are good, and I haven't already found the end line
					{
						if (argTokArray[0].startsWith("x"))
						{
							p2File.input("E" + argTokArray[0].substring(1));			
						}
						else if (argTokArray[0].startsWith("#"))
						{
							p2File.input("E" + Integer.valueOf(argTokArray[0].substring(1), 16));
						}
						else //Must be symbol
						{
							p2File.input("E" + shortToHexString(SymbolTable.getValue(argTokArray[0])));
						}
					}
					else
					{
						badArg = true;
					}

				}
				else //If there are no args
				{
					p2File.input("E" + shortToHexString(startOfExecution)); //First line of execution

				}
			}
		}
		else if (op.equals(".EQU"))
		{
			doNothing = true;
		}
		else if (op.equals(".FILL"))
		{
			getArgTokens(1, st);
			doNothing = true;

			boolean needsRelocation = false;
			if (argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]) && SymbolTable.isRelative(argTokArray[0]))
			{
				needsRelocation = true;
			}


			if(!st.hasMoreElements() && argTokArray[0] != null)
			{
				try{
					//Decimal
					if (argTokArray[0].startsWith("#") && Integer.valueOf(argTokArray[0].substring(1)) >= -32786
							&& Integer.valueOf(argTokArray[0].substring(1)) <= 32767)
					{
						if (needsRelocation)
						{
							p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)) + "M1");
						}
						else
						{
							p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1))));							
						}

					}
					//Hex
					else if(argTokArray[0].startsWith("x") && Integer.valueOf(argTokArray[0].substring(1), 16) >= 0
							&& Integer.valueOf(argTokArray[0].substring(1), 16) <= 0xFFFF)
					{
						if (needsRelocation)
						{
							p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)) + "M1");								
						}
						else
						{
							p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)));							
						}

					}
					else
					{
						badArg = true;
					}
				}
				catch(NumberFormatException e)
				{
					badArg = true;
				}
			}
			else
			{
				badArg = true;
			}

			LocationCounter.incrementAmt(1);
		}

		else if (op.equals(".STRZ"))
		{
			getArgTokens(1, st);
			doNothing = true;
			if (!st.hasMoreElements() && argTokArray[0] != null && argTokArray[0].endsWith("\"") && argTokArray[0].startsWith("\""))
			{
				int index = 1;
				//Write all the array values to text records
				while(argTokArray[0].length() > index + 1)
				{
					System.out.println(argTokArray[0].charAt(index));
					p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString((int)argTokArray[0].charAt(index)));
					LocationCounter.incrementAmt(1);
					index++;
				}

				//Write the null terminator to a text record
				p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(0));				
				LocationCounter.incrementAmt(1);
			}
			else
			{
				badArg = true;
			}

		}
		else if (op.equals(".BLKW"))
		{
			getArgTokens(1, st);
			doNothing = true;

			try
			{
				if (!st.hasMoreElements() && argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]))
				{
					if(!SymbolTable.isRelative(argTokArray[0]))
					{
						LocationCounter.incrementAmt(SymbolTable.getValue(argTokArray[0]));
					}
					else
					{
						badArg = true;
					}
				}
				else if(!st.hasMoreElements() && argTokArray[0] != null && argTokArray[0].startsWith("#")&& Integer.valueOf(argTokArray[0].substring(1)) >= 1
						&& Integer.valueOf(argTokArray[0].substring(1)) <= 0xFFFF)
				{
					LocationCounter.incrementAmt(Integer.valueOf(argTokArray[0].substring(1)));
				}
				else if (!st.hasMoreElements() && argTokArray[0] != null && argTokArray[0].startsWith("x")&& Integer.valueOf(argTokArray[0].substring(1), 16) >= 1
						&& Integer.valueOf(argTokArray[0].substring(1) , 16) <= 0xFFFF)
				{
					LocationCounter.incrementAmt(Integer.valueOf(argTokArray[0].substring(1) , 16));
				}
				else
				{
					badArg = true;
				}

			}
			catch(NumberFormatException e)
			{
				badArg = true;
			}
		}
		else if(op.contentEquals(".ORIG"))
		{
			doNothing = true;
		}


		if (badArg)
		{
			//Something is wrong with the arguements, putting in an errorline comment
			System.out.println("ERROR: Improperly formed argument at line " + body.getReport());
			p2File.input(";ERROR IN ARGUMENT ON THIS LINE: " + op + " " + arg);
		}
		else if (!foundEndLine)
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

			if (needsRelocation && !doNothing)
			{
				p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())).concat(shortToHexString(finalOp)) + "M0"); //Get finished op, turn it into a string, append it to the output string, and the write it to the file
			}
			else if (!doNothing){
				p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())).concat(shortToHexString(finalOp))); //Get finished op, turn it into a string, append it to the output string, and the write it to the file				
			}

			if(MachineOpTable.isOp(op))
			{
				LocationCounter.incrementAmt(1);
			}
		}
	}

	/**
	 * RET and DEBUG have no arguments, and it takes either one, then writes the appropriate text record to p2File for them.
	 * @param op	RET or DEBUG
	 */
	private void processWrite(String op) //Write op with no arguments to p2File
	{
		if (op.equals(".END"))
		{
			p2File.input("E" + shortToHexString(startOfExecution)); //First line of execution
		}
		else
		{
			String input = new String("T");
			p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())).concat(shortToHexString(MachineOpTable.getOp(op)))); //Get op from machineop table, turn it into a string, append it to the output string, and the write it to the file
			LocationCounter.incrementAmt(1);			
		}

	}

	/**
	 * Takes a short input and returns a four character hex representation of it from 0000 to FFFF 
	 * @param data	input
	 * @return	hex representation from 0000 to FFFF
	 */
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
	/**
	 * Takes an int input and returns a four character hex representation of it from 0000 to FFFF 
	 * @param data	input
	 * @return	hex representation from 0000 to FFFF
	 */
	public static String intToHexString(int data) {
		String returnVal = Integer.toHexString(data);
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