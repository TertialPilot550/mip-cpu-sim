package software.assembly;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import software.datatypes.Protogram;
import software.datatypes.StatementCollection;
import software.datatypes.StaticDataElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Assembler class for creating test code and working with compilation.
 * 
 * currently supports two types of expressions, .data and .text, but by default assumes .text
 * supports labels, but must be first token in the line of text
 * 
 * data statements must follow the format: "LABEL: .type value" or "LABEL: .type value1, value2, ..."
 * text statements must be a valid mips instruction supported by this simulation, see the MIPS_ISA class for more.
 * 
 * This class relies on the single use StatementCollection as well as the more used Program Object in the data types package
 * 
 * @sammc
 */
public class Assembler {
	
	/**
	 * High Level function that assembles the file with the provided file path.
	 * 
	 * Manages program object pieces and adjust statements as necessary.
	 * 
	 * @param filepath
	 * @return a Program object
	 * @throws Exception
	 */
	public Protogram assemble(String filepath) throws Exception {
		File file = new File(filepath);
		if (!file.exists() || !file.getName().contains(".s") || file.isDirectory()) {
			throw new Exception("No suitable file found to assemble.");
		}
		
		// Read the file into individual statements. Statements for data should contain labels,
		// statements for text may optionally have labels
		StatementCollection rawStatements = parseFile(file);
		boolean allProperlyFormed = validateStatements(rawStatements);
		if (!allProperlyFormed) throw new Exception("PARSING ERROR. CHECK YOUR SYNTAX.");
		
		// check for failure to find instructions
		if (rawStatements.text.size() == 0) {
			throw new Exception("No instructions found in assembly file!");
		}
		
		// extract labels here
		Map<String, Integer> textLabels = extractLabelsFromStatements(rawStatements.text);
		
		return buildProtogram(rawStatements, textLabels);
	}
	
	// Remove labels from certain lines, and record the index of the statement in the list along with
	private Map<String, Integer> extractLabelsFromStatements(List<String> statements) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		// For each statement ...
		for (int i = 0; i < statements.size(); i++) {
			
			// If the statement contains a label ...
			boolean containsLabel = statements.get(i).contains(":");
			if (containsLabel) { // Remove it.
				String[] fields = statements.get(i).split(":");
				statements.set(i, fields[1].trim());
				// Also create a new Label string and add it to the list 
				// (format is label:i, where i is the index in the list of statements)
				result.put(fields[0], i);
			}
			
		}
		return result;
	}
	
	
	
	private boolean validateStatements(StatementCollection statements) {
		return false; // TODO
	}
	
	/**
	 * Collects the 'program pieces' into a single object.
	 * 
	 * Given a StatementCollection object, returns a program object with a label table and list of instructions. IS NOT LINKED YET.
	 * 
	 * @param statements
	 * @return
	 * @throws Exception
	 */
	private Protogram buildProtogram(StatementCollection statements, Map<String, Integer> textLabels) throws Exception {
		// Basically just holds all the parts that combine to make the final program
		Protogram result = new Protogram();
		// Helper objects to manage translation
		LabelTableFactory lt = new LabelTableFactory();						// this object can combine the labels from both sections into one map
		InstructionTranslator ts = new InstructionTranslator();				// this object is how text statements are processed
		StaticDataElementFactory sde = new StaticDataElementFactory(); 		// this object is how data statements are processed
		
		// Use the helper objects to translate program information
		result.textBin = recontainInstructions(ts.translateTextStatements(statements.text));					// all instructions, in order, without labels, in integer forms.
		List<StaticDataElement> staticData = sde.translateDataStatements(statements.data);	// all static data elements with labels and word values
		result.dataBin = buildDataBin(staticData);
		result.labelTable = lt.createLabelTable(staticData, textLabels);					// make the label table
		return result;
	}
	
	/**
	 * Change the container holding the instructions values
	 * @param p
	 * @return
	 */
	private int[] recontainInstructions(List<Integer> text) {
		int[] textBin = new int[text.size()];
		
		for (int i = 0; i < textBin.length; i++) {
			textBin[i] = text.get(i);
		}
	
		return textBin;
	}
	
	
	
	
	/**
	 * Create the final binary for the static data section
	 * @param p
	 * @return
	 */
	private int[] buildDataBin(List<StaticDataElement> staticData) {
		int[] staticDataBin = new int[staticData.size()];
		int ind = 0;
		// For each static data element that is to be stored... 
		for (StaticDataElement element : staticData) {
			// and for each word of that static data
			for (Integer value : element.values) {
				// add that word to the final int[] 
				staticDataBin[ind] = value;
				
				// increment the count of ind;
				ind++;
			}	
		}
		return staticDataBin;
	}
	
	/**
	 * Get the statements out of the file.
	 * 
	 * Given a file object, reads each statement into one of two categories:
	 * Text, or Data, and returns a composite object with a List of each type
	 * 
	 * (I'm assuming that this caller has to deal with io stuff.)
	 * 
	 * @param file is the file to parse. 
	 * @return All text statements, in order, in a list. Also all data statements, in order, in a list.
	 * @throws FileNotFoundException
	 */
	private StatementCollection parseFile(File file) throws FileNotFoundException {
		FileInputStream fs = new FileInputStream(file);
		Scanner scan = new Scanner(fs);
		StatementCollection result = new StatementCollection();
		
		final int TEXT_MODE = 1;
		final int DATA_MODE = 2;
		int mode = 1;
		
		// Read the whole document with this process.
		while (scan.hasNextLine()) {
			// Take next line
			String statement = scan.nextLine();
			
			// If section label, set label and move on
			if (statement.trim().equals(".text")) {
				mode = TEXT_MODE;
				continue;
			} else if (statement.trim().equals(".data")) {
				mode = DATA_MODE;
				continue;
			} 
			
			// Otherwise, it's a statement of either type data or text, so add it.
			if (mode == TEXT_MODE) {
				result.text.add(statement);
			} else if (mode == DATA_MODE) {
				result.data.add(statement);
			}	
		}
		scan.close();
		return result;
	}
	
	
	
	
	
	
}
