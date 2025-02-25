package software.linking;

import java.util.HashMap;
import java.util.List;

import hardware.cpu.CPU;
import hardware.datatypes.Instruction;
import software.datatypes.Protogram;
import software.datatypes.StaticData;

public class LabelTable {

	private HashMap<String, Integer> data;
	
	public LabelTable(Protogram p) {
		data  = new HashMap<String, Integer>();
		buildTable(p);
	}

	/**
	 * Build a LabelTable object
	 * @param p
	 * @return
	 */
	public void buildTable(Protogram p) {
		addTextLabels(p.instructions);
		addDataLabels(p.data);		
	}
	
	
	/**
	 * Get the tied address for the provided label
	 * @param label
	 * @return
	 */
	public Integer match(String label) {
		return data.get(label);
	}
	
	
	/**
	 * Adds the labels from the static element to the label table with it's calculated address in memory.
	 * 
	 * @param labelTable
	 * @param staticData
	 */
	private void addDataLabels(List<StaticData> staticData) {
		int numWordsAllocated = 0;
		// for each piece of static data ... 
		for (int i = 0; i < staticData.size(); i++) {
			// calculate it's address
			int addressOfData = CPU.STATIC_DATA + numWordsAllocated;
			
			// Increment the amount of static data allocated so far for this program
			int dataLength = staticData.get(i).values.length;
			numWordsAllocated += dataLength;
			
			// add the label and the address as a pair to the labelTable
			data.put(staticData.get(i).label, addressOfData);
		}
	}
	
	/**
	 * Add the labels from the text section to the label table with it's calculated address in memory.
	 * @param labelTable
	 * @param staticData
	 */
	private void addTextLabels(List<Instruction> instructions) {
		
		for (int i = 0; i < instructions.size(); i++) {
			// if the instruction is labeled, then add the label to the label table with the literal location in memory
			if (instructions.get(i).isLabeled) {
				data.put(instructions.get(i).label, CPU.PC_STARTING_ADDRESS + i);
			}
		}
	}
	
}
