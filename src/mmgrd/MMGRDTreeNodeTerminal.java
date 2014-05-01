package mmgrd;


import mmgrd.operations.MMGRDBinary;

import org.matheclipse.core.eval.EvalUtilities;

public class MMGRDTreeNodeTerminal extends MMGRDTreeNode {
	private String value;

	public MMGRDTreeNodeTerminal(MMGRDTreeNode parent,  String value){
		super();
		try{
			if (value.contains("/")){
				String[] numDem = value.split("/");
				double num = new Double(numDem[0]);
				double den = new Double(numDem[1]);
				this.value = ""+ (num/den);
			}else{
				this.value = value;
			}
		}catch(Exception e){
			this.value = value;
		}
		this.parent = parent;
	}
	@Override
	public boolean isTerminal() {
		return true;
	}
	@Override
	public double evaluate(double[] variableValues,String[] variableNames) {
		try{
			if (value.contains("/")){
				String[] numDem = value.split("/");
				double num = new Double(numDem[0]);
				double den = new Double(numDem[1]);
				return num/den;
			}else{
				return new Double(value);
			}
		}catch(Exception e){
			try{
			return variableValues[getVariableIndex(value,variableNames)];
			}catch (Exception e2){

				System.err.println(value);
				return Double.NaN;
			}
		}
	}
	
	private int getVariableIndex(String variable, String[] variables){
		for (int i=0; i< variables.length; i++){
			if (variable.equals(variables[i])){
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getNumberOfOperands() {
		return 0;
	}

	@Override
	public MMGRDTreeNode clone(MMGRDTreeNode parent) {
		MMGRDTreeNode treeNode = new MMGRDTreeNodeTerminal(null,value);
		return treeNode;
	}
	@Override
	public String toString(){
		return value;
	}
	@Override
	public MMGRDTreeNode simplify(EvalUtilities util){
		//A terminal is always in its simplest expression
		return this;
	}
	
	@Override
	public MMGRDTreeNode addConstantTerm(double constant) {
		MMGRDTreeNode node = new MMGRDTreeNodeFunction(this.parent, new MMGRDBinary("+"));
		node.addChild(new MMGRDTreeNodeTerminal(node, constant+""));
		node.addChild(this);
		return node;
	}
}
