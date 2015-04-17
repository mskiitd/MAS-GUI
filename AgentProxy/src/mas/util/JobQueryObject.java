package mas.util;

import jade.core.AID;
import jade.util.leap.Serializable;
import mas.jobproxy.Batch;
import mas.util.JobQueryObject.Builder;

public class JobQueryObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private Batch currentBatch;
	private AID currentMachine;
	private Boolean isJobOnMachine;
	//must be take from ID.GlobalScheduler.requestType
	private String type; 

	public static class Builder {
		Batch currBatch;
		AID currMachine;
		private Boolean isUnderProcess=null;
		//boolean is not used because it has false as default value
		//which indicates that batch is not on machine
		//but when JobQueryObject is initialized, isUnderProcess 
		//has no meaning unless assigned true/false value
		String requestType;

		public Builder() {

		}

		public Builder currentBatch(Batch j) {
			currBatch  =j;
			return this;
		}

		public Builder currentMachine(AID machineAID) {
			currMachine = machineAID;
			return this;
		}

		public Builder underProcess(Boolean value) {
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
		currentBatch = builder.currBatch;
		currentMachine = builder.currMachine;
		isJobOnMachine = builder.isUnderProcess;
		type = builder.requestType;
	}

	public AID getCurrentMachine() {
		return currentMachine;
	}

	public Batch getCurrentBatch() {
		return currentBatch;
	}

	public Boolean isOnMachine(){
		return isJobOnMachine;
	}

	public String getType() {
		return type;
	}
	/*
	public void setType(String type) {
		this.type = type;
	}*/

	@Override
	public String toString() {
		return "JobQueryObject [currentBatch=" + currentBatch
				+ ", currentMachine=" + currentMachine + ", isJobOnMachine="
				+ isJobOnMachine + ", type=" + type + "]";
	}

}
