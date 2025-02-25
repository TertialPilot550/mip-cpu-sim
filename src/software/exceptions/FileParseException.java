package software.exceptions;

public class FileParseException extends Exception {

	private static final long serialVersionUID = 181406864589847238L;

	public FileParseException() {
		super("PARSING ERROR. CHECK YOUR SYNTAX.");
	}
}
