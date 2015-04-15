package mas.jobproxy;

import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a batch of jobs
 * @author Anand Prajapati
 */

public class Batch implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final double lowRegretMultiplier = 1,
			MediumRegretMultiplier = 2,
			HighRegretMultiplier = 3;

	private ArrayList<job> jobsInBatch;
	
	private String customerId = null;
	private String batchId = null;
	private int batchNo;

	private double CPN;
	private double Cost;
	private double penaltyRate;

	private long waitingTime;
	private long startTime;
	private long completionTime;
	private Date generationTime;

	private Date dueDateByCustomer;

	private AID WinnerLSA;
	private AID LSABidder;
	private double BidByLSA ;

	private double slack;
	private double regret;

	private int currentJobIndex = 0;
	private boolean isBatchComplete = false;;
	private boolean isAllJobsComplete = false;

	// position is used as an index of batch in it's queue.
	// it is used for scheduling sequence of batches
	private int position;
	private double profit;
	private int currentOperationIndex = 0;
	
	public Batch(String batchId) {
		this.batchId = batchId;
		jobsInBatch = new ArrayList<job>();
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public double getRegretMultiplier() {
		if(this.regret < 1.0)
			return lowRegretMultiplier;
		else if( this.regret < 1.1)
			return MediumRegretMultiplier;
		else
			return HighRegretMultiplier;
	}

	public double getSlack() {
		return slack;
	}

	public void setSlack(double slack) {
		this.slack = slack;
	}

	public double getRegret() {
		return regret;
	}

	public void setRegret(double regret) {
		this.regret = regret;
	}

	public double getCPN() {
		return CPN;
	}

	public void setCPN(double cPN) {
		CPN = cPN;
	}

	public double getCost() {
		return Cost;
	}

	public void setCost(double cost) {
		Cost = cost;
	}

	public double getPenaltyRate() {
		return penaltyRate;
	}

	public void setPenaltyRate(double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	public double getBidByLSA() {
		return BidByLSA;
	}

	public void setBidByLSA(double bidByLSA) {
		BidByLSA = bidByLSA;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return total processing time for this batch
	 */
	public long getTotalBatchProcessingTime() {
		return getFirstJob().getTotalProcessingTime() * getBatchCount();
	}

//	public void setBatchProcessingTime(long batchProcessingTime) {
//		this.batchProcessingTime = batchProcessingTime;
//	}

	public boolean isBatchComplete() {
		return getFirstJob().isComplete();
//		return isBatchComplete;
	}

	public boolean isAllJobsComplete() {
		return this.isAllJobsComplete;
	}

	public void resetJobsComplete() {
		isAllJobsComplete = false;
		this.currentJobIndex = 0;
	}

	public job getCurrentJob() {
		return jobsInBatch.get(currentJobIndex);
	}

	public int getCurrentJobNo() {
		return currentJobIndex;
	}

	public void incrementCurrentJob() {
		this.currentJobIndex++ ;
		/**
		 *  if index becomes >= the size of the batch it means all jobs are done
		 *  index for last job is 'size()-1'
		 */
		if(this.currentJobIndex >= this.jobsInBatch.size() ){
			isAllJobsComplete = true;
		} else {
			isAllJobsComplete = false;
		}
	}

	public AID getWinnerLSA() {
		return WinnerLSA;
	}

	public void setWinnerLSA(AID winnerLSA) {
		WinnerLSA = winnerLSA;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public AID getLSABidder() {
		return LSABidder;
	}

	public void setLSABidder(AID lSABidder) {
		LSABidder = lSABidder;
	}

	public Date getDueDateByCustomer() {
		return dueDateByCustomer;
	}

	public void setDueDateByCustomer(Date dueDateByCustomer) {
		this.dueDateByCustomer = dueDateByCustomer;
	}

	public void setDueDateMillisByCustomer(long dueDateByCustomer) {
		this.dueDateByCustomer = new Date(dueDateByCustomer);
	}

	public long getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}

	public long getStartTimeMillis() {
		return startTime;
	}

	public void setStartTimeMillis(long startTime) {
		this.startTime = startTime;
	}

	public long getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(long completionTime) {
		this.completionTime = completionTime;
	}

	public int getBatchNumber() {
		return batchNo;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNo = batchNumber;
	}

	public Date getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(Date generationTime) {
		this.generationTime = generationTime;
	}

	public void setGenerationTime(long generationTime) {
		this.generationTime = new Date(generationTime);
	}

	public int getBatchCount() {
		return jobsInBatch.size();
	}
	
	public ArrayList<job> getJobsInBatch() {
		return jobsInBatch;
	}
	
	public void setJobsInBatch(ArrayList<job> jobsInBatch) {
		this.jobsInBatch = jobsInBatch;
		for(int i = 0; i < jobsInBatch.size() ; i++) {
			jobsInBatch.get(i).setJobNo(i + 1);
		}
	}
	
	public String getBatchId() {
		return batchId;
	}
	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public void clearAllJobs() {
		this.jobsInBatch.clear();
	}

	public job getFirstJob() {

		if(! jobsInBatch.isEmpty())
			return jobsInBatch.get(0);

		return null;
	}
	
	public job getLastJob() {
		
		if(! jobsInBatch.isEmpty())
			return jobsInBatch.get(jobsInBatch.size() -1);
		
		return null;
	}

	public int getCurrentOperationNumber() {
		return currentOperationIndex;
	}

	public String getCurrentOperationType() {
		return getFirstJob().getCurrentOperation().getJobOperationType();
	}

	/**
	 * increment current operation index by 1 
	 */

	public void IncrementOperationNumber() {
		this.currentOperationIndex ++ ;
		
		for(int i = 0; i < jobsInBatch.size(); i++) {
			jobsInBatch.get(i).IncrementOperationNumber();
		}
		/**
		 *  if index becomes >= the size of the operations it means all operations are done
		 *  index for last operation is 'size()-1'
		 */
		if(this.currentOperationIndex >= this.getFirstJob().getOperations().size() ) {
			isBatchComplete = true;
		} else {
			this.isBatchComplete = false;
		}
	}

	/**
	 * set Start time of current operation for the batch i.e.
	 * set start time of current operation for first job in the batch 
	 * @param startTime
	 */
	public void setCurrentOperationStartTime(long startTime) {
		this.getFirstJob().getOperations().get(currentOperationIndex).setStartTime(startTime);
	}

	/**
	 * get Start time of current operation for the batch i.e.
	 * get start time of current operation for first job in the batch 
	 */
	public long getCurrentOperationStartTime() {
		return this.getFirstJob().getOperations().get(currentOperationIndex ).getStartTime();
	}

	/**
	 * set completion time of current operation for the batch i.e.
	 * set completion time of current operation for first job in the batch 
	 * @param completiontime
	 */

	public void setCurrentOperationCompletionTime(long completionTime) {
		this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
		get(currentOperationIndex).setCompletionTime(completionTime);
	}

	public long getCurrentOperationCompletionTime() {
		return this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
				get(currentOperationIndex).getCompletionTime();
	}

	/**
	 * @return processing time for current operation of whole batch
	 */
	public long getCurrentOperationProcessingTime() {
		return this.getFirstJob().getCurrentOperationProcessingTime() * getBatchCount();
	}

	/**
	 * set processing time for current operation for each job in the batch.
	 * processingTime is the time for one job of the corresponding operation
	 * in the batch and not for the whole batch. Processing time should be passed in 
	 * milliseconds.
	 * @param processingTime
	 */
	public void setCurrentOperationProcessingTime(long processingTime) {
		for(int i = 0; i < jobsInBatch.size() ; i++ ) {
			this.jobsInBatch.get(i).setCurrentOperationProcessingTime(processingTime);
		}
	}

	public long getCurrentOperationDueDate() {
		return this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
				get(currentOperationIndex).getCompletionTime();
	}
	
	public long getTotalProcessingTime() {
		return this.getFirstJob().getTotalProcessingTime()*getBatchCount();
	}

	public int getNumOperations() {
		return getFirstJob().getOperations().size();
	}
	
	public void setOperations(ArrayList<jobOperation> ops) {
		for(int i = 0; i < jobsInBatch.size() ; i++) {
			this.jobsInBatch.get(i).setOperations(ops);
		}
	}

	/**
	 * update the job in the batch with the coming job.
	 * @param comingJob
	 */
	public void updateJob(job comingJob) {
		int jNum = comingJob.getJobNo();
		for(int i = 0 ; i < jobsInBatch.size(); i++) {
			if(jobsInBatch.get(i).getJobNo() == jNum) {
				jobsInBatch.set(i, comingJob);
			}
		}
	}

	public void resetCurrentOperationNumber() {
		this.currentOperationIndex = 0;
		isBatchComplete = false;
		for(int i = 0; i < jobsInBatch.size(); i++) {
			jobsInBatch.get(i).resetOpnNoToZero();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchId == null) ? 0 : batchId.hashCode());
		result = prime * result + batchNo;
		result = prime * result
				+ ((customerId == null) ? 0 : customerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Batch other = (Batch) obj;
		if (batchId == null) {
			if (other.batchId != null)
				return false;
		} else if (!batchId.equals(other.batchId))
			return false;
		if (batchNo != other.batchNo)
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		return true;
	}
}
