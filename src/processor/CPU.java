package processor;

import java.util.ArrayList;

import software.Program;
import software.Translator;

public class CPU implements MipsIsa {
	
	private int R[]; // Registers
	private int M[]; // Memory
	private int PC;  // Program Counter
	
	/**
	 * Memory Structure:
	 * 
	 * Data:
	 * [0x0000 -> 0x00FF]
	 * Text:
	 * [0x0100 -> 0x0FFF]
	 * 
	 * dynamic memory : stack and heap
	 * [0x1000 -> 0xFFFF]
	 */
	
	public CPU() {
		PC = 0x0100;          // Initialize Program Counter
		R = new int[32];   // Initialize Registers
		M = new int[262144]; // Initialize memory 2^18 ~ 262 kb
	}
	
	// start executing code in a fetch decode execute cycle starting at the default address of 0x0100
	public void start() {		
		fetchExecuteLoop();
	}
	
	/**
	 * Main loop for function of the cpu
	 */
	private void fetchExecuteLoop() {
		boolean debug = true;
		int i = 0; // cycle count
		while (PC <= 0x1000) {				// run	
			int instruction = M[PC];		// fetch
			
			if (debug) {
				System.out.printf("[%d] Instruction: %x - %s\n", i, instruction, Translator.seperateInstruction(instruction));
			}
			
			if (instruction == 0xFFFFFFFF)   	// Sentinel, halt program execution
				break;
			
			execute(instruction);			// execute
			PC++;
			i++;
		}
		// Print State upon exit
		printState();
	}
	
	/**
	 * Flash a compiled program object into the cpu simulation's memory at the default text address
	 * 
	 * @param p
	 * @throws Exception 
	 */
	public void flash(Program p) throws Exception {
		if (p.getRawInstructions().size() >= 0x0EFF) {
			throw new Exception("Program too large, flash failed.");
		}
		loadMemory(p.getRawInstructions() , 0x0100);
	}
	
	@Override
	public void add(int Rd, int Rs, int Rt) {
		R[Rd] = R[Rs] + R[Rt]; // Doesn't cause interrupt for now TODO
	}

	@Override
	public void addu(int Rd, int Rs, int Rt) {
		R[Rd] = R[Rs] + R[Rt];
	}

	@Override
	public void and(int Rd, int Rs, int Rt) {
		R[Rd] = R[Rs] & R[Rt];
	}

	@Override
	public void jr(int Rs) {
		PC = R[Rs];
	}

	@Override
	public void nor(int Rd, int Rs, int Rt) {
		R[Rd] = ~(R[Rs] | R[Rt]);
	}

	@Override
	public void or(int Rd, int Rs, int Rt) {
		R[Rd] = R[Rs] | R[Rt];
	}

	@Override
	public void slt(int Rd, int Rs, int Rt) {
		R[Rd] = (Rs < Rt) ? 1 : 0; // currently the same as the unsigned version TODO
	}

	@Override
	public void sltu(int Rd, int Rs, int Rt) {
		R[Rd] = (Rs < Rt) ? 1 : 0;
	}

	@Override
	public void sll(int Rd, int Rs, int shamt) {
		R[Rd] = R[Rs] << shamt;
	}
	

	@Override
	public void srl(int Rd, int Rs, int shamt) {
		R[Rd] = R[Rs] >> shamt;
		
	}

	@Override
	public void sub(int Rd, int Rs, int Rt) {
		R[Rd] = R[Rs] - R[Rt];
		
	}

	@Override
	public void subu(int Rd, int Rs, int Rt) {
		R[Rd] = R[Rs] - R[Rt];
	}

	@Override
	public void addi(int Rd, int Rs, int Immediate) {
		R[Rd] = R[Rs] + Immediate;
	}

	@Override
	public void addiu(int Rd, int Rs, int Immediate) {
		R[Rd] = R[Rs] + Immediate;
		
	}

	@Override
	public void andi(int Rd, int Rs, int Immediate) {
		R[Rd] = R[Rs] & Immediate;
		
	}

	@Override
	public void ori(int Rd, int Rs, int Immediate) {
		R[Rd] = R[Rs] | Immediate;
		
	}

	@Override
	public void beq(int Rs, int Rt, int Immediate) {
		if (R[Rs] == R[Rt]) {
			// branch
			PC = Immediate;
		}
	}

	@Override
	public void bne(int Rs, int Rt, int Immediate) {
		if (R[Rs] != R[Rt]) {
			// branch
			PC = Immediate;
		}
	}

	@Override
	public void lbu(int Rd, int Rs, int Immediate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lhu(int Rd, int Rs, int Immediate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ll(int Rd, int Rs, int Immediate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lui(int Rd, int Immediate) {
		R[Rd] = Immediate << 16; 		
	}

	@Override
	public void lw(int Rd, int Rs, int Immediate) {
		R[Rd] = M[Rs + Immediate];
	}

	@Override
	public void sb(int Rd, int Rs, int Immediate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sc(int Rd, int Rs, int Immediate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sh(int Rd, int Rs, int Immediate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sw(int Rd, int Rs, int Immediate) {
		M[R[Rs] + Immediate] = R[Rd];	
	}

	@Override
	public void jal(int addr) {
		R[$at] = PC + 1;
		PC = addr;
	}

	@Override
	public void j(int addr) {
		PC = addr;
	}
	
	/**
	 *  Loads the given list of Integer values into memory starting at
	 *  the provided address.
	 *  
	 *  @param ArrayList<Integer> payload, to load into memory at...
	 *  @param int address
	 * @throws Exception 
	 */
	public void loadMemory(ArrayList<Integer> payload, int address) throws Exception {
		// If the payload is too large for it's specified place in memory...
		if (payload.size() + address > M.length) {
			// then don't do it
			throw new Exception("Not enough room for memory load at address " + address);
		}
		
		// otherwise load it up!
		for (int i = 0; i < payload.size(); i++) {
			M[address + i] = payload.get(i);
		}
	}
	
	
	private void printState() {
		
		System.out.println("-------------------------------------------");
		System.out.println("PC: " + Integer.toHexString(PC));
		System.out.println("-------------------------------------------");
		System.out.println("Registers State:");
		
		for (int i = 0; i < R.length; i++) {
			System.out.println("\t[$" + i + "] = " + R[i]);
		}
		
		printMemory();
	}
	
	private void printMemory() {
		System.out.println("***********************************************");
		System.out.println("\t\tMemory Segment");
		System.out.println("***********************************************");
		
		int rows = 32;
		int columns = 32;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int address = columns*i + j;
				System.out.printf("[0x%x]=%x\t", address, M[address]);
			}
			System.out.println("\n-----------------------------------------------");			
		}
		System.out.println("***********************************************");

	}


	

}
