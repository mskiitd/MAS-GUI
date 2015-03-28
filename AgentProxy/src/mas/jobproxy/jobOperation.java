package mas.jobproxy;

import java.io.Serializable;
import java.util.ArrayList;

public class jobOperation implements operationInterface,Serializable {

	private static final long serialVersionUID = 1L;
	private long processingTime;
	private long startTime;
	private long CompletionTime;

	private String machineOperated;
	private long dueDate;

	private OperationType jobOperationType;
	private ArrayList<jobDimension> jDims;

	public String getMachineOperated() {
		return machineOperated;
	}

	public void setMachineOperated(String machineOperated) {
		this.machineOperated = machineOperated;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getCompletionTime() {
		return CompletionTime;
	}

	public void setCompletionTime(long completionTime) {
		CompletionTime = completionTime;
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}

	public OperationType getJobOperationType() {
		return jobOperationType;
	}

	public void setJobOperationType(OperationType jobOperationType) {
		this.jobOperationType = jobOperationType;
	}

	public ArrayList<jobDimension> getjDims() {
		return jDims;
	}

	public void setjDims(ArrayList<jobDimension> jDims) {
		this.jDims = jDims;
	}

	public void addjDim(jobDimension jdim) {
		if(this.jDims == null) 
			this.jDims = new ArrayList<jobDimension>();

			this.jDims.add(jdim);
	}

	@Override
	public String toString() {
		return new StringBuilder().
				append(jobOperationType).
				toString();
	}

	@Override
	public long getDueDate() {
		return this.dueDate;
	}

	@Override
	public void setDueDate(long localDueDate) {
		this.dueDate = localDueDate;
	}

}
