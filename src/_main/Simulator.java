
package _main;

import hardware.cpu.CPU;
import software.assembly.Assembler;
import software.datatypes.Program;
import software.datatypes.Protogram;
import software.linking.Linker;

public class Simulator {

	// Static resources
	private static Assembler asm;
	private static Linker link;
	private static CPU cpu;
		
	// init static resources
	private static void init() {
		asm = new software.assembly.Assembler();
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
		
		Protogram p = asm.assemble(filepath);
		Program pFinal = link.link(p);
		cpu.loadProgram(pFinal);
		cpu.start();
				
	}
	
	
	
}
