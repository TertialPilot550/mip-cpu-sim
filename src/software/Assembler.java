package software;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {

	
	
	
	public Program assemble(String filepath) throws Exception {
		File file = new File(filepath);
		
		if (!file.exists() || !file.getName().contains(".s") || file.isDirectory()) {
			throw new Exception("No suitable file found to assemble.");
		}
		
		FileInputStream fs = new FileInputStream(file);
		Scanner scan = new Scanner(fs);
		
		// for now, just skip to text label
		
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			
			if (line.contains(".text")) {
				break;
			}
		}
		
		// Now we're at the meat and taters, or the end of the file, in which case the loop won't execute
		ArrayList<String> statements = new ArrayList<String>();
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (!line.trim().equals("")) { // dont add blank lines
				statements.add(line);
;
			}
		}
		
		scan.close();
		
		// check for failure to find
		if (statements.size() == 0) {
			throw new Exception("No instructions found in assembly file!");
		}
		
		
		// now we've definitely got stuff
		// TODO further verification for syntax?
		
		
		// maybe add data section to this as well
		return asm(statements);
		
		
		
	}
	
	private Program asm(ArrayList<String> statements) throws Exception {
		
		
		Translator ts = new Translator();
		ArrayList<Integer> instructions = ts.translateStatements(statements);
		
		Program result = new Program();
		result.setText(instructions);
		// set data or whatever TODO
		
		return result;
	}
	
	
	
}
