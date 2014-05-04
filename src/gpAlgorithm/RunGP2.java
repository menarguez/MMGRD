package gpAlgorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmgrd.MMGRDTreeNodeFunction;
import mmgrd.MMGRDTreeNodeTerminal;
import mmgrd.operations.MMGRDBinary;
import mmgrd.operations.MMGRDUnary;
import au.com.bytecode.opencsv.CSVWriter;


public class RunGP2 {
	

	
	public static void main(String[] args) {
		// Variable parameters
		int POPULATION_SIZE = 200;
		int NUM_EXPERIMENTS = 1;
		double SIMPLIFICATION_RATE = 0.35;
		int MAX_NUM_NODES = 35;
		int MAX_INI_DEPTH = 2;
		int MAX_ITERATIONS = 10000;
		double mse = 0.01;
		boolean INCLUDE_e = false;
		String filePathForOutputs = "outputData\\a\\(x-1)+xyy-xy+xxx";
		
		//Read data first
		Dataset dataset = new Dataset("inputs\\2d\\5log(x)+6xxx.csv");
		
		
		//Add or remove operators. Only sin,cos,+,-,/,* are implemented. Change MMGRD Binary/Unary to add more
		String[] unaryOperands = {"sin","cos","tan"};
//		String[] unaryOperands = {"log"};
		String[] bynaryOperands = {"+","-","*","/","^"};
		List<MMGRDTreeNodeFunction> operators = getAllOperators(unaryOperands, bynaryOperands);
		//Initialize terminals
		List<MMGRDTreeNodeTerminal> terminals = getAllTerminals(1,25,true,1.0d, dataset,INCLUDE_e);
		
		//Performance analysis variables
		List<List<Double>> fitness = new ArrayList<List<Double>>();
		List<Double> executionTime = new ArrayList<Double>();
		List<Boolean> success = new ArrayList<Boolean>();
		
		//Start running the algorithm
		
		for (int i = 0; i<NUM_EXPERIMENTS;i++){
			System.out.println("Starting experiment "+ i);
			MMGRD mmgrd = new MMGRD(dataset, POPULATION_SIZE, operators, terminals,SIMPLIFICATION_RATE,MAX_NUM_NODES,MAX_INI_DEPTH);
			long startTime = System.currentTimeMillis();
			mmgrd.iterate(MAX_ITERATIONS,mse);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			fitness.add(mmgrd.getGenerationFitness());
			executionTime.add(new Double(totalTime/1000));
			success.add(new Boolean(mmgrd.isFoundSolution()));
			System.out.println("Iteration: "+i+"Time spend: "+ totalTime/1000 + " seconds" );
			System.out.println("Number of generations: "+ mmgrd.getGeneration() );	
		}
		
		//Save results
		saveResultsToFile(filePathForOutputs,fitness,executionTime,success);
	
	}

	/**
	 * 
	 * @param number to add from
	 * @param number to add to
	 * @param varToConstantRatio ratio of quantity of constants to terminals ratio
	 * @param dataset where data is stored (need # of var. and names)
	 * @param INCLUDE_e whether e constant will added or not
	 * @return list with all terminal nodes
	 */
	
	private static List<MMGRDTreeNodeTerminal> getAllTerminals(int from, int to, boolean onlyPrimes, double varToConstantRatio, Dataset dataset, boolean INCLUDE_e) {
		List<MMGRDTreeNodeTerminal> terminals = new ArrayList<MMGRDTreeNodeTerminal>();
		for (int i=from; i<to; i++){
			if (!onlyPrimes||isPrime(i) ){
				terminals.add(new MMGRDTreeNodeTerminal(null, ""+i));
			}
		}	
		
		if (INCLUDE_e){
			terminals.add(new MMGRDTreeNodeTerminal(null, Math.E + ""));
		}
		
		int terminalCount = terminals.size();
		for (int j = 0; j < (int)(terminalCount*varToConstantRatio); j++) {
			for (int i = 0; i < dataset.getDimension(); i++) {
				terminals.add(new MMGRDTreeNodeTerminal(null, dataset
						.getVariableName(i)));
			}
		}
		
		
		
		return terminals;
	}
	/**
	 * 
	 * @param unaryOperands possible unary operators implemented. Result must be implemented in MMGRDUnary
	 * @param bynaryOperands possible unary operators implemented. Result must be implemented in MMGRDBinary
	 * @return list with all operator nodes as MMGRDTreeNode
	 */
	private static List<MMGRDTreeNodeFunction> getAllOperators(String[] unaryOperands, String[] bynaryOperands) {

		List<MMGRDTreeNodeFunction> operators = new ArrayList<MMGRDTreeNodeFunction>();
		for (String op: unaryOperands){
			operators.add(new MMGRDTreeNodeFunction(null, new MMGRDUnary(op)));
		}
		for (String op: bynaryOperands){
			operators.add(new MMGRDTreeNodeFunction(null, new MMGRDBinary(op)));
		}
		
		return operators;
	}
	
	/**
	 * Retrieved from http://professorjava.weebly.com/isprime.html
	 * @param num number to be tested
	 * @return true if number is prime
	 */
	
	public static Boolean isPrime(int num){ //method signature. returns Boolean, true if number isPrime, false if not
	    if(num==2){ //for case num=2, function returns true. detailed explanation underneath
	      return(true);
	    }
	    for(int i=2;i<=(int)Math.sqrt(num)+1;i++){ //loops through 2 to sqrt(num). All you need to check- efficient
	      if(num%i==0){ //if a divisor is found, its not prime. returns false
	        return(false);
	      }
	    }
	    return(true); //if all cases don't divide num, it is prime.
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
			File file = new File(filenameResults);
			file.getParentFile().mkdirs();
			FileWriter fileWriter = new FileWriter(file);
			writer = new CSVWriter(fileWriter, ',');
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
			File file = new File(filename);
			file.getParentFile().mkdirs();
			FileWriter fileWriter = new FileWriter(file);
			writer = new CSVWriter(fileWriter, ',');
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
}
