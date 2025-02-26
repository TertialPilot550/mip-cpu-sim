package hardware;

import hardware.datatypes.I_Instruction;
import hardware.datatypes.Instruction;
import hardware.datatypes.J_Instruction;
import hardware.datatypes.R_Instruction;
import hardware.exceptions.InstructionNotSupportedException;

/**
 * Holds all important details of implementation defined by the MIPS instruction
 * set architectures
 */

public interface MipsIsa {

	/*
	 * Register shortcuts
	 */

	public final int NUMBER_OF_REGISTERS = 32;

	public final int $0 = 0;
	public final int $at = 1;

	public final int $v0 = 2;
	public final int $v1 = 3;

	public final int $a0 = 4;
	public final int $a1 = 5;
	public final int $a2 = 6;
	public final int $a3 = 7;

	public final int $t0 = 8;
	public final int $t1 = 9;
	public final int $t2 = 10;
	public final int $t3 = 11;
	public final int $t4 = 12;
	public final int $t5 = 13;
	public final int $t6 = 14;
	public final int $t7 = 15;

	public final int $s0 = 16;
	public final int $s1 = 17;
	public final int $s2 = 18;
	public final int $s3 = 19;
	public final int $s4 = 20;
	public final int $s5 = 21;
	public final int $s6 = 22;
	public final int $s7 = 23;

	public final int $t8 = 24;
	public final int $t9 = 25;

	public final int $k0 = 26;
	public final int $k1 = 27;
	public final int $gp = 28;
	public final int $sp = 29;
	public final int $fp = 30;
	public final int $ra = 31;

	/**
	 * Execute an instruction on this object which implements the mips isa
	 * 
	 * @param instruction
	 */
	public default void execute(Instruction instruction) {
		System.out.printf("ATTEMPTING TO EXECUTE: %x\n", instruction.getASM());
		if (instruction.opcode == 0) {
			executeRType((R_Instruction) instruction);
		} else if (instruction.opcode == 2 || instruction.opcode == 3) {
			executeJType((J_Instruction) instruction);
		} else {
			executeIType((I_Instruction) instruction);
		}
	}

	// R - type
	public void add(int Rd, int Rs, int Rt) throws Exception;

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

	default void executeRType(R_Instruction instruction) {
		switch (instruction.func) {
		case 0x20:
			try {
				add(instruction.rd, instruction.rs, instruction.rt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		case 0x21:
			addu(instruction.rd, instruction.rs, instruction.rt);
		case 0x24:
			and(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x8:
			jr(instruction.rs);
			return;
		case 0x27:
			nor(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x25:
			or(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x2a:
			slt(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x2b:
			sltu(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x00:
			sll(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x02:
			srl(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x22:
			sub(instruction.rd, instruction.rs, instruction.rt);
			return;
		case 0x23:
			subu(instruction.rd, instruction.rs, instruction.rt);
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

	public void lbu(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException;

	public void lhu(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException;

	public void ll(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException;

	public void lui(int Rd, int Immediate);

	public void lw(int Rd, int Rs, int Immediate);

	public void sb(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException;

	public void sc(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException;

	public void sh(int Rd, int Rs, int Immediate) throws InstructionNotSupportedException;

	public void sw(int Rd, int Rs, int Immediate);

	public void sltiu(int rs, int rt, int immediate);

	public void slti(int rs, int rt, int immediate);

	default void executeIType(I_Instruction instruction) {
		switch (instruction.opcode) {
		case 0x8:
			addi(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0x9:
			addiu(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0xc:
			andi(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0x4:
			beq(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0x5:
			bne(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0xf:
			lui(instruction.rs, instruction.immediate);
			return;
		case 0x23:
			lw(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0xd:
			ori(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0xa:
			slti(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0xb:
			sltiu(instruction.rs, instruction.rt, instruction.immediate);
			return;
		case 0x2b:
			sw(instruction.rs, instruction.rt, instruction.immediate);
			return;
		}
	}

	// J-Type
	void jal(int addr);

	void j(int addr);

	default void executeJType(J_Instruction instruction) {
		if (instruction.opcode == 2) {
			j(instruction.address);
		} else if (instruction.opcode == 3) {
			jal(instruction.address);
		}
	}

	/*
	 * Translation functions
	 */

	/**
	 * Returns the function value associated with the provided neumonic.
	 * 
	 * This function assumes that the neumonic provided is related to an R-Type
	 * instruction.
	 * 
	 * @param neumonic
	 * @return
	 */
	public static int translateInsToFunc(String neumonic) {
		if (getOpcode(neumonic) != 0) {
			return 0;
		} else if (neumonic.toLowerCase().equals("add")) {
			return 0x20;
		} else if (neumonic.toLowerCase().equals("addu")) {
			return 0x21;
		} else if (neumonic.toLowerCase().equals("and")) {
			return 0x24;
		} else if (neumonic.toLowerCase().equals("jr")) {
			return 0x8;
		} else if (neumonic.toLowerCase().equals("nor")) {
			return 0x27;
		} else if (neumonic.toLowerCase().equals("or")) {
			return 0x25;
		} else if (neumonic.toLowerCase().equals("slt")) {
			return 0x2a;
		} else if (neumonic.toLowerCase().equals("sltu")) {
			return 0x2b;
		} else if (neumonic.toLowerCase().equals("sll")) {
			return 0x0;
		} else if (neumonic.toLowerCase().equals("srl")) {
			return 0x2;
		} else if (neumonic.toLowerCase().equals("sub")) {
			return 0x22;
		} else if (neumonic.toLowerCase().equals("subu")) {
			return 0x23;
		} else
			return -1;
	}

	/**
	 * Given a regiser shortcut, returns a reigster number (eg. getRegNumber("$0")
	 * == 0)
	 * 
	 * @param reg
	 * @return
	 */
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

	/**
	 * Retrieves the neumonic associated with the provided Instruction object
	 * 
	 * @param ins
	 * @return
	 */
	public static String getNeumonic(Instruction ins) {
		if (ins.isJType()) {
			if (ins.opcode == 2) {
				return "j";
			} else {
				return "jal";
			}
		}

		if (ins.isIType()) {
			switch (ins.opcode) {
			case 0x08:
				return "addi";
			case 0x09:
				return "addiu";
			case 0x0c:
				return "andi";
			case 0x04:
				return "beq";
			case 0x05:
				return "bne";
			case 0x24:
				return "lbu";
			case 0x25:
				return "lhu";
			case 0x30:
				return "ll";
			case 0x0f:
				return "lui";
			case 0x23:
				return "lw";
			case 0x0d:
				return "ori";
			case 0x0a:
				return "slti";
			case 0x0b:
				return "sltiu";
			case 0x28:
				return "sb";
			case 0x38:
				return "sc";
			case 0x29:
				return "sh";
			case 0x2b:
				return "sw";
			}
		}

		// is R type
		if (ins.isRType()) {
			switch (((R_Instruction) ins).func) {
			case 0x20:
				return "add";
			case 0x21:
				return "addu";
			case 0x24:
				return "and";
			case 0x08:
				return "jr";
			case 0x27:
				return "nor";
			case 0x25:
				return "or";
			case 0x2a:
				return "slt";
			case 0x2b:
				return "sltu";
			case 0x00:
				return "sll";
			case 0x02:
				return "srl";
			case 0x22:
				return "sub";
			case 0x23:
				return "subu";
			}
		}

		if (ins.isHalt()) {
			return "halt";
		}

		return "TRANSLATION_FAILURE";

	}

	/**
	 * Returns the numeric value of the opcode associated with the neumonic
	 * parameter
	 * 
	 * @param neumonic
	 * @return
	 */
	public static int getOpcode(String neumonic) {
		switch (neumonic.toLowerCase()) {

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
		case "sltiu":
			return 0xb;

		case "sw":
			return 0x2b;

		case "halt":
			return 0x3f;

		default:
			return 0;

		}
	}

	/**
	 * Returns the register shortcut give the register number (eg. getReg(0) ==
	 * "$0")
	 * 
	 * @param regNum
	 * @return
	 */
	public static String getReg(int regNum) {
		switch (regNum) {
		case 0:
			return "$0";
		case 1:
			return "$at";
		case 2:
			return "$v0";
		case 3:
			return "$v1";
		case 4:
			return "$a0";
		case 5:
			return "$a1";
		case 6:
			return "$a2";
		case 7:
			return "$a3";
		case 8:
			return "$t0";
		case 9:
			return "$t1";
		case 10:
			return "$t2";
		case 11:
			return "$t3";
		case 12:
			return "$t4";
		case 13:
			return "$t5";

		case 14:
			return "$t6";
		case 15:
			return "$t7";
		case 16:
			return "$s0";
		case 17:
			return "$s1";
		case 18:
			return "$s2";
		case 19:
			return "$s3";
		case 20:
			return "$s4";
		case 21:
			return "$s5";
		case 22:
			return "$s6";

		case 23:
			return "$s7";
		case 24:
			return "$t8";
		case 25:
			return "$t9";
		case 26:
			return "$k0";
		case 27:
			return "$k1";
		case 28:
			return "$gp";
		case 29:
			return "$sp";
		case 30:
			return "$fp";
		case 31:
			return "$ra";
		}
		return "";
	}

}
