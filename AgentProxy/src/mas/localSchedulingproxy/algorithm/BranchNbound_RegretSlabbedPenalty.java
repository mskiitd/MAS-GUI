package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import mas.jobproxy.Batch;

public class BranchNbound_RegretSlabbedPenalty implements ScheduleSequenceIFace {
	/** Minimizes penalty of a given sequence of jobs by using branch and bound algorithm 
	 *  To use it, create a node with state equal to ArrayList of a sequence of jobs
	 *  The solution for the given job sequence will be calculated and stored in the ArrayList "best"
	 *  Minimum penalty will be given by the variables "lowBound".
	 *  @author Anand Prajapati 
	 *  @Email  anandprajapati389@gmail.com
	 */ 

	/**
	 * depth of tree starts from zero
	 * Level of tree with depth of 0 is ignored
	 */
	ArrayList<Batch> best = new ArrayList<Batch>();		// best solution stored in this ArrayList when algo runs
	Double lowBound = Double.MAX_VALUE;			// Upper bound for solutions
	public Node rootNode;
	public Batch fixedBatch;

	public BranchNbound_RegretSlabbedPenalty(ArrayList<Batch> s) {
		fixedBatch = s.get(0);
		s.remove(0);
		/**
		 * Store the position of all the jobs in the initial sequence so that 
		 * we can compare regret factor of jobs after sequencing
		 */
		for(int i= 0; i < s.size() ;i++) {
			s.get(i).setPosition(i);
		}
		this.rootNode = new Node(s);
	}

	public class Node {
		ArrayList<Batch> sequence;		//sequence of batch
		Node[] child;				//  child nodes at any point
		Node parent;				// parent node
		int depth;					// depth of node in tree
		int rank;					// to differentiate between different children at same level 
		double penalty;				// penalty of node

		public Node(ArrayList<Batch> s) {
			depth = 0;		
			rank = -1;
			penalty = 0;
			this.sequence = new ArrayList<Batch>(s);
			int n = sequence.size();
			// i'th child will be equal to that of its parent and one job kept at last
			child = new Node [n];		

			for( int i=0 ; i < n ; i++) {
				child[i]= null;
			}
		}

		/**
		 * Giving child of any node at a position index starting zero from left in the solution tree
		 * @param index
		 * @return child at the index
		 */
		Node getChildNode(int index) {
			if (child[index] == null)
				child[index] = new Node(this.sequence);

			child[index].sequence = new ArrayList<Batch>(this.sequence);
			// rank is equal to node's position from left in the subtree
			child[index].rank = index;				
			child[index].depth = this.depth + 1;
			child[index].parent = this;

			// job at rank's position is kept fixed (with suitable offset in this case)
			Collections.swap(child[index].sequence,				
					child[index].depth-1,
					child[index].depth + index -1);

			return child[index];
		}

		/**Penalty of a child is calculated by adding its individual penalty to parent's penalty
		 * @return Penalty of this node
		 * 
		 */
		public double getValue() {
			int n = this.sequence.size();
			long makeSpan = 0;
			long startingTime = 0;
			/**
			 * Calculate penalty of job by putting all jobs up to index 'depth - 1'
			 * before it fixed and based on lateness and regret multiplier
			 * find its penalty
			 */

			for (int i = this.depth - 1; i < n ; i++) {
				makeSpan += this.sequence.get(i).getCurrentOperationProcessingTime();
			}

			/**
			 * update start time of all jobs.
			 * update regret of all jobs
			 */

			/**
			 * Penalty will be calculated by using a multiplier which in turn depends on regret factor.
			 * First update start and processing times of the job and then the regret factor.
			 */
			startingTime = makeSpan - this.sequence.get(this.depth - 1).getCurrentOperationProcessingTime() +
					System.currentTimeMillis();

			int elements = this.depth;
			this.sequence.get(elements - 1).setCurrentOperationProcessingTime(startingTime);

			for (int k = elements - 2 ; k >= 0; k--) {
				this.sequence.get(k).setCurrentOperationStartTime(this.sequence.get(k+1).getCurrentOperationStartTime()	+ 
						this.sequence.get(k+1).getCurrentOperationProcessingTime());
			}

			updateRegret(this);
			//-------------------------------------------------------

			Batch j = this.sequence.get(depth-1);
			double latenessPenalty = (makeSpan - j.getCurrentOperationDueDate())*j.getCPN()*j.getRegretMultiplier();

			if(latenessPenalty < 0) {
				//				System.out.println("negative " + lateness + "with "+parent.penalty);
				latenessPenalty = 0;
			}
			if( this.parent != null)
				latenessPenalty += this.parent.penalty;

			this.penalty = latenessPenalty;
			return latenessPenalty;
		}
	}

	/**
	 * Don't forget to add the first job which is fixed throughout
	 * in the final sequence of jobs
	 * Solves and returns the optimal sequence
	 */

	public ArrayList<Batch> solve() {
		if(this.rootNode.sequence.size() == 0) {
			this.best.add(fixedBatch);
			return this.best;
		}
		else if(this.rootNode.sequence.size() == 1) {
			this.best.add(fixedBatch);
			this.best.add(this.rootNode.sequence.get(0));
			return this.best;
		}
		RecursiveSolver(this.rootNode);
		/** reverse the sequence as the correct schedule in the tree goes from
		 * bottom to top
		 */
		Collections.reverse(this.best);
		this.best.add(0,fixedBatch);
		return this.best;
	}

	private void RecursiveSolver(Node node) {
		int size = node.sequence.size();
		// Base case when we hit the leaf
		if( node.depth == size) {
			// check if this node is the best node
			double val = node.penalty;
			//			System.out.println("Hit " + val + "d " + node.depth);
			if(val < this.lowBound) {
				best = node.sequence;
				lowBound = node.getValue();
			}
			return ;
		}

		// if penalty of node exceeds lower bound, don't expand this node
		if(node.penalty > lowBound)
			return;

		// end is the number of children that any particular node will have in the tree
		int end = node.sequence.size() - node.depth ;
		//		System.out.println("Childs are " + end + "wt depth " + node.depth);
		for ( int i = 0; i < end ; i++) {
			Node child = node.getChildNode(i);
			child.getValue();

			RecursiveSolver(child);
		}
	}

	public void updateRegret(Node node) {
		int elements = node.depth;
		double lateness;					
		for ( int i = 0; i < elements ; i++){
			lateness =	node.sequence.get(i).getCurrentOperationStartTime() +
					node.sequence.get(i).getCurrentOperationProcessingTime() -
					node.sequence.get(i).getCurrentOperationDueDate();

			if(lateness < 0)
				lateness = 0;

			node.sequence.get(i).setRegret(lateness/node.sequence.get(i).getSlack());
		}
	}
}