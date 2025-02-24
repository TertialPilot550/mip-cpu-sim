
package main;

import datatypes.Program;
import processor.CPU;
import software.Assembler;

public class Simulator {

	// Static resources
	private static Assembler asm;
	private static CPU cpu;
		
	// init static resources
	private static void init() {
		asm = new Assembler();
		cpu = new CPU();
	}
	
	public static void main(String[] args) {
		init();
		
		try {
			runProgram("./test.s");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runProgram(String filepath) throws Exception {
		
		Program p = asm.assemble(filepath);
		cpu.flash(p);
		cpu.start();
				
	}
	
	
	
}
