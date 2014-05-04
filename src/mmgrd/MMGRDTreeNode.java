package mmgrd;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.matheclipse.core.eval.EvalUtilities;

public abstract class MMGRDTreeNode implements Cloneable {
	
	protected List<MMGRDTreeNode> childs;
	
	protected MMGRDTreeNode parent;
	
	protected UUID id;
	
	protected int numberOfTotalNodes = 1;
	
	protected MMGRDTreeNode(){
		this.id = UUID.randomUUID();
		this.childs = new ArrayList<MMGRDTreeNode>();
	}
	public abstract boolean isTerminal(); 
	
	public abstract double evaluate(double[] variableValues,String[] variableNames);
	
	public abstract MMGRDTreeNode addConstantTerm(double constant);
	
	public abstract int getNumberOfOperands();
	
	public abstract MMGRDTreeNode clone(MMGRDTreeNode parent);
	
	/**
	 * This is used to keep a O(1) calculation for the number of children at each node
	 */
	protected void incrementNumberOfTotalNodes(int difference){
		this.numberOfTotalNodes += difference;
		if (parent!= null){
			parent.incrementNumberOfTotalNodes(difference);
		}
	}
	
	public void addChild(MMGRDTreeNode child){
		if (childs.size() < getNumberOfOperands()){
			child.parent = this;
			childs.add(child);
			incrementNumberOfTotalNodes(child.numberOfTotalNodes);
		}else{
			System.err.println("Error: This Node cannot add more childs. Aborting...");
        	System.exit(-3);
		}
	}
	
	public void addChild(MMGRDTreeNode child, int childPosition) {
		if (childs.size() < getNumberOfOperands()){
			child.parent = this;
			childs.add(childPosition, child);
			incrementNumberOfTotalNodes(child.numberOfTotalNodes);
		}else{
			System.err.println("Error: This Node cannot add more childs. Aborting...");
        	System.exit(-3);
		}
		
	}
	
	public void removeChild(MMGRDTreeNode child){
		if (childs.contains(child)){
			incrementNumberOfTotalNodes(-child.numberOfTotalNodes);
			childs.remove(child);
//			child.parent = null;
		}else{
			System.err.println("Error: Child to remove not found. Aborting ...");
        	System.exit(-3);
		}
	}
	public MMGRDTreeNode getChild(int position){
		if (0<=position && position < childs.size()){
			return childs.get(position);
		}else{
			System.err.println("Error: Child to retrieve out of range. Aborting ...");
        	System.exit(-3);
        	return null;
		}
	}
	public int getNumberofTotalNodes(){
		return numberOfTotalNodes;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MMGRDTreeNode other = (MMGRDTreeNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public abstract MMGRDTreeNode simplify(EvalUtilities util);
	
	public MMGRDTreeNode getParent(){
		return parent;
	}
	
	public abstract String toString();
	
	public List<MMGRDTreeNode> getAllNodes(){
		List<MMGRDTreeNode> list = new ArrayList<MMGRDTreeNode>();
		for(MMGRDTreeNode node : childs){
			list.addAll(node.getAllNodes());
		}
		list.add(this);
		return list;
	}
	/**
	 * This is used to check that we do not have elements in the form of sin(cos(sin...))) or 2^2^...
	 * @param terminals
	 * @param operators
	 */
	public abstract void checkAndApplyConstraints(List<MMGRDTreeNodeTerminal> terminals, List<MMGRDTreeNodeFunction> operators, int numTrigonFound, int numPowerFound,int childPosition) ;

	public void setParent(MMGRDTreeNode parentNode){
		this.parent = parentNode;
	}
}
