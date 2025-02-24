package hardware.datatypes;

import hardware.MipsIsa;
/**
 * Represent Jump type instructions
 * @sammc
 */

//opcode (31:26) address (25:0)
public class J_Instruction extends Instruction {
	public int address;
	
	// Construct from components
	public J_Instruction(int opcode, int address) {
		super(-1);
		this.usesLabel = true;
		int instruction = 0;

		// opcode is always zero
		instruction += (opcode << 26);
		instruction += address;
				
		this.value = instruction;
	}
			
	// Construct from integer
	public J_Instruction(int instruction) {
		super(instruction);
		this.usesLabel = true;
		// Find fields
		opcode = copyBitField(instruction, 31, 26);
		address = copyBitField(instruction, 25, 0);
	}
	
	
	@Override
	public String getASM() {
		return "(op:" + MipsIsa.getInstructionName(this) + ", addr:" + address + ")";
	}
	
}