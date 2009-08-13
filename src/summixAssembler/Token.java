package summixAssembler;


/**
 * The Token class provides a container for a token's text and type.
 * 
 * @author Michael Pinnegar
 * @see TokenType
 */
public class Token {
	
	/** The type of the token. */
	private TokenType type;
	/** The text of the token. */
	private String text; 
	
	/**
	 * Creates a new instance of token.
	 * @param text	The text of the token
	 * @param type	The type of the token
	 */
	Token(String text, TokenType type)
	{
		this.type = type;
		this.text = text;
	}
	
	/**
	 * Getter for text
	 * @return Text from the token
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * Getter for TokenType
	 * @return 
	 */
	public TokenType getType()
	{
		return type;
	}

	@Override
	public String toString ()
	{
		return "TokenText:" + text + " TokenType:" + type.toString();
	}
}