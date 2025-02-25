package hardware.exceptions;

public class InstructionNotSupportedException extends Exception {

	private static final long serialVersionUID = -4948080613406485354L;

	public InstructionNotSupportedException() {
		super("ERROR: This instruction is not supported.");
	}
}
