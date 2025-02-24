package software.assembly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hardware.CPU;
import software.datatypes.StaticDataElement;

/**
 * Build the label table to complete the assembly process
 * 
 * @sammc
 */
public class LabelTableFactory {
	
	/**
	 * Create the label table from static data and text labels..
	 * 
	 * @param p
	 * @param otherLabels 
	 * @return
	 */
	public Map<String, Integer> createLabelTable(List<StaticDataElement> staticData, Map<String, Integer> otherLabels) {
		// first create the final label table
		Map<String, Integer> labelTable = new HashMap<String, Integer>();
		addDataLabels(labelTable, staticData);		// add data labels
		addTextLabels(labelTable, otherLabels);		// add text labels
		return labelTable;
	}
	
	
	/**
	 * Adds the labels from the static element to the label table with it's calculated address in memory.
	 * 
	 * @param labelTable
	 * @param staticData
	 */
	private void addDataLabels(Map<String, Integer> labelTable, List<StaticDataElement> staticData) {
		int numWordsAllocated = 0;
		// for each piece of static data ... 
		for (int i = 0; i < staticData.size(); i++) {
			// calculate it's address
			int addressOfData = CPU.STATIC_DATA + numWordsAllocated;
			
			// Increment the amount of static data allocated so far for this program
			int dataLength = staticData.get(i).values.length;
			numWordsAllocated += dataLength;
			
			// add the label and the address as a pair to the labelTable
			labelTable.put(staticData.get(i).label, addressOfData);
		}
	}
	
	/**
	 * Add the labels from the text section to the label table with it's calculated address in memory.
	 * @param labelTable
	 * @param staticData
	 */
	private void addTextLabels(Map<String, Integer> labelTable, Map<String, Integer> staticData) {
		// Get the label set
		Set<String> labels = staticData.keySet();
		// For each label in the set ... 
		for (String label : labels) {
			int textIndex = staticData.get(label);
			int addr = CPU.PC_STARTING_ADDRESS + textIndex;
			// add its label and address
			labelTable.put(label, addr);
		}	
	}
	
	
}
