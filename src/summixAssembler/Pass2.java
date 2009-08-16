package summixAssembler;

import java.util.ArrayList;
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
	/** The beginning of execution*/
	private short startOfExecution = 0;
	/** True if there is an .ORIG in the source code */
	private boolean foundFirstHeader = false;
	private int foundOpAt;

	/**
	 * Creates a Pass2 object and readies it to process p1File.
	 * 
	 * @param incomingSource	The p1File generated by pass1 	
	 */
	public Pass2(TextFile incomingSource)
	{
		body = incomingSource;
		body.reset();
		body.stripComments();
	}

	/**
	 * Populates the array token_array with up to four tokens from the current line of body.
	 * The total number of tokens on that line is stored in numberOfTokens.
	 */
	private void getTokens()
	{
		//Initialize array to nothing
		token_array[0] = null;
		token_array[1] = null;
		token_array[2] = null;
		token_array[3] = null;

		numberOfTokens = 0;
		Token temp = new Token(";", TokenType.COMMENT);


		//Fill in array
		while(temp.getType() != TokenType.EOL)
		{ 
			temp = body.getToken();
			//If array is full, keep taking out tokens, but don't add them to the array
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
		//while(not end record or end of file) {output text records}
		while(!body.isEndOfFile() && !foundEndLine)
		{
			processAnyLine();
		}

		if (!foundEndLine)
		{
			System.out.println("ERROR: No end of line record present in sourcecode. Expected at line " + body.getReport());
		}

		SymbolTable.checkTable();
		SymbolTable.printPass2Table(p2File);
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
		else if (numberOfTokens == 2 || numberOfTokens == 3 || numberOfTokens == 4)
		{
			processTextLine();
		}
		//else{numberOfTokens == 1, so do nothing}
	}

	/**
	 * Checks to see if a well formed line of source code has an op in it. If it does not, an error code is printed to the screen and written to the object file.
	 * Otherwise processWrite() is called on that line of code. 
	 */
	private void processTextLine()
	{
		if (isAnOp(token_array[0].getText()) && (numberOfTokens == 3 || numberOfTokens == 2)) //<op><maybe arg>
		{
			foundOpAt = 0;
			//check to see if there are args
			if (numberOfTokens == 3) //Then it has an arg; <op><arg>
			{
				processArgOp(token_array[0].getText(), token_array[1].getText());
			}
			else //(Number of tokens == 2) It has no arg; <op>
			{
				processNoArgOp(token_array[0].getText());
			}
		}
		else if(isAnOp(token_array[1].getText()) && (numberOfTokens == 4 || numberOfTokens == 3)) //<label><op><maybe arg>
		{			
			foundOpAt = 1;
			//check to see if there are args
			if (numberOfTokens == 4) //There is an arg; <label><op><arg>eolTok
			{
				processArgOp(token_array[1].getText(), token_array[2].getText());
			}
			else //There is no arg; <label><op>eolTok
			{
				processNoArgOp(token_array[1].getText());
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
		if (!validAddressPage((short)returnVal)) {
			//page rolls over
			System.out.println("ERROR: Address field rolls over to next page at line " + body.getReport());
		}
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
	private short literalAddressVal(String key)
	{
		short returnVal = 0;

		if (key.startsWith("#"))
		{//decimal value constant
			returnVal = Short.valueOf(key.substring(1));
		}
		else if(key.startsWith("x"))
		{//Hex value constant
			returnVal = Short.valueOf(key.substring(1), 16);
		}
		else if(key.startsWith("=#"))
		{//literal	
			returnVal = LiteralTable.getAddress(key.substring(2));
		}
		else if(key.startsWith("=x"))
		{//literal
			returnVal = LiteralTable.getAddressFromHex(key.substring(2));
		}
		else
		{//Must be symbol
			returnVal = SymbolTable.getValue(key);
		}

		returnVal &= 0x1FF;
		return returnVal;
	}

	/**
	 * Returns true if the given string is a valid literal
	 * @param key string to test whether or not it is a literal in the form x<hex number> or #<decimal number>
	 * @return true if the given string is a valid literal
	 */
	private boolean isValLiteral(String key)
	{
		boolean flag = false;
		try{
			if (key != null)
			{
				if(key.startsWith("=x") && (Integer.parseInt(key.substring(2), 16) >= -32768) && (Integer.parseInt(key.substring(2), 16) <= 32767))
				{
					flag = true;
				}
				else if (key.startsWith("=#") && Integer.valueOf(key.substring(2)) >= -32768 && Integer.valueOf(key.substring(2)) <= 32767)
				{
					flag = true;	
				}
				else if(key.startsWith("x") && Integer.valueOf(key.substring(1), 16) >= -32768 && Integer.valueOf(key.substring(1)) <= 32767)
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
	private void processArgOp(String op, String arg) //Write op with arguments to p2File
	{

		short DR, SR1, SR2, imm5, pgoffset9, index6, BaseR, SR, trapvect8;
		int addr;
		//Figure out which op you have
		StringTokenizer st = new StringTokenizer(arg, ",");
		String input = new String("T");
		boolean badArg = false;

		if (PseudoOpTable.isPseudoOp(op))
		{
			processPuesdoOpWithArg(op, arg);
		}
		else if(foundFirstHeader) //Else is a machine op. If we have found the first header record, then this is fine!
			//Otherwise it's an error because we got a machine op before the header!
		{
			short finalOp = MachineOpTable.getOp(op);
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
				else
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
					pgoffset9 = literalAddressVal(argTokArray[1]);
					pgoffset9 &= 0x1FF;

					finalOp |= DR << 9;
					finalOp |= pgoffset9;

				}
				//Load has R1, an the next one is an address that isn't in the literal table
				else if (!st.hasMoreElements() && isValReg(argTokArray[0]) && (argTokArray[1] != null)
						&& !LiteralTable.isLitereal(argTokArray[1]) && isValAddr(argTokArray[1]))
				{
					p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())) + finalOp);
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


			if (badArg) //If badargs, output error
			{
				//Something is wrong with the arguments, putting in an errorline comment
				System.out.println("ERROR: Improperly formed argument at line " + body.getReport());
				p2File.input(";ERROR IN ARGUMENT ON THIS LINE: " + op + " " + arg);
			}
			else //If not badArgs, means args are good! Process them.
			{
				boolean needsRelocation = false;
				boolean needsExternalRecord = false;
				int externalHere = 0;
				//Check to see if the args have a relative symbol in them
				if (argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]) && SymbolTable.isRelative(argTokArray[0]))
				{
					needsRelocation = true;
				}
				if (argTokArray[1] != null && SymbolTable.isDefined(argTokArray[1]) && SymbolTable.isRelative(argTokArray[1]))
				{
					needsRelocation = true;
				}
				if (argTokArray[2] != null && SymbolTable.isDefined(argTokArray[2]) && SymbolTable.isRelative(argTokArray[2]))
				{
					needsRelocation = true;
				}
				
				//Check to see if the args have an .EXT symbol in them
				if (argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]) && SymbolTable.isRelative(argTokArray[0]) && SymbolTable.isExt(argTokArray[0]))
				{
					needsExternalRecord = true;
					externalHere = 0;
				}
				if (argTokArray[1] != null && SymbolTable.isDefined(argTokArray[1]) && SymbolTable.isRelative(argTokArray[1]) && SymbolTable.isExt(argTokArray[1]))
				{
					needsExternalRecord = true;
					externalHere = 1;
				}
				if (argTokArray[2] != null && SymbolTable.isDefined(argTokArray[2]) && SymbolTable.isRelative(argTokArray[2]) && SymbolTable.isExt(argTokArray[2]))
				{
					needsExternalRecord = true;
					externalHere = 2;
				}
				//If the arg contains a relocatable symbol AND the program is relative
				if (needsRelocation && LocationCounter.isRelative() && !needsExternalRecord)
				{
					p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())).concat(shortToHexString(finalOp)) + "M0"); //Get finished op, turn it into a string, append it to the output string, and the write it to the file
				}
				else if(needsRelocation && LocationCounter.isRelative() && needsExternalRecord)
				{
					p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())).concat(shortToHexString(finalOp)) + "M0X" + argTokArray[externalHere]); //Get finished op, turn it into a string, append it to the output string, and the write it to the file
				}
				else
				{
					p2File.input(input.concat(shortToHexString(LocationCounter.getAddress())).concat(shortToHexString(finalOp))); //Get finished op, turn it into a string, append it to the output string, and the write it to the file			
				}
				LocationCounter.incrementAmt(MachineOpTable.getSize(op)); //Increment location counter for the size of the machine op
			}
		}
		else
		{
			p2File.input(";ERROR non-comment/non-blank line preceeds .ORIG op.");
		}
	}
	/**
	 * Returns true if the string arg is a valid argument for .END
	 * @param arg the argument to test
	 * @return true iff the argument is valid for .END
	 */ 
	private boolean isValENDArg(String arg)
	{
		boolean flag = false;

		try
		{
			if (arg.startsWith("x") && Integer.parseInt(arg.substring(1), 16) >= 0 && Integer.parseInt(arg.substring(1), 16) <= 0xFFFF)
			{
				flag = true;
			}
			else if(SymbolTable.isDefined(arg) && SymbolTable.getValue(arg) >= 0 && SymbolTable.getValue(arg) <= 0xFFFF)
			{
				flag = true;
			}
		}
		catch(NumberFormatException e)
		{}

		return flag;
	}

	/**
	 * Handles pseudo operations with arguments
	 * @param op the pseudo operation to handle
	 * @param arg the argument of the given pseudo operation
	 */
	private void processPuesdoOpWithArg(String op, String arg) {

		StringTokenizer st = new StringTokenizer(arg, ",");

		if(!op.equals(".ORIG") && !foundFirstHeader)
		{
			p2File.input(";ERROR non-comment/non-blank line preceeds .ORIG op.");
		}
		else if(op.equals(".ORIG"))
		{//.ORIG has been processed by pass1, print out the header file. If you find a second one, print an error file.
			if(!foundFirstHeader)
			{
				foundFirstHeader = true;
				p2File.input(body.getHeader());
				String header = new String(body.getHeader());//Get header
				String start = new String(header.substring(7, 11));//Get start location from the header
				LocationCounter.set(Integer.parseInt(start, 16), header.endsWith("R"));//Set LC to start location
				startOfExecution = (short)Integer.parseInt(start, 16);
			}
			else
			{
				p2File.input(";ERROR multiple .ORIG entries.");
			}
		}
		else if (op.equals(".END"))
		{
			getArgTokens(1, st);
			if(!st.hasMoreTokens() && argTokArray[0] != null && isValENDArg(argTokArray[0])) //prove args are good
			{
				printLiterals();
				if(SymbolTable.isDefined(argTokArray[0]))
				{
					p2File.input("E" + shortToHexString(SymbolTable.getValue(argTokArray[0])));
				}
				else//Must be hex value as a string, use it directly after removing x
				{

					p2File.input("E" + argTokArray[0].substring(1));
				}
				foundEndLine = true;
				System.out.println("Found the .END op on line " + body.getReport() + " compilation complete.");
				System.out.println("");
			}
			else //badargs
			{
				System.out.println("ERROR: Improperly formed argument at line " + body.getReport());
				p2File.input(";ERROR IN ARGUMENT ON THIS LINE: " + op + " " + arg);
			}
		}
		else if (op.equals(".EQU"))
		{/*Do nothing*/}
		else if(op.equals(".ENT"))
		{/*Do nothing*/}
		else if(op.equals(".EXT"))
		{/*Do nothing*/}
		else if (op.equals(".FILL"))
		{
			getArgTokens(1, st);
			boolean badArgs = false;
			boolean needsExternalRecord = false;
			boolean needsRelocation = false;
			
			
			if (argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]) && SymbolTable.isRelative(argTokArray[0]) && LocationCounter.isRelative())
			{
				needsRelocation = true;
			}
			
			if (argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0]) && SymbolTable.isRelative(argTokArray[0]) && LocationCounter.isRelative() && SymbolTable.isExt(argTokArray[0]))
			{
				needsExternalRecord = true;
			}


			if(!st.hasMoreElements() && argTokArray[0] != null)
			{
				try{
					//Decimal
					if (argTokArray[0].startsWith("#") && Integer.valueOf(argTokArray[0].substring(1)) >= -32786
							&& Integer.valueOf(argTokArray[0].substring(1)) <= 32767)
					{
						p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1))));	
					}//Hex
					else if(argTokArray[0].startsWith("x") && Integer.valueOf(argTokArray[0].substring(1), 16) >= 0
							&& Integer.valueOf(argTokArray[0].substring(1), 16) <= 0xFFFF)
					{
						p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)));							
					}//Relative Symbol
					else if(needsRelocation && !needsExternalRecord)
					{
						p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)) + "M1");
					}//Relative .EXT Symbol
					else if (needsRelocation && needsExternalRecord)
					{
						p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)) + "M1X" + argTokArray[0]);
					}//Absolute symbol
					else if(SymbolTable.isDefined(argTokArray[0]) && !SymbolTable.isRelative(argTokArray[0]))
					{
						p2File.input("T" + shortToHexString(LocationCounter.getAddress()) +  intToHexString(Integer.valueOf(argTokArray[0].substring(1), 16)));
					}
					else
					{
						badArgs = true;
					}
				}
				catch(NumberFormatException e)
				{
					badArgs = true;
				}
			}
			else
			{
				badArgs = true;
			}

			LocationCounter.incrementAmt(1);
			if (badArgs)
			{
				System.out.println("ERROR: Improperly formed argument at line " + body.getReport());
				p2File.input(";ERROR IN ARGUMENT ON THIS LINE: " + op + " " + arg);
			}
		}
		else if (op.equals(".STRZ"))
		{
			getArgTokens(1, st);
			if (!st.hasMoreElements() && argTokArray[0] != null && argTokArray[0].endsWith("\"") && argTokArray[0].startsWith("\""))
			{
				int index = 1;
				//Write all the array values to text records
				while(argTokArray[0].length() > index + 1)
				{
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
				System.out.println("ERROR: Improperly formed argument at line " + body.getReport());
				p2File.input(";ERROR IN ARGUMENT ON THIS LINE: " + op + " " + arg);
			}

		}
		else if (op.equals(".BLKW"))
		{
			getArgTokens(1, st);

			boolean badArgs = false;
			try
			{
				if (!st.hasMoreElements() && argTokArray[0] != null && SymbolTable.isDefined(argTokArray[0])
						&& !SymbolTable.isRelative(argTokArray[0]) && SymbolTable.getValue(argTokArray[0]) >= 1
						&& SymbolTable.getValue(argTokArray[0]) <= 0xFFFF)
				{
					LocationCounter.incrementAmt(SymbolTable.getValue(argTokArray[0]));
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
					badArgs = true;
				}

			}
			catch(NumberFormatException e)
			{
				badArgs = true;
			}

			if(badArgs)
			{
				System.out.println("ERROR: Improperly formed argument at line " + body.getReport());
				p2File.input(";ERROR IN ARGUMENT ON THIS LINE: " + op + " " + arg);
			}
		}
	}




	/**
	 * RET and DEBUG have no arguments, and it takes either one, then writes the appropriate text record to p2File for them.
	 * @param op	RET or DEBUG
	 */
	private void processNoArgOp(String op) //Write op with no arguments to p2File
	{
		if(!op.equals(".ORIG") && !foundFirstHeader)
		{
			p2File.input(";ERROR non-comment/non-blank line preceeds .ORIG op.");
		}
		else if(op.equals(".ORIG"))
		{//.ORIG has been processed by pass1, print out the header file. If you find a second one, print an error file.
			if(!foundFirstHeader)
			{
				foundFirstHeader = true;
				p2File.input(body.getHeader());
				String header = new String(body.getHeader());//Get header
				String start = new String(header.substring(7, 11));//Get start location from the header
				LocationCounter.set(Integer.valueOf(start, 16), header.endsWith("R"));//Set LC to start location
				startOfExecution = (short)Integer.parseInt(start, 16);
			}
			else
			{
				p2File.input(";ERROR multiple .ORIG entries.");
			}
		}
		else if (op.equals(".END"))
		{
			printLiterals();
			foundEndLine = true;
			p2File.input("E" + shortToHexString(startOfExecution)); //First line of execution
			System.out.println("Found the .END op on line " + body.getReport() + " compilation complete.");
			System.out.println("");
		}
		else if(op.equals("RET"))
		{
			p2File.input("T" + shortToHexString(LocationCounter.getAddress()) + shortToHexString(MachineOpTable.getOp(op)));
			LocationCounter.incrementAmt(1);
		}
		else if (op.equals("DEBUG"))
		{
			p2File.input("T" + shortToHexString(LocationCounter.getAddress()) + (shortToHexString(MachineOpTable.getOp(op)))); //Get op from machineop table, turn it into a string, append it to the output string, and the write it to the file
			LocationCounter.incrementAmt(1);
		}
		else //Command should have an arg, but doesn't
		{
			System.out.println("ERROR: Operation encountered that should have an arguement but does not.");
			p2File.input(";ERROR argOp encountered with no args.");
		}



	}


	/**
	 * Takes a short input and returns a four character hex representation of it from 0000 to FFFF 
	 * @param data	input
	 * @return	hex representation from 0000 to FFFF
	 */
	private String shortToHexString(short data) {
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
	private String intToHexString(int data) {
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

	/**
	 * Tests the given address to ensure there is no page roll over from the current instruction
	 * to the given address.
	 * @param addr The address to test
	 * @return validAddressPage is true iff there is no page roll over
	 */
	private boolean validAddressPage(short addr) {
		boolean returnVal = true;
		//bit mask off upper 7 bits
		addr &= 0x1FF; 
		if (addr==511) {	//if the offset would cause a rollover we have a bad address
			returnVal = false;
		}
		return returnVal;
	}

	private void printLiterals() {
		p2File = LiteralTable.printPass2Table(p2File);		
	}
}

