package datatypes;

public class StaticDataElement {
	
	public String label;
	public Integer[] values;
	
	public StaticDataElement(String label, int[] values) {
		this.label = label;
		
		this.values = new Integer[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i];
		}
	}	

}
