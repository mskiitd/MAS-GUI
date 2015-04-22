package mas.jobproxy;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Anand Prajapati
 * 
 * Represents an operation to be performed on a manufacturing job as in grooving, slotting, cutting, milling, turning etc.
 *
 */
public class jobOperation implements operationInterface,Serializable {

	private static final long serialVersionUID = 1L;
	private long processingTime;
	private long startTime;
	private long CompletionTime;

	private String machineOperated;
	private long GorLdueDate;//Global or Local dueDatewill be set during global/local due date calculation

	private String jobOperationType;
	private ArrayList<jobDimension> jDims;

	public String getMachineOperated() {
		return machineOperated;
	}

	public void setMachineOperated(String machineOperated) {
		this.machineOperated = machineOperated;
	}

	/**
	 * @return start time of this operation
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Sets start time for this operation
	 * @param startTime
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return completion time of this operation
	 */
	public long getCompletionTime() {
		return CompletionTime;
	}

	/**
	 * Set completion time for this operation
	 * @param completionTime
	 */
	public void setCompletionTime(long completionTime) {
		CompletionTime = completionTime;
	}

	/**
	 * @return Global or local due date for this operation
	 */
	public long getGorLdueDate() {
		return GorLdueDate;
	}

	/**
	 * Sets Local or global due date for this operation
	 * @param gorLdueDate
	 */
	public void setGorLdueDate(long gorLdueDate) {
		GorLdueDate = gorLdueDate;
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}

	/**
	 * Sets processing time for this operation
	 * @param processingTime
	 */
	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}

	/**
	 * @return operation type
	 */
	public String getJobOperationType() {
		return jobOperationType;
	}

	/**
	 * Sets operation type
	 * @param jobOperationType
	 */
	public void setJobOperationType(String jobOperationType) {
		this.jobOperationType = jobOperationType;
	}

	/**
	 * @return List of dimensions associated with this operation
	 */
	public ArrayList<jobDimension> getjDims() {
		return jDims;
	}

	/**
	 * Sets list of dimensions associated with this operation
	 * @param jDims
	 */
	public void setjDims(ArrayList<jobDimension> jDims) {
		this.jDims = jDims;
	}

	/**
	 * add a dimension to list of dimensions associated with this operation
	 * @param jdim
	 */
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

}
