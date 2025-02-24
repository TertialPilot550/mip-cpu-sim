package datatypes;

import java.util.ArrayList;

/**
 * Store a half parsed program. Used by the assembler
 */

public class StatementCollection {
	
	public ArrayList<String> data;
	public ArrayList<String> text;
	
	public StatementCollection() {
		data = new ArrayList<String>();
		text = new ArrayList<String>();
	}
	
	
}