package mas.globalSchedulingproxy.goal;

import mas.jobproxy.Batch;
import bdi4jade.goal.Goal;

public class QueryJobGoal implements Goal {

	private static final long serialVersionUID = 1L;
	private Batch queryBatch=null;
	
	public QueryJobGoal(Batch b){
		this.queryBatch=b;
	}
	
	public Batch getBatchToQuery(){
		return queryBatch;
	}
	

}
