package software.exceptions;

import java.util.Arrays;

public class ImproperInstructionArgumentException extends Exception {

	private static final long serialVersionUID = 8668240726961335910L;

	public ImproperInstructionArgumentException(String[] args) {
		super("Improper argument count translating r-type statement: " + Arrays.toString(args));
	}

}
