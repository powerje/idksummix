package summixAssembler;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * @author Michael Pinnegar
 *
 */


public class TextFile {
	
	
	/** The main storage body for each string that represents the slurped file*/
	private ArrayList<String> body;
	/** The horizontal depth into a string of the body array*/ 
	private int posPointer;
	/** The vertical depth down the rows of strings in the body array*/
	private int rowPointer;
	
	/**
	 * Constructor for TextFile
	 * 
	 * @param inputFilename Name of the file to be slurped
	 * @throws IOException Missing file
	 */
	
	public TextFile(String inputFilename) throws IOException
	{
		body = new ArrayList<String>();
		posPointer = 0;
		rowPointer = 0;
		
		BufferedReader in = new BufferedReader(new FileReader(inputFilename));
		
		while (in.ready()) //While not end of file, take in another line
		{
			String currentInput = in.readLine();
			body.add(currentInput);
		}
		
		in.close();
	}
	
	public String getToken()
	{
		String rVal = body.get(rowPointer); //Get the string out of the array located at the rowPointer
		rVal = rVal.substring(posPointer); //Get the portion of the string to the right of the posPointer
		int numOfSpaces = 0;
		boolean haveGottenToken = false, insideQuotes = false, insideComment = false;
		String temp, returnTok = "";
		
		StringTokenizer st = new StringTokenizer(rVal,"; \"",true);
	    
		while (!haveGottenToken && st.hasMoreTokens()) //Until a well formed token is ready, or we run out of tokens, keep taking them out
		{
	    	temp = st.nextToken();
	    	
	    	if (insideComment) //If inside of a comment, just add everything till I get to the end
	    	{
	    		returnTok = returnTok.concat(temp);
	    	}
	    	else //I'm not inside a comment, then we want to be careful
	    	{
		    	if (insideQuotes && !temp.startsWith("\"")) //inside quote, but haven't found the ending quotes
		    	{
		    		returnTok = returnTok.concat(temp);
		    	}
		    	else if (insideQuotes && temp.startsWith("\"")) //inside quote, but just found the ending quotes, token done
		    	{
		    		returnTok = returnTok.concat(temp);
		    		insideQuotes = false;
		    		haveGottenToken = true;
		    	}
		    	else //I'm not inside of a comment, and I'm not inside of a quote
		    	{
		    		switch (temp.charAt(0)) //Make decision based on the first element
		    		{
		    			case '"': //Beginning of a set of quotation set
		    				insideQuotes = true;
		    				returnTok = returnTok.concat(temp);
		    				break;
		    			case ';': //Beginning a comment
		    				insideComment = true;
		    				returnTok = returnTok.concat(temp);
		    				break;
		    			case ' ': //Useless space, count for pos increment
		    				numOfSpaces++;
		    				break;
		    			case '\t': //Useless tab, count for pos increment
		    				numOfSpaces++;
		    				break;
		    			default: //Just an alphanumeric string, we want the whole thing
		    				haveGottenToken = true;
		    				returnTok = returnTok.concat(temp);
		    		}
		    	}
	    	}

//	    	System.out.println("Token extracted:" + temp); //Debugging code, REMOVEME
//	    	System.out.println("Token built:" + returnTok); //Debugging code, REMOVEME
	    	}
		
//		System.out.print("Token to be returned:" + returnTok); //Debugging code, REMOVEME
		
		if (body.get(rowPointer).substring(posPointer).isEmpty()) //If at end of a line, go down to the next line with the pos pointer
		{
			rowPointer++;
			posPointer = 0;
		}
		else //You're not at the end of a line, so you have to update the pos pointer based on the size of the token extracted 
		{
			posPointer += returnTok.length() + numOfSpaces;
		}
		
		return returnTok;
	}
	
	public String getLine()
	{
		String rVal = body.get(rowPointer); //Get the string out of the array located at the rowPointer
		rVal = rVal.substring(posPointer); //Get the portion of the string to the right of the posPointer
		
		rowPointer++;
		posPointer = 0;
		
		return rVal;
	}
	
	
	public void write(String outputFilename) throws IOException
	{
		int i = 0;
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
		while(i < body.size()) //Print out each line in the body representation of the text file
		{
			out.println(body.get(i));
			i++;
		}
		out.close();
	}
	
	public void input(String entry)
	{
		body.add(entry);
	}
	
	public void display()
	{
		int i = 0;
		
		while(i < body.size())
		{
			System.out.println(body.get(i));
			i++;
		}
	}
	
	public void reset()
	{
		posPointer = 0;
		rowPointer = 0;
	}
	
	public boolean isEndOfFile()
	{
		if (body.size() == 0 || (body.size() == 1 && body.get(0).isEmpty())) //If file is empty, you're automatically at EoF
		{
			return true;
		}
		else if(body.size() - 1 < rowPointer) //If the file isn't empty, is the horizontal marker too far?
		{
			return true;
		}
		else //If the file isn't empty, and the marker isn't at the last spot in the file, then you must not be at EoF
		{
			return false;
		}
	}
	
}
