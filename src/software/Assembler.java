package software;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import datatypes.StatementCollection;
import datatypes.Program;

public class Assembler {


	
	
	
	public Program assemble(String filepath) throws Exception {
		File file = new File(filepath);
		if (!file.exists() || !file.getName().contains(".s") || file.isDirectory()) {
			throw new Exception("No suitable file found to assemble.");
		}
		
		
		StatementCollection rawStatements = parseFile(file);
		
		// check for failure to find instructions
		if (rawStatements.text.size() == 0) {
			throw new Exception("No instructions found in assembly file!");
		}
		
	
		
		// extract labels here
		
		
		
		
		return asm(statements);
		
		
		
	}
	
	private Program asm(List<String> statements) throws Exception {
		Translator ts = new Translator();		
		Program result = new Program();
		result.instructions = ts.translateStatements(statements);
		// set data or whatever TODO
		
		return result;
	}
	
	
	
	// retrieve all instructions and static data in an ordered list
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
