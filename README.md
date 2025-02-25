# mip-cpu-sim

## Right now:
Simulator of a simple 32 bit cpu implementing the mips instruction set. 

## Still working:

Also includes tools for basic assembly, and 'compiling' a defined c-like language to assembly, which can than be run on the simulator.


## Modules

### _main

Contains the code that configures and runs the simulation. 
	
For testing this is what loads programs and makes decisions about 
the system.


### hardware

Contains code related to the hardware implementation. The interface 
for working with this, Simulator Computer, simplifies the interactions 
between the simulator and the assembler, and is all that needs to be 
touched, besides the implementation CPU of the interface.
	
```
SimulatorComputer computer = new CPU(); // interface implementation
	
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
```
		
		

### software:

Contains code related to any software generation process, including assembly and linking. 

All of these classes take on one stage of the build process, with a simple object to call methods.
	

##### Assembly
	
	Assembler asm = new Assembler();
	
	/**
	 * High Level function that assembles the file with the provided file path.
	 * 
	 * Manages program object pieces and adjust statements as necessary.
	 * 
	 * @param filepath
	 * @return a Program object
	 * @throws Exception
	 */
	public Protogram assemble(String filepath) throws Exception;
	


