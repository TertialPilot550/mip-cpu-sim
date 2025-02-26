package software.exceptions;

public class DataStatementArgumentException extends Exception {

	private static final long serialVersionUID = -3507021031146322699L;

	public DataStatementArgumentException() {
		super("Error parsing static data, improper number of element per line.");
	}

}
