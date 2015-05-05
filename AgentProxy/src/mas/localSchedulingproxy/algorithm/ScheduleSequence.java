package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;
import mas.jobproxy.Batch;

/**
 * @author Anand Prajapati
 * 
 * Encapsulates the scheduling algorithm for local scheduling agent
 *
 */
public class ScheduleSequence {
	
	ScheduleSequenceIFace scheduler;
	
	public ScheduleSequence(ArrayList<Batch> s) {
		scheduler = new BranchNbound_RegretSlabbedPenalty(s);
	}
	
	/**
	 * @return the best sequence of batches.
	 * The notion of best here depends upon the algorithm implemented
	 */
	public ArrayList<Batch> getSolution(){
		return scheduler.solve();
	}
}
