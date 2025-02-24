package software.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Store a sorted but unparsed program. Used by the assembler
 */

public class StatementCollection {
	
	public List<String> data;
	public List<String> text;
	
	public StatementCollection() {
		data = new ArrayList<String>();
		text = new ArrayList<String>();
	}
	
	
}