package mmgrd;

import java.util.List;

import mmgrd.operations.MMGRDBinary;
import mmgrd.operations.MMGRDUnary;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.NumberNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.eval.DoubleEvaluator;

public class MMGRDTreeNodeFunction extends MMGRDTreeNode {
	private MMGRDFunction function;
	
	public MMGRDTreeNodeFunction(MMGRDTreeNode parent,MMGRDFunction function){
		super();
		this.function = function;
		this.parent = parent;
	}
	@Override
	public boolean isTerminal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double evaluate(double[] variableValues,String[] variableNames) {
		return function.evaluate(childs, variableValues, variableNames);
	}

	@Override
	public int getNumberOfOperands() {
		// TODO Auto-generated method stub
		return function.getNumberOfOperands();
	}

	public MMGRDTreeNode clone(MMGRDTreeNode parent) {
		MMGRDTreeNode treeNode = new MMGRDTreeNodeFunction(null,function);
		for (int i =0; i<childs.size();i++){
			treeNode.addChild(childs.get(i).clone(treeNode));
		}
		return treeNode;
	}
	@Override
	public String toString(){
		if (this.getNumberOfOperands()==2){
			return "("+childs.get(0).toString()+function.operator+childs.get(1).toString()+")";
		}else{
			return function.operator+"("+childs.get(0).toString()+")";
		}
	}
	
	@Override
	public MMGRDTreeNode simplify(EvalUtilities util){
		
		String output;
		try {
			IExpr expr = getIExpr(util, true);
			if (expr!=null){
				StringBufferWriter buf = new StringBufferWriter();
				IExpr result2 = util.evaluate(expr);
				OutputFormFactory.get().convert(buf, result2);
				buf.close();
				output = buf.toString();
				Parser p = new Parser();
				ASTNode obj = p.parse(output);
				return (convertASTNode(obj));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return null;
		
	}
	
	private MMGRDTreeNode convertASTNode(ASTNode node){
		MMGRDTreeNode newNode = null;
		if (node instanceof NumberNode || node.getClass().equals(SymbolNode.class)){
			newNode = new MMGRDTreeNodeTerminal(parent, node.toString());
		}else if (node.getClass().equals(FunctionNode.class)){
			//Get what function is it
			FunctionNode fNode = (FunctionNode) node;
			if (fNode.size()==2){
				MMGRDFunction fun = new MMGRDUnary("");
				fun.setOperatorFromASTNode(fNode.get(0).getString());
				newNode = new MMGRDTreeNodeFunction(parent, fun );
				MMGRDTreeNode child1 = convertASTNode(fNode.get(1));
				newNode.addChild(child1);
			}else if (fNode.size()==3) {
				MMGRDFunction fun = new MMGRDBinary("");
				fun.setOperatorFromASTNode(fNode.get(0).getString());
				newNode = new MMGRDTreeNodeFunction(parent, fun);
				MMGRDTreeNode child1 = convertASTNode(fNode.get(1));
				MMGRDTreeNode child2 = convertASTNode(fNode.get(2));
				newNode.addChild(child1);
				newNode.addChild(child2);
			}
		}
		
		return newNode;
	}
	
	private IExpr getIExpr(EvalUtilities util, boolean numeric){
		ASTNode node = null;
		 try{
			 String input = this.toString();
			 try {
					Parser parser = new Parser(true);
					node = parser.parse(input);
				} catch (SyntaxError se) {
					Parser parser = new Parser(false);
					node = parser.parse(input);
				}
		      
		      IExpr inExpr = AST2Expr.CONST.convert(node);
		      if (numeric){
		    	  inExpr = F.N(inExpr);
		      }
		      return inExpr;
		 }
		catch (Exception e){
			
		}
			 return null;
	}
	@Override
	public MMGRDTreeNode addConstantTerm(double constant) {
		MMGRDTreeNode node = new MMGRDTreeNodeFunction(this.parent, new MMGRDBinary("+"));
		node.addChild(new MMGRDTreeNodeTerminal(node, constant+""));
		node.addChild(this);
		return node;
	}

	
	@Override
	public void checkAndApplyConstraints(List<MMGRDTreeNodeTerminal> terminals,
			List<MMGRDTreeNodeFunction> operators, int numTrigonFound, int numPowerFound,int childPosition) {
		if (function.getOperator().equals("sin")||function.getOperator().equals("cos") || function.getOperator().equals("tan")){
			numTrigonFound++;
		}
		if (function.getOperator().equals("^")){
			numPowerFound++;
		}
		if (numTrigonFound>1 || numPowerFound>1){
			int terminalPos = (int)(Math.random()*(terminals.size()-1));
			MMGRDTreeNode newTerminal = terminals.get(terminalPos).clone(null);
			newTerminal.parent = this.parent;
			MMGRDTreeNode linkToParent = this.parent; 
			this.parent.removeChild(this);
			//Required because this parent is null now
			linkToParent.addChild(newTerminal,childPosition);
			
		}else{
			for (int i=0; i<this.childs.size();i++){
				childs.get(i).checkAndApplyConstraints(terminals,operators,numTrigonFound,numPowerFound,i);
			}
			
		}
	}
	

}
