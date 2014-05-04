package datasetGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * This class provides the methods required to generate dataset provided 1D or
 * 2D functions. Code could be optimized and an n-dimension method could had
 * been created. Reserved for future implementations
 * 
 * @author mamenarguez
 * 
 */
public class DatasetGenerator {

	/**
	 * Generates a csv file with a 1D dataset with the parameters of input
	 * 
	 * @param filePath
	 *            path to be saved icluding name and file extension
	 * @param dimension
	 *            dimension parameters and ranges
	 * @param f
	 *            1D function that will calculate the output
	 */
	public static void generate1DCSV(String filePath,
			DatasetDimension dimension, CustomFunction1D f) {
		double stepSize = (double) (dimension.getTo()[0] - dimension.getFrom()[0])
				/ dimension.getNumberOfSamples()[0];
		double valuesY[] = new double[dimension.getNumberOfSamples()[0]];
		double valuesX[] = new double[dimension.getNumberOfSamples()[0]];

		double currentValue = dimension.getFrom()[0];
		for (int i = 0; i < dimension.getNumberOfSamples()[0]; i++) {
			double random = (double) Math.random();
			valuesX[i] = (double) (currentValue + Math.pow(-1, (int) random)
					* random * stepSize);
			valuesY[i] = f.evaluate(valuesX[i]);
			currentValue += stepSize;
		}

		CSVWriter writer;
		try {

			File file = new File(filePath);
			file.getParentFile().mkdirs();
			FileWriter fileWriter = new FileWriter(file);
			writer = new CSVWriter(fileWriter, ',');
			
			//write header
			String[] header = {"x","y"};
			writer.writeNext(header);
			
			// feed in your array (or convert your data to an array)
			for (int i = 0; i < dimension.getNumberOfSamples()[0]; i++) {
				String[] entries = new String[] { "" + valuesX[i],
						"" + valuesY[i] };
				writer.writeNext(entries);
			}

			writer.close();
		} catch (IOException e) {
			System.err.println("error writing CSV file");
			System.exit(-2);
		}
	}

	/**
	 * Generates a 1D dataset with the parameters of input
	 * 
	 * @param dimension
	 *            dimension parameters and ranges
	 * @param f
	 *            1D function that will calculate the output
	 */
	public static Object[] generateDataset1D(DatasetDimension dimension,
			CustomFunction1D f) {
		double stepSize = (double) (dimension.getTo()[0] - dimension.getFrom()[0])
				/ dimension.getNumberOfSamples()[0];
		double valuesY[] = new double[dimension.getNumberOfSamples()[0]];
		double valuesX[] = new double[dimension.getNumberOfSamples()[0]];

		double currentValue = dimension.getFrom()[0];
		for (int i = 0; i < dimension.getNumberOfSamples()[0]; i++) {
			double random = (double) Math.random();
			valuesX[i] = (double) ((double) currentValue + Math.pow(-1,
					(int) random) * random * stepSize);
			valuesY[i] = f.evaluate(valuesX[i]);
			currentValue += stepSize;
		}
		return new Object[] { valuesX, valuesY };
	}

	/**
	 * Generates a 2D dataset with the parameters of input
	 * 
	 * @param filePath
	 *            path to be saved including name and file extension
	 * @param dimension
	 *            dimension parameters and ranges
	 * @param f
	 *            2D function that will calculate the output
	 */
	public static Object[] generateDataset2D(DatasetDimension dimension,
			CustomFunction2D f) {
		int numSamplesX = dimension.getNumberOfSamples()[0];
		int numSamplesY = dimension.getNumberOfSamples()[1];

		double stepSizeX = (double) (dimension.getTo()[0] - dimension.getFrom()[0])
				/ numSamplesX;
		double stepSizeY = (double) (dimension.getTo()[1] - dimension.getFrom()[1])
				/ numSamplesY;

		double valuesX[] = new double[numSamplesX];
		double valuesY[] = new double[numSamplesY];
		double valuesZ[][] = new double[numSamplesX][numSamplesY];
		double currentValueX = dimension.getFrom()[0];
		double currentValueY = dimension.getFrom()[1];
		for (int i = 0; i < numSamplesX; i++) {
			double randomX = (double) Math.random();
			double randomY = (double) Math.random();
			valuesX[i] = (double) (currentValueX + Math.pow(-1, (int) randomX)
					* randomX * stepSizeX);
			valuesY[i] = (double) (currentValueY + Math.pow(-1, (int) randomY)
					* randomY * stepSizeY);
			currentValueX += stepSizeX;
			currentValueY += stepSizeY;
		}
		for (int i = 0; i < numSamplesX; i++) {
			for (int j = 0; j < numSamplesY; j++) {
				valuesZ[i][j] = f.evaluate(valuesX[i], valuesY[j]);
			}
		}
		return new Object[] { valuesX, valuesY, valuesZ };
	}

	/**
	 * Generates a csv file with a 2D dataset with the parameters of input
	 * 
	 * @param filePath
	 *            path to be saved including name and file extension
	 * @param dimension
	 *            dimension parameters and ranges
	 * @param f
	 *            2D function that will calculate the output
	 */
	public static void generate2DCSV(String filePath,
			DatasetDimension dimension, CustomFunction2D f) {
		int numSamplesX = dimension.getNumberOfSamples()[0];
		int numSamplesY = dimension.getNumberOfSamples()[1];

		double stepSizeX = (double) (dimension.getTo()[0] - dimension.getFrom()[0])
				/ numSamplesX;
		double stepSizeY = (double) (dimension.getTo()[1] - dimension.getFrom()[1])
				/ numSamplesY;

		double valuesX[] = new double[numSamplesX];
		double valuesY[] = new double[numSamplesY];
		double valuesZ[][] = new double[numSamplesX][numSamplesY];
		double currentValueX = dimension.getFrom()[0];
		double currentValueY = dimension.getFrom()[1];
		for (int i = 0; i < numSamplesX; i++) {
			double randomX = (double) Math.random();
			double randomY = (double) Math.random();
			valuesX[i] = (double) (currentValueX + Math.pow(-1, (int) randomX)
					* randomX * stepSizeX);
			valuesY[i] = (double) (currentValueY + Math.pow(-1, (int) randomY)
					* randomY * stepSizeY);
			currentValueX += stepSizeX;
			currentValueY += stepSizeY;
		}
		for (int i = 0; i < numSamplesX; i++) {
			for (int j = 0; j < numSamplesY; j++) {
				valuesZ[i][j] = f.evaluate(valuesX[i], valuesY[j]);
			}
		}

		CSVWriter writer;
		try {
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			FileWriter fileWriter = new FileWriter(file);
			writer = new CSVWriter(fileWriter, ',');
			//write header
			String[] header = {"x","y","z"};
			writer.writeNext(header);
			// feed in your array, (or convert your data to an array)
			for (int i = 0; i < numSamplesX; i++) {
				for (int j = 0; j < numSamplesY; j++) {
					String[] entries = new String[] { "" + valuesX[i],
							"" + valuesY[j], "" + valuesZ[i][j] };
					writer.writeNext(entries);
				}
			}

			writer.close();
		} catch (IOException e) {
			System.err.println("error writing CSV file");
			System.exit(-2);
		}
	}
}
