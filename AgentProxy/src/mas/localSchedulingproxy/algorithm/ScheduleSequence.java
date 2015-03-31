package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;
import mas.jobproxy.Batch;

public class ScheduleSequence {
	
	ScheduleSequenceIFace scheduler;
	
	public ScheduleSequence(ArrayList<Batch> s){
		scheduler = new BranchNbound_RegretSlabbedPenalty(s);
	}
	
	public ArrayList<Batch> getSolution(){
		return scheduler.solve();
	}
}
