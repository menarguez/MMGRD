package gpAlgorithm;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;

import mmgrd.MMGRDTreeNode;
import mmgrd.MMGRDTreeNodeFunction;
import mmgrd.MMGRDTreeNodeTerminal;

public class MMGRD {

	private Dataset dataset;
	private int generation;
	private MMGRDTreeNode[] population;
	private double[] evaluation;
	private List<MMGRDTreeNodeFunction> operators;
	private List<MMGRDTreeNodeTerminal> terminals;
	private List<Double> generationFitness;

	private int TOURNAMENT_SELECTION_SIZE = 4;
	private double MUTATION_RATE = 0.15d;
	private EvalUtilities util;
	private double SIMPLIFICATION_RATE = 0.35;
	private double MAX_NODES =  30;
	private int MAX_NODE_INI_DEPTH = 3;
	private boolean foundSolution;
	public MMGRD(Dataset dataset, int populationSize,
			List<MMGRDTreeNodeFunction> operators,
			List<MMGRDTreeNodeTerminal> terminals) {
		this.dataset = dataset;
		this.operators = operators;
		this.terminals = terminals;
		initialize(populationSize);
	}

	public void initialize(int populationSize) {

		this.generation = 0;
		population = new MMGRDTreeNode[populationSize];
		evaluation = new double[populationSize];
		generationFitness = new ArrayList<Double>();
		foundSolution = false;
		int growLimit = (int) (0.5 * populationSize);
		for (int i = 0; i < growLimit; i++) {
			population[i] = initializeIndividualGrow(0, (int)(Math.random()*MAX_NODE_INI_DEPTH)+1);
		}

		for (int i = growLimit; i < populationSize; i++) {
			population[i] = initializeIndividualFull((int)(Math.random()*MAX_NODE_INI_DEPTH)+1);
		}

		// Static initialization of the MathEclipse engine instead of null
		// you can set a file name to overload the default initial
		// rules. This step should be called only once at program setup:
		F.initSymbols(null);
		util = new EvalUtilities();
	}

	public void iterate(int numberOfIterations, double mse ) {
		for (int i = 0; i < numberOfIterations; i++) {
			generation ++;
			mutatePopulation();
			evaluatePopulation();
			MMGRDTreeNode[] best = getFittest(1);
			int bestIndex = getIndividualIndex(best[0]);
			double bestFitness = evaluation[bestIndex];
			generationFitness.add(bestFitness);
			System.out.println(generation + ","
					+ bestFitness+ "," + best[0].simplify(util));
			if (evaluation[getIndividualIndex(best[0])]<mse){
				foundSolution = true;
				evaluate(best[0]);
				System.out.println("Minimum MSE reached. Function found = "+ best[0].simplify(util));
				break;
			}
			population = getNewPopulation();
//			evaluatePopulation();
			simplifyNodes();
			
		}
	}

	public int getGeneration() {
		return generation;
	}

	public List<Double> getGenerationFitness() {
		return generationFitness;
	}

	public int getIndividualIndex(MMGRDTreeNode node) {
		for (int i = 0; i < population.length; i++) {
			if (node.equals(population[i])) {
				return i;
			}
		}
		return -1;
	}

	public void evaluatePopulation() {
		for (int i = 0; i < population.length; i++) {
			if (population[i].getNumberofTotalNodes() <= MAX_NODES){
			evaluation[i] = evaluate(population[i]);
			}else{
				evaluation[i] = Double.MAX_VALUE;
			}
		}
	}

	private double evaluate(MMGRDTreeNode node) {
		// String equation = node.evaluate();
		double totalSquaredError = 0.0d;
		for (int i = 0; i < dataset.getNumberOfRows(); i++) {

			double[] values = dataset.getDatasetValueSet(i);
			String[] valueNames = dataset.getVariableNames();

			Double result = null;

			result = node.evaluate(values,valueNames);

			totalSquaredError += Math.sqrt((values[values.length - 1] - result)
					* (values[values.length - 1] - result));
		}
		
		return totalSquaredError / dataset.getNumberOfRows();
	}

	private void mutatePopulation() {
		for (int i = 0; i < population.length; i++) {
			if (Math.random() < MUTATION_RATE) {
				mutate(population[i]);
			}
		}
	}
	
	public boolean isFoundSolution() {
		return foundSolution;
	}

	private void mutate(MMGRDTreeNode root) {
		MMGRDTreeNode randomNode = getRandomNode(root);
		if (randomNode.getParent() != null) {
			MMGRDTreeNode parent = randomNode.getParent();
			randomNode.getParent().removeChild(randomNode);
			randomNode = initializeIndividualGrow(0, (int)(Math.random()*MAX_NODE_INI_DEPTH)+1);
			parent.addChild(randomNode);
		}
	}
	
	private void simplifyNodes(){
		for (int i = 0; i<population.length;i++){
			if (Math.random()< SIMPLIFICATION_RATE){
	//			System.out.println("Simpl "+i+": "+ population[i]);
				MMGRDTreeNode oldNode =  population[i];
				MMGRDTreeNode simplifiedNode = oldNode.simplify(util);
				double oldScore=0,newScore=0;
	//			if (!oldNode.equals(simplifiedNode)){
	//				oldScore = evaluate(oldNode);
	//				newScore = evaluate(simplifiedNode);
	//				if (oldScore!= newScore){
	//					System.err.println("WTF "+ "Old: "+oldScore+", new: "+ newScore );
	//				}
	//			}
	//			System.out.println("Old: "+oldScore+", new: "+ newScore );
				while (simplifiedNode==null || simplifiedNode.toString().contains("Indet") || simplifiedNode.toString().contains("I") || simplifiedNode.toString().contains("\\") ){
	 				simplifiedNode = initializeIndividualGrow(0, (int)(Math.random()*MAX_NODE_INI_DEPTH)+1).simplify(util);
	
				}
				population[i]=simplifiedNode;
			}
		}
	}

	private MMGRDTreeNode[] getNewPopulation() {
		MMGRDTreeNode[] newPopulation = new MMGRDTreeNode[population.length];
		int eliteLimit = (int) (0.1 * population.length);
		int tournamentLimit = (int) (0.5 * population.length);
		int randomLimit = (int) (0.75 * population.length);
		int newNodeLimit = ((int) (population.length))-2;

		MMGRDTreeNode[] elite = getFittest(eliteLimit);
		for (int i = 0; i < eliteLimit; i++) {
			newPopulation[i] = elite[i].clone(null);
		}

		for (int i = eliteLimit; i < tournamentLimit; i += 2) {
			MMGRDTreeNode[] selectedParents = tournamentSelection();
			selectedParents = crossover(selectedParents[0], selectedParents[1]);
			newPopulation[i] = selectedParents[0];
			newPopulation[i + 1] = selectedParents[1];
		}

		for (int i = tournamentLimit; i < randomLimit; i += 2) {
			int parent1 = (int) (Math.random() * (population.length - 1));
			int parent2 = (int) (Math.random() * (population.length - 1));
			MMGRDTreeNode[] result = crossover(population[parent1],
					population[parent2]);
			newPopulation[i] = result[0];
			newPopulation[i + 1] = result[1];

		}
		for (int i = randomLimit; i < newNodeLimit; i++) {
			if (Math.random()<0.5){
				newPopulation[i] = initializeIndividualGrow(0,(int)(Math.random()*MAX_NODE_INI_DEPTH)+1);
			}else{
				newPopulation[i] = initializeIndividualFull((int)(Math.random()*MAX_NODE_INI_DEPTH)+1);
			}
		}
		MMGRDTreeNode bestNode = getFittest(1)[0];
		double fitness = evaluate(bestNode);
		newPopulation[population.length-2] = bestNode.clone(null).addConstantTerm(fitness);
		newPopulation[population.length-1] = bestNode.clone(null).addConstantTerm(-fitness);
		
		return newPopulation;
	}

	private MMGRDTreeNode[] crossover(MMGRDTreeNode parent1,
			MMGRDTreeNode parent2) {
		// Get Nodes that will be swapped
		parent1 = parent1.clone(null);
		parent2 = parent2.clone(null);
		MMGRDTreeNode parent1Node = getRandomNode(parent1);
		MMGRDTreeNode parent2Node = getRandomNode(parent2);
		// Swap nodes without loosing references
		MMGRDTreeNode auxParent1 = parent1Node.getParent();
		MMGRDTreeNode auxParent2 = parent2Node.getParent();
		if (auxParent1 != null) {
			auxParent1.removeChild(parent1Node);
			auxParent1.addChild(parent2Node);
		} else {
			parent1 = parent2Node;
		}
		if (auxParent2 != null) {
			auxParent2.removeChild(parent2Node);
			auxParent2.addChild(parent1Node);
		} else {
			parent2 = parent2Node;
		}

		return new MMGRDTreeNode[] { parent1, parent2 };
	}

	private MMGRDTreeNode getRandomNode(MMGRDTreeNode node) {
		List<MMGRDTreeNode> nodes = node.getAllNodes();
		return nodes.get((int) (Math.random() * (nodes.size() - 1)));
		// return node;
	}

	private MMGRDTreeNode[] tournamentSelection() {
		int[] selectedIndividuals = new int[TOURNAMENT_SELECTION_SIZE];
		double[] selectedIndividualsFitness = new double[TOURNAMENT_SELECTION_SIZE];
		for (int i = 0; i < TOURNAMENT_SELECTION_SIZE; i++) {
			// We allow an individual to be selected twice
			// TODO test removing individual duplicity;
			selectedIndividuals[i] = (int) (Math.random() * (population.length - 1));
			selectedIndividualsFitness[i] = evaluation[selectedIndividuals[i]];
		}
		int minPos1 = -1, minPos2 = -1;
		double minFit1 = Double.MAX_VALUE, minFit2 = Double.MAX_VALUE;
		for (int i = 0; i < TOURNAMENT_SELECTION_SIZE; i++) {
			if (minFit1 == Double.MAX_VALUE) {
				minPos1 = i;
				minFit1 = selectedIndividualsFitness[i];
			} else if (minFit2 == Double.MAX_VALUE) {
				minPos2 = i;
				minFit2 = selectedIndividualsFitness[i];
			} else if ((selectedIndividualsFitness[i] < minFit1 || selectedIndividualsFitness[i] < minFit2)) {
				if (minFit1 >= minFit2) {
					minPos2 = i;
					minFit2 = selectedIndividualsFitness[i];
				} else {
					minPos1 = i;
					minFit1 = selectedIndividualsFitness[i];
				}
			}
		}
		if (minPos1==-1 ||minPos2 ==-1){
			return tournamentSelection();
		}
		return new MMGRDTreeNode[] { population[minPos1].clone(null),
				population[minPos2].clone(null) };

	}

	// TODO implement
	private MMGRDTreeNode[] getFittest(int position) {
		List<Double> fittestFitness = new ArrayList<Double>();
		List<Integer> fittestPositions = new ArrayList<Integer>();
		for (int i = 0; i < population.length; i++) {
			if (!Double.isInfinite(evaluation[i]) && !Double.isNaN(evaluation[i])){
				if (fittestPositions.size() < position) {
					fittestPositions.add(new Integer(i));
					fittestFitness.add(new Double(evaluation[i]));
				} else {
					checkIfIsInFittest(fittestFitness, fittestPositions, i);
				}
			}
		}
		MMGRDTreeNode[] result = new MMGRDTreeNode[position];
		for (int i = 0; i < fittestPositions.size(); i++) {
			result[i] = population[fittestPositions.get(i).intValue()];
		}
		return result;
	}

	private void checkIfIsInFittest(List<Double> fittestFitness,
			List<Integer> fittestPositions, int position) {
		double fitnessToReplace = Double.MAX_VALUE;
		int fitnessToReplacePos = -1;
		for (int i = 0; i < fittestFitness.size(); i++) {
			double currentFitness = fittestFitness.get(i).doubleValue();
			if (evaluation[position] < currentFitness
					&& fitnessToReplace > currentFitness) {
				fitnessToReplace = fittestFitness.get(i).doubleValue();
				fitnessToReplacePos = i;
			}
		}
		if (fitnessToReplacePos != -1) {
			fittestFitness.remove(fitnessToReplacePos);
			fittestPositions.remove((int) fitnessToReplacePos);
			fittestFitness.add(new Double(evaluation[position]));
			fittestPositions.add(new Integer(position));
		}

	}

	private MMGRDTreeNode initializeIndividualGrow(int currentDepth,
			int maxDepth) {
		MMGRDTreeNode root;
		double terminalChance = 0.35;
		if (currentDepth == maxDepth || Math.random() < terminalChance) {
			root = terminals
					.get((int) (Math.random() * (terminals.size() - 1))).clone(
							null);
		} else {
			// Is an operator
			root = operators
					.get((int) (Math.random() * (operators.size() - 1))).clone(
							null);
			for (int i = 0; i < root.getNumberOfOperands(); i++) {
				root.addChild(initializeIndividualGrow(currentDepth + 1,
						maxDepth));
			}
		}

		return root;
	}

	private MMGRDTreeNode initializeIndividualFull(int depth) {
		MMGRDTreeNode root;
		if (depth == 0) {
			root = terminals
					.get((int) (Math.random() * (terminals.size() - 1))).clone(
							null);
		} else {
			root = operators
					.get((int) (Math.random() * (operators.size() - 1))).clone(
							null);
			for (int i = 0; i < root.getNumberOfOperands(); i++) {
				root.addChild(initializeIndividualFull(depth - 1));
			}
		}
		return root;
	}
}
