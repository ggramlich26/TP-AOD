/**
 * The Class Node represents a node in the call tree.
 * This means that a node represents a solution to a certain subproblem. The node objects will be reused and therefor represent a more general subproblem in each iteration step.
 * Each node has to have a cost, which stores the minimal cost for the subproblem the node is used for.
 * It also has a variable patch of type String, which stores the optimal patch for the nodes subproblem.
 *
 * @ author Georg Gramlich
 */

public class Node{
	private int cost;
	private PatchList patch;
	private int inLine;
	private int outLine;

	public Node(){
		cost = Integer.MAX_VALUE;
		patch = null;
		inLine = outLine = 0;
	}

	/**
	 * This method returns the minimal cost for the subproblem the node is currently referring to.
	 * @return minimal cost for the subproblem the node is referring to.
	 */
	public int getCost(){
		return cost;
	}

	/**
	 * This function is used to set the cost of a node
	 * @param cost new cost
	 * @return nothing
	 */
	public void setCost(int cost){
		this.cost = cost;
	}
	
//	/** this function is used to append a line to the optimal patch for the subproblem the node is referring to.
//	 * @param line The line to be added to the path without new line
//	 * @return nothing
//	 */
//	public void appendPatch(String line){
//		if(line == null){
//			return;
//		}
//		patch += line + "\n";
//	}

	/**
	 * this function is used to set the String representation of the optimal patch for the subproblem the node is referring to.
	 * @param patch The String representation of the optimal patch
	 * @return nothing
	 */
	public void setPatchList(PatchList patch){
		this.patch = patch;
	}

	/**
	 * This function is used to get a String representation of the optimal patch for the subproblem the node is referring to.
	 * @return String representation of the path
	 */
	public PatchList getPatchList(){
		return patch;
	}

	public int getInLine(){
		return this.inLine;
	}

	public void setInLine(int line){
		this.inLine = line;
	}

	public int getOutLine(){
		return this.outLine;
	}

	public void setOutLine(int line){
		this.outLine = line;
	}
}
