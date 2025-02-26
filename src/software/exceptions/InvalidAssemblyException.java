package software.exceptions;

public class InvalidAssemblyException extends Exception {

	private static final long serialVersionUID = 6367002302931657506L;

	public InvalidAssemblyException() {
		super("Invalid assembly syntax in the provided assembly file. Check your syntax and re-assemble");
	}
}
