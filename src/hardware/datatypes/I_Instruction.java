package hardware.datatypes;

import hardware.MipsIsa;

/**
 * Represent Immediate type instructions
 * 
 * @sammc
 */

//opcode (31:26) Rs(25:21) Rt (20:16) Immediate (15:0)
public class I_Instruction extends Instruction {
	public int rs;
	public int rt;
	public int immediate;

	public String labelOperand = "";

	// Construct from components
	public I_Instruction(int opcode, int rs, int rt, int immediate) {
		super(-1);
		int instruction = 0;

		this.opcode = opcode;
		this.rs = rs;
		this.rt = rt;
		this.immediate = immediate;

		instruction += (opcode << 26);
		instruction += (rs << 21);
		instruction += (rt << 16);
		instruction += immediate;

		this.value = instruction;
	}

	// Construct from integer
	public I_Instruction(int instruction) {
		super(instruction);
		// Find fields
		rs = copyBitField(instruction, 25, 21);
		rt = copyBitField(instruction, 20, 16);
		immediate = copyBitField(instruction, 15, 0);
	}

	public boolean isBranch() {
		return (opcode == 4 || opcode == 5);
	}

	/**
	 * Returns true if the instruciton's immediate value is actually a label
	 * substitute
	 * 
	 * @return
	 */
	public boolean hasLabelImm() {
		return !labelOperand.equals("");
	}

	@Override
	public String getASM() {
		String head = MipsIsa.getNeumonic(this) + " ";

		if (isLabeled) {
			head = label + " " + head;
		}

		String body = head + MipsIsa.getReg(rt) + " " + MipsIsa.getReg(rs) + " ";

		boolean isMemOp = opcode == 0x23 || opcode == 0x2b;
		if (isMemOp) {
			return head + MipsIsa.getReg(rs) + " " + immediate + "(" + MipsIsa.getReg(rt) + ")";
		}

		if (hasLabelImm()) {
			return body + labelOperand;
		}

		return body + "0x" + Integer.toHexString(immediate);

	}

	/**
	 * Update the integer value of the instruction based on the fields of this
	 * object. Call this right away EVERY TIME you modify an instruction.
	 */
	public void updateValue() {
		int instruction = 0;

		instruction += (opcode << 26);
		instruction += (rs << 21);
		instruction += (rt << 16);
		instruction += immediate;

		this.value = instruction;

	}

}
