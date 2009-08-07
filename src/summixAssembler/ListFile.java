package summixAssembler;

public class ListFile {
	
	public boolean commentSource, commentP2;
	
	private String ProcessLineSource(TextFile source)
	{
		String listSource = null, line;
		Token token;
		
		token = source.getToken();
		if(token.getType() == TokenType.COMMENT)
		{
			listSource = token.getText();
			commentSource=true;
		}
		else
		{
			line = source.getLine();
			// add original token to the line, following by rest of the line.
			listSource = token.getText();
			listSource.concat("\t");
			listSource.concat(line);
		}
		return listSource;
	}
	
	private String ProcessLineP2Address(TextFile p2)
	{
		String listP2Address = null;
		String tempP2Address = p2.getLine();
		
		listP2Address = tempP2Address.substring(1,4);
		
		return listP2Address;
	}
	
	private String ProcessLineP2Op(TextFile p2)
	{
		String listP2Op = null;
		String tempP2Op = p2.getLine();
		
		listP2Op = tempP2Op.substring(1,4);
		
		return listP2Op;
	}
	
	private int hexstringToInt(String input) {
		int returnVal = 0; // needs initialized in the case an exception is caught
		try {
			returnVal = Integer.valueOf(input, 16).intValue();
		} catch (NumberFormatException e)	{
			System.out.println("");
			System.exit(-1); //error
		}
		return returnVal;
	}
	
	private String OutputBinaryP2(String op)
	{
		String binaryString = null;
		String tempString = null;
		int opInt = hexstringToInt(op);
		
		tempString = Integer.toBinaryString(opInt);
		int len = tempString.length();
		while(len < 16)
		{
			binaryString.concat("0");
			len++;
		}
		binaryString.concat(tempString);
		
		return binaryString;
	}
	
	
	public String CreateListFile(TextFile p1, TextFile p2)
	{
		String listFile = null;
		
		
		return listFile;
	}

}
