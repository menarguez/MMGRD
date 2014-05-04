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
	
	public String getOperator(){
		return ""+operator;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MMGRDFunction other = (MMGRDFunction) obj;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		return true;
	}
	
}
