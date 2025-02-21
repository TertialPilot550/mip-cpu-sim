
package main;

import processor.CPU;
import processor.MipsIsa;
import software.Assembler;
import software.Program;

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
