package software.assembly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import software.datatypes.Protogram;
import software.datatypes.StatementCollection;
import software.exceptions.FileParseException;
import software.exceptions.InvalidAssemblyException;

/**
 * This object, given a file path or File object, will return a partially
 * assembled proto-gram object, analogous to an object file.
 * 
 * @sammc
 */

public class Assembler {

	public Protogram assemble(String filepath) {
		File f = new File(filepath);
		if (f.exists() && !f.isDirectory()) {
			try {
				return assembleFile(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * High Level function that assembles the file with the provided file path.
	 * 
	 * Manages program object pieces and adjust statements as necessary.
	 * 
	 * @param filepath
	 * @return a Program object
	 * @throws Exception
	 */
	public Protogram assembleFile(File file) throws Exception {
		// validate file
		if (!file.exists() || !file.getName().contains(".s") || file.isDirectory()) {
			throw new FileNotFoundException();
		}

		// Statements for data should contain labels, statements for text may optionally
		// have labels
		StatementCollection rawStatements = parseFile(file);

		// Error Checking
		boolean allProperlyFormed = validateStatements(rawStatements);
		if (!allProperlyFormed)
			throw new FileParseException();

		// Build program result
		Protogram result = new Protogram();

		// Translate .text statements into list of Instructions objects for easier
		// linking
		InstructionTranslator it = new InstructionTranslator();
		result.instructions = it.translateTextStatements(rawStatements.text);

		// Translate .data statements into list of StaticData
		StaticDataFactory sd = new StaticDataFactory();
		result.data = sd.translateDataStatements(rawStatements.data);

		return result;
	}

	// TODO
	private boolean validateStatements(StatementCollection statements) throws Exception {
		// check for failure to find instructions
		if (statements.text.size() == 0)
			throw new InvalidAssemblyException();

		return true; // TODO
	}

	/**
	 * Get the statements out of the file.
	 * 
	 * Given a file object, reads each statement into one of two categories: Text,
	 * or Data, and returns a composite object with a List of each type
	 * 
	 * (I'm assuming that this caller has to deal with io stuff.)
	 * 
	 * @param file is the file to parse.
	 * @return All text statements, in order, in a list. Also all data statements,
	 *         in order, in a list.
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
			if (statement.trim().equals("")) {
				continue;
			}

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
