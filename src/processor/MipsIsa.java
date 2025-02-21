package processor;


public interface MipsIsa {

	final int $0 = 0;
	final int $at = 1;
	
	final int $v0 = 2;
	final int $v1 = 3;
	
	final int $a0 = 4;
	final int $a1 = 5;
	final int $a2 = 6;
	final int $a3 = 7;
	
	final int $t0 = 8;
	final int $t1 = 9;
	final int $t2 = 10;
	final int $t3 = 11;
	final int $t4 = 12;
	final int $t5 = 13;
	final int $t6 = 14;
	final int $t7 = 15;
	
	final int $s0 = 16;
	final int $s1 = 17;
	final int $s2 = 18;
	final int $s3 = 19;
	final int $s4 = 20;
	final int $s5 = 21;
	final int $s6 = 22;
	final int $s7 = 23;
	
	final int $t8 = 24;
	final int $t9 = 25;
	
	final int $k0 = 26;
	final int $k1 = 27;
	final int gp  = 28;
	final int sp = 29;
	final int fp = 30;
	final int ra = 31;
	
	// R - type
	public void add(int Rd, int Rs, int Rt);
	public void addu(int Rd, int Rs, int Rt);
	
	public void and(int Rd, int Rs, int Rt);
	
	public void jr(int Rs);
	public void nor(int Rd, int Rs, int Rt);
	public void or(int Rd, int Rs, int Rt);
	public void slt(int Rd, int Rs, int Rt);
	public void sltu(int Rd, int Rs, int Rt);
	
	public void sll(int Rd, int Rs, int shamt);
	public void srl(int Rd, int Rs, int shamt);
	
	
	public void sub(int Rd, int Rs, int Rt);
	public void subu(int Rd, int Rs, int Rt);
	
	public default void execute(int instruction) {
		System.out.printf("ATTEMPTING TO EXECUTE: %x\n", instruction);
		
		int opcode = copyBitField(instruction, 31, 26);
		if (opcode == 0) {
			executeRType(instruction);
		} else if (opcode == 2 || opcode == 3) {
			executeJType(instruction);
		} else {
			executeIType(instruction);
		}
		
		
		
	}
	
	// opcode (31:26) Rs(25:21) Rt (20:16) Rd (15:11) shamt (10:6) funct (5:0)
	default void executeRType(int instruction) {
		int Rs = copyBitField(instruction, 25, 21);
		int Rt = copyBitField(instruction, 20, 16);
		int Rd = copyBitField(instruction, 15, 11);
		int shamt = copyBitField(instruction, 10, 6); // not accounted for yet TODO
		int funct = copyBitField(instruction, 5, 0);
		System.out.println("[" + Rs + " " + Rt + " " + Rd + " " + shamt + " " + funct + "]");
		switch (funct) {
		
		
		case 0x20:
			add(Rd, Rs, Rt);
			return;
			
		case 0x21: 
			addu(Rd, Rs, Rt);
			
		case 0x24:
			and(Rd, Rs, Rt);
			return;
			
		case 0x8:
			jr(Rs);
			return;
			
		case 0x27:
			nor(Rd, Rs, Rt);
			return;
		
		case 0x25:
			or(Rd, Rs, Rt);
			return;
			
		case 0x2a:
			slt(Rd, Rs, Rt);
			return;
			
		case 0x2b:
			sltu(Rd, Rs, Rt);
			return;
			
		case 0x00:
			sll(Rd, Rs, shamt);
			return;
			
		case 0x02:
			srl(Rd, Rs, shamt);
			return;
			
		case 0x22:
			sub(Rd, Rs, Rt);
			return;
			
		case 0x23:
			subu(Rd, Rs, Rt);
			return;
		}
	}
	
	// I-Type
	
	public void addi(int Rd, int Rs, int Immediate);
	public void addiu(int Rd, int Rs, int Immediate);
	
	public void andi(int Rd, int Rs, int Immediate);
	public void ori(int Rd, int Rs, int Immediate);

	public void beq(int Rd, int Rs, int Immediate);
	public void bne(int Rd, int Rs, int Immediate);
	
	public void lbu(int Rd, int Rs, int Immediate);
	public void lhu(int Rd, int Rs, int Immediate);
	public void ll(int Rd, int Rs, int Immediate);
	public void lui(int Rd, int Immediate);
	public void lw(int Rd, int Rs, int Immediate);
	
	public void sb(int Rd, int Rs, int Immediate);
	public void sc(int Rd, int Rs, int Immediate);
	public void sh(int Rd, int Rs, int Immediate);
	public void sw(int Rd, int Rs, int Immediate);
	
	// opcode (31:26) Rs(25:21) Rt (20:16) Immediate (15:0)
	default void executeIType(int instruction) {
		System.out.println("II");

		int opcode = copyBitField(instruction, 31, 26);
		int Rs = copyBitField(instruction, 25, 21);
		int Rt = copyBitField(instruction, 20, 16);
		int Immediate = copyBitField(instruction, 15, 0);
		System.out.println("[" + opcode + " " + Rs + " " + Rt + " " + Immediate + "]");

		switch (opcode) {
		
		case 0x8:
			addi(Rs, Rt, Immediate);
			return;
		case 0x9:
			addiu(Rs, Rt, Immediate);
			return;
		case 0xc:
			andi(Rs, Rt, Immediate);
			return;
		case 0x4:
			beq(Rs, Rt, Immediate);
			return;
		case 0x5:
			bne(Rs, Rt, Immediate);
			return;
			
		case 0xf:
			lui(Rs, Immediate);
			return;
			
		case 0x23:
			lw(Rs, Rt, Immediate);
			return;
			
		case 0xd:
			ori(Rs, Rt, Immediate);
			return;
			
		case 0xa:
			//slti(Rs, Rt, Immediate); TODO
			return;
			
		case 0xb:
			//sltiu(Rs, Rt, Immediate); TODO
			return;
			
		case 0x2b:
			sw(Rs, Rt, Immediate);
			return;
		}
	}
	
	
	// J-Type
	
	void jal(int addr);
	void j(int addr);
	
	
	
	
	
	// opcode (31:26) address (25:0)
	default void executeJType(int instruction) {		
		int opcode = copyBitField(instruction, 31, 26);
		int addr = copyBitField(instruction, 25, 0);
		
		if (opcode == 2) {
			j(addr);
		} else if (opcode == 3) {
			jal(addr);
		}
		
	}
	
	public static int copyBitField(int src, int lInd, int rInd) {
		// 0x0000.0000
	    src >>= rInd;
	    // construct a bit string of the appropriate length
	    int len = lInd - rInd;
	    System.out.println("Len: " + len);
	    // 2^n - 1 = n bit number with all digits as 1
	    int lenBitsOfOne = (int) (Math.pow(2, len) - 1);
	    // Preserve the bits you mean too, clear the rest
	    System.out.print(Integer.toBinaryString(src) + "&");
	    System.out.print(Integer.toBinaryString(lenBitsOfOne) + "=");
	    System.out.println(Integer.toBinaryString(lenBitsOfOne & src));

	    return lenBitsOfOne & src;
	    
	}
	
	public static int getFunct(String command) {
		if (getOpcode(command) != 0) {
			return 0;
		} else if (command.toLowerCase().equals("add")) {
			return 0x20;
		} else if (command.toLowerCase().equals("addu")) {
			return 0x21;
		} else if (command.toLowerCase().equals("and")) {
			return 0x24;
		} else if (command.toLowerCase().equals("jr")) {
			return 0x8;
		} else if (command.toLowerCase().equals("nor")) {
			return 0x27;
		} else if (command.toLowerCase().equals("or")) {
			return 0x25;
		} else if (command.toLowerCase().equals("slt")) {
			return 0x2a;
		} else if (command.toLowerCase().equals("sltu")) {
			return 0x2b;
		} else if (command.toLowerCase().equals("sll")) {
			return 0x0;
		} else if (command.toLowerCase().equals("srl")) {
			return 0x2;
		} else if (command.toLowerCase().equals("sub")) {
			return 0x22;
		} else if (command.toLowerCase().equals("subu")) {
			return 0x23;
		} else return -1;
	}
	
	public static int getRegNumber(String reg) {
		switch (reg.toLowerCase()) {
		
		case "$0":
			return 0;
		case "$at":
			return 1;
		case "$v0":
			return 2;
		case "$v1":
			return 3;
		case "$a0":
			return 4;
		case "$a1":
			return 5;
		case "$a2":
			return 6;
		case "$a3":
			return 7;
		case "$t0":
			return 8;
		case "$t1":
			return 9;
		case "$t2":
			return 10;
		case "$t3":
			return 11;
		case "$t4":
			return 12;
		case "$t5":
			return 13;
		case "$t6":
			return 14;
		case "$t7":
			return 15;
		case "$s0":
			return 16;
		case "$s1":
			return 17;
		case "$s2":
			return 18;
		case "$s3":
			return 19;
		case "$s4":
			return 20;
		case "$s5":
			return 21;
		case "$s6":
			return 22;
		case "$s7":
			return 23;
		case "$t8":
			return 24;
		case "$t9":
			return 25;
		case "$k0": 
			return 26;
		case "$k1":
			return 27;
		case "$gp":
			return 28;
		case "$sp":
			return 29;
		case "$fp":
			return 30;
		case "$ra":
			return 31;
		default:
			return 0;
			
		}
	}

	public static int getOpcode(String command) {
		switch (command.toLowerCase()) {
		
		case "add":
		case "addu":
		case "and":
		case "nor":
		case "or":
		case "slt":
		case "sltu":
		case "sll":
		case "srl":
		case "sub":
		case "subu":
		case "jr":
			return 0x0;
			
			
			
		case "addi":
			return 0x8;
		case "addiu":
			return 0x9;
		
		case "andi":
			return 0xc;
		case "beq":
			return 0x4;
		case "bne":
			return 0x5;
		case "j":
			return 0x2;
		case "jal":
			return 0x3;
		case "ori":
			return 0xd;
		case "lui":
			return 0xf;
		case "lw":
			return 0x23;
			
		case "slti":
			return 0xa;
		case "sltiu" :
			return 0xb;
			
		case "sw":
			return 0x2b;
		
		default: 
			return 0;
		
		}
	}
	

	
	
}
