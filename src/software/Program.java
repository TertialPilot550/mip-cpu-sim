package software;

import java.util.ArrayList;
import java.util.HashMap;

public class Program {

	private HashMap<String, Integer> data;
	private ArrayList<Integer> instructions;
	
	public Program() {
		data = new HashMap<String, Integer>();
		instructions = new ArrayList<Integer>();
	}
	
	public void setText(ArrayList<Integer> textSection) {
		instructions = textSection;
	}
	
	public ArrayList<Integer> getRawInstructions() {
		return instructions;
	}
	
}
