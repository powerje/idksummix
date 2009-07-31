package summixAssembler;



public class Token {
	
	private TokenType type;
	private String text; 
	/**
	 * @param args
	 */
	
	Token(String text, TokenType type)
	{
		this.type = type;
		this.text = text;
	}
	
	String getText()
	{
		return text;
	}
	
	TokenType getType()
	{
		return type;
	}
}