package summixAssembler;

public class Pass2 {
	TextFile body;
	
	public Pass2(TextFile incomingSource)
	{
		body = incomingSource;
	}
	
	public TextFile processFile()
	{
		//This is where all the work has to be done. This is just temp code to make the complier happy for the assembler main() class
		return body;
	}
}
