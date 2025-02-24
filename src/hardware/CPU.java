package hardware;


import hardware.datatypes.Instruction;
import hardware.exceptions.OverflowException;
import software.datatypes.Program;

/**
 * Class which represents the hardware control structure. 
 * 
 * Contains registers, ram, fetch execute cycle, and all instruction definitions.
 * 
 * @sammc
 */

public class CPU implements MipsIsa {
	
	// Debug mode?
	public static final boolean debug_mode = true;
	
	// RAM Details
	public static final int PC_STARTING_ADDRESS = 0x0040_0000; 
	public static final int STATIC_DATA = 0x1000_0000;
	public static final int START_DYNAMIC_DATA = 0x1000_8000;
	public static final int END_DYNAMIC_DATA = 0x07fff_fffc;
	public static final int RAM_ADDRESS_SPACE = 0x7fff_fffd; // number of addresses
	
	private int R[]; // Registers
	private int M[]; // Random Access Memory
	private int PC;  // Program Counter
	// TODO to store permanent information, use files in a directory in the project
	
	
	public CPU() {
		PC = PC_STARTING_ADDRESS;          // Initialize Program Counter
		R = new int[NUMBER_OF_REGISTERS];   // Initialize Registers
		M = new int[RAM_ADDRESS_SPACE]; // Initialize memory 2^18 ~ 262 kb
	}
	
	// start executing code in a fetch decode execute cycle starting at the default address of 0x0100
	public void start() {		
		fetchExecuteLoop();
	}
	
	/**
	 * Main loop for function of the cpu
	 */
	private void fetchExecuteLoop() {
		int i = 0; // cycle count
		while (PC <= STATIC_DATA) {							// run	
			Instruction ins = new Instruction(M[PC]);		// fetch
			
			if (debug_mode) {
				System.out.printf("[%d] Instruction: %x - %s\n", i, ins, ins.getASM());
			}
			
			
			if (ins.value == 0xFFFFFFFF)   	// Sentinel, halt program execution
				break;
			
			execute(ins);			// execute
			PC++;
			i++;
		}
		// Print State upon exit
		printState();
	}
	
	/**
	 * Loads a assembled and linked program object into the cpu simulation's memory at the default text address
	 * 
	 * @param p
	 * @throws Exception 
	 */
	public void loadProgram(Program p) throws Exception {
		if (p.bin.length >= STATIC_DATA - PC_STARTING_ADDRESS) { 
			throw new Exception("Program too large, load failed.");
		}
		if (p.bin.length >= START_DYNAMIC_DATA - STATIC_DATA) { 
			throw new Exception("Data too large, load failed.");
		}
		// load static data
		loadMemory(p.staticData, STATIC_DATA);
		// load instructions
		loadMemory(p.bin , PC_STARTING_ADDRESS);
	}
	
	@Override
	public void add(int Rd, int Rs, int Rt) throws Exception {
		R[Rd] = R[Rs] + R[Rt]; 
		
		boolean didOverflow = (R[Rs] < 0 && R[Rt] < 0 && R[Rd] > 0) || (R[Rs] > 0 && R[Rt] > 0 && R[Rd] < 0);
		if (didOverflow) {
			throw new OverflowException();
		} 
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
		R[Rd] = (Rs < Rt) ? 1 : 0; 
	}

	@Override
	public void sltu(int Rd, int Rs, int Rt) {
		R[Rd] = (Integer.toUnsignedLong(Rs) < Integer.toUnsignedLong(Rt)) ? 1 : 0;
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

	@Override // TODO FIX THIS TO DO RELATIVE ADDRESSING
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

	@Override // TODO this is not right
	public void jal(int addr) {
		R[$at] = PC + 1;
		PC = addr;
	}

	@Override // TODO this is not right
	public void j(int addr) {
		PC = addr;
	}
	
	/**
	 *  Loads the given array of Integer values into memory starting at
	 *  the provided address.
	 *  
	 *  @param ArrayList<Integer> pay load, to load into memory at...
	 *  @param int address
	 * @throws Exception 
	 */
	public void loadMemory(int[] instructions, int address) throws Exception {
		// If the pay load is too large for it's specified place in memory...
		if (instructions.length + address > M.length) {
			// then don't do it
			throw new Exception("Not enough room for memory load at address " + address);
		}
		
		// otherwise load it up!
		for (int i = 0; i < instructions.length; i++) {
			M[address + i] = instructions[i];
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
