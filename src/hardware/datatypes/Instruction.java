package hardware.datatypes;

import hardware.MipsIsa;

/**
 * Use this data type to greatly simplify dealing with instructions and conversions
 * Should remove any need for the programmer to think about bits or bit fields and whatnot.
 * Can also be quickly converted to integer value or asm
 * @sammc
 */

public class Instruction {
	
	public int value;
	public int opcode;
	
	public Instruction(int val) {
		this.value = val;
		this.opcode = copyBitField(val, 31, 26);
	}

	/*
	 *	Conversion functions
	 * 
	 */
	
	public R_Instruction getAsRType() {
		return new R_Instruction(value);
	}
	
	public I_Instruction getAsIType() {
		return new I_Instruction(value);
	}
	
	public J_Instruction getAsJType() {
		return new J_Instruction(value);
	}
	
	@Override
	public String toString() {
		return Integer.toHexString(value);
	}
	
	public boolean isRType() {
		return (opcode == 0);
	}
	
	public boolean isJType() {
		return (opcode == 2 || opcode == 3);
	}
	
	public boolean isIType() {
		return (opcode > 3);
	}
	
	public boolean isHalt() {
		return (opcode == 0x3F);
	}
	
	public String getASM() {
		return "(" + MipsIsa.getInstructionName(this) + ", ?)";
	}
	
	/*
	 *  Utility function for dealing with instructions
	 *  Private, since bit manipulation should only happen in this class
	 */
	protected static int copyBitField(int src, int lInd, int rInd) {
		// 0x0000.0000
	    src >>= rInd;
	    // construct a bit string of the appropriate length
	    int len = lInd - rInd;
	    System.out.println("Len: " + len);
	    // 2^n - 1 = n bit number with all digits as 1
	    int lenBitsOfOne = (int) (Math.pow(2, len) - 1);
	    // Preserve the bits you mean too, clear the rest
	    System.out.print(Integer.toBinaryString(src) + "&");
	    System.out.print(Integer.toBinaryString(lenBitsOfOne) + "=");
	    System.out.println(Integer.toBinaryString(lenBitsOfOne & src));

	    return lenBitsOfOne & src;   
	}
	
}
