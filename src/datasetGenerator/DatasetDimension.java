package datasetGenerator;
/**
 * This class implements the dataset to be used and its parameters
 * 
 * @author mamenarguez
 * 
 */
public class DatasetDimension {
	private double from[], to[];
	private int numberOfSamples[];

	/**
	 * 
	 * @param from
	 *            array specifying min range on inputs in order
	 * @param to
	 *            array specifying max range on inputs by order
	 * @param numberOfSamples
	 *            number of sample wanted in the dataset
	 */
	public DatasetDimension(double[] from, double[] to, int[] numberOfSamples) {
		super();
		this.from = from;
		this.to = to;
		this.numberOfSamples = numberOfSamples;
	}

	public double[] getFrom() {
		return from;
	}

	public void setFrom(double[] from) {
		this.from = from;
	}

	public double[] getTo() {
		return to;
	}

	public void setTo(double[] to) {
		this.to = to;
	}

	public int[] getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int[] numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

}
