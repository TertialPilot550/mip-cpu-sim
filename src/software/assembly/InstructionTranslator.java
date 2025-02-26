package software.assembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hardware.MipsIsa;
import hardware.datatypes.I_Instruction;
import hardware.datatypes.Instruction;
import hardware.datatypes.J_Instruction;
import hardware.datatypes.R_Instruction;
import software.exceptions.ImproperInstructionArgumentException;

/**
 * Class that can cleanly convert singular assembly statements and lists of
 * statements into machine code instructions in the for of integers.
 * 
 * Deal with the difficult string parsing bits in a separate place.
 * 
 * @sammc
 */
public class InstructionTranslator {

	/**
	 * Translate a list of mips assembly statements into a list of integers
	 * representing the instructions
	 * 
	 * @param statements
	 * @return
	 * @throws Exception
	 */
	public List<Instruction> translateTextStatements(List<String> statements) throws Exception {
		List<Instruction> programInstructions = new ArrayList<Instruction>();
		for (String statement : statements) {
			programInstructions.add(translateText(statement));
		}
		return programInstructions;
	}

	// ------------------------------------------------------------------

	/**
	 * High Level Function that translates one instruction of mips assembly into an
	 * Instruction Object
	 * 
	 * @param statement
	 * @return instruction
	 * @throws Exception upon failure to translate
	 */
	Instruction translateText(String statement) throws Exception {

		// extract label if present
		String label = "";
		boolean containsLabel = statement.contains(":");
		if (containsLabel) { // Remove it.
			String[] fields = statement.split(":");
			statement = fields[1].trim();
			label = fields[0] + ":";
		}

		Instruction ins = parseStatement(statement);

		// add label if necessary
		ins.isLabeled = containsLabel;
		ins.label = label;

		// if opcode is 0b11_1111, return 0xFFFF to signal that the program should halt

		// Otherwise, return the value of the instruction
		return ins;
	}

	public Instruction parseStatement(String statement) throws Exception {
		String[] fields = statement.split(" ");

		// strip commas
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].trim().endsWith(",")) {
				fields[i] = fields[i].trim().substring(0, fields[i].trim().length() - 1);
			}
		}

		// handle by instruction type
		int opcode = MipsIsa.getOpcode(fields[0]);

		if (opcode == 0x3f) {
			return new Instruction(0xFFFFFFFF);
		}

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

	/**
	 * Given a list of String fields, returns a valid R-Type instruction or throws
	 * an exception.
	 * 
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	Instruction parseRType(String[] fields) throws Exception {
		// opcode (31:26) Rs(25:21) Rt (20:16) Rd (15:11) shamt (10:6) funct (5:0)

		if (fields.length < 2) {
			throw new Exception("Improper argument count translating r-type statement: " + Arrays.toString(fields));
		}

		int opcode = MipsIsa.getOpcode(fields[0]);
		int rs = 0, rt = 0;
		int shamt = 0;
		int rd = MipsIsa.getRegNumber(fields[1]);
		int func = MipsIsa.translateInsToFunc(fields[0]);

		boolean shiftOperation = opcode == 0 && (func == 0 || func == 2);
		if (shiftOperation) {

			if (fields.length != 4) {
				throw new ImproperInstructionArgumentException(fields);
			}

			// Set shift amount. No rs
			shamt = Integer.parseInt(fields[3]);
			rs = MipsIsa.getRegNumber(fields[2]);

		} else if (func == MipsIsa.translateInsToFunc("jr")) { // if it's a jr operation
			if (fields.length != 2) {
				throw new ImproperInstructionArgumentException(fields);
			}

			rs = MipsIsa.getRegNumber(fields[1]);

		} else {
			if (fields.length != 4) {
				throw new ImproperInstructionArgumentException(fields);
			}
			rs = MipsIsa.getRegNumber(fields[2]);
			rt = MipsIsa.getRegNumber(fields[3]);

		}

		return new R_Instruction(rs, rt, rd, shamt, func);
	}

	// assumes valid instruction syntax
	Instruction parseIType(String[] fields) throws Exception {

		int opcode = MipsIsa.getOpcode(fields[0]);

		int rs, immediate = 0;
		int rt = MipsIsa.getRegNumber(fields[1]);

		// If the immediate doesn't start with number, then it's a label
		boolean usesLabel = fields.length > 3 && !Character.isDigit(fields[3].charAt(0));
		boolean memoryOp = opcode == MipsIsa.getOpcode("lw") || opcode == MipsIsa.getOpcode("sw");
		if (memoryOp) {

			String[] t = fields[2].split("\\(");

			// If there is not a proper bracket pair
			if (t.length == 0 || t[1].charAt(t[1].length() - 1) != ')') {
				throw new ImproperInstructionArgumentException(fields);
			}

			rs = MipsIsa.getRegNumber(t[1].substring(0, t[1].length() - 1));

			immediate = Integer.parseInt(t[0]);

		} else {
			// addiu $t1 $t0 5
			rt = MipsIsa.getRegNumber(fields[1]);
			rs = MipsIsa.getRegNumber(fields[2]);
			if (!usesLabel) {
				immediate = Integer.parseInt(fields[3]);
			}
		}

		I_Instruction result = new I_Instruction(opcode, rs, rt, immediate);
		if (usesLabel) {
			result.labelOperand = fields[3];
		}

		return result;
	}

	Instruction parseJType(String[] fields) throws Exception {
		int opcode = MipsIsa.getOpcode(fields[0]);
		int address = 0;

		if (fields.length != 2) {
			throw new ImproperInstructionArgumentException(fields);
		}

		boolean usesLabel = !Character.isDigit(fields[1].charAt(0));

		if (!usesLabel) {
			address = Integer.parseInt(fields[1]);
		}

		J_Instruction result = new J_Instruction(opcode, address);
		if (usesLabel) {
			result.labelOperand = fields[1];

		}

		return result;

	}

}