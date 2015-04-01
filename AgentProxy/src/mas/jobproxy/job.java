package mas.jobproxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/** Represents a manufacturing shop floor job
 */

public class job implements Serializable {

	private static final long serialVersionUID = 1L;

	private int jobNo;
	private String jobID;

	private Date startTime;
	private Date jobDuedate;
	private Date completionTime;

	private ArrayList<jobOperation> operations;
	public int currentOperationNumber = 0;

	// a flag to indicate that all operations of the job are finished
	private boolean IsComplete = false;

	public static class Builder {
		private int jobNo;
		private String jobID;
		//due date mentioned by customer for job
		private Date custdDate;  
		//this will be same as due date of last operation for local due date calculation
		//start date of job given by customer i.e. start time of 1st operation
		private Date custStartDate;
		// Optional parameters - initialized to default values
		private ArrayList<jobOperation> jOperations;

		public Builder(String jobID) {
			this.jobID = jobID;
			this.jOperations = new ArrayList<jobOperation>();
		}

		public Builder jobDueDateTime(Date val)
		{ custdDate = val; return this; }

		public Builder jobDueDateTime(long val)
		{ custdDate = new Date(val); return this; }

		public Builder jobStartTimeByCust(long val){
			custStartDate = new Date(val);
			return this;
		}

		public Builder jobOperation(ArrayList<jobOperation> val)
		{ jOperations.addAll(val); return this; }

		public job build() {
			return new job(this);
		}
	}
	private job(Builder builder) {
		jobID = builder.jobID;
		jobNo = builder.jobNo;
		jobDuedate = builder.custdDate;
		this.operations = new ArrayList<jobOperation>();
		operations.addAll(builder.jOperations);
		startTime=builder.custStartDate;
	}

	@Override
	public boolean equals(Object o) {

		if( !(o instanceof job)) {
			return false;
		}
		job j = (job)o;

		return (this.jobNo == j.jobNo) && (this.jobID.equals(j.jobID));
	}

	public void setCurrentOperationDueDate(long dueDate) {
		this.operations.get(currentOperationNumber).setDueDate(dueDate);
	}

	public long getCurrentOperationDueDate() {
		return this.operations.get(currentOperationNumber).getDueDate();
	}

	public void setCurrentOperationStartTime(long startTime) {
		this.operations.get(currentOperationNumber).setStartTime(startTime);
	}

	public long getCurrentOperationStartTime() {
		return this.operations.get(currentOperationNumber).getStartTime();
	}

	public ArrayList<jobDimension> getCurrentOperationDimensions() {
		return this.operations.get(this.currentOperationNumber).getjDims();
	}

	public void setCurrentOperationDimension(ArrayList<jobDimension> jDim) {
		this.operations.get(currentOperationNumber).setjDims(jDim);
	}

	public void setCurrentOperationCompletionTime(long completionTime) {
		this.operations.get(currentOperationNumber).setCompletionTime(completionTime);
	}

	public long getCurrentOperationCompletionTime() {
		return this.operations.get(currentOperationNumber).getCompletionTime();
	}

	public ArrayList<jobOperation> getOperations() {
		return operations;
	}

	public jobOperation getCurrentOperation() {
		if(this.currentOperationNumber < operations.size())
			return operations.get(this.currentOperationNumber);
		return null;
	}

	public int getCurrentOperationNumber() {
		return currentOperationNumber;
	}

	public void setCurrentOperationNumber(int currentOperationNumber) {
		this.currentOperationNumber = currentOperationNumber;
	}

	public long getCurrentOperationProcessTime() {
		return operations.get(this.currentOperationNumber).getProcessingTime();
	}

	public long getProcessTime(int index) {
		return this.operations.get(index).getProcessingTime();
	}

	public void setCurrentOperationProcessingTime(long processingTime) {
		operations.get(currentOperationNumber).setProcessingTime(processingTime);
	}

	public void IncrementOperationNumber() {
		this.currentOperationNumber++ ;
		if(this.currentOperationNumber > this.operations.size()-1){
			IsComplete = true;
		}
	}

	public boolean isComplete(){
		return this.IsComplete;
	}

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

	public Date getStartTimeByCust() {
		return startTime;
	}

	public void setJobStartTimeByCust(Date startTime) {
		this.startTime = startTime;
	}

	public void setJobStartTimeByCust(long startTime) {
		this.startTime = new Date(startTime);
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public long getCompletionTime(int index) {
		return this.operations.get(index).getCompletionTime();
	}

	public long getStartTime(int index) {
		return this.operations.get(index).getStartTime();
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public void setCompletionTime(long completionTime) {
		this.completionTime = new Date(completionTime);
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public Date getJobDuedatebyCust() {
		return jobDuedate;
	}

	public void setJobDuedatebyCust(Date duedate) {
		this.jobDuedate = duedate;
	}

	public void setJobDuedatebyCust(long duedate) {
		this.jobDuedate.setTime(duedate);
	}

}

