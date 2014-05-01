package gpAlgorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import au.com.bytecode.opencsv.CSVWriter;
import mmgrd.MMGRDTreeNodeFunction;
import mmgrd.MMGRDTreeNodeTerminal;
import mmgrd.operations.MMGRDBinary;
import mmgrd.operations.MMGRDUnary;

public class RunGP {

	public static void main(String[] args) {
		// CONSTANTS
		int POPULATION_SIZE = 100;
		int NUM_TERMINALS = 3;
		int NUM_EXPERIMENTS = 10;
		String filePath = "outputData\\multifunctions\\sin(2xxy)cos(5y)";
		
		double mse = 0.01;
		// Initialize dataset
		Dataset dataset = new Dataset("inputData//sin(4xx+y)+sin(x+y).csv");
//		Dataset dataset = new Dataset("inputData//X-1 + (X x Y x Y ) - (X x Y) + (X x X x X).csv");
//		Dataset dataset = new Dataset("inputData//flux-reduced.csv");
		// Initialize set of operators
		MMGRDTreeNodeFunction addition = new MMGRDTreeNodeFunction(null,
				new MMGRDBinary("+"));
		MMGRDTreeNodeFunction substraction = new MMGRDTreeNodeFunction(null,
				new MMGRDBinary("-"));
		MMGRDTreeNodeFunction multiplicaton = new MMGRDTreeNodeFunction(null,
				new MMGRDBinary("*"));
		MMGRDTreeNodeFunction division = new MMGRDTreeNodeFunction(null,
				new MMGRDBinary("/"));
		MMGRDTreeNodeFunction sin = new MMGRDTreeNodeFunction(null,
				new MMGRDUnary("sin"));
		MMGRDTreeNodeFunction cos = new MMGRDTreeNodeFunction(null,
				new MMGRDUnary("cos"));
		MMGRDTreeNodeFunction tan = new MMGRDTreeNodeFunction(null,
				new MMGRDUnary("tan"));
//		MMGRDTreeNodeFunction power = new MMGRDTreeNodeFunction(null,
//				new MMGRDBinary("^"));
		List<MMGRDTreeNodeFunction> operators = new ArrayList<MMGRDTreeNodeFunction>();
		operators.add(addition);
		operators.add(substraction);
		operators.add(multiplicaton);
		operators.add(division);
//		operators.add(power);
		operators.add(sin);
		operators.add(cos);
		operators.add(tan);

		// Initialize set of terminals
		List<MMGRDTreeNodeTerminal> terminals = new ArrayList<MMGRDTreeNodeTerminal>();
		terminals.add(new MMGRDTreeNodeTerminal(null, 4 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 63 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 12 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 2 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 3 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 5 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 7 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 11 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 13 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 17 + ""));
		terminals.add(new MMGRDTreeNodeTerminal(null, 23 + ""));
		for (int j = 0; j < NUM_TERMINALS; j++) {
			for (int i = 0; i < dataset.getDimension(); i++) {
				terminals.add(new MMGRDTreeNodeTerminal(null, dataset
						.getVariableName(i)));
			}
			terminals.add(new MMGRDTreeNodeTerminal(null, Math.E + ""));
		}
		
		List<List<Double>> fitness = new ArrayList<List<Double>>();
		List<Double> executionTime = new ArrayList<Double>();
		List<Boolean> success = new ArrayList<Boolean>();
		
		for (int i = 0; i<NUM_EXPERIMENTS;i++){
			System.out.println("Starting experiment "+ i);
			MMGRD mmgrd = new MMGRD(dataset, POPULATION_SIZE, operators, terminals);
			long startTime = System.currentTimeMillis();
			mmgrd.iterate(10000,mse);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			fitness.add(mmgrd.getGenerationFitness());
			executionTime.add(new Double(totalTime/1000));
			success.add(new Boolean(mmgrd.isFoundSolution()));
			System.out.println("Iteration: "+i+"Time spend: "+ totalTime/1000 + " seconds" );
			System.out.println("Number of generations: "+ mmgrd.getGeneration() );	
		}
		//Save results

		saveResultsToFile(filePath,fitness,executionTime,success);
	}
	
	public static void saveResultsToFile(String filepath, List<List<Double>> fitness, List<Double> executionTime, List<Boolean> success){
		
		List<Double> bestFitness = getBest(fitness);
		List<Double> worstFitness = getWorst(fitness);
		List<Integer> numberOfGenerations = getGenerationsAsList(fitness);
		
		String filenameFitnessBest = filepath + "_fitness_best.csv";
		String filenameFitnessWorst = filepath + "_fitness_worst.csv";
		String filenameResults = filepath + ".csv";
		
		writeFitnessToFile(filenameFitnessBest,bestFitness);
		writeFitnessToFile(filenameFitnessWorst,worstFitness);
		
		writeResults(filenameResults,numberOfGenerations,  executionTime,success );
		
		
	}
	private static void writeResults(String filenameResults, List<Integer> numGen,  List<Double> execTime, List<Boolean> success) {
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(filenameResults), ',');
			String[] header = new String[] {"Number of generations", "Average execution time", "Success ratio" };
			writer.writeNext(header);
			for (int i =0; i< numGen.size(); i++){
				String[] results = {""+numGen.get(i),""+execTime.get(i),""+success.get(i)};
				writer.writeNext(results);
			}
			
			writer.close();
			
		} catch (IOException e) {
			System.err.println("error writing CSV file");
			System.exit(-2);
		}
		
	}

	private static List<Integer> getGenerationsAsList(List<List<Double>> fitness) {
		List <Integer> result = new ArrayList<Integer>();
		for (List<Double> list : fitness){
			result.add(new Integer(list.size()));
		}
		return result;
	}

	private static void writeFitnessToFile(String filename, List<Double> data){
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(filename), ',');
			String[] header = new String[] {"Generation", "Fitness" };
			writer.writeNext(header);
			for (int i = 0; i < data.size(); i++) {
					String[] entries = new String[] {""+i,""+data.get(i) };
					writer.writeNext(entries);
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("error writing CSV file");
			System.exit(-2);
		}
		
	}
	private static List<Double> getWorst(List<List<Double>> fitness) {
		int worst = 0;
		List<Double> result = fitness.get(0);
		for (List<Double> list : fitness){
			if (worst < list.size()){
				worst = list.size();
				result = list;
			}
		}
		return result;
	}

	private static List<Double> getBest(List<List<Double>> fitness) {
		int best = Integer.MAX_VALUE;
		List<Double> result = fitness.get(0);
		for (List<Double> list : fitness){
			if (best > list.size()){
				best = list.size();
				result = list;
			}
		}
		return result;
	}
	
	private static double getDoubleAverage(List<Double> data){
		double d = 0.0;
		for (Double current: data){
			d+=current.doubleValue();
		}
		if (data.size()>0){
			d = d / ((double)data.size());
		}
		return d;
	}
	private static double getBooleanAverage(List<Boolean> data){
		int num_success = 0;
		for (Boolean current: data){
			num_success += (current) ? 1 : 0;
		}
		double d = 0;
		if (data.size()>0){
			d = num_success / ((double)data.size());
		}
		return d;
	}
	
}
