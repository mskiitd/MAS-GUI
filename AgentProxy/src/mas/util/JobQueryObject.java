package mas.util;

import jade.core.AID;
import jade.util.leap.Serializable;
import mas.jobproxy.Batch;
import mas.jobproxy.job;

public class JobQueryObject implements Serializable {

	private Batch currentJob;
	private AID currentMachine;
	private boolean isJobOnMachine;
	private String type; //must be take from ID.GlobalScheduler.requestType


	public static class Builder {
		Batch currJob;
		AID currMachine;
		private boolean isUnderProcess;
		String requestType;

		public Builder() {

		}

		public Builder currentJob(Batch j) {
			currJob  =j;
			return this;
		}

		public Builder currentMachine(AID machineAID) {
			currMachine = machineAID;
			return this;
		}

		public Builder underProcess(boolean value) {
			isUnderProcess=value;
			return this;

		}
		
		public Builder requestType(String reqType){
			requestType=reqType;
			return this;
		}
		
		public JobQueryObject build() {
			return new JobQueryObject(this);
		}
	}

	private JobQueryObject(Builder builder) {
		currentJob=builder.currJob;
		currentMachine=builder.currMachine;
		isJobOnMachine=builder.isUnderProcess;
		type=builder.requestType;
	}

	public AID getCurrentMachine() {
		return currentMachine;
	}

	public Batch getCurrentJob() {
		return currentJob;
	}

	public boolean isOnMachine(){
		return isJobOnMachine;
	}

	public String getType() {
		return type;
	}
/*
	public void setType(String type) {
		this.type = type;
	}*/

	

}
