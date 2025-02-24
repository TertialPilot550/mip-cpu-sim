package exceptions;

public class OverflowException extends Exception {
	private static final long serialVersionUID = -9167408816983158770L;
	public OverflowException() {
		super("ARITHMATIC ERROR: OVERFLOW");
	}
}
