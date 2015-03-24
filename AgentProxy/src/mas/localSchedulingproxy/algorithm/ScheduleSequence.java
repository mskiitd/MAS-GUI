package mas.localSchedulingproxy.algorithm;

import java.util.ArrayList;

import mas.job.job;

public class ScheduleSequence {
	
	ScheduleSequenceIFace scheduler;
	
	public ScheduleSequence(ArrayList<job> s){
		scheduler = new BranchNbound_RegretSlabbedPenalty(s);
	}
	
	public ArrayList<job> getSolution(){
		return scheduler.solve();
	}
}
