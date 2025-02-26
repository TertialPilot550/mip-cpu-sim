package hardware.exceptions;

public class MemoryAccessException extends Exception {

	private static final long serialVersionUID = -4940412139218200577L;

	public MemoryAccessException() {
		super("Data too large for expected memory space, load failed.");
	}
}
