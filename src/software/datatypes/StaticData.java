package software.datatypes;

public class StaticData {

	public String label;
	public Integer[] values;

	public StaticData(String label, int[] values) {
		this.label = label;

		this.values = new Integer[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i];
		}
	}

}
