package hardware.datatypes;

/**
 * Use this data type to greatly simplify dealing with instructions and
 * conversions Should remove any need for the programmer to think about bits or
 * bit fields and whatnot. Can also be quickly converted to integer value or asm
 * 
 * @sammc
 */

public class Instruction {

	public int value;
	public int opcode;
	public boolean isLabeled = false;
	public String label = "";

	public Instruction(int val) {
		this.value = val;
		this.opcode = copyBitField(val, 31, 26);
	}

	/*
	 * Conversion functions
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
		return (opcode > 3 && opcode != 0x3f);
	}

	public boolean isHalt() {
		return (opcode == 0x3F);
	}

	public boolean equals(Instruction c) {
		// if the values match ...
		if (value == c.value) {

			// and labels match or label condition matches return true
			if (isLabeled && c.isLabeled && (label.equals(c.label))) {
				return true;

			} else if (isLabeled == c.isLabeled) {
				return true;
			}
		}
		return false;
	}

	public String getASM() {
		if (isHalt()) {
			return "halt";
		}
		if (isRType()) {
			return ((R_Instruction) this).getASM();
		} else if (isIType()) {
			return ((I_Instruction) this).getASM();
		} else {
			return ((J_Instruction) this).getASM();
		}
	}

	/*
	 * Utility function for dealing with instructions Private, since bit
	 * manipulation should only happen in this class
	 */
	protected static int copyBitField(int src, int lInd, int rInd) {
		// 0x0000.0000
		src >>= rInd;
		// construct a bit string of the appropriate length
		int len = lInd - rInd;
		// 2^n - 1 = n bit number with all digits as 1
		int lenBitsOfOne = (int) (Math.pow(2, len + 1) - 1);
		// Preserve the bits you mean too, clear the rest
		return lenBitsOfOne & src;
	}

}
