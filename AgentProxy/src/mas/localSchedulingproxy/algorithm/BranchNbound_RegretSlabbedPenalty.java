package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.jobproxy.Batch;

/** Minimizes penalty of a given sequence of jobs by using branch and bound algorithm 
 *  To use it, create a node with state equal to ArrayList of a sequence of jobs
 *  The solution for the given job sequence will be calculated and stored in the ArrayList "best"
 *  Minimum penalty will be given by the variables "lowBound".
 *  
 *  This takes into account the regret of a particular batch also. Each batch has a regret multiplier which 
 *  depends upon the regret of the batch. This multiplier is used while calculating lateness penalty of the batch.
 *  Penalty of the batch is now also multiplied with this dynamic regret multiplier.
 *  Thus job having higher regret shall have higher penalty for being late.
 *  
 *  @author Anand Prajapati 
 */

public class BranchNbound_RegretSlabbedPenalty implements ScheduleSequenceIFace {

	// best solution stored in this ArrayList when algorithm runs
	ArrayList<Batch> best = new ArrayList<Batch>();		
	// lower bound for best solution
	private Double lowBound = Double.MAX_VALUE;			
	public Node rootNode;
	public Batch fixedBatch;
	private ArrayList<Batch> copiedList;
	private Logger log;

	/**
	 * Store the position of all the jobs in the initial sequence so that 
	 * we can compare regret factor of jobs after sequencing
	 */
	public BranchNbound_RegretSlabbedPenalty(ArrayList<Batch> s) {
		this.log = LogManager.getLogger();

		this.copiedList = new ArrayList<Batch>(s);
		fixedBatch = copiedList.get(0);
		copiedList.remove(0);

		log.info("\n\n--------------------Scheduling beginning---------------------\n\n");
		log.info("Queue is  : " + s );
		log.info("Fixed Batch : " + fixedBatch);

		for(int i= 0; i < copiedList.size() ;i++) {
			copiedList.get(i).setPosition(i);
		}
		this.rootNode = new Node(copiedList);
	}

	public class Node {
		//sequence of batch
		ArrayList<Batch> sequence;	
		//  child nodes at any point
		Node[] child;				
		// parent node
		private Node parent;	
		// depth of node in tree
		int depth;				
		// penalty of node
		double penalty;				

		public Node(ArrayList<Batch> seq) {
			depth = 0;		
			penalty = 0;
			this.sequence = new ArrayList<Batch>(seq);
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

			child[index].sequence = new ArrayList<Batch>();
			// rank is equal to node's position from left in the subtree
			child[index].depth = this.depth + 1;
			child[index].parent = this;

			for(int i = 0 ; i < (this.sequence.size() - 1); i++) {
				child[index].sequence.add(this.sequence.get(i));
			}
			// swap first element by last one because in this node
			// we keep the last batch last in the sequence
			Collections.swap(child[index].sequence,	index, child[index].sequence.size() - 1 );

			return child[index];
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}
		/**
		 * Penalty of a node is calculated by adding its individual penalty to parent's penalty
		 * 
		 * Calculate penalty of job by putting all jobs up to index 'depth - 1'
		 * before it fixed and based on lateness and regret multiplier
		 * find its penalty.
		 * 
		 * update start time of all jobs.
		 * update regret of all jobs.
		 * 
		 * Penalty will be calculated by using a multiplier which in turn depends on regret factor.
		 * First update start and processing times of the job and then the regret factor.
		 * 
		 * @return Penalty of this node
		 * 
		 */
		public double updatePenaltyValue() {
			int n = this.sequence.size();
			long relativeMakeSpan = 0;
			long startingTime = 0;

			for (int i = 0; i < n ; i++) {
				relativeMakeSpan += this.sequence.get(i).getCurrentOperationProcessingTime();
			}

			startingTime = relativeMakeSpan - this.sequence.get(n-1).getCurrentOperationProcessingTime() +
					System.currentTimeMillis();

			this.sequence.get(n - 1).setCurrentOperationStartTime(startingTime);

			for (int k = n - 2 ; k >= 0; k--) {
				this.sequence.get(k).setCurrentOperationStartTime(this.sequence.get(k+1).getCurrentOperationStartTime()	+ 
						this.sequence.get(k+1).getCurrentOperationProcessingTime());
			}
			updateRegret(this);
			//-------------------------------------------------------
			Batch lastBatchInThisNode = this.sequence.get(n-1);
			double latenessPenalty =
					( (System.currentTimeMillis() + relativeMakeSpan) - lastBatchInThisNode.getCurrentOperationDueDate() )/1000
					*lastBatchInThisNode.getCPN() * lastBatchInThisNode.getPenaltyRate() * lastBatchInThisNode.getRegretMultiplier();

			if(latenessPenalty < 0) {
				latenessPenalty = 0;
			}
			//			log.info("last node : " + this.sequence.get(n-1) + "penalty : " + latenessPenalty + " size : " + n);
			if( this.parent != null) {
				latenessPenalty += this.parent.penalty;
			}

			this.penalty = latenessPenalty;
			return latenessPenalty;
		}
	}

	/**
	 * Don't forget to add the first job which is fixed throughout
	 * in the final sequence of jobs.
	 * Solves and returns the optimal sequence.
	 */
	@Override
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

		int end = rootNode.sequence.size();
		for ( int i = 0; i < end ; i++) {
			Node tempRootNode = new Node(rootNode.sequence);
			Collections.swap(tempRootNode.sequence, i, tempRootNode.sequence.size() - 1);
			//			log.info(tempRootNode.sequence);
			tempRootNode.updatePenaltyValue();
			RecursiveSolver(tempRootNode);
		}
		this.best.add(0,fixedBatch);
		log.info("-----------------------------Scheduling finished-------------------------------------");
		log.info("-----------------lowest Penalty is : " + this.getLowBound() + "---------------------------");
		return this.best;
	}

	private void RecursiveSolver(Node node) {
		int size = node.sequence.size();
		// Base case when we hit the leaf
		if( size == 1) {
			// check if this node is the best node
			double val = node.penalty;
			if(val < this.getLowBound()) {
				// get the best solution by traversing all the way up to the root
				best.clear();
				best.add(node.sequence.get(node.sequence.size() -1 ) );
				Node parent = node.getParent();

				while(parent != null) {
					best.add(parent.sequence.get(parent.sequence.size() -1 ));
					parent = parent.getParent();
				}
				//				log.info("last : " + node.sequence);
				//				log.info("best :  " + best);
				setLowBound(val);
			}
			return ;
		}

		// if penalty of node exceeds lower bound, don't expand this node
		if(node.penalty > getLowBound()) {
			return;
		}

		// end is the number of children that any particular node will have in the tree
		int end = node.sequence.size() - 1;
		for ( int i = 0; i < end ; i++) {
			Node child = node.getChildNode(i);
			//			log.info(" child : " + child.sequence + " parent "+ child.getParent().sequence.get(child.getParent().sequence.size()-1));

			child.updatePenaltyValue();
			RecursiveSolver(child);
		}
	}

	/**
	 * @param node
	 * Update regret for sequence of batch within this node of the tree
	 */
	public void updateRegret(Node node) {
		int elements = node.sequence.size();
		double lateness;					
		for ( int i = 0; i < elements ; i++) {
			lateness =	node.sequence.get(i).getCurrentOperationStartTime() +
					node.sequence.get(i).getCurrentOperationProcessingTime() -
					node.sequence.get(i).getCurrentOperationDueDate();

			if(lateness < 0)
				lateness = 0;

			if(node.sequence.get(i).getSlack() != 0) {
				node.sequence.get(i).setRegret(lateness/node.sequence.get(i).getSlack());
			} else {
				node.sequence.get(i).setRegret(Double.MAX_VALUE/100);
			}
		}
	}

	/**
	 * @return lower bound on the penalty of sequence of batch
	 */
	public Double getLowBound() {
		return lowBound;
	}

	/**
	 * 
	 * @param lowBound
	 * Sets lower bound on the penalty of sequence of batch
	 */
	public void setLowBound(Double lowBound) {
		this.lowBound = lowBound;
	}
}