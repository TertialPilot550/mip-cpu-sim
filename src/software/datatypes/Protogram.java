package software.datatypes;

import java.util.Arrays;
import java.util.List;

import hardware.datatypes.Instruction;

/**
 * The product of the assembler, analogous to an object file. Needs to be
 * processed with a linker object before a program can be produced.
 * 
 * @sammc
 */

public class Protogram {

	public List<Instruction> instructions;
	public List<StaticData> data;

	@Override
	public String toString() {
		String res = "";
		for (Instruction i : instructions) {
			res = res + i.getASM() + "\n";
		}

		res = res + "\n";
		for (StaticData d : data) {

			res = res + d.label + " " + Arrays.toString(d.values) + "\n";

		}
		return res;
	}
}
