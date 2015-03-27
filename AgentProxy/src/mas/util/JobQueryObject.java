package mas.util;

import jade.core.AID;
import mas.jobproxy.job;

public class JobQueryObject {

	private job currentJob;
	private AID currentMachine;
	private boolean isJobOnMachine;


	public static class Builder {
		job currJob;
		AID currMachine;
		private boolean isUnderProcess;

		public Builder() {

		}

		public Builder currentJob(job j) {
			currJob=j;
			return this;
		}

		public Builder currentMachine(AID machineAID) {
			currMachine=machineAID;
			return this;
		}

		public Builder underProcess(boolean value) {
			isUnderProcess=value;
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
	}

	public AID getCurrentMachine() {
		return currentMachine;
	}

	public job getCurrentJob() {
		return currentJob;
	}

	public boolean isOnMachine(){
		return isJobOnMachine;
	}

}
