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
 * Class that transforms a Protogram into a fully fledged program object that can be loaded into the cpu simulation
 * 
 * @sammc
 */
public class Linker {
	
	public Program link(Protogram p) {
		// Link labels
		LabelTable lt = new LabelTable(p);
		matchLabels(p.instructions, lt);
		
		// Now instructions have proper values always so it should be trivial to convert them
		
		// Build Result
		Program result = new Program();
		
		result.bin = createInstructionBin(p.instructions);
		result.staticData = createDataBin(p.data);
		
		
		return result;
	}
	
	private int[] createInstructionBin(List<Instruction> ins) {
		int[] result = new int[ins.size()];
		for (int i = 0; i < ins.size(); i++) {
			result[i] = ins.get(i).value;
		}
		return result;
	}
	
	private int[] createDataBin(List<StaticData> data) {
		int[] result = new int[data.size()];
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
	private void matchLabels(List<Instruction> instructions, LabelTable table) {
		for (int i = 0; i < instructions.size(); i++) {
			
			Instruction ins = instructions.get(i);
			
			if (ins.isIType()) {
				I_Instruction iType = (I_Instruction) ins;
				
				// if it has a label value
				if (iType.hasLabelImm) {
					// extract it
					int labelVal = table.match(iType.labelOperand);
					
					// if it's beq or ne
					if (iType.isBranch()) {
						// address of the current instruction + 1
						int currentAddr = CPU.PC_STARTING_ADDRESS + 1 + i;
						int distanceToBranch = labelVal - currentAddr;
						iType.immediate = distanceToBranch;
						
						
					} else {
						// otherwise just set the value of the immediate
						iType.immediate = labelVal;
					}
					// Ensure that using the value field of the instruction object will work for translation
					iType.updateValue();	
				}
				
			} else if (ins.isJType()) {
				J_Instruction jType = (J_Instruction) ins;
				
				if (jType.hasLabelImm) {
					int labelVal = table.match(jType.labelOperand);
					jType.address = labelVal;
					jType.updateValue();
				}	
				
			}			
		}
	}
}