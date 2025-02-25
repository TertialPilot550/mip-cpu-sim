package hardware.cpu;

import hardware.MipsIsa;
import hardware.SimulatedComputer;
import hardware.datatypes.Instruction;
import hardware.exceptions.HardwareMemoryLoadException;
import hardware.exceptions.InstructionNotSupportedException;
import hardware.exceptions.OverflowException;
import software.datatypes.Program;

/**
 * Class which represents the hardware control structure. 
 * 
 * Contains registers, ram, fetch execute cycle, and all instruction definitions.
 * 
 * @sammc
 */
public class CPU implements MipsIsa, SimulatedComputer {
	
	public boolean debug_mode = true; // Debug Mode
	
	// RAM Details
	public static final int PC_STARTING_ADDRESS = 0x0040_0000; 
	public static final int STATIC_DATA = 0x1000_0000;
	public static final int START_DYNAMIC_DATA = 0x1000_8000;
	public static final int END_DYNAMIC_DATA = 0x07fff_fffc;
	public static final int RAM_ADDRESS_SPACE = 0x7fff_fffd; // number of addresses
	
	private int R[]; // Registers
	private int M[]; // Random Access Memory
	private int PC;  // Program Counter
	
	/*
	 * Public Interface
	 */
	
	// Constructor
	public CPU() {
		PC = PC_STARTING_ADDRESS;          // Initialize Program Counter
		R = new int[NUMBER_OF_REGISTERS];   // Initialize Registers
		M = new int[RAM_ADDRESS_SPACE]; // Initialize memory 2^18 ~ 262 kb
	}
	
	@Override
	public void start() {		
		fetchExecuteLoop();
	}
	
	@Override
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
		loadMemory(p.bin, PC_STARTING_ADDRESS);
	}

	/*
	 * Internal Methods 
	 */
	
	/**
	 * Main loop for function of the cpu
	 */
	private void fetchExecuteLoop() {
		int i = 0; // cycle count
		while (PC <= STATIC_DATA) {	// while there are valid instructions left	
			Instruction ins = new Instruction(M[PC]); // fetch
			
			// Debug: Print the instruction fetched
			if (debug_mode) System.out.printf("[%d] Instruction: %x - %s\n", i, ins, ins.getASM());
			
			
			if (ins.value == 0xFFFFFFFF) // Sentinel, halt program execution
				break;
			
			execute(ins); // execute
			PC++;
			i++;
		}
		// Print State upon exit
		if (debug_mode) printState();
	}
	
	/**
	 *  Loads the given array of Integer values into memory starting at
	 *  the provided address.
	 *  
	 *  @param ArrayList<Integer> pay load, to load into memory at...
	 *  @param int address
	 * @throws Exception 
	 */
	private void loadMemory(int[] data, int address) throws Exception {
		// If the pay load is too large for it's specified place in memory...
		if (data.length + address > M.length) {
			// then don't do it
			throw new HardwareMemoryLoadException(address);
		}
		
		// otherwise load it up!
		for (int i = 0; i < data.length; i++) {
			M[address + i] = data[i];
		}
	}
	
	// -------------------------------------------------------------------------------- //
	
	/*
	 * Instruction Definitions
	 */
	
	@Override
	public void add(int Rd, int Rs, int Rt) throws OverflowException {
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
	public void sltiu(int rs, int rt, int immediate) {
		R[rs] = (rt < Integer.toUnsignedLong(immediate)) ? 1 : 0; 
	}

	@Override
	public void slti(int rs, int rt, int immediate) {
		R[rs] = (rt < immediate) ? 1 : 0; 		
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

	@Override 
	public void beq(int Rs, int Rt, int Immediate) {
		if (R[Rs] == R[Rt]) {
			// branch
			PC += Immediate;
		}
	}

	@Override
	public void bne(int Rs, int Rt, int Immediate) {
		if (R[Rs] != R[Rt]) {
			// branch
			PC += Immediate;
		}
	}

	@Override
	public void lbu(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException {
		throw new InstructionNotSupportedException();
		
	}

	@Override
	public void lhu(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException {
		throw new InstructionNotSupportedException();
	}

	@Override
	public void ll(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException {
		throw new InstructionNotSupportedException();
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
	public void sb(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException{
		throw new InstructionNotSupportedException	();	
	}

	@Override
	public void sc(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException {
		throw new InstructionNotSupportedException();
	}

	@Override
	public void sh(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException{
		throw new InstructionNotSupportedException();
	}

	@Override
	public void sw(int Rd, int Rs, int Immediate) {
		M[R[Rs] + Immediate] = R[Rd];	
	}

	@Override 
	public void jal(int addr) {
		R[$ra] = PC + 1;
		PC = getTrueJAddress(addr);	
	}
	
	@Override 
	public void j(int addr) {
		PC = getTrueJAddress(addr);
	}
	
	private int getTrueJAddress(int addrImmediate) {
		int PCPart = PC & 0xFF000000; // 32 bit number, keep the first 2 bytes, clear the rest
		return (PCPart + addrImmediate);
	}
	
	
	
	// -------------------------------------------------------------------------------- //
	
	/*
	 * DEBUG: 
	 */
	
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
