package datatypes;

import processor.MipsIsa;
/**
 * Represent Register type instructions
 * @sammc
 */

//opcode (31:26) Rs(25:21) Rt (20:16) Rd (15:11) shamt (10:6) func (5:0)
public class R_Instruction extends Instruction {

	public int opcode;
	public int rs;
	public int rt;
	public int rd;
	public int shamt;
	public int func;
	
	// Construct from components
	public R_Instruction(int rs, int rt, int rd, int shamt, int func) {
		super(-1);
		int instruction = 0;

		// opcode is always zero
		instruction += (rs << 21);
		instruction += (rt << 16);
		instruction += (rd << 11);
		instruction += (shamt << 6);
		instruction += func;
		
		this.value = instruction;
	}
	
	// Construct from integer
	public R_Instruction(int instruction) {
		super(instruction);
		// Find fields
		rs = copyBitField(instruction, 25, 21);
		rt = copyBitField(instruction, 20, 16);
		rd = copyBitField(instruction, 15, 11);
		shamt = copyBitField(instruction, 10, 6); 
		func = copyBitField(instruction, 5, 0);
	}
	
	@Override
	public String getASM() {
		return "(op:" + MipsIsa.getInstructionName(this) + ", rd:" + rd + ", rs:" + rs + ", rt:" + rt + ", shamt:" + shamt + ")";
	}
	
}