package mmgrd;

import java.util.List;

public abstract class MMGRDFunction {
	
	protected String operator;

	protected MMGRDFunction(String operator){
		this.operator = operator;
	}
	public abstract double evaluate(List<MMGRDTreeNode> childs, double[] variableValues,String[] variableNames);
	
	public abstract int getNumberOfOperands();
	
	public abstract void setOperatorFromASTNode(String op);
}
