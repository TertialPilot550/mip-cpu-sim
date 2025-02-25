package hardware;

import software.datatypes.Program;

/**
 * Interface for configuring hardware for the simulation
 * 
 * @sammc
 */

public interface SimulatedComputer {
	
	/**
	 * Start executing the currently loaded program.
	 */
	public abstract void start();
	
	/**
	 * Loads a program object into the system. Once the start method is called, 
	 * the computer will execute this program
	 * 
	 * @param Program p
	 * @throws Exception
	 */
	public abstract void loadProgram(Program p) throws Exception;
	
	
	
}
