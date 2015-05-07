package mas.globalSchedulingproxy.goal;

import mas.jobproxy.Batch;
import bdi4jade.goal.Goal;
/*
 * Query about batch
 */
public class QueryJobGoal implements Goal {

	private static final long serialVersionUID = 1L;
	private Batch queryBatch=null;
	private String queryType;
	
	public QueryJobGoal(Batch b, String requestType){
		this.queryBatch=b;
		this.queryType=requestType;
	}
	
	public Batch getBatchToQuery(){
		return queryBatch;
	}

	public String getQueryType() {
		return queryType;
	}
	

}
