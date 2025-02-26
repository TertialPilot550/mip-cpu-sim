package hardware.datatypes;

import hardware.MipsIsa;

/**
 * Represent Jump type instructions
 * 
 * @sammc
 */

//opcode (31:26) address (25:0)
public class J_Instruction extends Instruction {
	public int address;
	public String labelOperand = "";

	// Construct from components
	public J_Instruction(int opcode, int address) {
		super(-1);
		int instruction = 0;

		this.opcode = opcode;
		this.address = address;

		instruction += (opcode << 26);
		instruction += address;

		this.value = instruction;
	}

	public J_Instruction(int opcode, String label) {
		super(-1);
		int instruction = 0;

		this.opcode = opcode;
		this.labelOperand = label;

		instruction += (opcode << 26);

		this.value = instruction;
	}

	// Construct from integer
	public J_Instruction(int instruction) {
		super(instruction);
		this.isLabeled = true;
		// Find fields
		opcode = copyBitField(instruction, 31, 26);
		address = copyBitField(instruction, 25, 0);
	}

	public void updateValue() {
		int instruction = 0;

		instruction += (opcode << 26);
		instruction += address;

		this.value = instruction;

	}

	public boolean hasLabelImm() {
		return !labelOperand.equals("");
	}

	@Override
	public String getASM() {

		String body = MipsIsa.getNeumonic(this) + " ";

		if (isLabeled) {
			body = label + " " + body;
		}

		if (hasLabelImm()) {
			return body + labelOperand;
		}

		return body + address;

	}

}