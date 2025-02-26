package hardware.exceptions;

public class HardwareMemoryLoadException extends Exception {

	private static final long serialVersionUID = 7813403174869173808L;

	public HardwareMemoryLoadException(int address) {
		super("HARDWARE MEMORY LOADING FAILURE. Not enough space found at address: " + address);
	}

}
