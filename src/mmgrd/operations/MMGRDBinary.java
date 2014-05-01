package mmgrd.operations;

import java.util.List;

import mmgrd.MMGRDFunction;
import mmgrd.MMGRDTreeNode;

public class MMGRDBinary extends MMGRDFunction {
	
	private String OP_ADDITION = "Plus" ;
//	private String OP_SUBSTRACTION = "Plus" ;
	private String OP_MULTIPLICATION = "Times" ;
	private String OP_POWER = "Power" ;
//	private String OP_DIVISION = "Plus" ;
	public MMGRDBinary(String operator){
		super(operator);
	}
	@Override
	public double evaluate(List<MMGRDTreeNode> childs, double[] variableValues, String[] variableNames) {
		try{
			double result = 0.0d;
			switch (operator.charAt(0)){
				case '+':
					result =  childs.get(0).evaluate(variableValues,variableNames) + childs.get(1).evaluate(variableValues,variableNames);
					break;
				case '-':
					result =  childs.get(0).evaluate(variableValues,variableNames) - childs.get(1).evaluate(variableValues,variableNames);
					break;
				case '*':
					result =  childs.get(0).evaluate(variableValues,variableNames) * childs.get(1).evaluate(variableValues,variableNames);
					break;
				case '/':
					result =  childs.get(0).evaluate(variableValues,variableNames) / childs.get(1).evaluate(variableValues,variableNames);
					break;
				case '^':
					result =  Math.pow(childs.get(0).evaluate(variableValues,variableNames) , childs.get(1).evaluate(variableValues,variableNames));
					break;
			}
			if (Double.isNaN(result) || Double.isInfinite(result)){
				result = Double.POSITIVE_INFINITY;
			}
			return result;
		}catch (Exception e){
			return Double.POSITIVE_INFINITY;
		}
	}

	@Override
	public int getNumberOfOperands() {
		return 2;
	}
	@Override
	public void setOperatorFromASTNode(String op) {
		if (op.equals(OP_ADDITION)){
			operator= "+";
		}else if (op.equals(OP_MULTIPLICATION)){
			operator= "*";
		}else if (op.equals(OP_POWER)){
			operator= "^";
		}else{
			System.err.println("Operator not included: " + op);
			System.exit(-1);
		}
	}
}
