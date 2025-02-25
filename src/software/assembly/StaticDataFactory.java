package software.assembly;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import software.datatypes.StaticData;


/**
 * Create an instance of the StaticData class from String 'statements' of the form
 * 
 * "LABEL: .type value"
 * "LABEL: .type value1, ..., valueN"
 * 
 * Use this to translate data statements from text to labels which correspond 
 * to a list of integers, which represent together an element of static data 
 * that should be established as part of the program.
 * 
 * @sammc
 */
public class StaticDataFactory {
	
	/**
	 * Translate a list of data statements, with the provided format in this classes description.
	 * @param statements
	 * @return
	 * @throws Exception
	 */
	public List<StaticData> translateDataStatements(List<String> statements) throws Exception {
		List<StaticData> staticData = new ArrayList<StaticData>();
				
		for (String statement: statements) {
			staticData.add(translateData(statement));
		}
		
		return staticData;
	}
	
	
	// parse one line of static data declaration
	
	// I can pass the type and 
	
	// .word, .space(general storage), .asciiz
	// for now, assume that everything is clean in this statement, so the following format:
	// label: .type "value"
	
	/**
	 * Translates a single declaration of static data using the provided format in this classes description
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public StaticData translateData(String statement) throws Exception {
		Scanner scan = new Scanner(statement);
		
		if (!scan.hasNext()) {
			scan.close();
			throw new Exception("Error parsing static data, improper number of element per line.");
		}
		
		
		String label = scan.next();
		
		
		if (!scan.hasNext()) {
			scan.close();
			throw new Exception("Error parsing static data, improper number of element per line.");
		}
		
		String type = scan.next();
		
		if (!scan.hasNext()) {
			scan.close();
			throw new Exception("Error parsing static data, improper number of element per line.");
		}
		
		// take the rest of the values to parse them into a proper list of values that will be inserted into memory
		// based on it's length and associated with it's label
		String values = scan.nextLine();
		int[] wordValues = encodeData(type, values);
		
		scan.close();
		
		return new StaticData(label, wordValues);
		
		
		
	}
	
	
	// expecting a string of format x, y, z where x y z and z are the appropriate types
	private int[] encodeData(String type, String values) throws Exception {
		String[] v = values.split(",");
		int[] result = null;
		
		if (type.equals(".space")) {
			int numReservedWords = Integer.parseInt(values);
			result = new int[numReservedWords];
		} else if (type.equals(".asciiz")) {
			
			// take off first and last characters
			String valueNoQuotes = values.substring(1, values.length() - 2);
			
			// Assign result size
			result = new int[valueNoQuotes.length() + 1]; // null terminate
			result[valueNoQuotes.length()] = 0;
			
			// For each character, put the numeric value at the next spot starting at index 0
			int i = 0;
			for (char c : valueNoQuotes.toCharArray()) {
				result[i++] = Character.getNumericValue(c);
			}
			
			
		} else if (type.equals(".word")) {
			result = new int[v.length];
		
			for (int i = 0; i < v.length; i++) {
				String sVal = v[i].trim();
			
				
				
				if (sVal.endsWith(",")) {
					sVal = sVal.substring(0, sVal.length() - 1);
				}
			
			
				result[i] = Integer.parseInt(sVal);
			}
		
		}
	
		return result;
		
		
	}
}
