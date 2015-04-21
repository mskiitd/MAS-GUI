package mas.util;

import jade.core.AID;
import jade.util.leap.Serializable;
import mas.jobproxy.Batch;

/**
 * Query object for a batch
 */

public class BatchQueryObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private Batch currentBatch;
	private AID currentMachine;
	private Boolean isJobOnMachine;
	
	//must be take from ID.GlobalScheduler.requestType
	private String type; 

	public static class Builder {
		Batch currBatch;
		AID currMachine;
		private Boolean isUnderProcess = null;
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

		public BatchQueryObject build() {
			return new BatchQueryObject(this);
		}
	}

	private BatchQueryObject(Builder builder) {
		currentBatch = builder.currBatch;
		currentMachine = builder.currMachine;
		isJobOnMachine = builder.isUnderProcess;
		type = builder.requestType;
	}

	/**
	 * @return machine where this batch is getting processed or is in the waiting queue
	 */
	public AID getCurrentMachine() {
		return currentMachine;
	}

	/**
	 * @return the batch for which this query is created
	 */
	public Batch getCurrentBatch() {
		return currentBatch;
	}

	/**
	 * @return true if the queried batch is loaded on the machine
	 */
	public Boolean isOnMachine(){
		return isJobOnMachine;
	}

	/**
	 * @return type of the query made as in cancel/get current status/ change due date etc.
	 */
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
