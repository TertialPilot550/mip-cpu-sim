package software;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import datatypes.Instruction;
import datatypes.R_Instruction;
import datatypes.StaticDataElement;
import datatypes.I_Instruction;
import datatypes.J_Instruction;

import processor.MipsIsa;

/**
 * Class that can cleanly convert singular assembly statements and lists of statements.
 */

public class Translator {

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
	
	
	/**
	 * Translate one instruction of mips assembly into an integer representing the instruction
	 * @param statement
	 * @return instruction
	 * @throws Exception upon failure to translate
	 */
	public int translateText(String statement) throws Exception {	
		Instruction ins = parseStatement(statement);
		
		// if opcode is 0b11_1111, return 0xFFFF to signal that the program should halt
		if (ins.opcode == 0x3F) return 0xFFFF_FFFF;
		
		// Otherwise, return the value of the instruction
		return ins.value;
	}
	
	
	public HashMap<String, Integer[]> translateDataStatements(ArrayList<String> statements) throws Exception {
		HashMap<String, Integer[]> staticData = new HashMap<String, Integer[]>();
				
		for (String statement: statements) {
			StaticDataElement e = translateData(statement);
			staticData.put(e.label, e.values);
		}
		
		return staticData;
	}
	
	
	// parse one line of static data declaration
	
	// I can pass the type and 
	
	// .word, .space(general storage), .asciiz
	// for now, assume that everything is clean in this statement, so the following format:
	// label: .type "value"
	public StaticDataElement translateData(String statement) throws Exception {
		Scanner scan = new Scanner(statement);
		
		if (!scan.hasNext()) {
			scan.close();
			throw new Exception("Error parsing static data, improper number of element per line.");
		}
		
		
		String label = scan.next();
		
		
		if (!scan.hasNext()) {
			scan.close();
			throw new Exception("Error parsing static data, improper number of element per line.");
		}
		
		String type = scan.next();
		
		if (!scan.hasNext()) {
			scan.close();
			throw new Exception("Error parsing static data, improper number of element per line.");
		}
		
		// take the rest of the values to parse them into a proper list of values that will be inserted into memory
		// based on it's length and associated with it's label
		String values = scan.nextLine();
		int[] wordValues = encodeData(type, values);
		
		scan.close();
		
		return new StaticDataElement(label, wordValues);
		
		
		
	}
	
	
	// expecting a string of format x, y, z where x y z and z are the appropriate types
	private int[] encodeData(String type, String values) throws Exception {
		String[] v = values.split(",");
		int[] result = null;
		
		if (type.equals(".space")) {
			int numReservedWords = Integer.parseInt(values);
			result = new int[numReservedWords];
		} else if (type.equals(".asciiz")) {
			
			// take off first and last characters
			String valueNoQuotes = values.substring(1, values.length() - 2);
			
			// Assign result size
			result = new int[valueNoQuotes.length() + 1]; // null terminate
			result[valueNoQuotes.length()] = 0;
			
			// For each character, put the numeric value at the next spot starting at index 0
			int i = 0;
			for (char c : valueNoQuotes.toCharArray()) {
				result[i++] = Character.getNumericValue(c);
			}
			
			
		} else if (type.equals(".word")) {
			result = new int[v.length];
		
			for (int i = 0; i < v.length; i++) {
				String sVal = v[i].trim();
			
				
				
				if (sVal.endsWith(",")) {
					sVal = sVal.substring(0, sVal.length() - 1);
				}
			
			
				result[i] = Integer.parseInt(sVal);
			}
		
		}
	
		return result;
		
		
	}
	
	
	// ------------------------------------------------------------------
	
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
	private Instruction parseIType(String[] fields) throws Exception {
		
		
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
		
		
		return new I_Instruction(opcode, rs, rt, immediate);
	}
	private Instruction parseJType(String[] fields) {		
		int opcode = MipsIsa.getOpcode(fields[0]);
		int addr = Integer.parseInt(fields[1]);
		return new J_Instruction(opcode, addr);
	}

	
	
}