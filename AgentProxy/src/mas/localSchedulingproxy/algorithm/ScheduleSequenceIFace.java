package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;

import mas.jobproxy.Batch;

public interface ScheduleSequenceIFace {

	/**
	 * @return optimal sequence of batches which has the minimum overall penalty or minimum regret 
	 */
	public ArrayList<Batch> solve();
}
