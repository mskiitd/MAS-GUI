package mas.util;

import jade.core.AID;
import mas.job.job;

public class JobQueryObject {

	private job currentJob;
	private AID currentMachine;
	
	public JobQueryObject(job j) {
		this.setCurrentJob(j);
		currentMachine = new AID();
	}
	
	public AID getCurrentMachine() {
		return currentMachine;
	}
	
	public void setCurrentMachine(AID currentMachine) {
		this.currentMachine = currentMachine;
	}

	public job getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(job currentJob) {
		this.currentJob = currentJob;
	}
	
}
