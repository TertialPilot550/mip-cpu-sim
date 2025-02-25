package software.exceptions;

public class InvalidAssemblyFileException extends Exception {
	
	private static final long serialVersionUID = 8151775138535095928L;

	public InvalidAssemblyFileException() {
		super("Provided file for assembly is not valid.");
	}

}
