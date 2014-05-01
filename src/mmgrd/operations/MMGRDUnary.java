package mmgrd.operations;

import java.util.List;

import mmgrd.MMGRDFunction;
import mmgrd.MMGRDTreeNode;

public class MMGRDUnary extends MMGRDFunction {

	private String OP_SIN= "sin" ;
	private String OP_COS = "cos" ;
	private String OP_TAN = "tan" ;
	public MMGRDUnary(String operator) {
		super(operator);
	}

	@Override
	public double evaluate(List<MMGRDTreeNode> childs, double[] variableValues,
			String[] variableNames) {
		double result = Double.POSITIVE_INFINITY;;
		try {

			if (operator.equals("sin")) {

				result = Math.sin(childs.get(0).evaluate(variableValues, variableNames));
			} else if (operator.equals("cos")) {

				result = Math.cos(childs.get(0).evaluate(variableValues, variableNames));
			} else if (operator.equals("tan")) {

				result = Math.tan(childs.get(0).evaluate(variableValues, variableNames));
			}
		} catch (Exception e) {
			return Double.POSITIVE_INFINITY;
		}
		return result;
	}

	@Override
	public int getNumberOfOperands() {
		return 1;
	}

	public void setOperatorFromASTNode(String op) {
		if (op.equals(OP_SIN)){
			operator= "sin";
		}else if (op.equals(OP_COS)){
			operator= "cos";
		}else if (op.equals(OP_TAN)){
			operator= "tan";
		}
	}
}
