package mas.jobproxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.alee.log.Log;

/** Represents a manufacturing shop floor job
 * 
 *  There is no need to explicitly set start time of job.
 *  Start time of job will be set whenever we set start time of current operation
 *  Start time of job will just be the start time of it's first operation
 *  Similarly finish time of job is set
 */

public class job implements Serializable {

	private static final long serialVersionUID = 1L;

	private int jobNo;
	private String jobID;

	private Date startTime;
	private Date jobDuedateByCustomer;
	private Date completionTime;

	private ArrayList<jobOperation> operations;
	private int currentOperationIndex = 0;

	// a flag to indicate that all operations of the job are finished
	private boolean IsComplete = false;

	public static class Builder {
		private int jobNo;
		private String jobID;
		//due date mentioned by customer for job
		private Date custdDate;  
		//this will be same as due date of last operation for local due date calculation
		private ArrayList<jobOperation> jOperations;

		public Builder(String jobID) {
			this.jobID = jobID;
			this.jOperations = new ArrayList<jobOperation>();
		}

		public Builder jobDueDateByCustomer(Date val)
		{ custdDate = val; return this; }

		public Builder jobDueDateByCustomer(long val)
		{ custdDate = new Date(val); return this; }

		public Builder jobOperation(ArrayList<jobOperation> val)
		{ jOperations.addAll(val); return this; }

		public job build() {
			return new job(this);
		}
	}
	private job(Builder builder) {
		jobID = builder.jobID;
		jobNo = builder.jobNo;
		jobDuedateByCustomer = builder.custdDate;
		this.operations = new ArrayList<jobOperation>();
		operations.addAll(builder.jOperations);
	}

	public job(job other) {
		this.jobID = other.jobID;
		this.jobNo = other.jobNo;

		if(other.startTime != null) {
			this.startTime = (Date) other.startTime.clone();
		}
		if(other.jobDuedateByCustomer != null) {
			this.jobDuedateByCustomer = (Date) other.jobDuedateByCustomer.clone();
		}
		if(other.completionTime != null) {
			this.completionTime = (Date) other.completionTime.clone();
		}

		this.operations = new ArrayList<jobOperation>();
		this.operations.addAll(other.operations);

		this.currentOperationIndex = other.currentOperationIndex;
		this.IsComplete = other.IsComplete;
	}

	@Override
	public boolean equals(Object o) {

		if( !(o instanceof job)) {
			return false;
		}
		job j = (job)o;

		return (this.jobNo == j.jobNo) && (this.jobID.equals(j.jobID));
	}

	public long getCurrentOperationFinishTime() {
		return this.operations.get(currentOperationIndex).getCompletionTime();
	}

	/**
	 * sets start time for current operation of the job
	 * @param startTime
	 */
	public void setCurrentOperationStartTime(long startTime) {
		this.operations.get(currentOperationIndex).setStartTime(startTime);
		if(currentOperationIndex == 0) {
			// indicates start time of job
			this.startTime = new Date(startTime);
		}
	}

	public long getCurrentOperationStartTime() {
		return this.operations.get(currentOperationIndex).getStartTime();
	}

	public ArrayList<jobDimension> getCurrentOperationDimensions() {
		return this.operations.get(this.currentOperationIndex).getjDims();
	}

	public void setCurrentOperationDimension(ArrayList<jobDimension> jDim) {
		this.operations.get(currentOperationIndex).setjDims(jDim);
	}

	/**
	 * Sets completion time of the current operation 
	 * @param completionTime
	 */
	public void setCurrentOperationCompletionTime(long completionTime) {
		this.operations.get(currentOperationIndex).setCompletionTime(completionTime);
		if(this.currentOperationIndex == operations.size() - 1) {
			this.completionTime = new Date(completionTime);
		}
	}

	public long getCurrentOperationCompletionTime() {
		return this.operations.get(currentOperationIndex).getCompletionTime();
	}

	public ArrayList<jobOperation> getOperations() {
		return operations;
	}

	public jobOperation getCurrentOperation() {
		if(this.currentOperationIndex < operations.size())
			return operations.get(this.currentOperationIndex);
		return null;
	}

	public int getCurrentOperationNumber() {
		return currentOperationIndex;
	}

	private void setCurrentOperationNumber(int currentOperationNumber) {
		this.currentOperationIndex = currentOperationNumber;
	}

	public long getCurrentOperationProcessingTime() {
		return operations.get(this.currentOperationIndex).getProcessingTime();
	}

	/**
	 * @param index
	 * @return processing time of operation no <code> index + 1 </code>
	 * It is used while calculating utilization of machine
	 */
	public long getProcessTime(int index) {
		return this.operations.get(index).getProcessingTime();
	}

	/**
	 * @param processingTime
	 * Sets processing time for the current operation of the job
	 */
	public void setCurrentOperationProcessingTime(long processingTime) {
		operations.get(currentOperationIndex).setProcessingTime(processingTime);
	}

	public void IncrementOperationNumber() {
		this.currentOperationIndex++ ;
		if(this.currentOperationIndex > this.operations.size()-1){
			IsComplete = true;
			Log.info("all operations of job " + jobID +" done - flag : ", IsComplete);
		}
	}

	/**
	 * Resets current Operation Index to 0
	 */
	public void resetOpnNoToZero() {
		IsComplete = false;
		this.setCurrentOperationNumber(0);
	}

	public boolean isComplete() {
		return this.IsComplete;
	}

	/**
	 * @return total processing time of the job, i.e. summation of processing times of all the operations
	 * to be performed on this job
	 */
	public long getTotalProcessingTime() {
		long total = 0;
		for(int i = 0 ; i < operations.size(); i++){
			total += operations.get(i).getProcessingTime();
		}
		return total;
	}

	public void setOperations(ArrayList<jobOperation> operations) {
		this.operations = operations;
	}

	public String getJobID(){
		return this.jobID;
	}

	public int getJobNo() {
		return jobNo;
	}

	public void setJobNo(int jobNo) {
		this.jobNo = jobNo;
	}

	//	public void setJobStartTime(Date startTime) {
	//		this.startTime = startTime;
	//	}
	//
	//	public void setJobStartTime(long startTime) {
	//		this.startTime = new Date(startTime);
	//	}

	/**
	 * @param operationNumber
	 * @return start time of the operation no <code>OperationNumber + 1</code>
	 * This is used while calculating the utilization of machine.
	 */
	public long getStartTime(int operationNumber) {
		return this.operations.get(operationNumber).getStartTime();
	}

	/**
	 * @return completion time of the job
	 */
	public Date getJobCompletionTime() {
		return completionTime;
	}

	/**
	 * @param index
	 * @return completion time of the operation no <code> index + 1 </code>
	 */
	public long getCompletionTime(int index) {
		return this.operations.get(index).getCompletionTime();
	}

	/**
	 * @return start time of the job
	 */
	public Date getJobStartTime() {
		return this.startTime;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * @return due date of the job as specified by the customer
	 */
	public Date getJobDuedatebyCust() {
		return jobDuedateByCustomer;
	}

	/**
	 * @param duedate
	 *  Changes due date specified by the customer. Used while negotiating
	 */
	public void setJobDuedatebyCust(Date duedate) {
		this.jobDuedateByCustomer = duedate;
	}

	/**
	 * @param duedate
	 *  Changes due date specified by the customer. Used while negotiating
	 */
	public void setJobDuedatebyCust(long duedate) {
		if(this.jobDuedateByCustomer != null) {
			this.jobDuedateByCustomer.setTime(duedate);
		}else {
			this.jobDuedateByCustomer = new Date(duedate);
		}
	}
}

