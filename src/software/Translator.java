package software;

import java.util.ArrayList;
import java.util.Arrays;

import processor.MipsIsa;

public class Translator {
	
	public static String seperateInstruction(int instruction) {
		
		String ins = Integer.toBinaryString(instruction);
		
		// fill it out with zeros
		while (ins.length() < 32) {
			ins = "0" + ins;
		}
		
		int opcode = Integer.parseUnsignedInt(ins.substring(0, 6));
		
		if (opcode == 0) 
			// opcode (31:26) Rs(25:21) Rt (20:16) Rd (15:11) shamt (10:6) funct (5:0)
			return ins.substring(0, 6) + " " + ins.substring(6, 11) + " " + ins.substring(11, 16) + " " + 
					ins.substring(16, 21) + " " + ins.substring(21, 26) + " " + ins.substring(26);
		else if (opcode == 2 || opcode == 3)
			// opcode (31:26) address (25:0)
			return ins.substring(0, 6) + " " + ins.substring(6);
		else
			// opcode (31:26) Rs(25:21) Rt (20:16) Immediate (15:0)
			return ins.substring(0, 6) + " " + ins.substring(6, 11) + " " + ins.substring(11, 16) + " " + ins.substring(16);
	}
	
	/**
	 * Translate a list of mips assembly statements into a list of integers representing the instructions
	 * @param statements
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Integer> translateStatements(ArrayList<String> statements) throws Exception {
		ArrayList<Integer> programInstructions = new ArrayList<Integer>();
		for (String statement : statements) {
			programInstructions.add(translate(statement));
		}
		return programInstructions;
	}
	
	
	/**
	 * Translate one instruction of mips assembly into an integer representing the instruction
	 * @param statement
	 * @return instruction
	 * @throws Exception upon failure to translate
	 */
	public int translate(String statement) throws Exception {
		String[] fields = statement.split(" ");
		if (fields.length == 0) {
			return 0;
		}
		
		// return 0xFFFF to signal that the program should halt
		if (fields.length == 1 && fields[0].equals("halt")) {
			return 0xFFFF_FFFF;
		}
		
		int opcode = MipsIsa.getOpcode(fields[0]);	
		if (opcode == 0) {
			return translateRType(fields);
		} else if (opcode == 2 || opcode == 3) {
			return translateJType(fields);
		} else {
			return translateIType(fields);
		}	
	}
	
	private int translateJType(String[] fields) {
		int instruction = 0;
		
		int opcode = MipsIsa.getOpcode(fields[0]);
		int addr = Integer.parseInt(fields[1]);
		
		instruction += (opcode << 26);
		instruction += addr;
		
		return instruction;
	}


	private int translateIType(String[] fields) throws Exception {
		int instruction = 0;
		
		
		
		
		int opcode = MipsIsa.getOpcode(fields[0]);
		
		int rs, immediate = 0;
		int rt = MipsIsa.getRegNumber(fields[1]);

		
		boolean memoryOp = opcode == MipsIsa.getOpcode("lw") || opcode == MipsIsa.getOpcode("sw");
		if (memoryOp) {
			
			
			String[] t = fields[2].split("\\(");
			
			// If there is not a proper bracket pair
			if (t.length == 0 || t[1].charAt(t[1].length() - 1) != ')') {
				throw new Exception("Error Assembling Memory Operation I-Type Statement: " +  t.length + " field(s)::" + t[0] + "," + t[1]);
			}
			
			
			rs = MipsIsa.getRegNumber(t[1].substring(0, t[1].length() - 1));
			immediate = Integer.parseInt(t[0]);
						
		} else {
			// addiu $t1 $t0 5
			rt = MipsIsa.getRegNumber(fields[1]);
			rs = MipsIsa.getRegNumber(fields[2]);
			immediate = Integer.parseInt(fields[3]);
		}
		
		instruction += (opcode << 26);
		instruction += (rs << 21);
		instruction += (rt << 16);
		instruction += immediate;
		return instruction;
	}


	


	private int translateRType(String[] fields) throws Exception {
		int instruction = 0;
		
		// opcode (31:26) Rs(25:21) Rt (20:16) Rd (15:11) shamt (10:6) funct (5:0)
		
		if (fields.length > 4 || fields.length < 4) {
			throw new Exception("Improper argument count translating r-type statement: " + Arrays.toString(fields));
		}
		
		int opcode = MipsIsa.getOpcode(fields[0]);
		int rs = 0, rt = 0;
		int shamt = 0;
		int rd = MipsIsa.getRegNumber(fields[1]);
		int funct = MipsIsa.getFunct(fields[0]);
		
		boolean shiftOperation = opcode == MipsIsa.getOpcode("sll") || opcode == MipsIsa.getOpcode("srl");
		if (shiftOperation) {
			shamt = Integer.parseInt(fields[3]);
			rt = MipsIsa.getRegNumber(fields[2]);
		
		} else {
			rs = MipsIsa.getRegNumber(fields[2]);
			rt = MipsIsa.getRegNumber(fields[3]);

		}
		
		if (funct == -1) {
			throw new Exception("Exception translating R-Type Instruction");
		}

		instruction += (rs << 21);
		instruction += (rt << 16);
		instruction += (rd << 11);
		instruction += (shamt << 6);
		instruction += funct;
	
		return instruction;
	}

}

// Verification

// add
// 0b00000000111010100001000000100000 <- expected
//			 111010100001000000100000


// addi
// 0b00100001001011010000000000010000 <- expected
//     100001001011010000000000010000


// j 100
// 0b00001000000000000000000001100100 <- expected
// 		 1000000000000000000001100100


// lw $t3 5($t5)
// 0b10001101101010110000000000000101 <- expected
//   10001101101010110000000000000101
