package hardware.datatypes;

import hardware.MipsIsa;

/**
 * Represent Immediate type instructions
 * @sammc
 */

//opcode (31:26) Rs(25:21) Rt (20:16) Immediate (15:0)
public class I_Instruction extends Instruction {
	public int opcode; 
	public int rs;
	public int rt;
	public int immediate;
	
	// Construct from components
	public I_Instruction(int opcode, int rs, int rt, int immediate) {
		super(-1);
		int instruction = 0;

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
	
	@Override
	public String getASM() {
		return "(op:" + MipsIsa.getInstructionName(this) + ", rs:" + rs + ", rt:" + rt + ", immediate:" + immediate + ")";
	}
	
}
	