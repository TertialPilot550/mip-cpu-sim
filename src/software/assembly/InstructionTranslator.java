package software.assembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hardware.MipsIsa;
import hardware.datatypes.I_Instruction;
import hardware.datatypes.Instruction;
import hardware.datatypes.J_Instruction;
import hardware.datatypes.R_Instruction;

/**
 * Class that can cleanly convert singular assembly statements and lists of 
 * statements into machine code instructions in the for of integers
 * 
 * @sammc
 */
public class InstructionTranslator {

	/**
	 * Translate a list of mips assembly statements into a list of integers representing the instructions
	 * @param statements
	 * @return
	 * @throws Exception
	 */
	public List<Integer> translateTextStatements(List<String> statements) throws Exception {
		List<Integer> programInstructions = new ArrayList<Integer>();
		for (String statement : statements) {
			programInstructions.add(translateText(statement));
		}
		return programInstructions;
	}
	
	// ------------------------------------------------------------------
	
	/**
	 * Translate one instruction of mips assembly into an integer representing the instruction
	 * @param statement
	 * @return instruction
	 * @throws Exception upon failure to translate
	 */
	private int translateText(String statement) throws Exception {	
		Instruction ins = parseStatement(statement);
		
		// if opcode is 0b11_1111, return 0xFFFF to signal that the program should halt
		if (ins.opcode == 0x3F) return 0xFFFF_FFFF;
		
		// Otherwise, return the value of the instruction
		return ins.value;
	}
	
	
	/*
	 * Simple translating back and forth as utility functions before i continue to rebuild
	 */
	
	private Instruction parseStatement(String statement) throws Exception {
		String[] fields = statement.split(" ");
		
		// strip commas
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].endsWith(",")) {
				fields [i] = fields[i].substring(0, fields[i].length());
			}
		}
		
		// handle by instruction type
		int opcode = MipsIsa.getOpcode(fields[0]);
		if (opcode == 0) {
			// r type
			return parseRType(fields);
		} else if (opcode == 2 || opcode == 3) {
			// j type
			return parseJType(fields);
		} else {
			// i type
			return parseIType(fields);
		}
	}

	private Instruction parseRType(String[] fields) throws Exception {		
		// opcode (31:26) Rs(25:21) Rt (20:16) Rd (15:11) shamt (10:6) funct (5:0)
		
		if (fields.length > 4 || fields.length < 4) {
			throw new Exception("Improper argument count translating r-type statement: " + Arrays.toString(fields));
		}
		
		int opcode = MipsIsa.getOpcode(fields[0]);
		int rs = 0, rt = 0;
		int shamt = 0;
		int rd = MipsIsa.getRegNumber(fields[1]);
		int func = MipsIsa.translateInsToFunc(fields[0]);
		
		boolean shiftOperation = opcode == MipsIsa.getOpcode("sll") || opcode == MipsIsa.getOpcode("srl");
		if (shiftOperation) {
			// Set shift amount. No rs
			shamt = Integer.parseInt(fields[3]);
			rs = MipsIsa.getRegNumber(fields[2]);
		
		} else {
			// 
			rs = MipsIsa.getRegNumber(fields[2]);
			rt = MipsIsa.getRegNumber(fields[3]);

		}
		
		if (func == -1) {
			throw new Exception("Exception translating R-Type Instruction");
		}
	
		return new R_Instruction(rs, rt, rd, shamt, func);
	}
	// assumes valid instruction syntax
	private Instruction parseIType(String[] fields) throws Exception {
		
		
		int opcode = MipsIsa.getOpcode(fields[0]);
		
		int rs, immediate = 0;
		int rt = MipsIsa.getRegNumber(fields[1]);

		// If the immediate doesn't start with number, then it's a label
		boolean usesLabel = !Character.isDigit(fields[3].charAt(0));
		boolean memoryOp = opcode == MipsIsa.getOpcode("lw") || opcode == MipsIsa.getOpcode("sw");
		if (memoryOp) {
			
			
			String[] t = fields[2].split("\\(");
			
			// If there is not a proper bracket pair
			if (t.length == 0 || t[1].charAt(t[1].length() - 1) != ')') {
				throw new Exception("Error Assembling Memory Operation I-Type Statement: " +  t.length + " field(s)::" + t[0] + "," + t[1]);
			}
			
			
			rs = MipsIsa.getRegNumber(t[1].substring(0, t[1].length() - 1));
			if (!usesLabel) {
				immediate = Integer.parseInt(t[0]);
			}
						
		} else {
			// addiu $t1 $t0 5
			rt = MipsIsa.getRegNumber(fields[1]);
			rs = MipsIsa.getRegNumber(fields[2]);
			if (!usesLabel) {
				immediate = Integer.parseInt(fields[3]);
			}
		}
		
		I_Instruction result = new I_Instruction(opcode, rs, rt, immediate);
		result.usesLabel = usesLabel;
		return result;
	}
	private Instruction parseJType(String[] fields) {		
		int opcode = MipsIsa.getOpcode(fields[0]);		
		return new J_Instruction(opcode << 26);
	}

	
	
}