package software.linking;

import java.util.List;

import hardware.cpu.CPU;
import hardware.datatypes.I_Instruction;
import hardware.datatypes.Instruction;
import hardware.datatypes.J_Instruction;
import software.datatypes.Program;
import software.datatypes.Protogram;
import software.datatypes.StaticData;

/**
 * Class that transforms a Protogram into a fully fledged program object that
 * can be loaded into the cpu simulation
 * 
 * @sammc
 */
public class Linker {

	/**
	 * Transform a partially compiled proto-gram into lists of ints containing the
	 * instruction binary values
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public Program link(Protogram p) throws Exception {
		// Link labels
		LabelTable lt = new LabelTable(p);
		matchLabels(p.instructions, lt);

		// Now instructions have proper values always so it should be trivial to convert
		// them

		// Build Result
		Program result = new Program();

		result.bin = createInstructionBin(p.instructions);
		result.staticData = createDataBin(p.data);

		return result;
	}

	int[] createInstructionBin(List<Instruction> ins) {
		int[] result = new int[ins.size()];
		for (int i = 0; i < ins.size(); i++) {
			result[i] = ins.get(i).value;
		}
		return result;
	}

	int[] createDataBin(List<StaticData> data) {

		// make a properly sized array
		int sumDataSize = 0;
		for (StaticData elem : data) {
			sumDataSize += elem.values.length;
		}
		int[] result = new int[sumDataSize];

		// assign values
		int ind = 0;
		// for each element of static data
		for (StaticData elem : data) {
			// for each value in that data segment
			for (Integer val : elem.values) {
				// set the value and increase the index
				result[ind++] = val;
			}

		}
		return result;
	}

	// for any instruction that is an i or j type and has a label parameter link it
	// this doesn't return anything, but it modifies the instructions, like
	// 'applying' the table as a function
	public void matchLabels(List<Instruction> instructions, LabelTable table) throws Exception {
		for (int i = 0; i < instructions.size(); i++) {

			Instruction ins = instructions.get(i);

			if (ins.isIType()) {
				matchIType((I_Instruction) ins, table, i);
			} else if (ins.isJType()) {
				matchJType((J_Instruction) ins, table);
			}

		}
	}

	void matchIType(I_Instruction instruction, LabelTable table, int currentInstructionNumber) throws Exception {
		// already done
		if (!instruction.hasLabelImm()) {
			return;
		}

		// match to get the value
		int labelVal = table.match(instruction.labelOperand);

		if (instruction.isBranch()) { // implement offset addressing
			// address of the current instruction + 1
			int currentAddr = CPU.PC_STARTING_ADDRESS + 1 + currentInstructionNumber;
			int distanceToBranch = labelVal - currentAddr;
			instruction.immediate = distanceToBranch;

		} else { // otherwise just set the value of the immediate
			instruction.immediate = labelVal;
		}
		// update binary instruction value
		instruction.labelOperand = "";
		instruction.updateValue();

	}

	void matchJType(J_Instruction instruction, LabelTable table) throws Exception {
		if (instruction.hasLabelImm()) {
			int labelVal = table.match(instruction.labelOperand);
			instruction.address = labelVal;
			instruction.updateValue();
		}
	}
}